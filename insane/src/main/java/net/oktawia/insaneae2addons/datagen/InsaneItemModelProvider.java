package net.oktawia.insaneae2addons.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;

public class InsaneItemModelProvider extends ItemModelProvider {
    public InsaneItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, InsaneAddons.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (var item : InsaneItemRegistrar.getItems()) {
            simpleItem(item);
        }
    }

    private ItemModelBuilder simpleItem(Item item) {
        return withExistingParent(ForgeRegistries.ITEMS.getKey(item).getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(InsaneAddons.MODID, "item/" + ForgeRegistries.ITEMS.getKey(item).getPath()));
    }
}
