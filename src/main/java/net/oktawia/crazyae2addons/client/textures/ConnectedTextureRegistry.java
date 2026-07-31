package net.oktawia.crazyae2addons.client.textures;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;

public final class ConnectedTextureRegistry {
    private static final Map<Supplier<? extends Block>, ConnectedTextureEntry> ENTRIES = new ConcurrentHashMap<>();

    private ConnectedTextureRegistry() {
    }

    public static void register(Supplier<? extends Block> block, ConnectedTextureEntry entry) {
        ENTRIES.put(block, entry);
    }

    public static Map<Supplier<? extends Block>, ConnectedTextureEntry> all() {
        return Map.copyOf(ENTRIES);
    }
}
