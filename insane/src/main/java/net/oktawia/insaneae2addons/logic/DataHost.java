package net.oktawia.insaneae2addons.logic;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DataHost extends ItemMenuHost {
    public DataHost(Player player, int slot, ItemStack stack) {
        super(player, slot, stack);
    }
}
