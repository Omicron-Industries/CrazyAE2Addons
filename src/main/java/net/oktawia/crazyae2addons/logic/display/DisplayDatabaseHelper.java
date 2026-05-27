package net.oktawia.crazyae2addons.logic.display;

import appeng.api.networking.IGrid;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class DisplayDatabaseHelper {

    private DisplayDatabaseHelper() {
    }

    @Nullable
    public static String findValue(IGrid grid, String key) {
        if (grid == null || key == null || key.isBlank()) {
            return null;
        }

        for (DisplayDatabaseProvider provider : grid.getMachines(DisplayDatabaseProvider.class)) {
            if (provider == null) {
                continue;
            }

            String value = provider.getDisplayVariable(key);
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    public static Map<String, String> collectVariables(IGrid grid) {
        Map<String, String> result = new LinkedHashMap<>();

        if (grid == null) {
            return result;
        }

        for (DisplayDatabaseProvider provider : grid.getMachines(DisplayDatabaseProvider.class)) {
            if (provider == null) {
                continue;
            }

            result.putAll(provider.getDisplayVariables());
        }

        return result;
    }
}