package net.oktawia.crazyae2addons.mixins.cpupriority.ae2cln;

import org.spongepowered.asm.mixin.Mixin;

import net.oktawia.crazyae2addons.logic.interfaces.ICpuListView;

@Mixin(targets = "appeng.menu.me.crafting.CraftingCpuList", remap = false)
public abstract class MixinCraftingCpuList implements ICpuListView {
}
