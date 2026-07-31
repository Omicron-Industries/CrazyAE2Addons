package net.oktawia.crazyae2addons.logic.display;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public class DisplayImageStore extends SavedData {

    private static final String NAME = "crazyae2_display_images";

    private final Map<String, byte[]> images = new HashMap<>();

    public static DisplayImageStore get(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            throw new IllegalStateException("DisplayImageStore is server-only");
        }
        ServerLevel overworld = serverLevel.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(
                DisplayImageStore::load,
                DisplayImageStore::new,
                NAME);
    }

    @Nullable
    public byte[] getImage(String id) {
        return images.get(id);
    }

    public void putImage(String id, byte[] bytes) {
        images.put(id, bytes);
        setDirty();
    }

    public void removeImage(String id) {
        if (images.remove(id) != null) {
            setDirty();
        }
    }

    @Nullable
    public String copyImage(String srcId) {
        byte[] bytes = images.get(srcId);
        if (bytes == null) {
            return null;
        }
        String newId = UUID.randomUUID().toString();
        images.put(newId, bytes.clone());
        setDirty();
        return newId;
    }

    public static DisplayImageStore load(CompoundTag tag) {
        DisplayImageStore store = new DisplayImageStore();
        for (String key : tag.getAllKeys()) {
            store.images.put(key, tag.getByteArray(key));
        }
        return store;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        for (Map.Entry<String, byte[]> entry : images.entrySet()) {
            tag.putByteArray(entry.getKey(), entry.getValue());
        }
        return tag;
    }
}
