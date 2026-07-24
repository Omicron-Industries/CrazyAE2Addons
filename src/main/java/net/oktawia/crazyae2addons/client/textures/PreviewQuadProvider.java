package net.oktawia.crazyae2addons.client.textures;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface PreviewQuadProvider {
    List<BakedQuad> previewQuads(BlockState state, Direction face);
}
