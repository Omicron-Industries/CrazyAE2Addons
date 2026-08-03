package net.oktawia.crazyae2addons.mixins.cpupriority.ae2cln;

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

import net.oktawia.crazyae2addons.logic.cpupriority.CpuPriorityHelper;

@Mixin(value = CraftConfirmMenu.class, remap = false)
public abstract class MixinCraftConfirmMenuCpuListPrio {

    @Shadow
    @Final
    private WeakHashMap<ICraftingCPU, Integer> cpuSerialMap;

    @Shadow
    private ImmutableSet<ICraftingCPU> lastCpuSet;

    @Inject(method = "createCpuList", at = @At("RETURN"))
    private void crazyae2addons$fillPrio(CallbackInfoReturnable<Object> cir) {
        CpuPriorityHelper.applyEntryPriorities(cir.getReturnValue(), this.lastCpuSet, this.cpuSerialMap);
    }
}
