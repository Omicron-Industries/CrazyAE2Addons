package net.oktawia.insaneae2addons.datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.oktawia.crazyae2addons.util.Utils;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneFluidRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;

public class InsaneLangProvider extends LanguageProvider {
    public InsaneLangProvider(PackOutput output, String locale) {
        super(output, InsaneAddons.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        for (var item : InsaneItemRegistrar.getItems()) {
            this.add(item.getDescriptionId(), Utils.toTitle(ForgeRegistries.ITEMS.getKey(item).getPath()));
        }
        for (var block : InsaneBlockRegistrar.getBlocks()) {
            this.add(block.getDescriptionId(), Utils.toTitle(ForgeRegistries.BLOCKS.getKey(block).getPath()));
        }
        for (var entry : LangDefs.values()) {
            this.add(entry.getTranslationKey(), entry.getEnglishText());
        }

        for (var fluid : InsaneFluidRegistrar.getFluids()) {
            this.add("fluid_type." + InsaneAddons.MODID + "." + fluid.name() + "_type", fluid.displayName());
            this.add(fluid.bucket().get().getDescriptionId(), fluid.displayName() + " Bucket");
            this.add(fluid.block().get().getDescriptionId(), fluid.displayName());
        }
    }
}
