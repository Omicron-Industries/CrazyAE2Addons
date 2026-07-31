package net.oktawia.crazyae2addons.menus.block;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.minecraft.world.entity.player.Inventory;

import lombok.Getter;

import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;

import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.CrazyConfig;
import net.oktawia.crazyae2addons.defs.regs.CrazyMenuRegistrar;
import net.oktawia.crazyae2addons.entities.DisplayDatabaseBE;

public class DisplayDatabaseMenu extends AEBaseMenu {

    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() {
    }.getType();
    private static final Type STRING_LIST_TYPE = new TypeToken<List<String>>() {
    }.getType();

    public static final String ACTION_PUT = "putDisplayDatabaseEntry";
    public static final String ACTION_REMOVE = "removeDisplayDatabaseEntry";
    public static final String ACTION_CLEAR = "clearDisplayDatabaseEntries";

    @GuiSync(150)
    public String entriesJson = "{}";

    @Getter
    private final DisplayDatabaseBE host;

    private transient String lastParsedJson = null;
    private transient Map<String, String> parsedCache = new LinkedHashMap<>();

    public DisplayDatabaseMenu(int id, Inventory playerInventory, DisplayDatabaseBE host) {
        super(CrazyMenuRegistrar.DISPLAY_DATABASE_MENU.get(), id, playerInventory, host);
        this.host = host;

        syncEntriesFromHost();

        registerClientAction(ACTION_PUT, String.class, this::putEntryFromPayload);
        registerClientAction(ACTION_REMOVE, String.class, this::removeEntry);
        registerClientAction(ACTION_CLEAR, this::clearEntries);

        createPlayerInventorySlots(playerInventory);
    }

    @Override
    public void broadcastChanges() {
        if (!isClientSide()) {
            syncEntriesFromHost();
        }

        super.broadcastChanges();
    }

    private void syncEntriesFromHost() {
        if (!CrazyConfig.COMMON.DISPLAY_DATABASE_ENABLED.get() && CrazyConfig.COMMON.DISPLAY_ENABLED.get()) {
            this.entriesJson = "{}";
            this.lastParsedJson = null;
            return;
        }

        if (!isClientSide()) {
            host.synchronizeWithGrid();
        }

        this.entriesJson = host.getVariablesJson();
        this.lastParsedJson = null;
    }

    public Map<String, String> getEntries() {
        if (lastParsedJson == null || !lastParsedJson.equals(entriesJson)) {
            try {
                Map<String, String> parsed = GSON.fromJson(entriesJson, MAP_TYPE);
                parsedCache = parsed != null ? new LinkedHashMap<>(parsed) : new LinkedHashMap<>();
            } catch (Exception e) {
                CrazyAddons.LOGGER.debug("failed to parse display database entries", e);
                parsedCache = new LinkedHashMap<>();
            }

            lastParsedJson = entriesJson;
        }

        return parsedCache;
    }

    public void putEntry(String key, String value) {
        if (!CrazyConfig.COMMON.DISPLAY_DATABASE_ENABLED.get() && CrazyConfig.COMMON.DISPLAY_ENABLED.get()) {
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

        applyPutEntry(key, value);

        if (isClientSide()) {
            sendClientAction(ACTION_PUT, GSON.toJson(List.of(key, value)));
        }
    }

    private void putEntryFromPayload(String payload) {
        if (payload == null || payload.isEmpty()) {
            return;
        }

        try {
            List<String> parts = GSON.fromJson(payload, STRING_LIST_TYPE);
            if (parts == null || parts.size() < 2) {
                return;
            }

            applyPutEntry(parts.get(0), parts.get(1));
        } catch (Exception e) {
            CrazyAddons.LOGGER.debug("failed to decode display database put action", e);
        }
    }

    private void applyPutEntry(String key, String value) {
        host.putVariable(key, value);
        syncEntriesFromHost();
    }

    public void removeEntry(String key) {
        if (!CrazyConfig.COMMON.DISPLAY_DATABASE_ENABLED.get() && CrazyConfig.COMMON.DISPLAY_ENABLED.get()) {
            return;
        }

        if (key == null || key.isBlank()) {
            return;
        }

        host.removeVariable(key);
        syncEntriesFromHost();

        if (isClientSide()) {
            sendClientAction(ACTION_REMOVE, key);
        }
    }

    public void clearEntries() {
        if (!CrazyConfig.COMMON.DISPLAY_DATABASE_ENABLED.get() && CrazyConfig.COMMON.DISPLAY_ENABLED.get()) {
            return;
        }

        host.clearVariables();
        syncEntriesFromHost();

        if (isClientSide()) {
            sendClientAction(ACTION_CLEAR);
        }
    }
}
