package net.oktawia.insaneae2addons.datagen;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.blocks.EnergyStorageBlock;
import net.oktawia.insaneae2addons.blocks.research.ResearchCableBlock;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import org.jetbrains.annotations.Nullable;

public class InsaneBlockStateProvider extends BlockStateProvider {
    private static final float CTM_TILE_U = 16.0f / 5.0f;
    private static final float OVERLAY_OFFSET = 0.02f;
    private static final ResourceLocation CONTROLLER_OVERLAY =
            new ResourceLocation("crazyae2addons", "block/controller");

    public InsaneBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, InsaneAddons.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (var block : InsaneBlockRegistrar.getBlocks()) {
            if (block != InsaneBlockRegistrar.AMPERE_METER_BLOCK.get()
                    && block != InsaneBlockRegistrar.AUTO_BUILDER_BLOCK.get()
                    && block != InsaneBlockRegistrar.BROKEN_PATTERN_PROVIDER_BLOCK.get()
                    && block != InsaneBlockRegistrar.RESEARCH_STATION_BLOCK.get()
                    && block != InsaneBlockRegistrar.RESEARCH_UNIT_FRAME_BLOCK.get()
                    && block != InsaneBlockRegistrar.RESEARCH_UNIT_BLOCK.get()
                    && block != InsaneBlockRegistrar.RESEARCH_CABLE_BLOCK.get()
                    && block != InsaneBlockRegistrar.RESEARCH_CABLE_PINK_BLOCK.get()
                    && block != InsaneBlockRegistrar.RESEARCH_CABLE_WHITE_BLOCK.get()
                    && block != InsaneBlockRegistrar.RESEARCH_PEDESTAL_BOTTOM_BLOCK.get()
                    && block != InsaneBlockRegistrar.RESEARCH_PEDESTAL_TOP_BLOCK.get()
                    && block != InsaneBlockRegistrar.ENTROPY_CRADLE_CONTROLLER_BLOCK.get()
                    && block != InsaneBlockRegistrar.ENTROPY_CRADLE_BLOCK.get()
                    && block != InsaneBlockRegistrar.ENTROPY_CRADLE_CAPACITOR_BLOCK.get()
                    && block != InsaneBlockRegistrar.PENROSE_FRAME_BLOCK.get()
                    && block != InsaneBlockRegistrar.PENROSE_GLASS_BLOCK.get()
                    && block != InsaneBlockRegistrar.PENROSE_COIL_BLOCK.get()
                    && block != InsaneBlockRegistrar.PORTABLE_PENROSE_SPHERE_CONTROLLER_BLOCK.get()
                    && block != InsaneBlockRegistrar.MOB_FARM_CONTROLLER_BLOCK.get()
                    && block != InsaneBlockRegistrar.MOB_FARM_WALL_BLOCK.get()
                    && block != InsaneBlockRegistrar.SPAWNER_EXTRACTOR_CONTROLLER_BLOCK.get()
                    && block != InsaneBlockRegistrar.SPAWNER_EXTRACTOR_WALL_BLOCK.get()
                    && !(block instanceof EnergyStorageBlock)
            ) {
                simpleBlock(block, cubeAll(block));
            }
        }

