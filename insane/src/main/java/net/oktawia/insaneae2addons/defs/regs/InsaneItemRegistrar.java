package net.oktawia.insaneae2addons.defs.regs;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.items.BuilderPatternItem;
import net.oktawia.insaneae2addons.items.DataDrive;

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

    private InsaneItemRegistrar() {
    }
}
