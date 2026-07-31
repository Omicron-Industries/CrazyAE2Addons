package net.oktawia.insaneae2addons.defs.regs;

import java.util.List;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import appeng.api.parts.IPart;
import appeng.api.parts.PartModels;
import appeng.items.materials.UpgradeCardItem;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import appeng.items.storage.StorageTier;

import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.items.DataDrive;
import net.oktawia.insaneae2addons.items.EntityTickerPartItem;
import net.oktawia.insaneae2addons.items.MultiblockBuilderItem;
import net.oktawia.insaneae2addons.items.XpShardItem;
import net.oktawia.insaneae2addons.items.autobuilder.BuilderPatternItem;
import net.oktawia.insaneae2addons.items.mobstorage.MobAnnihilationPlanePartItem;
import net.oktawia.insaneae2addons.items.mobstorage.MobExportBusPartItem;
import net.oktawia.insaneae2addons.items.mobstorage.MobFormationPlanePartItem;
import net.oktawia.insaneae2addons.items.mobstorage.MobKeySelectorItem;
import net.oktawia.insaneae2addons.items.mobstorage.MobStorageCell;
import net.oktawia.insaneae2addons.items.nbt.NbtExportBusPartItem;
import net.oktawia.insaneae2addons.items.nbt.NbtStorageBusPartItem;
import net.oktawia.insaneae2addons.items.nbt.NbtViewCellItem;

public class InsaneItemRegistrar {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            InsaneAddons.MODID);

    public static List<Item> getItems() {
        return ITEMS.getEntries()
                .stream()
                .map(RegistryObject::get)
                .toList();
    }

    public static final RegistryObject<BuilderPatternItem> BUILDER_PATTERN = ITEMS.register("builder_pattern",
            () -> new BuilderPatternItem(new Item.Properties()));

    public static final RegistryObject<DataDrive> DATA_DRIVE = ITEMS.register("data_drive",
            () -> new DataDrive(new Item.Properties()));

    public static final RegistryObject<MultiblockBuilderItem> MULTIBLOCK_BUILDER = ITEMS.register("multiblock_builder",
            () -> new MultiblockBuilderItem(new Item.Properties()));

    public static final RegistryObject<NbtViewCellItem> NBT_VIEW_CELL = ITEMS.register("nbt_view_cell",
            () -> new NbtViewCellItem(new Item.Properties()));

    public static final RegistryObject<NbtExportBusPartItem> NBT_EXPORT_BUS = ITEMS.register("nbt_export_bus",
            () -> new NbtExportBusPartItem(new Item.Properties()));

    public static final RegistryObject<NbtStorageBusPartItem> NBT_STORAGE_BUS = ITEMS.register("nbt_storage_bus",
            () -> new NbtStorageBusPartItem(new Item.Properties()));

    public static final RegistryObject<EntityTickerPartItem> ENTITY_TICKER = ITEMS.register("entity_ticker",
            () -> new EntityTickerPartItem(new Item.Properties()));

    public static final RegistryObject<XpShardItem> XP_SHARD = ITEMS.register("xp_shard",
            () -> new XpShardItem(new Item.Properties()));

    public static final RegistryObject<MobKeySelectorItem> MOB_KEY_SELECTOR = ITEMS.register("mob_key_selector",
            () -> new MobKeySelectorItem(new Item.Properties()));

    public static final RegistryObject<MobAnnihilationPlanePartItem> MOB_ANNIHILATION_PLANE = ITEMS.register(
            "mob_annihilation_plane",
            () -> new MobAnnihilationPlanePartItem(new Item.Properties()));

    public static final RegistryObject<MobExportBusPartItem> MOB_EXPORT_BUS = ITEMS.register("mob_export_bus",
            () -> new MobExportBusPartItem(new Item.Properties()));

    public static final RegistryObject<MobFormationPlanePartItem> MOB_FORMATION_PLANE = ITEMS.register(
            "mob_formation_plane",
            () -> new MobFormationPlanePartItem(new Item.Properties()));

    public static final RegistryObject<Item> MOB_CELL_HOUSING = ITEMS.register("mob_cell_housing",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<MobStorageCell> MOB_CELL_1K = ITEMS.register("mob_storage_cell_1k",
            () -> new MobStorageCell(new Item.Properties().stacksTo(1), StorageTier.SIZE_1K, MOB_CELL_HOUSING.get()));

    public static final RegistryObject<MobStorageCell> MOB_CELL_4K = ITEMS.register("mob_storage_cell_4k",
            () -> new MobStorageCell(new Item.Properties().stacksTo(1), StorageTier.SIZE_4K, MOB_CELL_HOUSING.get()));

    public static final RegistryObject<MobStorageCell> MOB_CELL_16K = ITEMS.register("mob_storage_cell_16k",
            () -> new MobStorageCell(new Item.Properties().stacksTo(1), StorageTier.SIZE_16K, MOB_CELL_HOUSING.get()));

    public static final RegistryObject<MobStorageCell> MOB_CELL_64K = ITEMS.register("mob_storage_cell_64k",
            () -> new MobStorageCell(new Item.Properties().stacksTo(1), StorageTier.SIZE_64K, MOB_CELL_HOUSING.get()));

    public static final RegistryObject<MobStorageCell> MOB_CELL_256K = ITEMS.register("mob_storage_cell_256k",
            () -> new MobStorageCell(new Item.Properties().stacksTo(1), StorageTier.SIZE_256K, MOB_CELL_HOUSING.get()));

    public static final RegistryObject<UpgradeCardItem> PLAYER_UPGRADE_CARD = ITEMS.register("player_upgrade_card",
            () -> new UpgradeCardItem(new Item.Properties()));

    public static final RegistryObject<UpgradeCardItem> AUTOMATION_UPGRADE_CARD = ITEMS.register(
            "automation_upgrade_card",
            () -> new UpgradeCardItem(new Item.Properties()));

    public static final RegistryObject<UpgradeCardItem> LOOTING_UPGRADE_CARD = ITEMS.register("looting_upgrade_card",
            () -> new UpgradeCardItem(new Item.Properties()));

    public static final RegistryObject<UpgradeCardItem> EXPERIENCE_UPGRADE_CARD = ITEMS.register(
            "experience_upgrade_card",
            () -> new UpgradeCardItem(new Item.Properties()));

    public static final RegistryObject<Item> SUPER_SINGULARITY = ITEMS.register("super_singularity",
            () -> new Item(new Item.Properties()));

    public static void registerPartModels() {
        for (Item item : getItems()) {
            if (item instanceof PartItem<?> partItem) {
                Class<?> partClass = partItem.getPartClass();
                if (partClass != null) {
                    PartModels.registerModels(
                            PartModelsHelper.createModels(partClass.asSubclass(IPart.class)));
                }
            }
        }
    }

    private InsaneItemRegistrar() {
    }
}
