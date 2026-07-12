package net.oktawia.insaneae2addons.entities;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.ticking.TickRateModulation;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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
import net.oktawia.insaneae2addons.blocks.ResearchUnitFrameBlock;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneFluidRegistrar;
import net.oktawia.insaneae2addons.multiblock.InsaneMultiblocks;

import java.util.List;
import java.util.function.Predicate;

public class ResearchUnitBE extends AbstractMultiblockControllerBE {

    public static final int FLUID_BUFFER_CAPACITY = 64_000;
    private static final int COMPUTATION_POWER_COST = 64;
    private static final int FLUID_COST_DIVISOR = 4;
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
    @DescSynced
    @Getter
    private final FluidTank fluidBuffer = new FluidTank(FLUID_BUFFER_CAPACITY, RESEARCH_FLUID_ONLY);

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
        return InsaneMultiblocks.UNIT_FRAME_SYMBOL;
    }

    @Override
    protected void setOwnFormedState(boolean formed) {
        if (this.formed != formed) {
            this.formed = formed;
            setChanged();
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
            refillFluidBuffer();
        }

        return result;
    }

    public int getComputation() {
        Level level = getLevel();
        if (!this.formed || level == null) {
            return 0;
        }

        long sum = 0;
        for (BlockPos pos : this.multiblockState.getBlocksBySymbol(InsaneMultiblocks.UNIT_CORE_SYMBOL)) {
            sum += tierValue(level.getBlockState(pos).getBlock());
        }

        long computation = sum / 16;
        return (int) Math.min(Integer.MAX_VALUE, computation);
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

        IGrid grid = getMainNode().getGrid();
        if (grid == null) {
            return false;
        }

        double powerCost = (double) computation * COMPUTATION_POWER_COST;
        IEnergyService energy = grid.getEnergyService();
        if (energy.extractAEPower(powerCost, Actionable.SIMULATE, PowerMultiplier.CONFIG) < powerCost) {
            return false;
        }

        int fluidNeed = computation / FLUID_COST_DIVISOR;
        if (fluidNeed > 0 && this.fluidBuffer.getFluidAmount() < fluidNeed) {
            return false;
        }

        energy.extractAEPower(powerCost, Actionable.MODULATE, PowerMultiplier.CONFIG);
        if (fluidNeed > 0) {
            this.fluidBuffer.drain(fluidNeed, IFluidHandler.FluidAction.EXECUTE);
        }
        return true;
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

        List<BlockPos> tanks = this.multiblockState.getBlocksBySymbol(InsaneMultiblocks.UNIT_TANK_SYMBOL);
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
