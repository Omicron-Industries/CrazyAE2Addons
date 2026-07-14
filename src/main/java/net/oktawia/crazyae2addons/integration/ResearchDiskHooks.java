package net.oktawia.crazyae2addons.integration;

import org.jetbrains.annotations.Nullable;

public final class ResearchDiskHooks {

    @Nullable
    private static ResearchDiskHook instance;

    private ResearchDiskHooks() {
    }

    public static void register(ResearchDiskHook hook) {
        instance = hook;
    }

    @Nullable
    public static ResearchDiskHook get() {
        return instance;
    }

    public static boolean isPresent() {
        return instance != null;
    }
}
