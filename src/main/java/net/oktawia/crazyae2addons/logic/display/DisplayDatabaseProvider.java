package net.oktawia.crazyae2addons.logic.display;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import appeng.api.networking.IGridNodeService;

public interface DisplayDatabaseProvider extends IGridNodeService {

    Map<String, String> getDisplayVariables();

    String getDisplayDatabaseJson();

    long getDisplayDatabaseRevision();

    void acceptDisplayDatabaseSnapshot(String variablesJson, long revision);

    @Nullable
    default String getDisplayVariable(String key) {
        if (key == null) {
            return null;
        }

        return getDisplayVariables().get(key);
    }
}
