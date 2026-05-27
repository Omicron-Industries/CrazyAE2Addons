package net.oktawia.crazyae2addons.compat.CC;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import net.minecraft.resources.ResourceLocation;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.CrazyConfig;
import net.oktawia.crazyae2addons.entities.DisplayDatabaseBE;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class DisplayDatabasePeripheral implements GenericPeripheral {

    private static final Pattern VALID_KEY = Pattern.compile("^[A-Za-z0-9_]+$");

    @Override
    public String id() {
        return new ResourceLocation(CrazyAddons.MODID, "me_display_database").toString();
    }

    @LuaFunction(mainThread = true)
    public final boolean add(DisplayDatabaseBE database, String key, String value) throws LuaException {
        validateDatabase(database);

        key = normalizeKey(key);

        if (value == null) {
            value = "";
        }

        database.putVariable(key, value);
        return true;
    }

    @LuaFunction(mainThread = true)
    public final boolean remove(DisplayDatabaseBE database, String key) throws LuaException {
        validateDatabase(database);

        key = normalizeKey(key);

        database.synchronizeWithGrid();

        boolean existed = database.getDisplayVariables().containsKey(key);
        database.removeVariable(key);

        return existed;
    }

    @LuaFunction(mainThread = true)
    public final Map<String, String> list(DisplayDatabaseBE database) throws LuaException {
        validateDatabase(database);

        database.synchronizeWithGrid();

        return new LinkedHashMap<>(database.getDisplayVariables());
    }

    private static void validateDatabase(DisplayDatabaseBE database) throws LuaException {
        if (!CrazyConfig.COMMON.DISPLAY_DATABASE_ENABLED.get()) {
            throw new LuaException("ME Display Database is disabled in config");
        }

        if (database == null || database.isRemoved() || database.getLevel() == null) {
            throw new LuaException("ME Display Database is not available");
        }
    }

    private static String normalizeKey(String key) throws LuaException {
        if (key == null) {
            throw new LuaException("key cannot be nil");
        }

        key = key.trim();

        if (key.isEmpty()) {
            throw new LuaException("key cannot be empty");
        }

        if (!VALID_KEY.matcher(key).matches()) {
            throw new LuaException("key may only contain letters, numbers and underscores");
        }

        return key;
    }
}