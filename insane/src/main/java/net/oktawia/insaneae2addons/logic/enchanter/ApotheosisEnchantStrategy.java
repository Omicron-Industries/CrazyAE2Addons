package net.oktawia.insaneae2addons.logic.enchanter;

import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import dev.shadowsoffire.apotheosis.ench.objects.TreasureShelfBlock;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingStatRegistry;
import dev.shadowsoffire.apotheosis.ench.table.RealEnchantmentHelper;

public class ApotheosisEnchantStrategy implements EnchantStrategy {

    private record Stats(float eterna, float quanta, float arcana, boolean treasure) {
    }

    @Override
    public EnchantRoll roll(RandomSource random, Level level, BlockPos tablePos, ItemStack input, int option) {
        Stats stats = stats(level, tablePos);
        int xpLevel = RealEnchantmentHelper.getEnchantmentCost(random, option, stats.eterna(), input);
        if (xpLevel <= 0) {
            return EnchantRoll.EMPTY;
        }
        List<EnchantmentInstance> enchantments = RealEnchantmentHelper.selectEnchantment(
                random, input, xpLevel, stats.quanta(), stats.arcana(), stats.eterna(), stats.treasure(), Set.of());
        return enchantments.isEmpty() ? EnchantRoll.EMPTY : new EnchantRoll(xpLevel, enchantments);
    }

    @Override
    public int costLevel(RandomSource random, Level level, BlockPos tablePos, ItemStack input, int option) {
        return Math.max(0,
                RealEnchantmentHelper.getEnchantmentCost(random, option, stats(level, tablePos).eterna(), input));
    }

    private static Stats stats(Level level, BlockPos tablePos) {
        float eterna = 0;
        float quanta = 0;
        float arcana = 0;
        boolean treasure = false;
        for (BlockPos pos : BlockPos.betweenClosed(tablePos.offset(-2, 0, -2), tablePos.offset(2, 1, 2))) {
            BlockState state = level.getBlockState(pos);
            eterna += EnchantingStatRegistry.getEterna(state, level, pos);
            quanta += EnchantingStatRegistry.getQuanta(state, level, pos);
            arcana += EnchantingStatRegistry.getArcana(state, level, pos);
            if (!treasure && state.getBlock() instanceof TreasureShelfBlock) {
                treasure = true;
            }
        }
        return new Stats(eterna, quanta, arcana, treasure);
    }
}
