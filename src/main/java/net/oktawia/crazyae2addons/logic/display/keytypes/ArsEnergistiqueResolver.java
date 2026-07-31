package net.oktawia.crazyae2addons.logic.display.keytypes;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import gripe._90.arseng.me.key.SourceKey;

import appeng.api.stacks.AEKey;

public class ArsEnergistiqueResolver implements IDisplayKeyResolver {

    @Override
    public String getTypePrefix() {
        return "source";
    }

    @Override
    public @Nullable AEKey resolve(String id) {
        return SourceKey.KEY;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @Nullable ItemStack getIcon(String id) {
        return SourceKey.KEY.wrapForDisplayOrFilter();
    }
}
