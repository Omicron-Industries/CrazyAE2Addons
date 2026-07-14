package net.oktawia.insaneae2addons.defs.regs;

import appeng.block.AEBaseBlockItem;
import appeng.block.networking.EnergyCellBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.blocks.AmpereMeterBlock;
import net.oktawia.insaneae2addons.blocks.AutoBuilderBlock;
import net.oktawia.insaneae2addons.blocks.AutoEnchanterBlock;
import net.oktawia.insaneae2addons.blocks.AutoBuilderCreativeSupplyBlock;
import net.oktawia.insaneae2addons.blocks.BrokenPatternProviderBlock;
import net.oktawia.insaneae2addons.blocks.EntropyCradleBlock;
import net.oktawia.insaneae2addons.blocks.EntropyCradleCapacitorBlock;
import net.oktawia.insaneae2addons.blocks.EntropyCradleControllerBlock;
import net.oktawia.insaneae2addons.blocks.EnergyStorageBlock;
import net.oktawia.insaneae2addons.blocks.ResearchCableBlock;
import net.oktawia.insaneae2addons.blocks.ResearchPedestalBottomBlock;
import net.oktawia.insaneae2addons.blocks.ResearchPedestalTopBlock;
import net.oktawia.insaneae2addons.blocks.ResearchStationBlock;
import net.oktawia.insaneae2addons.blocks.ResearchUnitBlock;
import net.oktawia.insaneae2addons.blocks.ResearchUnitFrameBlock;
import net.oktawia.insaneae2addons.items.ResearchBlockItem;

import java.util.List;

