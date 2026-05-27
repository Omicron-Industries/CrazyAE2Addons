package net.oktawia.crazyae2addons.logic.display;

import appeng.api.networking.IGridNodeService;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

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