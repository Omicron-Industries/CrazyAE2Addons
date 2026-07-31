package net.oktawia.crazyae2addons.client.textures;

import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface PreviewQuadProvider {
    List<BakedQuad> previewQuads(BlockState state, Direction face);
}