        for (var block : InsaneBlockRegistrar.getBlocks()) {
            if (block instanceof EnergyStorageBlock) {
                String name = ForgeRegistries.BLOCKS.getKey(block).getPath();
                ModelFile model = models().cubeAll(name, modLoc("block/energy/" + name));
                getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(model).build());
            }
        }

        researchCable(InsaneBlockRegistrar.RESEARCH_CABLE_BLOCK.get(), "research_cable");
        researchCable(InsaneBlockRegistrar.RESEARCH_CABLE_PINK_BLOCK.get(), "research_cable_pink");
        researchCable(InsaneBlockRegistrar.RESEARCH_CABLE_WHITE_BLOCK.get(), "research_cable_white");

        Block frame = InsaneBlockRegistrar.RESEARCH_UNIT_FRAME_BLOCK.get();
        simpleBlock(frame, ctmModel(frame, modLoc("block/research_unit_frame"), true, null));

        Block unit = InsaneBlockRegistrar.RESEARCH_UNIT_BLOCK.get();
        simpleBlock(unit, ctmModel(unit, modLoc("block/research_unit_frame"), true, CONTROLLER_OVERLAY));

        Block station = InsaneBlockRegistrar.RESEARCH_STATION_BLOCK.get();
        orientedBlock(station, ctmModel(station, modLoc("block/research_pedestal_bottom"), false, CONTROLLER_OVERLAY));

        Block cradle = InsaneBlockRegistrar.ENTROPY_CRADLE_BLOCK.get();
        simpleBlock(cradle, ctmModel(cradle, modLoc("block/entropy_cradle"), true, null));

        Block cradleController = InsaneBlockRegistrar.ENTROPY_CRADLE_CONTROLLER_BLOCK.get();
        simpleBlock(cradleController, ctmModel(cradleController, modLoc("block/entropy_cradle"), true, CONTROLLER_OVERLAY));

        Block penroseFrame = InsaneBlockRegistrar.PENROSE_FRAME_BLOCK.get();
        simpleBlock(penroseFrame, ctmModel(penroseFrame, modLoc("block/penrose_frame"), true, null));

        Block penroseGlass = InsaneBlockRegistrar.PENROSE_GLASS_BLOCK.get();
        simpleBlock(penroseGlass, ctmModel(penroseGlass, modLoc("block/penrose_glass"), true, null)
                .renderType("cutout"));

        Block penroseCoil = InsaneBlockRegistrar.PENROSE_COIL_BLOCK.get();
        simpleBlock(penroseCoil, ctmModel(penroseCoil, modLoc("block/penrose_coil"), true, null));

        Block penroseController = InsaneBlockRegistrar.PORTABLE_PENROSE_SPHERE_CONTROLLER_BLOCK.get();
        simpleBlock(penroseController, ctmModel(penroseController, modLoc("block/penrose_frame"), true, CONTROLLER_OVERLAY));

        Block mobFarmWall = InsaneBlockRegistrar.MOB_FARM_WALL_BLOCK.get();
        simpleBlock(mobFarmWall, ctmModel(mobFarmWall, modLoc("block/mob_farm_wall"), true, null));

        Block mobFarmController = InsaneBlockRegistrar.MOB_FARM_CONTROLLER_BLOCK.get();
        simpleBlock(mobFarmController, ctmModel(mobFarmController, modLoc("block/mob_farm_wall"), true, CONTROLLER_OVERLAY));

        Block spawnerWall = InsaneBlockRegistrar.SPAWNER_EXTRACTOR_WALL_BLOCK.get();
        simpleBlock(spawnerWall, ctmModel(spawnerWall, modLoc("block/spawner_extractor_wall"), true, null));

        Block spawnerController = InsaneBlockRegistrar.SPAWNER_EXTRACTOR_CONTROLLER_BLOCK.get();
        simpleBlock(spawnerController, ctmModel(spawnerController, modLoc("block/spawner_extractor_wall"), true, CONTROLLER_OVERLAY));
    }

    private void researchCable(Block block, String texture) {
        ResourceLocation tex = modLoc("block/" + texture);
        String base = ForgeRegistries.BLOCKS.getKey(block).getPath();

        BlockModelBuilder core = cablePart(base + "_core", tex, 6, 6, 6, 10, 10, 10);
        BlockModelBuilder side = cablePart(base + "_side", tex, 6, 6, 0, 10, 10, 6);
        BlockModelBuilder sideVertical = cablePart(base + "_side_vertical", tex, 6, 10, 6, 10, 16, 10);

        BlockModelBuilder inv = cablePart(base + "_inv", tex, 6, 6, 6, 10, 10, 10);
        inv.element().from(6, 6, 0).to(10, 10, 6).allFaces((dir, face) -> face.texture("#all")).end();
        inv.element().from(6, 6, 10).to(10, 10, 16).allFaces((dir, face) -> face.texture("#all")).end();

        getMultipartBuilder(block)
                .part().modelFile(core).addModel().end()
                .part().modelFile(side).addModel().condition(ResearchCableBlock.NORTH, true).end()
                .part().modelFile(side).rotationY(180).addModel().condition(ResearchCableBlock.SOUTH, true).end()
                .part().modelFile(side).rotationY(270).addModel().condition(ResearchCableBlock.WEST, true).end()
                .part().modelFile(side).rotationY(90).addModel().condition(ResearchCableBlock.EAST, true).end()
                .part().modelFile(sideVertical).uvLock(true).addModel().condition(ResearchCableBlock.UP, true).end()
                .part().modelFile(sideVertical).rotationX(180).uvLock(true).addModel().condition(ResearchCableBlock.DOWN, true).end();
    }

    private BlockModelBuilder cablePart(String name, ResourceLocation tex,
                                        int x1, int y1, int z1, int x2, int y2, int z2) {
        BlockModelBuilder model = models().getBuilder(name)
                .parent(new ModelFile.UncheckedModelFile("block/block"))
                .texture("particle", tex)
                .texture("all", tex);
        model.element()
                .from(x1, y1, z1)
                .to(x2, y2, z2)
                .allFaces((dir, face) -> face.texture("#all"))
                .end();
        return model;
    }

    private BlockModelBuilder ctmModel(Block block, ResourceLocation base, boolean connected, @Nullable ResourceLocation overlay) {
        float uMax = connected ? CTM_TILE_U : 16.0f;
        String name = "block/" + ForgeRegistries.BLOCKS.getKey(block).getPath();

        BlockModelBuilder model = models().getBuilder(name)
                .parent(new ModelFile.UncheckedModelFile("block/block"))
                .texture("particle", base)
                .texture("all", base);
        model.element()
                .from(0, 0, 0)
                .to(16, 16, 16)
                .allFaces((dir, face) -> face.uvs(0, 0, uMax, 16).texture("#all").cullface(dir))
                .end();

        if (overlay != null) {
            model.renderType("cutout").texture("overlay", overlay);
            model.element()
                    .from(0, 0, -OVERLAY_OFFSET)
                    .to(16, 16, -OVERLAY_OFFSET)
                    .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#overlay").cullface(Direction.NORTH).end()
                    .end();
        }
        return model;
    }

    private void orientedBlock(Block block, ModelFile model) {
        getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            int rotationY = switch (facing) {
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };
            return ConfiguredModel.builder().modelFile(model).rotationY(rotationY).build();
        });
    }
}
