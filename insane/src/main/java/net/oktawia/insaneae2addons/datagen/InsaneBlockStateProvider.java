package net.oktawia.insaneae2addons.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;

public class InsaneBlockStateProvider extends BlockStateProvider {
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
            ) {
                simpleBlockWithItem(block);
            }
        }
    }

    private void simpleBlockWithItem(Block block) {
        simpleBlockWithItem(block, cubeAll(block));
    }
}
