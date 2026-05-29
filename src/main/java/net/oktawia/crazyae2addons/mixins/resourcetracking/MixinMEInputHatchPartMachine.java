package net.oktawia.crazyae2addons.mixins.resourcetracking;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import com.gregtechceu.gtceu.integration.ae2.machine.MEInputHatchPartMachine;
import net.minecraft.core.BlockPos;
import net.oktawia.crazyae2addons.tracking.IResourceTrackingService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MEInputHatchPartMachine.class, remap = false)
public class MixinMEInputHatchPartMachine {

    @Redirect(
        method = "syncME",
        at = @At(value = "INVOKE",
            target = "Lappeng/api/storage/MEStorage;extract(Lappeng/api/stacks/AEKey;JLappeng/api/config/Actionable;Lappeng/api/networking/security/IActionSource;)J")
    )
    private long crazyAe2$trackExtract(MEStorage storage, AEKey what, long amount, Actionable mode, IActionSource source) {
        long extracted = storage.extract(what, amount, mode, source);
        if (extracted > 0) {
            var self = (MEInputHatchPartMachine) (Object) this;
            var grid = self.getMainNode().getGrid();
            if (grid != null) {
                var svc = grid.getService(IResourceTrackingService.class);
                if (svc != null) {
                    BlockPos pos = self.getPos();
                    AEKey icon = AEItemKey.of(self.getDefinition().asStack());
                    svc.trackConsumption(what, extracted, "at " + pos.getX() + " " + pos.getY() + " " + pos.getZ(), icon, pos);
                }
            }
        }
        return extracted;
    }
}
