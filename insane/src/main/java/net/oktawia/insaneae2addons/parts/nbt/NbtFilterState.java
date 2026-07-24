package net.oktawia.insaneae2addons.parts.nbt;

import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.IManaged;
import com.lowdragmc.lowdraglib.syncdata.IManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.accessor.IManagedAccessor;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;

public final class NbtFilterState implements IManaged {
    private static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(NbtFilterState.class);

    private final Runnable onChange;
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Getter
    @Persisted
    @DescSynced
    private String data = "";

    public NbtFilterState(Runnable onChange) {
        this.onChange = onChange;
    }

    public void setData(String data) {
        this.data = data == null ? "" : data;
        markFieldDirty("data");
    }

    public CompoundTag savePersisted() {
        return IManagedAccessor.readManagedFields(this, new CompoundTag());
    }

    public void loadPersisted(CompoundTag tag) {
        IManagedAccessor.writePersistedFields(tag, getSyncStorage().getPersistedFields());
    }

    public CompoundTag saveSync(boolean force) {
        return IManagedAccessor.readSyncedFields(this, new CompoundTag(), force);
    }

    public void loadSync(CompoundTag tag) {
        new IManagedAccessor().writeToReadonlyField(AccessorOp.SYNCED, this, NbtTagPayload.of(tag));
    }

    private void markFieldDirty(String name) {
        getSyncStorage().getFieldByKey(getFieldHolder().getSyncedFieldIndex(name)).markAsDirty();
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public IManagedStorage getSyncStorage() {
        return syncStorage;
    }

    @Override
    public void onChanged() {
        onChange.run();
    }
}
