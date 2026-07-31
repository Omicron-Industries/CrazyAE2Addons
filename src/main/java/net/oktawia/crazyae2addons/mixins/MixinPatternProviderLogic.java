package net.oktawia.crazyae2addons.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.nbt.CompoundTag;

import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.util.inv.AppEngInternalInventory;

import net.oktawia.crazyae2addons.logic.interfaces.IProviderLogicResizable;

@Mixin(value = PatternProviderLogic.class, priority = 1100, remap = false)
public class MixinPatternProviderLogic implements IProviderLogicResizable {

    @Shadow
    @Mutable
    @Final
    private AppEngInternalInventory patternInventory;

    @Override
    public void crazyAE2Addons$setSize(int size) {
        var tag = new CompoundTag();
        var host = patternInventory.getHost();
        patternInventory.writeToNBT(tag, "patterns");
        this.patternInventory = new AppEngInternalInventory(host, size);
        this.patternInventory.readFromNBT(tag, "patterns");
    }
}
