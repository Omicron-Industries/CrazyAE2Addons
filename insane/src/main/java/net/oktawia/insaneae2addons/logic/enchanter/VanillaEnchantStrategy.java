package net.oktawia.insaneae2addons.logic.enchanter;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class VanillaEnchantStrategy implements EnchantStrategy {

    private static final int[][] BOOKSHELF_OFFSETS = {
            {-1, 0, -2}, {0, 0, -2}, {1, 0, -2},
            {-2, 0, -1}, {-2, 0, 0}, {-2, 0, 1},
            {-1, 0, 2}, {0, 0, 2}, {1, 0, 2},
            {2, 0, -1}, {2, 0, 0}, {2, 0, 1},
            {-1, 1, -2}, {0, 1, -2}, {1, 1, -2},
            {-2, 1, -1}, {-2, 1, 0}, {-2, 1, 1},
            {-1, 1, 2}, {0, 1, 2}, {1, 1, 2},
            {2, 1, -1}, {2, 1, 0}, {2, 1, 1}
    };

    @Override
    public EnchantRoll roll(RandomSource random, Level level, BlockPos tablePos, ItemStack input, int option) {
        int xpLevel = EnchantmentHelper.getEnchantmentCost(random, option, bookshelves(level, tablePos), input);
        if (xpLevel <= 0) {
            return EnchantRoll.EMPTY;
        }
        List<EnchantmentInstance> enchantments = EnchantmentHelper.selectEnchantment(random, input, xpLevel, false);
        return enchantments.isEmpty() ? EnchantRoll.EMPTY : new EnchantRoll(xpLevel, enchantments);
    }

    @Override
    public int costLevel(RandomSource random, Level level, BlockPos tablePos, ItemStack input, int option) {
        return Math.max(0, EnchantmentHelper.getEnchantmentCost(random, option, bookshelves(level, tablePos), input));
    }

    private static int bookshelves(Level level, BlockPos tablePos) {
        int count = 0;
        for (int[] offset : BOOKSHELF_OFFSETS) {
            if (level.getBlockState(tablePos.offset(offset[0], offset[1], offset[2])).is(Blocks.BOOKSHELF)) {
                count++;
            }
        }
        return count;
    }
}
