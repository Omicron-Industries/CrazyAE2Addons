package net.oktawia.insaneae2addons.entities.research;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.ticking.TickRateModulation;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.LazyManaged;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockControllerBE;
import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition;
import net.oktawia.insaneae2addons.blocks.ICableMachine;
import net.oktawia.insaneae2addons.blocks.ResearchUnitBlock;
import net.oktawia.insaneae2addons.blocks.ResearchUnitFrameBlock;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneFluidRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.logic.research.ResearchStatus;
import net.oktawia.insaneae2addons.menus.block.ResearchUnitMenu;
import net.oktawia.insaneae2addons.defs.InsaneMultiblocks;
import net.oktawia.insaneae2addons.InsaneConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ResearchUnitBE extends AbstractMultiblockControllerBE implements ICableMachine {

    public static final int FLUID_BUFFER_CAPACITY = 64_000;
    public static final double POWER_BUFFER_CAPACITY = 200_000.0;
    private static final int COMPUTATION_POWER_COST = 64;
    private static final int FLUID_COST_DIVISOR = 4;
    private static final int FLUID_LOW_FACTOR = 8;
    private static final String AE2 = "ae2";

    private static final Predicate<FluidStack> RESEARCH_FLUID_ONLY = stack ->
            !stack.isEmpty()
                    && stack.getFluid().getFluidType() == InsaneFluidRegistrar.RESEARCH_FLUID_TYPE.get();

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER =
            new ManagedFieldHolder(ResearchUnitBE.class);

    @DescSynced
    @Getter
    private boolean formed = false;

    @Persisted
    @LazyManaged
    @Getter
    private final FluidTank fluidBuffer = new FluidTank(FLUID_BUFFER_CAPACITY, RESEARCH_FLUID_ONLY);

    @Persisted
    @DescSynced
    @Getter
    private double storedPower = 0.0;

    public ResearchUnitBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.RESEARCH_UNIT_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.RESEARCH_UNIT_BLOCK.get()),
                1.0F
        );
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected MultiblockDefinition getMultiblockDefinition() {
        return InsaneMultiblocks.researchUnit();
    }

    @Override
    protected char frameSymbol() {
        return 'A';
    }

    @Override
    protected void setOwnFormedState(boolean formed) {
        if (this.formed != formed) {
            this.formed = formed;
            setChanged();
        }

        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = getBlockState();
        if (state.hasProperty(ResearchUnitBlock.FORMED)
                && state.getValue(ResearchUnitBlock.FORMED) != formed) {
            level.setBlock(getBlockPos(), state.setValue(ResearchUnitBlock.FORMED, formed), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void setMemberFormedState(BlockPos pos, boolean formed) {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(ResearchUnitFrameBlock.FORMED)
                && state.getValue(ResearchUnitFrameBlock.FORMED) != formed) {
            level.setBlock(pos, state.setValue(ResearchUnitFrameBlock.FORMED, formed), Block.UPDATE_CLIENTS);
        }
    }

    public void unformFramesForRemoval() {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        for (BlockPos pos : this.multiblockState.getBlocksBySymbol(frameSymbol())) {
            setMemberFormedState(pos, false);
        }
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        TickRateModulation result = super.tickingRequest(node, ticksSinceLastCall);

        Level level = getLevel();
        if (level != null && !level.isClientSide()) {
            refillPowerBuffer();
            refillFluidBuffer();
        }

        return result;
    }

    public int getComputation() {
        Level level = getLevel();
        if (!this.formed || level == null) {
            return 0;
        }

        Map<ResourceLocation, Integer> extraValues = extraQValues();
        long tierSum = 0;
        long extra = 0;
        for (BlockPos pos : this.multiblockState.getBlocksBySymbol('Q')) {
            Block block = level.getBlockState(pos).getBlock();
            tierSum += tierValue(block);

            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
            Integer value = id == null ? null : extraValues.get(id);
            if (value != null) {
                extra += value;
            }
        }

        long computation = tierSum / 16 + extra;
        return (int) Math.min(Integer.MAX_VALUE, computation);
    }

    private static Map<ResourceLocation, Integer> extraQValues() {
        Map<ResourceLocation, Integer> map = new HashMap<>();
        for (String entry : InsaneConfig.COMMON.RESEARCH_UNIT_EXTRA_Q_BLOCKS.get()) {
            int eq = entry.indexOf('=');
            if (eq <= 0) {
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(entry.substring(0, eq).trim());
            if (id == null) {
                continue;
            }
            try {
                int value = Integer.parseInt(entry.substring(eq + 1).trim());
                if (value != 0) {
                    map.put(id, value);
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return map;
    }

    public boolean doWork() {
        Level level = getLevel();
        if (!this.formed || level == null) {
            return false;
        }

        int computation = getComputation();
        if (computation <= 0) {
            return false;
        }

        double powerCost = (double) computation * COMPUTATION_POWER_COST;
        if (this.storedPower < powerCost) {
            return false;
        }

        int fluidNeed = computation / FLUID_COST_DIVISOR;
        if (fluidNeed > 0 && this.fluidBuffer.getFluidAmount() < fluidNeed) {
            return false;
        }

        this.storedPower -= powerCost;
        if (fluidNeed > 0) {
            this.fluidBuffer.drain(fluidNeed, IFluidHandler.FluidAction.EXECUTE);
        }
        setChanged();
        return true;
    }

    public ResearchStatus getNodeStatus() {
        if (!this.formed) {
            return ResearchStatus.STRUCTURE_INCOMPLETE;
        }

        int computation = getComputation();
        if (computation <= 0) {
            return ResearchStatus.NOT_ENOUGH_COMPUTATION;
        }

        if (this.storedPower < (double) computation * COMPUTATION_POWER_COST) {
            return ResearchStatus.NOT_ENOUGH_POWER;
        }

        int fluidNeed = computation / FLUID_COST_DIVISOR;
        if (fluidNeed > 0 && this.fluidBuffer.getFluidAmount() < fluidNeed) {
            return ResearchStatus.OUT_OF_RESEARCH_FLUID;
        }
        if (fluidNeed > 0 && this.fluidBuffer.getFluidAmount() < fluidNeed * FLUID_LOW_FACTOR) {
            return ResearchStatus.FLUID_LOW;
        }

        return ResearchStatus.READY;
    }

    @Override
    protected MenuType<?> getMenuType() {
        return InsaneMenuRegistrar.RESEARCH_UNIT_MENU.get();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ResearchUnitMenu(id, inventory, this);
    }

    private void refillPowerBuffer() {
        if (!this.formed) {
            return;
        }

        double room = POWER_BUFFER_CAPACITY - this.storedPower;
        if (room <= 0) {
            return;
        }

        IGrid grid = getMainNode().getGrid();
        if (grid == null) {
            return;
        }

        IEnergyService energy = grid.getEnergyService();
        double got = energy.extractAEPower(room, Actionable.MODULATE, PowerMultiplier.CONFIG);
        if (got > 0) {
            this.storedPower += got;
            setChanged();
        }
    }

    private void refillFluidBuffer() {
        Level level = getLevel();
        if (level == null) {
            return;
        }

        int room = this.fluidBuffer.getCapacity() - this.fluidBuffer.getFluidAmount();
        if (room <= 0) {
            return;
        }

        List<BlockPos> tanks = this.multiblockState.getBlocksBySymbol('E');
        if (tanks.isEmpty()) {
            return;
        }

        BlockEntity tankBe = level.getBlockEntity(tanks.get(0));
        if (tankBe == null) {
            return;
        }

        tankBe.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(handler -> {
            FluidStack drained = handler.drain(room, IFluidHandler.FluidAction.SIMULATE);
            if (!RESEARCH_FLUID_ONLY.test(drained)) {
                return;
            }

            int filled = this.fluidBuffer.fill(drained, IFluidHandler.FluidAction.EXECUTE);
            if (filled > 0) {
                handler.drain(filled, IFluidHandler.FluidAction.EXECUTE);
            }
        });
    }

    private static int tierValue(Block block) {
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
        if (id == null || !AE2.equals(id.getNamespace())) {
            return 0;
        }

        return switch (id.getPath()) {
            case "1k_crafting_storage" -> 1;
            case "4k_crafting_storage" -> 4;
            case "16k_crafting_storage" -> 16;
            case "64k_crafting_storage" -> 64;
            case "256k_crafting_storage" -> 256;
            default -> 0;
        };
    }
}
