package net.oktawia.crazyae2addons.mixins.resourcetracking;

import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageHelper;
import appeng.helpers.InterfaceLogic;
import appeng.helpers.InterfaceLogicHost;
import net.minecraft.core.GlobalPos;
import net.oktawia.crazyae2addons.tracking.IResourceTrackingService;
import net.oktawia.crazyae2addons.tracking.UsageTarget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = InterfaceLogic.class, remap = false)
public class MixinInterfaceLogic {

    @Shadow @Final
    protected IManagedGridNode mainNode;

    @Shadow @Final
    protected InterfaceLogicHost host;

    @Unique
    private String crazyAE2Addons$cachedDesc;

    @Unique
    private UsageTarget crazyAE2Addons$cachedTarget;

    @Redirect(
        method = "acquireFromNetwork",
        at = @At(value = "INVOKE",
            target = "Lappeng/api/storage/StorageHelper;poweredExtraction(Lappeng/api/networking/energy/IEnergySource;Lappeng/api/storage/MEStorage;Lappeng/api/stacks/AEKey;JLappeng/api/networking/security/IActionSource;)J")
    )
    private long crazyAE2Addons$trackActualExtraction(
            appeng.api.networking.energy.IEnergySource energy,
            MEStorage inv,
            AEKey what,
            long amount,
            IActionSource src) {
        long acquired = StorageHelper.poweredExtraction(energy, inv, what, amount, src);
        if (acquired > 0) {
            var grid = mainNode.getGrid();
            if (grid != null) {
                var svc = grid.getService(IResourceTrackingService.class);
                if (svc != null) {
                    if (crazyAE2Addons$cachedTarget == null) {
                        var be = host.getBlockEntity();
                        var pos = be.getBlockPos();
                        crazyAE2Addons$cachedDesc = "interface at " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
                        crazyAE2Addons$cachedTarget = UsageTarget.interfaceAt(GlobalPos.of(be.getLevel().dimension(), pos.immutable()));
                    }
                    svc.trackConsumption(what, acquired, crazyAE2Addons$cachedTarget, crazyAE2Addons$cachedDesc, null);
                }
            }
        }
        return acquired;
    }
}
