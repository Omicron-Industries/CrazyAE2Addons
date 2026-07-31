package net.oktawia.crazyae2addons.logic.display.keytypes;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import appeng.api.stacks.AEKey;

public interface IDisplayKeyResolver {

    String getTypePrefix();

    @Nullable
    AEKey resolve(String id);

    @OnlyIn(Dist.CLIENT)
    @Nullable
    ItemStack getIcon(String id);
}
