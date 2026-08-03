package net.oktawia.crazyae2addons.mixins.cpupriority.ae2;

import org.spongepowered.asm.mixin.Mixin;

import appeng.menu.me.crafting.CraftingStatusMenu.CraftingCpuList;

import net.oktawia.crazyae2addons.logic.interfaces.ICpuListView;

@Mixin(value = CraftingCpuList.class, remap = false)
public abstract class MixinCraftingCpuList implements ICpuListView {
}
