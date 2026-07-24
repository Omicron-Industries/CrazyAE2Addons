package net.oktawia.insaneae2addons.entities.mobstorage;

import appeng.api.config.Actionable;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageHelper;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.definitions.AEItems;
import appeng.util.ConfigInventory;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockControllerBE;
import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.blocks.mobstorage.MobFarmControllerBlock;
import net.oktawia.insaneae2addons.blocks.mobstorage.MobFarmWallBlock;
import net.oktawia.insaneae2addons.defs.InsaneMultiblocks;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.items.XpShardItem;
import net.oktawia.insaneae2addons.logic.mobstorage.MobLootSimulator;
import net.oktawia.insaneae2addons.menus.block.MobFarmControllerMenu;
import net.oktawia.insaneae2addons.mobstorage.MobKey;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MobFarmControllerBE extends AbstractMultiblockControllerBE
        implements IUpgradeableObject, InternalInventoryHost {

    private static final int KILL_INTERVAL_TICKS = 20;
    private static final int MOB_SLOTS = 3;
    private static final String UPGRADES_TAG = "upgrades";
    private static final String CONFIG_TAG = "config";
    private static final String TOOL_TAG = "tool";

    @Getter
    private final IUpgradeInventory upgrades = UpgradeInventories.forMachine(
            InsaneBlockRegistrar.MOB_FARM_CONTROLLER_BLOCK.get(), 5, this::saveChanges);

    @Getter
    private final ConfigInventory mobConfig = ConfigInventory.configTypes(
            key -> key instanceof MobKey,
            MOB_SLOTS,
            this::saveChanges);

    @Getter
    private final AppEngInternalInventory toolInventory = new AppEngInternalInventory(this, 1, 1);

    private int killCooldown;

    public MobFarmControllerBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.MOB_FARM_CONTROLLER_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.MOB_FARM_CONTROLLER_BLOCK.get()),
                2.0F
        );
    }

    @Override
    protected MultiblockDefinition getMultiblockDefinition() {
        return InsaneMultiblocks.mobFarm();
    }

    @Override
    protected char frameSymbol() {
        return 'A';
    }

    @Override
    protected MenuType<?> getMenuType() {
        return InsaneMenuRegistrar.MOB_FARM_CONTROLLER_MENU.get();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new MobFarmControllerMenu(id, inventory, this);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        this.upgrades.writeToNBT(tag, UPGRADES_TAG);
        this.mobConfig.writeToChildTag(tag, CONFIG_TAG);
        this.toolInventory.writeToNBT(tag, TOOL_TAG);
    }

    @Override
    public void loadTag(CompoundTag tag) {
        super.loadTag(tag);
        this.upgrades.readFromNBT(tag, UPGRADES_TAG);
        this.mobConfig.readFromChildTag(tag, CONFIG_TAG);
        this.toolInventory.readFromNBT(tag, TOOL_TAG);
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        drops.add(this.toolInventory.getStackInSlot(0));
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
    public void onChangeInventory(InternalInventory inv, int slot) {
        saveChanges();
    }

    public int getKillsPerCycle() {
        int base = InsaneConfig.COMMON.MOB_FARM_BASE_SPEED.get();
        int perCard = InsaneConfig.COMMON.MOB_FARM_SPEED_PER_CARD.get();
        return base + this.upgrades.getInstalledUpgrades(AEItems.SPEED_CARD) * perCard;
    }

    @Override
    protected void setOwnFormedState(boolean formed) {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = getBlockState();
        if (state.hasProperty(MobFarmControllerBlock.FORMED)
                && state.getValue(MobFarmControllerBlock.FORMED) != formed) {
            level.setBlock(getBlockPos(), state.setValue(MobFarmControllerBlock.FORMED, formed), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void setMemberFormedState(BlockPos pos, boolean formed) {
        setWallFormed(pos, formed);
    }

    public void unformMembersForRemoval() {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        for (BlockPos pos : this.multiblockState.getBlocksBySymbol(frameSymbol())) {
            setWallFormed(pos, false);
        }
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        TickRateModulation result = super.tickingRequest(node, ticksSinceLastCall);

        Level level = getLevel();
        if (!(level instanceof ServerLevel serverLevel) || !this.multiblockState.isFormed()) {
            return result;
        }

        if (++this.killCooldown < KILL_INTERVAL_TICKS) {
            return result;
        }
        this.killCooldown = 0;

        runKillCycle(serverLevel);
        return result;
    }

    private void runKillCycle(ServerLevel level) {
        IGrid grid = getMainNode().getGrid();
        if (grid == null) {
            return;
        }

        MEStorage storage = grid.getStorageService().getInventory();
        IEnergyService energy = grid.getEnergyService();
        IActionSource source = IActionSource.ofMachine(this);
        MobLootSimulator simulator = new MobLootSimulator(level);

        ItemStack weapon = this.toolInventory.getStackInSlot(0);
        int looting = this.upgrades.getInstalledUpgrades(InsaneItemRegistrar.LOOTING_UPGRADE_CARD.get());
        int experienceCards = this.upgrades.getInstalledUpgrades(InsaneItemRegistrar.EXPERIENCE_UPGRADE_CARD.get());

        for (int kill = 0; kill < getKillsPerCycle(); kill++) {
            if (!(this.mobConfig.getKey(kill % MOB_SLOTS) instanceof MobKey mobKey)) {
                continue;
            }

            if (StorageHelper.poweredExtraction(energy, storage, mobKey, 1, source, Actionable.MODULATE) <= 0) {
                continue;
            }

            EntityType<?> type = mobKey.getEntityType();
            MobLootSimulator.Result loot = simulator.simulate(type, weapon, looting);

            for (ItemStack drop : loot.drops()) {
                StorageHelper.poweredInsert(energy, storage, AEItemKey.of(drop.getItem()), drop.getCount(),
                        source, Actionable.MODULATE);
            }

            long shards = Math.max(1L,
                    (long) loot.experience() * (experienceCards + 1) / XpShardItem.XP_VAL);
            StorageHelper.poweredInsert(energy, storage, AEItemKey.of(InsaneItemRegistrar.XP_SHARD.get()), shards,
                    source, Actionable.MODULATE);
        }
    }

    private void setWallFormed(BlockPos pos, boolean formed) {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(MobFarmWallBlock.FORMED) && state.getValue(MobFarmWallBlock.FORMED) != formed) {
            level.setBlock(pos, state.setValue(MobFarmWallBlock.FORMED, formed), Block.UPDATE_CLIENTS);
        }
    }
}
