package net.oktawia.insaneae2addons.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneFluidRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;

import java.util.Set;

public class InsaneItemModelProvider extends ItemModelProvider {
    private static final Set<String> STATIC_ITEM_MODELS =
            Set.of("ampere_meter", "auto_builder", "broken_pattern_provider");
    private static final Set<String> CABLE_ITEMS =
            Set.of("research_cable", "research_cable_black", "research_cable_white");

    public InsaneItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, InsaneAddons.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (var item : InsaneItemRegistrar.getItems()) {
            simpleItem(item);
        }

        withExistingParent(
                ForgeRegistries.ITEMS.getKey(InsaneFluidRegistrar.RESEARCH_FLUID_BUCKET.get()).getPath(),
                new ResourceLocation("item/generated"))
                .texture("layer0", new ResourceLocation(InsaneAddons.MODID, "item/research_bucket"));

        for (var blockItem : InsaneBlockRegistrar.BLOCK_ITEMS.getEntries()) {
            String name = blockItem.getId().getPath();
            if (STATIC_ITEM_MODELS.contains(name)) {
                continue;
            }
            String blockModel = CABLE_ITEMS.contains(name) ? name + "_inv" : name;
            withExistingParent(name, new ResourceLocation(InsaneAddons.MODID, "block/" + blockModel));
        }
    }

    private ItemModelBuilder simpleItem(Item item) {
        return withExistingParent(ForgeRegistries.ITEMS.getKey(item).getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(InsaneAddons.MODID, "item/" + ForgeRegistries.ITEMS.getKey(item).getPath()));
    }
}
