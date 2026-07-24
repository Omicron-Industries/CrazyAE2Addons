package net.oktawia.crazyae2addons.client.textures;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public record ConnectedTextureEntry(
        Function<BlockState, ResourceLocation> textureSelector,
        ConnectedTextureRule rule,
        @Nullable BiFunction<BlockState, Direction, ResourceLocation> faceOverlay,
        int barThickness
) {
    public ConnectedTextureEntry(
            Function<BlockState, ResourceLocation> textureSelector,
            ConnectedTextureRule rule,
            @Nullable BiFunction<BlockState, Direction, ResourceLocation> faceOverlay
    ) {
        this(textureSelector, rule, faceOverlay, 0);
    }

    public ResourceLocation texture(BlockState state) {
        return textureSelector.apply(state);
    }

    public boolean hasOverlay() {
        return faceOverlay != null;
    }

    @Nullable
    public ResourceLocation faceOverlay(BlockState state, Direction face) {
        return faceOverlay == null ? null : faceOverlay.apply(state, face);
    }

    public static ConnectedTextureEntry single(ResourceLocation texture, ConnectedTextureRule rule) {
        return new ConnectedTextureEntry(state -> texture, rule, null);
    }

    public static ConnectedTextureEntry bars(ResourceLocation texture, ConnectedTextureRule rule, int thickness) {
        return new ConnectedTextureEntry(state -> texture, rule, null, thickness);
    }
}
