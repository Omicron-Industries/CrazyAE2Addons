package net.oktawia.insaneae2addons.mixins;

import appeng.parts.storagebus.StorageBusPart;
import appeng.util.prioritylist.IPartitionList;
import net.oktawia.insaneae2addons.logic.viewcell.NBTPriorityList;
import net.oktawia.insaneae2addons.parts.NbtStorageBusPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = StorageBusPart.class, remap = false)
public abstract class MixinStorageBusPart {

    @Inject(
            method = "createFilter()Lappeng/util/prioritylist/IPartitionList;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 1)
    private void insaneae2addons$nbtFilter(CallbackInfoReturnable<IPartitionList> cir) {
        if ((Object) this instanceof NbtStorageBusPart nbt) {
            cir.setReturnValue(new NBTPriorityList(nbt.getData()));
        }
    }
}
