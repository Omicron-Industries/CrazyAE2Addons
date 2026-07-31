package net.oktawia.insaneae2addons.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.oktawia.crazyae2addons.client.textures.ConnectedTextureEntry;
import net.oktawia.crazyae2addons.client.textures.ConnectedTextureRegistry;
import net.oktawia.crazyae2addons.client.textures.ConnectedTextureRule;
import net.oktawia.insaneae2addons.blocks.cradle.EntropyCradleBlock;
import net.oktawia.insaneae2addons.blocks.cradle.EntropyCradleControllerBlock;
import net.oktawia.insaneae2addons.blocks.research.ResearchUnitBlock;
import net.oktawia.insaneae2addons.blocks.penrose.PortablePenroseSphereControllerBlock;
import net.oktawia.insaneae2addons.blocks.penrose.PenroseFrameBlock;
import net.oktawia.insaneae2addons.blocks.mobstorage.MobFarmControllerBlock;
import net.oktawia.insaneae2addons.blocks.mobstorage.MobFarmWallBlock;
import net.oktawia.insaneae2addons.blocks.research.ResearchUnitFrameBlock;
import net.oktawia.insaneae2addons.blocks.mobstorage.SpawnerExtractorControllerBlock;
import net.oktawia.insaneae2addons.blocks.mobstorage.SpawnerExtractorWallBlock;
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

    private static final ResourceLocation PENROSE_FRAME_TEXTURE =
            new ResourceLocation("insaneae2addons", "block/penrose_frame");
    private static final ResourceLocation PENROSE_FRAME_FORMED_TEXTURE =
            new ResourceLocation("insaneae2addons", "block/penrose_frame_formed");
    private static final ResourceLocation PENROSE_GLASS_TEXTURE =
            new ResourceLocation("insaneae2addons", "block/penrose_glass");
    private static final int PENROSE_GLASS_BAR_THICKNESS = 1;
    private static final ResourceLocation PENROSE_COIL_TEXTURE =
            new ResourceLocation("insaneae2addons", "block/penrose_coil");
    private static final ResourceLocation MOB_FARM_WALL_TEXTURE =
            new ResourceLocation("insaneae2addons", "block/mob_farm_wall");
    private static final ResourceLocation MOB_FARM_WALL_FORMED_TEXTURE =
            new ResourceLocation("insaneae2addons", "block/mob_farm_wall_formed");
    private static final ResourceLocation SPAWNER_WALL_TEXTURE =
            new ResourceLocation("insaneae2addons", "block/spawner_extractor_wall");
    private static final ResourceLocation SPAWNER_WALL_FORMED_TEXTURE =
            new ResourceLocation("insaneae2addons", "block/spawner_extractor_wall_formed");

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

        ConnectedTextureRule sphereRule = (level, selfPos, selfState, otherPos, otherState, face) ->
                isSpherePart(selfState.getBlock()) && isSpherePart(otherState.getBlock());

        ConnectedTextureRegistry.register(
                InsaneBlockRegistrar.PENROSE_FRAME_BLOCK,
                new ConnectedTextureEntry(
                        state -> penroseFrameTexture(state.getValue(PenroseFrameBlock.FORMED)),
                        sphereRule,
                        null
                )
        );

        ConnectedTextureRegistry.register(
                InsaneBlockRegistrar.PENROSE_COIL_BLOCK,
                new ConnectedTextureEntry(state -> PENROSE_COIL_TEXTURE, sphereRule, null)
        );

        ConnectedTextureRule glassRule = (level, selfPos, selfState, otherPos, otherState, face) ->
                selfState.getBlock() == otherState.getBlock();

        ConnectedTextureRegistry.register(
                InsaneBlockRegistrar.PENROSE_GLASS_BLOCK,
                ConnectedTextureEntry.bars(PENROSE_GLASS_TEXTURE, glassRule, PENROSE_GLASS_BAR_THICKNESS)
        );

        ConnectedTextureRegistry.register(
                InsaneBlockRegistrar.PORTABLE_PENROSE_SPHERE_CONTROLLER_BLOCK,
                new ConnectedTextureEntry(
                        state -> penroseFrameTexture(state.getValue(PortablePenroseSphereControllerBlock.FORMED)),
                        sphereRule,
                        (state, face) -> face == state.getValue(BlockStateProperties.HORIZONTAL_FACING)
                                ? CONTROLLER_TEXTURE
                                : null
                )
        );

        ConnectedTextureRule mobFarmRule = (level, selfPos, selfState, otherPos, otherState, face) ->
                isMobFarmPart(selfState.getBlock()) && isMobFarmPart(otherState.getBlock());

        ConnectedTextureRegistry.register(
                InsaneBlockRegistrar.MOB_FARM_WALL_BLOCK,
                new ConnectedTextureEntry(
                        state -> mobFarmWallTexture(state.getValue(MobFarmWallBlock.FORMED)),
                        mobFarmRule,
                        null
                )
        );

        ConnectedTextureRegistry.register(
                InsaneBlockRegistrar.MOB_FARM_CONTROLLER_BLOCK,
                new ConnectedTextureEntry(
                        state -> mobFarmWallTexture(state.getValue(MobFarmControllerBlock.FORMED)),
                        mobFarmRule,
                        (state, face) -> face == state.getValue(BlockStateProperties.HORIZONTAL_FACING)
                                ? CONTROLLER_TEXTURE
                                : null
                )
        );

        ConnectedTextureRule spawnerRule = (level, selfPos, selfState, otherPos, otherState, face) ->
                isSpawnerPart(selfState.getBlock()) && isSpawnerPart(otherState.getBlock());

        ConnectedTextureRegistry.register(
                InsaneBlockRegistrar.SPAWNER_EXTRACTOR_WALL_BLOCK,
                new ConnectedTextureEntry(
                        state -> spawnerWallTexture(state.getValue(SpawnerExtractorWallBlock.FORMED)),
                        spawnerRule,
                        null
                )
        );

        ConnectedTextureRegistry.register(
                InsaneBlockRegistrar.SPAWNER_EXTRACTOR_CONTROLLER_BLOCK,
                new ConnectedTextureEntry(
                        state -> spawnerWallTexture(state.getValue(SpawnerExtractorControllerBlock.FORMED)),
                        spawnerRule,
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

    private static ResourceLocation penroseFrameTexture(boolean formed) {
        return formed ? PENROSE_FRAME_FORMED_TEXTURE : PENROSE_FRAME_TEXTURE;
    }

    private static boolean isSpherePart(Block block) {
        return block == InsaneBlockRegistrar.PENROSE_FRAME_BLOCK.get()
                || block == InsaneBlockRegistrar.PENROSE_COIL_BLOCK.get()
                || block == InsaneBlockRegistrar.PORTABLE_PENROSE_SPHERE_CONTROLLER_BLOCK.get();
    }

    private static ResourceLocation mobFarmWallTexture(boolean formed) {
        return formed ? MOB_FARM_WALL_FORMED_TEXTURE : MOB_FARM_WALL_TEXTURE;
    }

    private static boolean isMobFarmPart(Block block) {
        return block == InsaneBlockRegistrar.MOB_FARM_WALL_BLOCK.get()
                || block == InsaneBlockRegistrar.MOB_FARM_CONTROLLER_BLOCK.get();
    }

    private static ResourceLocation spawnerWallTexture(boolean formed) {
        return formed ? SPAWNER_WALL_FORMED_TEXTURE : SPAWNER_WALL_TEXTURE;
    }

    private static boolean isSpawnerPart(Block block) {
        return block == InsaneBlockRegistrar.SPAWNER_EXTRACTOR_WALL_BLOCK.get()
                || block == InsaneBlockRegistrar.SPAWNER_EXTRACTOR_CONTROLLER_BLOCK.get();
    }
}
