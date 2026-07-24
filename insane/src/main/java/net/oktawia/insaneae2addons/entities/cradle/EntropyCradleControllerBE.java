package net.oktawia.insaneae2addons.entities.cradle;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockControllerBE;
import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.blocks.cradle.EntropyCradleBlock;
import net.oktawia.insaneae2addons.blocks.cradle.EntropyCradleCapacitorBlock;
import net.oktawia.insaneae2addons.blocks.cradle.EntropyCradleControllerBlock;
import net.oktawia.insaneae2addons.defs.InsaneMultiblocks;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneRecipes;
import net.oktawia.insaneae2addons.menus.block.EntropyCradleControllerMenu;
import net.oktawia.insaneae2addons.recipes.CradleContext;
import net.oktawia.insaneae2addons.recipes.CradleRecipe;

import java.util.Optional;

public class EntropyCradleControllerBE extends AbstractMultiblockControllerBE {

    private static final int CAPACITOR_LEVELS = 6;

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER =
            new ManagedFieldHolder(EntropyCradleControllerBE.class);

    @Persisted
    @Getter
    private int storedEnergy = 0;

    private int lastLitLevels = -1;
    private boolean wasFull = false;

    public EntropyCradleControllerBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.ENTROPY_CRADLE_CONTROLLER_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.ENTROPY_CRADLE_CONTROLLER_BLOCK.get()),
                2.0F
        );
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected MultiblockDefinition getMultiblockDefinition() {
        return InsaneMultiblocks.entropyCradle();
    }

    @Override
    protected char frameSymbol() {
        return 'B';
    }

    @Override
    protected MenuType<?> getMenuType() {
        return InsaneMenuRegistrar.ENTROPY_CRADLE_CONTROLLER_MENU.get();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new EntropyCradleControllerMenu(id, inventory, this);
    }

    @Override
    protected void setOwnFormedState(boolean formed) {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = getBlockState();
        if (state.hasProperty(EntropyCradleControllerBlock.FORMED)
                && state.getValue(EntropyCradleControllerBlock.FORMED) != formed) {
            level.setBlock(getBlockPos(), state.setValue(EntropyCradleControllerBlock.FORMED, formed), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void setMemberFormedState(BlockPos pos, boolean formed) {
        setWallFormed(pos, formed);
    }

    @Override
    protected void afterFormed() {
        for (BlockPos pos : this.multiblockState.getBlocksBySymbol('A')) {
            setCapacitorFormed(pos, true);
        }
    }

    @Override
    protected void afterDisformed() {
        for (BlockPos pos : this.multiblockState.getBlocksBySymbol('A')) {
            setCapacitorFormed(pos, false);
            setCapacitorPower(pos, false);
        }
        this.lastLitLevels = -1;
    }

    public void unformMembersForRemoval() {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        for (BlockPos pos : this.multiblockState.getBlocksBySymbol('B')) {
            setWallFormed(pos, false);
        }
        for (BlockPos pos : this.multiblockState.getBlocksBySymbol('A')) {
            setCapacitorFormed(pos, false);
            setCapacitorPower(pos, false);
        }
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        TickRateModulation result = super.tickingRequest(node, ticksSinceLastCall);

        Level level = getLevel();
        if (level != null && !level.isClientSide() && this.multiblockState.isFormed()) {
            chargeFromNetwork();
            updateCapacitorLights();
        }

        return result;
    }

    private void chargeFromNetwork() {
        int max = InsaneConfig.COMMON.CRADLE_CAPACITY.get();
        if (this.storedEnergy >= max) {
            return;
        }

        IGrid grid = getMainNode().getGrid();
        if (grid == null) {
            return;
        }

        int remaining = max - this.storedEnergy;
        int maxAE = Math.min(remaining / 2, InsaneConfig.COMMON.CRADLE_CHARGING_SPEED.get());
        if (maxAE <= 0) {
            return;
        }

        double extractedAE = grid.getEnergyService().extractAEPower(maxAE, Actionable.MODULATE, PowerMultiplier.CONFIG);
        if (extractedAE > 0) {
            this.storedEnergy = Math.min(max, this.storedEnergy + (int) (extractedAE * 2));
            setChanged();
        }
    }

    private void updateCapacitorLights() {
        int max = InsaneConfig.COMMON.CRADLE_CAPACITY.get();
        boolean full = this.storedEnergy >= max;
        int litLevels = (int) Math.round((this.storedEnergy / (double) max) * CAPACITOR_LEVELS);

        if (litLevels != this.lastLitLevels) {
            this.lastLitLevels = litLevels;
            BlockPos controllerPos = getBlockPos();
            for (BlockPos capPos : this.multiblockState.getBlocksBySymbol('A')) {
                int level = capPos.getY() - controllerPos.getY();
                setCapacitorPower(capPos, level < litLevels);
            }
        }

        if (full != this.wasFull) {
            this.wasFull = full;
            notifyCapacitorComparators();
        }
    }

    public void onRedstonePulse() {
        Level level = getLevel();
        if (level == null || level.isClientSide() || !this.multiblockState.isFormed()) {
            return;
        }

        int cost = InsaneConfig.COMMON.CRADLE_COST.get();
        if (this.storedEnergy < cost) {
            return;
        }

        Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos origin = getBlockPos().relative(facing.getOpposite(), 5).above(3);

        Optional<CradleRecipe> match = level.getRecipeManager()
                .getRecipeFor(InsaneRecipes.CRADLE_TYPE.get(), new CradleContext(level, origin, facing), level);
        if (match.isEmpty() || match.get().resultBlock() == null) {
            return;
        }

        this.storedEnergy -= cost;
        setChanged();

        for (BlockPos capPos : this.multiblockState.getBlocksBySymbol('A')) {
            setCapacitorPower(capPos, false);
        }
        this.lastLitLevels = 0;

        clearChamber(level, origin);
        level.setBlock(origin, match.get().resultBlock().defaultBlockState(), Block.UPDATE_ALL);

        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.EXPLOSION,
                    origin.getX() + 0.5, origin.getY() + 0.5, origin.getZ() + 0.5,
                    30, 0.5, 0.5, 0.5, 0.01);
        }
        level.playSound(null, origin, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private void clearChamber(Level level, BlockPos center) {
        for (int dy = -2; dy <= 2; dy++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dx = -2; dx <= 2; dx++) {
                    BlockPos target = center.offset(dx, dy, dz);
                    if (level instanceof ServerLevel server
                            && (Math.abs(dx) == 2 || Math.abs(dy) == 2 || Math.abs(dz) == 2)) {
                        server.sendParticles(ParticleTypes.FIREWORK,
                                target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5,
                                5, 0.5, 0.5, 0.5, 0.01);
                    }
                    level.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private void setWallFormed(BlockPos pos, boolean formed) {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(EntropyCradleBlock.FORMED) && state.getValue(EntropyCradleBlock.FORMED) != formed) {
            level.setBlock(pos, state.setValue(EntropyCradleBlock.FORMED, formed), Block.UPDATE_CLIENTS);
        }
    }

    private void setCapacitorFormed(BlockPos pos, boolean formed) {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(EntropyCradleCapacitorBlock.FORMED)
                && state.getValue(EntropyCradleCapacitorBlock.FORMED) != formed) {
            level.setBlock(pos, state.setValue(EntropyCradleCapacitorBlock.FORMED, formed), Block.UPDATE_CLIENTS);
        }
    }

    private void setCapacitorPower(BlockPos pos, boolean powered) {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(EntropyCradleCapacitorBlock.POWER)
                && state.getValue(EntropyCradleCapacitorBlock.POWER) != powered) {
            level.setBlock(pos, state.setValue(EntropyCradleCapacitorBlock.POWER, powered), Block.UPDATE_CLIENTS);
        }
    }

    private void notifyCapacitorComparators() {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        Block capacitorBlock = InsaneBlockRegistrar.ENTROPY_CRADLE_CAPACITOR_BLOCK.get();
        for (BlockPos capPos : this.multiblockState.getBlocksBySymbol('A')) {
            level.updateNeighbourForOutputSignal(capPos, capacitorBlock);
        }
    }
}
