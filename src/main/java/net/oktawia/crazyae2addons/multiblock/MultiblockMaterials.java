package net.oktawia.crazyae2addons.multiblock;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MultiblockMaterials {
    private MultiblockMaterials() {
    }

    public static Map<Block, Integer> count(MultiblockDefinition definition) {
        Map<Block, Integer> totals = new LinkedHashMap<>();

        for (MultiblockDefinition.PatternEntry entry : definition.getEntries(Direction.NORTH)) {
            Block block = representative(definition, entry.symbol());
            if (block == Blocks.AIR) {
                continue;
            }

            totals.merge(block, 1, Integer::sum);
        }

        return totals;
    }

    public static Block representative(MultiblockDefinition definition, char symbol) {
        MultiblockDefinition.SymbolDef symbolDef = definition.getSymbol(symbol);
        if (symbolDef == null) {
            return Blocks.AIR;
        }

        List<Block> blocks = symbolDef.blocks();
        for (Block block : blocks) {
            if (block != Blocks.AIR) {
                return block;
            }
        }

        return Blocks.AIR;
    }
}
