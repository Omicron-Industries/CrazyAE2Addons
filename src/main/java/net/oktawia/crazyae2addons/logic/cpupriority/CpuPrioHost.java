package net.oktawia.crazyae2addons.logic.cpupriority;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import appeng.api.implementations.menuobjects.ItemMenuHost;

import net.oktawia.crazyae2addons.items.CpuPrioTunerItem;

public class CpuPrioHost extends ItemMenuHost {

    public CpuPrioHost(Player player, @Nullable Integer slot, ItemStack stack) {
        super(player, slot, stack);
    }

    public @Nullable BlockPos getTargetPos() {
        var tag = getItemStack().getTag();
        if (tag == null || !tag.contains(CpuPrioTunerItem.NBT_CPU_POS)) {
            return null;
        }

        return BlockPos.of(tag.getLong(CpuPrioTunerItem.NBT_CPU_POS));
    }
}
