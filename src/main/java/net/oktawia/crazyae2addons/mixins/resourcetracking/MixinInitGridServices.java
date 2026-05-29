package net.oktawia.crazyae2addons.mixins.resourcetracking;

import appeng.api.networking.GridServices;
import appeng.init.internal.InitGridServices;
import net.oktawia.crazyae2addons.tracking.IResourceTrackingService;
import net.oktawia.crazyae2addons.tracking.ResourceTrackingService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InitGridServices.class, remap = false)
public class MixinInitGridServices {

    @Inject(method = "init", at = @At("TAIL"))
    private static void registerResourceTrackingService(CallbackInfo ci) {
        GridServices.register(IResourceTrackingService.class, ResourceTrackingService.class);
    }
}
