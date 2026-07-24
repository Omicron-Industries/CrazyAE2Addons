package net.oktawia.crazyae2addons.client.misc;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.oktawia.crazyae2addons.network.NetworkHandler;
import net.oktawia.crazyae2addons.network.packets.RequestDisplayImagePacket;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@OnlyIn(Dist.CLIENT)
public final class DisplayImageClientCache {

    private static final Map<String, byte[]> CACHE = new ConcurrentHashMap<>();
    private static final Set<String> REQUESTED = ConcurrentHashMap.newKeySet();

    private DisplayImageClientCache() {}

    @Nullable
    public static byte[] get(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        byte[] bytes = CACHE.get(id);
        if (bytes == null) {
            requestOnce(id);
        }
        return bytes;
    }

    public static void accept(String id, byte[] bytes) {
        if (id == null || id.isEmpty()) {
            return;
        }
        REQUESTED.remove(id);
        if (bytes != null && bytes.length > 0) {
            CACHE.put(id, bytes);
        }
    }

    public static void invalidate(String id) {
        CACHE.remove(id);
        REQUESTED.remove(id);
    }

    private static void requestOnce(String id) {
        if (REQUESTED.add(id)) {
            NetworkHandler.sendToServer(new RequestDisplayImagePacket(id));
        }
    }
}
