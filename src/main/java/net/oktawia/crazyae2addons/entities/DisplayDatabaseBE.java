package net.oktawia.crazyae2addons.entities;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import lombok.Getter;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.blockentity.grid.AENetworkBlockEntity;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;

import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.CrazyConfig;
import net.oktawia.crazyae2addons.defs.regs.CrazyBlockEntityRegistrar;
import net.oktawia.crazyae2addons.defs.regs.CrazyBlockRegistrar;
import net.oktawia.crazyae2addons.defs.regs.CrazyMenuRegistrar;
import net.oktawia.crazyae2addons.logic.display.DisplayDatabaseProvider;
import net.oktawia.crazyae2addons.menus.block.DisplayDatabaseMenu;
import net.oktawia.crazyae2addons.util.IManagedBEHelper;
import net.oktawia.crazyae2addons.util.IMenuOpeningBlockEntity;

public class DisplayDatabaseBE extends AENetworkBlockEntity
        implements MenuProvider, IMenuOpeningBlockEntity, IManagedBEHelper, DisplayDatabaseProvider, IGridTickable {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(DisplayDatabaseBE.class);

    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() {
    }.getType();

    private static final double IDLE_POWER_USAGE = 1.0;

    private static final Map<IGrid, GridSnapshot> GRID_DATABASES = new WeakHashMap<>();

    private static final Set<DisplayDatabaseBE> LOADED_DATABASES = Collections.newSetFromMap(new WeakHashMap<>());

    @Getter
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Persisted
    @DescSynced
    private String variablesJson = "{}";

    @Persisted
    @DescSynced
    private long databaseRevision = 0L;

    private transient String cachedVariablesJson = null;
    private transient LinkedHashMap<String, String> cachedVariables = new LinkedHashMap<>();

    public DisplayDatabaseBE(BlockPos pos, BlockState state) {
        super(CrazyBlockEntityRegistrar.DISPLAY_DATABASE_BE.get(), pos, state);

        this.getMainNode()
                .addService(DisplayDatabaseProvider.class, this)
                .addService(IGridTickable.class, this)
                .setIdlePowerUsage(IDLE_POWER_USAGE)
                .setVisualRepresentation(new ItemStack(
                        CrazyBlockRegistrar.DISPLAY_DATABASE_BLOCK.get().asItem()));
    }

    public static boolean isFeatureEnabled() {
        return CrazyConfig.COMMON.DISPLAY_DATABASE_ENABLED.get() && CrazyConfig.COMMON.DISPLAY_ENABLED.get();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();

        if (!isClientSide()) {
            LOADED_DATABASES.add(this);
            synchronizeWithGrid();
        }
    }

    @Override
    public void setRemoved() {
        LOADED_DATABASES.remove(this);
        super.setRemoved();
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveManagedData(tag);
    }

    @Override
    public void loadTag(CompoundTag tag) {
        loadManagedData(tag);
        super.loadTag(tag);

        this.variablesJson = sanitizeJson(this.variablesJson);
        invalidateVariablesCache();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory invPlayer, Player player) {
        return new DisplayDatabaseMenu(id, invPlayer, this);
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        if (!isFeatureEnabled()) {
            return;
        }

        if (!player.level().isClientSide) {
            synchronizeWithGrid();
            forceSyncManaged();
        }

        MenuOpener.open(
                CrazyMenuRegistrar.DISPLAY_DATABASE_MENU.get(),
                player,
                locator);
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(20, 100, false, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (level == null || level.isClientSide) {
            return TickRateModulation.IDLE;
        }

        synchronizeWithGrid();
        return TickRateModulation.IDLE;
    }

    @Override
    public Map<String, String> getDisplayVariables() {
        if (!isFeatureEnabled()) {
            return Collections.emptyMap();
        }

        return Collections.unmodifiableMap(getVariablesMutable());
    }

    @Override
    public String getDisplayDatabaseJson() {
        if (!isFeatureEnabled()) {
            return "{}";
        }

        return getVariablesJson();
    }

    @Override
    public long getDisplayDatabaseRevision() {
        return databaseRevision;
    }

    @Override
    public void acceptDisplayDatabaseSnapshot(String incomingJson, long incomingRevision) {
        if (!isFeatureEnabled()) {
            return;
        }

        incomingJson = sanitizeJson(incomingJson);

        if (incomingRevision < this.databaseRevision) {
            return;
        }

        if (incomingRevision == this.databaseRevision
                && Objects.equals(incomingJson, this.variablesJson)) {
            return;
        }

        this.variablesJson = incomingJson;
        this.databaseRevision = incomingRevision;

        invalidateVariablesCache();
        markSnapshotChanged();
    }

    public void synchronizeWithGrid() {
        if (!isFeatureEnabled()) {
            return;
        }

        if (level == null || level.isClientSide) {
            return;
        }

        IGrid grid = getMainNode().getGrid();
        if (grid == null) {
            return;
        }

        List<GridSnapshot> snapshots = collectSnapshots(grid);
        GridSnapshot merged = mergeSnapshots(snapshots);

        GridSnapshot currentGridSnapshot = GRID_DATABASES.get(grid);

        GridSnapshot finalMerged = merged;
        boolean needsRevisionBump = currentGridSnapshot == null
                || !sameContent(currentGridSnapshot, merged)
                || snapshots.stream().anyMatch(snapshot -> !sameContent(snapshot, finalMerged));

        if (needsRevisionBump) {
            long maxRevision = snapshots.stream()
                    .mapToLong(GridSnapshot::revision)
                    .max()
                    .orElse(0L);

            long revision = Math.max(maxRevision + 1L, System.currentTimeMillis());
            merged = new GridSnapshot(merged.variablesJson(), revision);
        }

        GRID_DATABASES.put(grid, merged);
        broadcastSnapshotToGrid(grid, merged);
    }

    public static Map<String, String> getMergedVariablesForGrid(@Nullable IGrid grid) {
        if (!isFeatureEnabled()) {
            return Collections.emptyMap();
        }

        if (grid == null) {
            return Collections.emptyMap();
        }

        LinkedHashMap<String, String> liveValues = new LinkedHashMap<>();
        boolean hasLiveDatabase = false;

        try {
            for (DisplayDatabaseProvider provider : grid.getMachines(DisplayDatabaseProvider.class)) {
                if (provider == null) {
                    continue;
                }

                hasLiveDatabase = true;

                Map<String, String> vars = provider.getDisplayVariables();
                if (vars != null && !vars.isEmpty()) {
                    liveValues.putAll(vars);
                }
            }
        } catch (Throwable e) {
            CrazyAddons.LOGGER.debug("failed to read ME display database providers", e);
        }

        for (DisplayDatabaseBE database : new ArrayList<>(LOADED_DATABASES)) {
            if (database == null || database.isRemoved()) {
                LOADED_DATABASES.remove(database);
                continue;
            }

            Level level = database.getLevel();
            if (level == null || level.isClientSide) {
                continue;
            }

            IGrid otherGrid = database.getMainNode().getGrid();
            if (otherGrid != grid) {
                continue;
            }

            hasLiveDatabase = true;
            liveValues.putAll(parseVariables(database.getVariablesJson()));
        }

        if (!hasLiveDatabase) {
            return Collections.emptyMap();
        }

        LinkedHashMap<String, String> out = new LinkedHashMap<>();

        GridSnapshot gridSnapshot = GRID_DATABASES.get(grid);
        if (gridSnapshot != null) {
            out.putAll(parseVariables(gridSnapshot.variablesJson()));
        }

        out.putAll(liveValues);

        return out;
    }

    public String getVariablesJson() {
        this.variablesJson = sanitizeJson(this.variablesJson);
        return this.variablesJson;
    }

    public void putVariable(String key, String value) {
        if (!isFeatureEnabled()) {
            return;
        }

        if (key == null) {
            return;
        }

        key = key.trim();
        if (key.isEmpty()) {
            return;
        }

        if (value == null) {
            value = "";
        }

        if (isOnline()) {
            synchronizeWithGrid();
        }

        LinkedHashMap<String, String> variables = new LinkedHashMap<>(getVariablesMutable());
        variables.put(key, value);

        setVariablesFromMap(variables, true, isOnline());
    }

    public void removeVariable(String key) {
        if (!isFeatureEnabled()) {
            return;
        }

        if (key == null || key.isBlank()) {
            return;
        }

        if (isOnline()) {
            synchronizeWithGrid();
        }

        LinkedHashMap<String, String> variables = new LinkedHashMap<>(getVariablesMutable());

        if (variables.remove(key) != null) {
            setVariablesFromMap(variables, true, isOnline());
        }
    }

    public void clearVariables() {
        if (!isFeatureEnabled()) {
            return;
        }

        if (isOnline()) {
            synchronizeWithGrid();
        }

        if (getVariablesMutable().isEmpty()) {
            return;
        }

        setVariablesFromMap(new LinkedHashMap<>(), true, isOnline());
    }

    private boolean isOnline() {
        return !isClientSide() && getMainNode().getGrid() != null;
    }

    private List<GridSnapshot> collectSnapshots(IGrid grid) {
        List<GridSnapshot> snapshots = new ArrayList<>();

        GridSnapshot gridSnapshot = GRID_DATABASES.get(grid);
        if (gridSnapshot != null) {
            snapshots.add(gridSnapshot);
        }

        snapshots.add(localSnapshot());

        try {
            for (DisplayDatabaseProvider provider : grid.getMachines(DisplayDatabaseProvider.class)) {
                if (provider == null) {
                    continue;
                }

                snapshots.add(new GridSnapshot(
                        provider.getDisplayDatabaseJson(),
                        provider.getDisplayDatabaseRevision()));
            }
        } catch (Throwable e) {
            CrazyAddons.LOGGER.debug("failed to scan ME display database providers", e);
        }

        for (DisplayDatabaseBE database : new ArrayList<>(LOADED_DATABASES)) {
            if (database == null || database.isRemoved()) {
                LOADED_DATABASES.remove(database);
                continue;
            }

            Level level = database.getLevel();
            if (level == null || level.isClientSide) {
                continue;
            }

            IGrid otherGrid = database.getMainNode().getGrid();
            if (otherGrid != grid) {
                continue;
            }

            snapshots.add(database.localSnapshot());
        }

        return snapshots;
    }

    private GridSnapshot mergeSnapshots(List<GridSnapshot> snapshots) {
        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        LinkedHashMap<String, Long> valueRevisions = new LinkedHashMap<>();

        List<GridSnapshot> ordered = new ArrayList<>(snapshots);
        ordered.sort(Comparator
                .comparingLong(GridSnapshot::revision)
                .thenComparing(GridSnapshot::variablesJson));

        long maxRevision = 0L;

        for (GridSnapshot snapshot : ordered) {
            String json = sanitizeJson(snapshot.variablesJson());
            long snapshotRevision = snapshot.revision();

            maxRevision = Math.max(maxRevision, snapshotRevision);

            Map<String, String> parsed = parseVariables(json);

            for (Map.Entry<String, String> entry : parsed.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue() == null ? "" : entry.getValue();

                long currentRevision = valueRevisions.getOrDefault(key, Long.MIN_VALUE);
                String currentValue = values.getOrDefault(key, "");

                if (snapshotRevision > currentRevision
                        || (snapshotRevision == currentRevision && value.compareTo(currentValue) > 0)) {
                    values.put(key, value);
                    valueRevisions.put(key, snapshotRevision);
                }
            }
        }

        return new GridSnapshot(toVariablesJson(values), maxRevision);
    }

    private void setVariablesFromMap(Map<String, String> variables, boolean bumpRevision, boolean publish) {
        this.variablesJson = toVariablesJson(variables);

        if (bumpRevision) {
            this.databaseRevision = nextRevision();
        }

        invalidateVariablesCache();
        markSnapshotChanged();

        if (publish) {
            publishLocalSnapshotToGridReplacing();
        }
    }

    private long nextRevision() {
        long now = System.currentTimeMillis();
        return Math.max(this.databaseRevision + 1L, now);
    }

    private void publishLocalSnapshotToGridReplacing() {
        IGrid grid = getMainNode().getGrid();
        if (grid == null) {
            return;
        }

        GridSnapshot snapshot = localSnapshot();

        GRID_DATABASES.put(grid, snapshot);
        broadcastSnapshotToGrid(grid, snapshot);
    }

    private void broadcastSnapshotToGrid(IGrid grid, GridSnapshot snapshot) {
        snapshot = new GridSnapshot(
                sanitizeJson(snapshot.variablesJson()),
                snapshot.revision());

        try {
            for (DisplayDatabaseProvider provider : grid.getMachines(DisplayDatabaseProvider.class)) {
                if (provider == null) {
                    continue;
                }

                provider.acceptDisplayDatabaseSnapshot(
                        snapshot.variablesJson(),
                        snapshot.revision());
            }
        } catch (Throwable e) {
            CrazyAddons.LOGGER.debug("failed to broadcast ME display database via grid providers", e);
        }

        for (DisplayDatabaseBE database : new ArrayList<>(LOADED_DATABASES)) {
            if (database == null || database.isRemoved()) {
                LOADED_DATABASES.remove(database);
                continue;
            }

            Level level = database.getLevel();
            if (level == null || level.isClientSide) {
                continue;
            }

            IGrid otherGrid = database.getMainNode().getGrid();
            if (otherGrid != grid) {
                continue;
            }

            database.acceptDisplayDatabaseSnapshot(
                    snapshot.variablesJson(),
                    snapshot.revision());
        }
    }

    private GridSnapshot localSnapshot() {
        return new GridSnapshot(
                getVariablesJson(),
                databaseRevision);
    }

    private boolean sameContent(GridSnapshot a, GridSnapshot b) {
        if (a == null || b == null) {
            return false;
        }

        return Objects.equals(
                sanitizeJson(a.variablesJson()),
                sanitizeJson(b.variablesJson()));
    }

    private LinkedHashMap<String, String> getVariablesMutable() {
        String json = getVariablesJson();

        if (cachedVariablesJson != null && cachedVariablesJson.equals(json)) {
            return cachedVariables;
        }

        cachedVariables = new LinkedHashMap<>(parseVariables(json));
        cachedVariablesJson = json;
        return cachedVariables;
    }

    private static LinkedHashMap<String, String> parseVariables(String json) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();

        try {
            Map<String, String> parsed = GSON.fromJson(sanitizeJson(json), MAP_TYPE);
            if (parsed != null) {
                result.putAll(parsed);
            }
        } catch (Exception e) {
            CrazyAddons.LOGGER.debug("failed to parse ME display database JSON", e);
        }

        return result;
    }

    private static String toVariablesJson(Map<String, String> variables) {
        try {
            return GSON.toJson(variables != null ? variables : Map.of());
        } catch (Exception e) {
            CrazyAddons.LOGGER.debug("failed to serialize ME display database JSON", e);
            return "{}";
        }
    }

    private void markSnapshotChanged() {
        setChanged();
        markForUpdate();

        if (!isClientSide()) {
            markManagedDirty("variablesJson");
            markManagedDirty("databaseRevision");
            syncManaged();
        }
    }

    private void invalidateVariablesCache() {
        this.cachedVariablesJson = null;
        this.cachedVariables = new LinkedHashMap<>();
    }

    private static String sanitizeJson(String json) {
        if (json == null || json.isBlank() || json.equals("null")) {
            return "{}";
        }

        return json;
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    public boolean isClientSide() {
        return level == null || level.isClientSide;
    }

    private record GridSnapshot(String variablesJson, long revision) {
    }
}
