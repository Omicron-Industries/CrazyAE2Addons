package net.oktawia.crazyae2addons.mixins.cpupriority.ae2cln;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import appeng.client.Point;
import appeng.client.gui.widgets.CPUSelectionList;

import net.oktawia.crazyae2addons.CrazyConfig;
import net.oktawia.crazyae2addons.logic.cpupriority.CpuPriorityHelper;
import net.oktawia.crazyae2addons.logic.interfaces.ICpuPrio;

@Mixin(value = CPUSelectionList.class, remap = false)
public abstract class MixinCPUSelectionList {

    @Unique
    private Object crazyae2addons$lastHitCpu;

    @Redirect(method = "drawBackgroundLayer", at = @At(value = "INVOKE", target = "Ljava/util/List;subList(II)Ljava/util/List;"))
    private List<Object> crazyae2addons$sortThenSlice(
            List<Object> list,
            int from,
            int to) {
        if (!CrazyConfig.COMMON.CPU_PRIORITIES_ENABLED.get()) {
            return list.subList(from, to);
        }

        var sorted = CpuPriorityHelper.sortEntries(list);

        int safeFrom = Mth.clamp(from, 0, sorted.size());
        int safeTo = Mth.clamp(to, 0, sorted.size());

        return sorted.subList(safeFrom, safeTo);
    }

    @Redirect(method = "hitTestCpu", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    private Object crazyae2addons$hitTestOnSorted(List<Object> list, int index) {
        if (!CrazyConfig.COMMON.CPU_PRIORITIES_ENABLED.get()) {
            return list.get(index);
        }

        return CpuPriorityHelper.sortEntries(list).get(index);
    }

    @Inject(method = "hitTestCpu", at = @At("RETURN"))
    private void crazyae2addons$captureCpu(Point mousePos, CallbackInfoReturnable<Object> cir) {
        this.crazyae2addons$lastHitCpu = cir.getReturnValue();
    }

    @ModifyArg(method = "getTooltip", at = @At(value = "INVOKE", target = "Lappeng/client/gui/Tooltip;<init>(Ljava/util/List;)V"), index = 0)
    private List<Component> crazyae2addons$appendCpuInfo(List<Component> lines) {
        if (!CrazyConfig.COMMON.CPU_PRIORITIES_ENABLED.get()) {
            return lines;
        }

        var result = new ArrayList<>(lines);
        var cpu = this.crazyae2addons$lastHitCpu;

        if (cpu instanceof ICpuPrio prio) {
            result.add(Component.literal("Priority: " + prio.getPrio()));
        }

        return result;
    }
}
