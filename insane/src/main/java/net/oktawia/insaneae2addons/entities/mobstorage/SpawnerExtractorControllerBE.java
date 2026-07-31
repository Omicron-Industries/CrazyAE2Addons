package net.oktawia.insaneae2addons.entities.mobstorage;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import lombok.Getter;

import appeng.api.config.Actionable;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.StorageHelper;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.definitions.AEItems;

import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockControllerBE;
import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.blocks.mobstorage.SpawnerExtractorControllerBlock;
import net.oktawia.insaneae2addons.blocks.mobstorage.SpawnerExtractorWallBlock;
import net.oktawia.insaneae2addons.defs.InsaneMultiblocks;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.menus.block.SpawnerExtractorControllerMenu;
import net.oktawia.insaneae2addons.mixins.BaseSpawnerAccessor;
import net.oktawia.insaneae2addons.mobstorage.MobKey;

public class SpawnerExtractorControllerBE extends AbstractMultiblockControllerBE implements IUpgradeableObject {

    private static final int EXTRACT_INTERVAL_TICKS = 20;
    private static final int VANILLA_SPAWN_DELAY = 200;
    private static final String UPGRADES_TAG = "upgrades";

    @Getter
    private final IUpgradeInventory upgrades = UpgradeInventories.forMachine(
            InsaneBlockRegistrar.SPAWNER_EXTRACTOR_CONTROLLER_BLOCK.get(), 4, this::saveChanges);

    private int extractCooldown;

    public SpawnerExtractorControllerBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.SPAWNER_EXTRACTOR_CONTROLLER_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.SPAWNER_EXTRACTOR_CONTROLLER_BLOCK.get()),
                2.0F);
    }

    @Override
    protected MultiblockDefinition getMultiblockDefinition() {
        return InsaneMultiblocks.spawnerExtractor();
    }

    @Override
    protected char frameSymbol() {
        return 'A';
    }

    @Override
    protected MenuType<?> getMenuType() {
        return InsaneMenuRegistrar.SPAWNER_EXTRACTOR_CONTROLLER_MENU.get();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new SpawnerExtractorControllerMenu(id, inventory, this);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        this.upgrades.writeToNBT(tag, UPGRADES_TAG);
    }

    @Override
    public void loadTag(CompoundTag tag) {
        super.loadTag(tag);
        this.upgrades.readFromNBT(tag, UPGRADES_TAG);
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (int i = 0; i < this.upgrades.size(); i++) {
            drops.add(this.upgrades.getStackInSlot(i));
        }
    }

    @Override
    public @Nullable InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(ISegmentedInventory.UPGRADES)) {
            return this.upgrades;
        }
        return super.getSubInventory(id);
    }

    @Override
    protected void setOwnFormedState(boolean formed) {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = getBlockState();
        if (state.hasProperty(SpawnerExtractorControllerBlock.FORMED)
                && state.getValue(SpawnerExtractorControllerBlock.FORMED) != formed) {
            level.setBlock(getBlockPos(), state.setValue(SpawnerExtractorControllerBlock.FORMED, formed),
                    Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void setMemberFormedState(BlockPos pos, boolean formed) {
        setWallFormed(pos, formed);
    }

    @Override
    protected void afterFormed() {
        setSpawnerSuppressed(true);
    }

    @Override
    protected void afterDisformed() {
        setSpawnerSuppressed(false);
    }

    public void unformMembersForRemoval() {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        setSpawnerSuppressed(false);
        for (BlockPos pos : this.multiblockState.getBlocksBySymbol(frameSymbol())) {
            setWallFormed(pos, false);
        }
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        TickRateModulation result = super.tickingRequest(node, ticksSinceLastCall);

        Level level = getLevel();
        if (level == null || level.isClientSide() || !this.multiblockState.isFormed()) {
            return result;
        }

        if (++this.extractCooldown < EXTRACT_INTERVAL_TICKS) {
            return result;
        }
        this.extractCooldown = 0;

        if (!InsaneConfig.COMMON.SPAWNER_EXTRACTOR_PEACEFUL.get() && level.getDifficulty() == Difficulty.PEACEFUL) {
            return result;
        }

        extractMob(level);
        return result;
    }

    private void extractMob(Level level) {
        EntityType<?> type = readSpawnerEntity(level);
        if (type == null) {
            return;
        }

        IGrid grid = getMainNode().getGrid();
        if (grid == null) {
            return;
        }

        long amount = this.upgrades.getInstalledUpgrades(AEItems.SPEED_CARD) + 1L;
        StorageHelper.poweredInsert(
                grid.getEnergyService(),
                grid.getStorageService().getInventory(),
                MobKey.of(type),
                amount,
                IActionSource.ofMachine(this),
                Actionable.MODULATE);
    }

    private @Nullable EntityType<?> readSpawnerEntity(Level level) {
        SpawnerBlockEntity spawner = getSpawner(level);
        if (spawner == null) {
            return null;
        }

        SpawnData spawnData = ((BaseSpawnerAccessor) spawner.getSpawner()).getNextSpawnData();
        if (spawnData == null) {
            return null;
        }

        return EntityType.by(spawnData.getEntityToSpawn()).orElse(null);
    }

    private void setSpawnerSuppressed(boolean suppressed) {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        SpawnerBlockEntity spawner = getSpawner(level);
        if (spawner == null) {
            return;
        }

        ((BaseSpawnerAccessor) spawner.getSpawner())
                .setSpawnDelay(suppressed ? Integer.MAX_VALUE : VANILLA_SPAWN_DELAY);
        spawner.setChanged();
    }

    private @Nullable SpawnerBlockEntity getSpawner(Level level) {
        for (BlockPos pos : this.multiblockState.getBlocksBySymbol('D')) {
            if (level.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner) {
                return spawner;
            }
        }
        return null;
    }

    private void setWallFormed(BlockPos pos, boolean formed) {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(SpawnerExtractorWallBlock.FORMED)
                && state.getValue(SpawnerExtractorWallBlock.FORMED) != formed) {
            level.setBlock(pos, state.setValue(SpawnerExtractorWallBlock.FORMED, formed), Block.UPDATE_CLIENTS);
        }
    }
}
