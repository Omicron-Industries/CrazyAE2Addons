package net.oktawia.insaneae2addons.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.oktawia.crazyae2addons.client.textures.ConnectedTextureEntry;
import net.oktawia.crazyae2addons.client.textures.ConnectedTextureRegistry;
import net.oktawia.crazyae2addons.client.textures.ConnectedTextureRule;
import net.oktawia.insaneae2addons.blocks.EntropyCradleBlock;
import net.oktawia.insaneae2addons.blocks.EntropyCradleControllerBlock;
import net.oktawia.insaneae2addons.blocks.ResearchUnitBlock;
import net.oktawia.insaneae2addons.blocks.ResearchUnitFrameBlock;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;

public final class InsaneConnectedTextures {

    private static final ResourceLocation FRAME_TEXTURE =
            new ResourceLocation("insaneae2addons", "block/research_unit_frame");
    private static final ResourceLocation FRAME_FORMED_TEXTURE =
            new ResourceLocation("insaneae2addons", "block/research_unit_frame_formed");
    private static final ResourceLocation CONTROLLER_TEXTURE =
            new ResourceLocation("crazyae2addons", "block/controller");
    private static final ResourceLocation CRADLE_TEXTURE =
            new ResourceLocation("insaneae2addons", "block/entropy_cradle");
    private static final ResourceLocation CRADLE_FORMED_TEXTURE =
            new ResourceLocation("insaneae2addons", "block/entropy_cradle_formed");

    private InsaneConnectedTextures() {
    }

    public static void register() {
        ConnectedTextureRule unitRule = (level, selfPos, selfState, otherPos, otherState, face) ->
                isUnitPart(selfState.getBlock()) && isUnitPart(otherState.getBlock());

        ConnectedTextureRegistry.register(
                InsaneBlockRegistrar.RESEARCH_UNIT_FRAME_BLOCK,
                new ConnectedTextureEntry(
                        state -> frameTexture(state.getValue(ResearchUnitFrameBlock.FORMED)),
                        unitRule,
                        null
                )
        );

        ConnectedTextureRegistry.register(
                InsaneBlockRegistrar.RESEARCH_UNIT_BLOCK,
                new ConnectedTextureEntry(
                        state -> frameTexture(state.getValue(ResearchUnitBlock.FORMED)),
                        unitRule,
                        (state, face) -> face == state.getValue(BlockStateProperties.HORIZONTAL_FACING)
                                ? CONTROLLER_TEXTURE
                                : null
                )
        );

        ConnectedTextureRule cradleRule = (level, selfPos, selfState, otherPos, otherState, face) ->
                isCradlePart(selfState.getBlock()) && isCradlePart(otherState.getBlock());

        ConnectedTextureRegistry.register(
                InsaneBlockRegistrar.ENTROPY_CRADLE_BLOCK,
                new ConnectedTextureEntry(
                        state -> cradleTexture(state.getValue(EntropyCradleBlock.FORMED)),
                        cradleRule,
                        null
                )
        );

        ConnectedTextureRegistry.register(
                InsaneBlockRegistrar.ENTROPY_CRADLE_CONTROLLER_BLOCK,
                new ConnectedTextureEntry(
                        state -> cradleTexture(state.getValue(EntropyCradleControllerBlock.FORMED)),
                        cradleRule,
                        (state, face) -> face == state.getValue(BlockStateProperties.HORIZONTAL_FACING)
                                ? CONTROLLER_TEXTURE
                                : null
                )
        );
    }

    private static ResourceLocation frameTexture(boolean formed) {
        return formed ? FRAME_FORMED_TEXTURE : FRAME_TEXTURE;
    }

    private static ResourceLocation cradleTexture(boolean formed) {
        return formed ? CRADLE_FORMED_TEXTURE : CRADLE_TEXTURE;
    }

    private static boolean isUnitPart(Block block) {
        return block == InsaneBlockRegistrar.RESEARCH_UNIT_FRAME_BLOCK.get()
                || block == InsaneBlockRegistrar.RESEARCH_UNIT_BLOCK.get();
    }

    private static boolean isCradlePart(Block block) {
        return block == InsaneBlockRegistrar.ENTROPY_CRADLE_BLOCK.get()
                || block == InsaneBlockRegistrar.ENTROPY_CRADLE_CONTROLLER_BLOCK.get();
    }
}
