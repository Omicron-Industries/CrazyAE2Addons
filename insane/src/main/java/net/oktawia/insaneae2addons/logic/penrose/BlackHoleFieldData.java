package net.oktawia.insaneae2addons.logic.penrose;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public final class BlackHoleFieldData extends SavedData {

    private static final String NAME = "insaneae2addons_black_hole_fields";
    private static final String FIELDS = "fields";

    private final Map<UUID, BlackHoleField.Snapshot> snapshots = new HashMap<>();

    public static BlackHoleFieldData of(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(BlackHoleFieldData::load, BlackHoleFieldData::new, NAME);
    }

    public Collection<BlackHoleField.Snapshot> all() {
        return this.snapshots.values();
    }

    public void put(BlackHoleField.Snapshot snapshot) {
        this.snapshots.put(snapshot.id(), snapshot);
        setDirty();
    }

    public void remove(UUID id) {
        if (this.snapshots.remove(id) != null) {
            setDirty();
        }
    }

    private static BlackHoleFieldData load(CompoundTag tag) {
        BlackHoleFieldData data = new BlackHoleFieldData();

        ListTag list = tag.getList(FIELDS, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            UUID id = entry.getUUID("id");
            data.snapshots.put(id, new BlackHoleField.Snapshot(
                    id,
                    NbtUtils.readBlockPos(entry.getCompound("center")),
                    entry.getInt("radius"),
                    entry.getLongArray("processed")));
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();

        for (BlackHoleField.Snapshot snapshot : this.snapshots.values()) {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("id", snapshot.id());
            entry.put("center", NbtUtils.writeBlockPos(snapshot.center()));
            entry.putInt("radius", snapshot.radius());
            entry.putLongArray("processed", snapshot.processedChunks());
            list.add(entry);
        }

        tag.put(FIELDS, list);
        return tag;
    }
}
