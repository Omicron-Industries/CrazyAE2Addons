package net.oktawia.insaneae2addons.defs.regs;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.items.AEBaseItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.defs.LangDefs;

public final class InsaneCreativeTabRegistrar {

    public static final ResourceLocation ID = InsaneAddons.makeId("tab");

    public static final CreativeModeTab TAB = CreativeModeTab.builder()
            .title(Component.translatable(LangDefs.MOD_NAME.getTranslationKey()))
            .icon(() -> new ItemStack(InsaneBlockRegistrar.AUTO_BUILDER_BLOCK.get()))
            .displayItems(InsaneCreativeTabRegistrar::populate)
            .build();

    private static void populate(CreativeModeTab.ItemDisplayParameters ignored, CreativeModeTab.Output out) {
        InsaneItemRegistrar.ITEMS.getEntries().forEach(ro -> push(out, ro.get()));
        InsaneBlockRegistrar.BLOCK_ITEMS.getEntries().forEach(ro -> push(out, ro.get()));
        InsaneFluidRegistrar.ITEMS.getEntries().forEach(ro -> push(out, ro.get()));
    }

    private static void push(CreativeModeTab.Output out, Item item) {
        if (item instanceof AEBaseBlockItem bItem && bItem.getBlock() instanceof AEBaseBlock blk) {
            blk.addToMainCreativeTab(out);
        } else if (item instanceof AEBaseItem baseItem) {
            baseItem.addToMainCreativeTab(out);
        } else {
            out.accept(item);
        }
    }

    private InsaneCreativeTabRegistrar() {
    }
}
