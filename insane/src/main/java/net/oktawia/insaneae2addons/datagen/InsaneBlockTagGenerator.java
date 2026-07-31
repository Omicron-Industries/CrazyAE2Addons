package net.oktawia.insaneae2addons.datagen;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;

public class InsaneBlockTagGenerator extends BlockTagsProvider {
    public InsaneBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, InsaneAddons.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        for (var block : InsaneBlockRegistrar.getBlocks()) {
            this.tag(BlockTags.NEEDS_IRON_TOOL)
                    .add(block);
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .add(block);
        }
    }
}
