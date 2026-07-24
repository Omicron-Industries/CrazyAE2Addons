package net.oktawia.crazyae2addons.client.renderer.preview.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class PreviewCacheBuilder {
    private PreviewCacheBuilder() {
    }

    public static MultiblockPreviewInfo rebuild(MultiblockPreviewHost host) {
        var definition = host.getPreviewDefinition();
        var origin = host.getPreviewOrigin();
        var facing = host.getPreviewFacing();

        var blockInfos = new ArrayList<MultiblockPreviewInfo.BlockInfo>();
        Map<Character, Set<Block>> allowedBySymbol = new HashMap<>();

        for (MultiblockDefinition.PatternEntry entry : definition.getEntries(facing)) {
            var symbol = definition.getSymbol(entry.symbol());
            if (symbol == null) continue;

            BlockPos worldPos = origin.offset(entry.relX(), entry.relY(), entry.relZ());

            blockInfos.add(new MultiblockPreviewInfo.BlockInfo(
                    worldPos,
                    host.getPreviewState(entry, symbol),
                    allowedBySymbol.computeIfAbsent(entry.symbol(), ignored -> Set.copyOf(symbol.blocks()))
            ));
        }

        return new MultiblockPreviewInfo(origin, facing, blockInfos);
    }
}
