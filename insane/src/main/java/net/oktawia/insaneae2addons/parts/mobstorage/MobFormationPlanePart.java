package net.oktawia.insaneae2addons.parts.mobstorage;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Setting;
import appeng.api.config.Settings;
import appeng.api.config.IncludeExclude;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.security.IActionSource;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.AEKey;
import appeng.api.storage.IStorageMounts;
import appeng.api.storage.IStorageProvider;
import appeng.api.storage.MEStorage;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.core.definitions.AEItems;
import appeng.helpers.IConfigInvHost;
import appeng.helpers.IPriorityHost;
import appeng.items.parts.PartModels;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.automation.PlaneConnectionHelper;
import appeng.parts.automation.PlaneConnections;
import appeng.parts.automation.PlaneModelData;
import appeng.parts.automation.PlaneModels;
import appeng.parts.automation.UpgradeablePart;
import appeng.util.ConfigInventory;
import appeng.util.prioritylist.IPartitionList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.mobstorage.MobKey;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MobFormationPlanePart extends UpgradeablePart
        implements IStorageProvider, IPriorityHost, IConfigInvHost {

    private static final PlaneModels MODELS = new PlaneModels("part/formation_plane",
            "part/formation_plane_on");

    private static final long MAX_SPAWNS_PER_OPERATION = 64;

    @PartModels
    public static List<IPartModel> getModels() {
        return MODELS.getModels();
    }

    private final PlaneConnectionHelper connectionHelper = new PlaneConnectionHelper(this);
    private final MEStorage inventory = new InWorldStorage();
    private final ConfigInventory config;

    private boolean wasOnline = false;
    private int priority = 0;
    private IncludeExclude filterMode = IncludeExclude.WHITELIST;
    @Nullable
    private IPartitionList filter;

    public MobFormationPlanePart(IPartItem<?> partItem) {
        super(partItem);
        getMainNode().addService(IStorageProvider.class, this);
        this.config = ConfigInventory.configTypes(what -> what instanceof MobKey, 63, this::updateFilter);
        getConfigManager().registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        getConfigManager().registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
    }

    @Override
    public RedstoneMode getRSMode() {
        return getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED);
    }

    @Override
    protected void onSettingChanged(IConfigManager manager, Setting<?> setting) {
        getHost().markForSave();
    }

    @Override
    protected int getUpgradeSlots() {
        return 5;
    }

    @Override
    public void upgradesChanged() {
        updateFilter();
    }

    private void updateFilter() {
        var builder = IPartitionList.builder();
        int slotsToUse = 18 + getInstalledUpgrades(AEItems.CAPACITY_CARD) * 9;
        for (int x = 0; x < config.size() && x < slotsToUse; x++) {
            builder.add(config.getKey(x));
        }
        this.filter = builder.build();
        this.filterMode = isUpgradedWith(AEItems.INVERTER_CARD)
                ? IncludeExclude.BLACKLIST
                : IncludeExclude.WHITELIST;
    }

    private long spawn(MobKey mobKey, long amount, Actionable mode) {
        if (!(getLevel() instanceof ServerLevel level)) {
            return 0;
        }
        var pos = getBlockEntity().getBlockPos().relative(getSide());
        if (!MobExportBusPart.canSpawn(level, pos)) {
            return 0;
        }

        long requested = Math.min(amount, MAX_SPAWNS_PER_OPERATION);
        if (mode == Actionable.SIMULATE) {
            return requested;
        }

        long spawned = 0;
        for (long i = 0; i < requested; i++) {
            if (mobKey.getEntityType().spawn(level, pos, MobSpawnType.COMMAND) != null) {
                spawned++;
            }
        }
        if (spawned > 0) {
            level.sendParticles(ParticleTypes.FIREWORK, pos.getX(), pos.getY() + 1, pos.getZ(),
                    20, 0.5, 0.5, 0.5, 0.01);
        }
        return spawned;
    }

    private void remountStorage() {
        IStorageProvider.requestUpdate(getMainNode());
    }

    @Override
    protected void onMainNodeStateChanged(IGridNodeListener.State reason) {
        var currentOnline = getMainNode().isOnline();
        if (wasOnline != currentOnline) {
            wasOnline = currentOnline;
            remountStorage();
            getHost().markForUpdate();
        }
    }

    @Override
    public void mountInventories(IStorageMounts mounts) {
        if (getMainNode().isOnline()) {
            updateFilter();
            mounts.mount(inventory, priority);
        }
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int newValue) {
        this.priority = newValue;
        getHost().markForSave();
        remountStorage();
    }

    @Override
    public ConfigInventory getConfig() {
        return config;
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.priority = data.getInt("priority");
        config.readFromChildTag(data, "config");
        remountStorage();
    }

    @Override
    public void writeToNBT(CompoundTag data) {
        super.writeToNBT(data);
        data.putInt("priority", priority);
        config.writeToChildTag(data, "config");
    }

    @Override
    public boolean onPartActivate(Player player, InteractionHand hand, Vec3 pos) {
        if (!isClientSide()) {
            MenuOpener.open(getMenuType(), player, MenuLocators.forPart(this));
        }
        return true;
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(getMenuType(), player, MenuLocators.forPart(this));
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(getPartItem());
    }

    private MenuType<?> getMenuType() {
        return InsaneMenuRegistrar.MOB_FORMATION_PLANE_MENU.get();
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        connectionHelper.getBoxes(bch);
    }

    public PlaneConnections getConnections() {
        return connectionHelper.getConnections();
    }

    @Override
    public void onNeighborChanged(BlockGetter level, BlockPos pos, BlockPos neighbor) {
        if (!pos.relative(getSide()).equals(neighbor)) {
            connectionHelper.updateConnections();
        }
    }

    @Override
    public void onUpdateShape(Direction side) {
        if (getSide().getAxis() != side.getAxis()) {
            connectionHelper.updateConnections();
        }
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 1;
    }

    @Override
    public IPartModel getStaticModels() {
        return MODELS.getModel(isPowered(), isActive());
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(PlaneModelData.CONNECTIONS, getConnections())
                .build();
    }

    class InWorldStorage implements MEStorage {
        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
            if (!(what instanceof MobKey mobKey) || isSleeping()) {
                return 0;
            }
            if (filter != null && !filter.matchesFilter(what, filterMode)) {
                return 0;
            }
            return spawn(mobKey, amount, mode);
        }

        @Override
        public Component getDescription() {
            return getPartItem().asItem().getDescription();
        }
    }
}
