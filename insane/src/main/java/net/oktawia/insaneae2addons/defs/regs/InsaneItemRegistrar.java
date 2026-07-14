package net.oktawia.insaneae2addons.defs.regs;

import appeng.api.parts.IPart;
import appeng.api.parts.PartModels;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.items.BuilderPatternItem;
import net.oktawia.insaneae2addons.items.DataDrive;
import net.oktawia.insaneae2addons.items.NbtExportBusPartItem;
import net.oktawia.insaneae2addons.items.NbtStorageBusPartItem;
import net.oktawia.insaneae2addons.items.NbtViewCellItem;

import java.util.List;

public class InsaneItemRegistrar {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, InsaneAddons.MODID);

    public static List<Item> getItems() {
        return ITEMS.getEntries()
                .stream()
                .map(RegistryObject::get)
                .toList();
    }

    public static final RegistryObject<BuilderPatternItem> BUILDER_PATTERN =
            ITEMS.register("builder_pattern",
                    () -> new BuilderPatternItem(new Item.Properties()));

    public static final RegistryObject<DataDrive> DATA_DRIVE =
            ITEMS.register("data_drive",
                    () -> new DataDrive(new Item.Properties()));

    public static final RegistryObject<NbtViewCellItem> NBT_VIEW_CELL =
            ITEMS.register("nbt_view_cell",
                    () -> new NbtViewCellItem(new Item.Properties()));

    public static final RegistryObject<NbtExportBusPartItem> NBT_EXPORT_BUS =
            ITEMS.register("nbt_export_bus",
                    () -> new NbtExportBusPartItem(new Item.Properties()));

    public static final RegistryObject<NbtStorageBusPartItem> NBT_STORAGE_BUS =
            ITEMS.register("nbt_storage_bus",
                    () -> new NbtStorageBusPartItem(new Item.Properties()));

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
