package net.oktawia.insaneae2addons.mixins;

import appeng.api.stacks.AEKey;
import appeng.parts.storagebus.StorageBusPart;
import appeng.util.prioritylist.IPartitionList;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.logic.nbt.NBTPriorityList;
import net.oktawia.insaneae2addons.parts.nbt.NbtStorageBusPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = StorageBusPart.class, remap = false)
public abstract class MixinStorageBusPart {

    private static final IPartitionList BLOCK_ALL = new IPartitionList() {
        @Override
        public boolean isListed(AEKey input) {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Iterable<AEKey> getItems() {
            return List.of();
        }
    };

    @Inject(
            method = "createFilter()Lappeng/util/prioritylist/IPartitionList;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 1)
    private void insaneae2addons$nbtFilter(CallbackInfoReturnable<IPartitionList> cir) {
        if ((Object) this instanceof NbtStorageBusPart nbt) {
            if (!InsaneConfig.COMMON.NBT_STORAGE_BUS_ENABLED.get()) {
                cir.setReturnValue(BLOCK_ALL);
                return;
            }
            cir.setReturnValue(new NBTPriorityList(nbt.getData()));
        }
    }
}
