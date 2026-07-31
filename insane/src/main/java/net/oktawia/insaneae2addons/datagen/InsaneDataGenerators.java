package net.oktawia.insaneae2addons.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.oktawia.insaneae2addons.InsaneAddons;

@Mod.EventBusSubscriber(modid = InsaneAddons.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class InsaneDataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new InsaneRecipeProvider(packOutput));
        generator.addProvider(event.includeServer(), new InsaneFabricationRecipeProvider(packOutput));
        generator.addProvider(event.includeServer(), new InsaneResearchRecipeProvider(packOutput));
        generator.addProvider(event.includeServer(), new InsaneCradleRecipeProvider(packOutput));
        generator.addProvider(event.includeServer(), InsaneLootTableProvider.create(packOutput));

        generator.addProvider(event.includeClient(), new InsaneBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new InsaneItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new InsaneLangProvider(packOutput, "en_us"));

        InsaneBlockTagGenerator blockTagGenerator = generator.addProvider(event.includeServer(),
                new InsaneBlockTagGenerator(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(),
                new InsaneItemTagGenerator(packOutput, lookupProvider, blockTagGenerator.contentsGetter(),
                        existingFileHelper));
    }
}
