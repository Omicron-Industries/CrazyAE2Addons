package net.oktawia.insaneae2addons.logic.enchanter;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

import java.util.List;

public interface EnchantStrategy {

    EnchantRoll roll(RandomSource random, Level level, BlockPos tablePos, ItemStack input, int option);

    int costLevel(RandomSource random, Level level, BlockPos tablePos, ItemStack input, int option);

    record EnchantRoll(int xpLevel, List<EnchantmentInstance> enchantments) {

        public static final EnchantRoll EMPTY = new EnchantRoll(0, List.of());

        public boolean isEmpty() {
            return enchantments.isEmpty();
        }
    }
}