public class InsaneBlockRegistrar {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, InsaneAddons.MODID);

    public static final DeferredRegister<Item> BLOCK_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, InsaneAddons.MODID);

    public static List<Block> getBlocks() {
        return BLOCKS.getEntries()
                .stream()
                .map(RegistryObject::get)
                .toList();
    }

    private static final long MB = 1024L * 1024;
    private static final long GB = 1024L * MB;

    private static RegistryObject<EnergyStorageBlock> energyStorage(String id, long maxEnergy) {
        RegistryObject<EnergyStorageBlock> block = BLOCKS.register(id, () -> new EnergyStorageBlock(maxEnergy));
        BLOCK_ITEMS.register(id, () -> new EnergyCellBlockItem(block.get(), new Item.Properties()));
        return block;
    }

    public static final RegistryObject<EnergyStorageBlock> ENERGY_STORAGE_1K = energyStorage("energy_storage_1k", 8 * MB);
    public static final RegistryObject<EnergyStorageBlock> ENERGY_STORAGE_4K = energyStorage("energy_storage_4k", 32 * MB);
    public static final RegistryObject<EnergyStorageBlock> ENERGY_STORAGE_16K = energyStorage("energy_storage_16k", 128 * MB);
    public static final RegistryObject<EnergyStorageBlock> ENERGY_STORAGE_64K = energyStorage("energy_storage_64k", 512 * MB);
    public static final RegistryObject<EnergyStorageBlock> ENERGY_STORAGE_256K = energyStorage("energy_storage_256k", 2048 * MB);
    public static final RegistryObject<EnergyStorageBlock> ENERGY_STORAGE_1M = energyStorage("energy_storage_1m", 8 * GB);
    public static final RegistryObject<EnergyStorageBlock> ENERGY_STORAGE_4M = energyStorage("energy_storage_4m", 32 * GB);
    public static final RegistryObject<EnergyStorageBlock> ENERGY_STORAGE_16M = energyStorage("energy_storage_16m", 128 * GB);
    public static final RegistryObject<EnergyStorageBlock> ENERGY_STORAGE_64M = energyStorage("energy_storage_64m", 512 * GB);
    public static final RegistryObject<EnergyStorageBlock> ENERGY_STORAGE_256M = energyStorage("energy_storage_256m", 2048 * GB);

    public static final List<RegistryObject<EnergyStorageBlock>> ENERGY_STORAGES = List.of(
            ENERGY_STORAGE_1K, ENERGY_STORAGE_4K, ENERGY_STORAGE_16K, ENERGY_STORAGE_64K, ENERGY_STORAGE_256K,
            ENERGY_STORAGE_1M, ENERGY_STORAGE_4M, ENERGY_STORAGE_16M, ENERGY_STORAGE_64M, ENERGY_STORAGE_256M);

    public static final RegistryObject<AmpereMeterBlock> AMPERE_METER_BLOCK =
            BLOCKS.register("ampere_meter", AmpereMeterBlock::new);

    public static final RegistryObject<BlockItem> AMPERE_METER_BLOCK_ITEM =
            BLOCK_ITEMS.register("ampere_meter",
                    () -> new AEBaseBlockItem(AMPERE_METER_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<AutoBuilderBlock> AUTO_BUILDER_BLOCK =
            BLOCKS.register("auto_builder", AutoBuilderBlock::new);

    public static final RegistryObject<BlockItem> AUTO_BUILDER_BLOCK_ITEM =
            BLOCK_ITEMS.register("auto_builder",
                    () -> new AEBaseBlockItem(AUTO_BUILDER_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<AutoBuilderCreativeSupplyBlock> AUTO_BUILDER_CREATIVE_SUPPLY_BLOCK =
            BLOCKS.register("auto_builder_creative_supply", AutoBuilderCreativeSupplyBlock::new);

    public static final RegistryObject<BlockItem> AUTO_BUILDER_CREATIVE_SUPPLY_BLOCK_ITEM =
            BLOCK_ITEMS.register("auto_builder_creative_supply",
                    () -> new AEBaseBlockItem(AUTO_BUILDER_CREATIVE_SUPPLY_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<AutoEnchanterBlock> AUTO_ENCHANTER_BLOCK =
            BLOCKS.register("auto_enchanter", AutoEnchanterBlock::new);

    public static final RegistryObject<BlockItem> AUTO_ENCHANTER_BLOCK_ITEM =
            BLOCK_ITEMS.register("auto_enchanter",
                    () -> new AEBaseBlockItem(AUTO_ENCHANTER_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<BrokenPatternProviderBlock> BROKEN_PATTERN_PROVIDER_BLOCK =
            BLOCKS.register("broken_pattern_provider", BrokenPatternProviderBlock::new);

    public static final RegistryObject<BlockItem> BROKEN_PATTERN_PROVIDER_BLOCK_ITEM =
            BLOCK_ITEMS.register("broken_pattern_provider",
                    () -> new AEBaseBlockItem(BROKEN_PATTERN_PROVIDER_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<ResearchUnitBlock> RESEARCH_UNIT_BLOCK =
            BLOCKS.register("research_unit", ResearchUnitBlock::new);

    public static final RegistryObject<BlockItem> RESEARCH_UNIT_BLOCK_ITEM =
            BLOCK_ITEMS.register("research_unit",
                    () -> new ResearchBlockItem(RESEARCH_UNIT_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<ResearchUnitFrameBlock> RESEARCH_UNIT_FRAME_BLOCK =
            BLOCKS.register("research_unit_frame", ResearchUnitFrameBlock::new);

    public static final RegistryObject<BlockItem> RESEARCH_UNIT_FRAME_BLOCK_ITEM =
            BLOCK_ITEMS.register("research_unit_frame",
                    () -> new ResearchBlockItem(RESEARCH_UNIT_FRAME_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<ResearchPedestalTopBlock> RESEARCH_PEDESTAL_TOP_BLOCK =
            BLOCKS.register("research_pedestal_top", ResearchPedestalTopBlock::new);

    public static final RegistryObject<BlockItem> RESEARCH_PEDESTAL_TOP_BLOCK_ITEM =
            BLOCK_ITEMS.register("research_pedestal_top",
                    () -> new ResearchBlockItem(RESEARCH_PEDESTAL_TOP_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<ResearchPedestalBottomBlock> RESEARCH_PEDESTAL_BOTTOM_BLOCK =
            BLOCKS.register("research_pedestal_bottom", ResearchPedestalBottomBlock::new);

    public static final RegistryObject<BlockItem> RESEARCH_PEDESTAL_BOTTOM_BLOCK_ITEM =
            BLOCK_ITEMS.register("research_pedestal_bottom",
                    () -> new ResearchBlockItem(RESEARCH_PEDESTAL_BOTTOM_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<ResearchCableBlock> RESEARCH_CABLE_BLOCK =
            BLOCKS.register("research_cable", () -> new ResearchCableBlock(ResearchCableBlock.CableColor.NORMAL));

    public static final RegistryObject<BlockItem> RESEARCH_CABLE_BLOCK_ITEM =
            BLOCK_ITEMS.register("research_cable",
                    () -> new ResearchBlockItem(RESEARCH_CABLE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<ResearchCableBlock> RESEARCH_CABLE_PINK_BLOCK =
            BLOCKS.register("research_cable_pink", () -> new ResearchCableBlock(ResearchCableBlock.CableColor.PINK));

    public static final RegistryObject<BlockItem> RESEARCH_CABLE_PINK_BLOCK_ITEM =
            BLOCK_ITEMS.register("research_cable_pink",
                    () -> new ResearchBlockItem(RESEARCH_CABLE_PINK_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<ResearchCableBlock> RESEARCH_CABLE_WHITE_BLOCK =
            BLOCKS.register("research_cable_white", () -> new ResearchCableBlock(ResearchCableBlock.CableColor.WHITE));

    public static final RegistryObject<BlockItem> RESEARCH_CABLE_WHITE_BLOCK_ITEM =
            BLOCK_ITEMS.register("research_cable_white",
                    () -> new ResearchBlockItem(RESEARCH_CABLE_WHITE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<ResearchStationBlock> RESEARCH_STATION_BLOCK =
            BLOCKS.register("research_station", ResearchStationBlock::new);

    public static final RegistryObject<BlockItem> RESEARCH_STATION_BLOCK_ITEM =
            BLOCK_ITEMS.register("research_station",
                    () -> new ResearchBlockItem(RESEARCH_STATION_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<EntropyCradleControllerBlock> ENTROPY_CRADLE_CONTROLLER_BLOCK =
            BLOCKS.register("entropy_cradle_controller", EntropyCradleControllerBlock::new);

    public static final RegistryObject<BlockItem> ENTROPY_CRADLE_CONTROLLER_BLOCK_ITEM =
            BLOCK_ITEMS.register("entropy_cradle_controller",
                    () -> new AEBaseBlockItem(ENTROPY_CRADLE_CONTROLLER_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<EntropyCradleBlock> ENTROPY_CRADLE_BLOCK =
            BLOCKS.register("entropy_cradle", EntropyCradleBlock::new);

    public static final RegistryObject<BlockItem> ENTROPY_CRADLE_BLOCK_ITEM =
            BLOCK_ITEMS.register("entropy_cradle",
                    () -> new AEBaseBlockItem(ENTROPY_CRADLE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<EntropyCradleCapacitorBlock> ENTROPY_CRADLE_CAPACITOR_BLOCK =
            BLOCKS.register("entropy_cradle_capacitor", EntropyCradleCapacitorBlock::new);

    public static final RegistryObject<BlockItem> ENTROPY_CRADLE_CAPACITOR_BLOCK_ITEM =
            BLOCK_ITEMS.register("entropy_cradle_capacitor",
                    () -> new AEBaseBlockItem(ENTROPY_CRADLE_CAPACITOR_BLOCK.get(), new Item.Properties()));

    private InsaneBlockRegistrar() {
    }
}
