package net.oktawia.crazyae2addons.mixins.cpupriority.ae2cln;

import java.util.HashMap;
import java.util.WeakHashMap;

import com.google.common.collect.ImmutableSet;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import appeng.api.networking.crafting.ICraftingCPU;
import appeng.menu.me.crafting.CraftConfirmMenu;
import appeng.menu.me.crafting.CraftingStatusMenu.CraftingCpuList;

import net.oktawia.crazyae2addons.logic.cpupriority.CpuPriorityHelper;
import net.oktawia.crazyae2addons.logic.interfaces.ICpuPrio;

@Mixin(value = CraftConfirmMenu.class, remap = false)
public abstract class MixinCraftConfirmMenuCpuListPrio {

    @Shadow
    @Final
    private WeakHashMap<ICraftingCPU, Integer> cpuSerialMap;

    @Shadow
    private ImmutableSet<ICraftingCPU> lastCpuSet;

    @Inject(method = "createCpuList", at = @At("RETURN"))
    private void crazyae2addons$fillPrio(CallbackInfoReturnable<CraftingCpuList> cir) {
        var list = cir.getReturnValue();
        if (list == null || list.cpus().isEmpty() || this.lastCpuSet == null) {
            return;
        }

        var serialToCpu = new HashMap<Integer, ICraftingCPU>(cpuSerialMap.size());
        for (var cpu : this.lastCpuSet) {
            var serial = cpuSerialMap.get(cpu);
            if (serial != null) {
                serialToCpu.put(serial, cpu);
            }
        }

        for (var entry : list.cpus()) {
            var cpu = serialToCpu.get(entry.serial());
            ((ICpuPrio) (Object) entry).setPrio(cpu == null ? 0 : CpuPriorityHelper.getCpuPriority(cpu));
        }
    }
}
