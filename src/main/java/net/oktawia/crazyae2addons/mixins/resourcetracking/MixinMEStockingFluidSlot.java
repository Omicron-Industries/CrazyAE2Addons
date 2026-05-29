package net.oktawia.crazyae2addons.mixins.resourcetracking;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import com.gregtechceu.gtceu.integration.ae2.machine.MEStockingHatchPartMachine;
import net.minecraft.core.BlockPos;
import net.oktawia.crazyae2addons.tracking.IResourceTrackingService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.gregtechceu.gtceu.integration.ae2.machine.MEStockingHatchPartMachine$ExportOnlyAEStockingFluidSlot", remap = false)
public class MixinMEStockingFluidSlot {

    @Redirect(
        method = "drain(ILnet/minecraftforge/fluids/capability/IFluidHandler$FluidAction;)Lnet/minecraftforge/fluids/FluidStack;",
        at = @At(value = "INVOKE",
            target = "Lappeng/api/storage/MEStorage;extract(Lappeng/api/stacks/AEKey;JLappeng/api/config/Actionable;Lappeng/api/networking/security/IActionSource;)J")
    )
    private long crazyAe2$trackExtract(MEStorage storage, AEKey what, long amount, Actionable mode, IActionSource source) {
        long extracted = storage.extract(what, amount, mode, source);
        if (extracted > 0 && mode == Actionable.MODULATE) {
            source.machine().ifPresent(host -> {
                if (!(host instanceof MEStockingHatchPartMachine machine)) return;
                var grid = machine.getMainNode().getGrid();
                if (grid == null) return;
                var svc = grid.getService(IResourceTrackingService.class);
                if (svc == null) return;
                BlockPos pos = machine.getPos();
                AEKey icon = AEItemKey.of(machine.getDefinition().asStack());
                svc.trackConsumption(what, extracted, "at " + pos.getX() + " " + pos.getY() + " " + pos.getZ(), icon, pos);
            });
        }
        return extracted;
    }
}
