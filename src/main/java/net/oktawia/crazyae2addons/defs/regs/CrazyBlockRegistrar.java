package net.oktawia.crazyae2addons.defs.regs;

import java.util.List;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import appeng.block.AEBaseBlockItem;

import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.blocks.*;
import net.oktawia.crazyae2addons.items.block.CrazyPatternProviderBlockItem;
import net.oktawia.crazyae2addons.items.block.EjectorBlockItem;

public class CrazyBlockRegistrar {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            CrazyAddons.MODID);

    public static List<Block> getBlocks() {
        return BLOCKS.getEntries()
                .stream()
                .map(RegistryObject::get)
                .toList();
    }

    public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            CrazyAddons.MODID);

    public static final RegistryObject<CrazyPatternProviderBlock> CRAZY_PATTERN_PROVIDER_BLOCK = BLOCKS
            .register("crazy_pattern_provider", CrazyPatternProviderBlock::new);

    public static final RegistryObject<BlockItem> CRAZY_PATTERN_PROVIDER_BLOCK_ITEM = BLOCK_ITEMS.register(
            "crazy_pattern_provider",
            () -> new CrazyPatternProviderBlockItem(CRAZY_PATTERN_PROVIDER_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<EjectorBlock> EJECTOR_BLOCK = BLOCKS.register("ejector", EjectorBlock::new);

    public static final RegistryObject<BlockItem> EJECTOR_BLOCK_ITEM = BLOCK_ITEMS.register("ejector",
            () -> new EjectorBlockItem(EJECTOR_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<RecipeFabricatorBlock> RECIPE_FABRICATOR_BLOCK = BLOCKS
            .register("recipe_fabricator", RecipeFabricatorBlock::new);

    public static final RegistryObject<BlockItem> RECIPE_FABRICATOR_BLOCK_ITEM = BLOCK_ITEMS.register(
            "recipe_fabricator",
            () -> new AEBaseBlockItem(RECIPE_FABRICATOR_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<DisplayDatabaseBlock> DISPLAY_DATABASE_BLOCK = BLOCKS
            .register("me_display_database", DisplayDatabaseBlock::new);

    public static final RegistryObject<BlockItem> DISPLAY_DATABASE_BLOCK_ITEM = BLOCK_ITEMS.register(
            "me_display_database",
            () -> new AEBaseBlockItem(DISPLAY_DATABASE_BLOCK.get(), new Item.Properties()));

    private CrazyBlockRegistrar() {
    }
}
