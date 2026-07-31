package net.oktawia.insaneae2addons.logic.mobstorage;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.implementations.menuobjects.ItemMenuHost;

public class MobKeySelectorHost extends ItemMenuHost {
    public MobKeySelectorHost(Player player, @Nullable Integer slot, ItemStack itemStack) {
        super(player, slot, itemStack);
    }
}
