package net.oktawia.crazyae2addons.mixins.cpupriority.ae2cl;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.menu.me.crafting.CraftConfirmMenu;
import net.oktawia.crazyae2addons.CrazyConfig;
import net.oktawia.crazyae2addons.logic.cpupriority.CpuPriorityHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = CraftConfirmMenu.class, remap = false)
public abstract class MixinCraftConfirmMenuAutoCpuPriority {

    @Shadow
    private ICraftingPlan result;

    @Shadow
    private IGrid getGrid() {
        throw new AssertionError();
    }

    @ModifyArg(
            method = "startJob(Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/api/networking/crafting/ICraftingService;submitJob(Lappeng/api/networking/crafting/ICraftingPlan;Lappeng/api/networking/crafting/ICraftingRequester;Lappeng/api/networking/crafting/ICraftingCPU;ZLappeng/api/networking/security/IActionSource;Z)Lappeng/api/networking/crafting/ICraftingSubmitResult;"
            ),
            index = 2
    )
    private ICraftingCPU crazyae2addons$pickHighestPriorityCpuForAuto(ICraftingCPU selectedCpu) {
        if (selectedCpu != null || !CrazyConfig.COMMON.CPU_PRIORITIES_ENABLED.get()) {
            return selectedCpu;
        }

        var grid = getGrid();
        if (grid == null || this.result == null) {
            return null;
        }

        long requiredBytes = this.result.bytes();

        return grid.getCraftingService().getCpus().stream()
                .filter(cpu -> !cpu.isBusy())
                .filter(cpu -> cpu.getAvailableStorage() >= requiredBytes)
                .min(CpuPriorityHelper.cpuComparator())
                .orElse(null);
    }
}
