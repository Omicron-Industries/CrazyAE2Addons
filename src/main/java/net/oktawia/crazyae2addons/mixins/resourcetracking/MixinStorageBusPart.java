package net.oktawia.crazyae2addons.mixins.resourcetracking;

import appeng.api.networking.security.IActionHost;
import appeng.api.parts.IPartHost;
import appeng.api.storage.IStorageMounts;
import appeng.api.storage.MEStorage;
import appeng.helpers.InterfaceLogicHost;
import appeng.parts.storagebus.StorageBusPart;
import net.minecraft.core.BlockPos;
import net.oktawia.crazyae2addons.tracking.TrackingMEStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = StorageBusPart.class, remap = false)
public class MixinStorageBusPart {

    @Redirect(
        method = "mountInventories",
        at = @At(value = "INVOKE",
            target = "Lappeng/api/storage/IStorageMounts;mount(Lappeng/api/storage/MEStorage;I)V")
    )
    private void wrapIfInterface(IStorageMounts mounts, MEStorage handler, int priority) {
        var self = (StorageBusPart) (Object) this;

        BlockPos targetPos = self.getBlockEntity().getBlockPos().relative(self.getSide());
        var targetBe = self.getLevel().getBlockEntity(targetPos);

        Object host = targetBe;
        if (targetBe instanceof IPartHost partHost) {
            host = partHost.getPart(self.getSide().getOpposite());
        }

        if (host instanceof InterfaceLogicHost interfaceHost && host instanceof IActionHost actionHost) {
            var node = actionHost.getActionableNode();
            if (node != null) {
                var mainGrid = node.getGrid();
                if (mainGrid != null) {
                    mounts.mount(new TrackingMEStorage(handler, mainGrid, interfaceHost, targetPos), priority);
                    return;
                }
            }
        }

        mounts.mount(handler, priority);
    }
}
