package net.oktawia.crazyae2addons.ldlib.accessors;

import appeng.api.stacks.GenericStack;
import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.IAccessor;
import com.lowdragmc.lowdraglib.syncdata.managed.IManagedVar;
import com.lowdragmc.lowdraglib.syncdata.managed.IRef;
import com.lowdragmc.lowdraglib.syncdata.managed.ManagedRef;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.PrimitiveTypedPayload;
import net.minecraft.nbt.CompoundTag;

public final class GenericStackAccessor implements IAccessor {
    private byte defaultType = -1;

    @Override
    public boolean hasPredicate() {
        return true;
    }

    @Override
    public boolean test(Class<?> type) {
        return GenericStack.class.isAssignableFrom(type);
    }

    @Override
    public boolean isManaged() {
        return true;
    }

    @Override
    public void setDefaultType(byte payloadType) {
        this.defaultType = payloadType;
    }

    @Override
    public byte getDefaultType() {
        return this.defaultType;
    }

    @Override
    public ITypedPayload<?> readField(AccessorOp op, IRef field) {
        GenericStack value = field.readRaw();
        if (value == null) {
            return PrimitiveTypedPayload.ofNull();
        }
        return NbtTagPayload.of(GenericStack.writeTag(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeField(AccessorOp op, IRef field, ITypedPayload<?> payload) {
        if (!(field instanceof ManagedRef managedRef)) {
            return;
        }

        CompoundTag tag = payload.serializeNBT() instanceof CompoundTag c ? c : new CompoundTag();
        IManagedVar<GenericStack> managedVar = managedRef.getField();
        managedVar.set(GenericStack.readTag(tag));
    }
}
