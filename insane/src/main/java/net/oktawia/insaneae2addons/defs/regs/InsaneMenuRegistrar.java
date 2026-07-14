package net.oktawia.insaneae2addons.defs.regs;

import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.oktawia.crazyae2addons.IsModLoaded;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.compat.GregTech.GTAmpereMeterBE;
import net.oktawia.insaneae2addons.entities.AmpereMeterBE;
import net.oktawia.insaneae2addons.entities.AutoBuilderBE;
import net.oktawia.insaneae2addons.entities.AutoEnchanterBE;
import net.oktawia.insaneae2addons.entities.BrokenPatternProviderBE;
import net.oktawia.insaneae2addons.entities.cradle.EntropyCradleControllerBE;
import net.oktawia.insaneae2addons.entities.research.ResearchPedestalBottomBE;
import net.oktawia.insaneae2addons.entities.research.ResearchStationBE;
import net.oktawia.insaneae2addons.entities.research.ResearchUnitBE;
import net.oktawia.insaneae2addons.logic.DataHost;
import net.oktawia.insaneae2addons.logic.autobuilder.BuilderPatternHost;
import net.oktawia.insaneae2addons.menus.block.AmpereMeterMenu;
import net.oktawia.insaneae2addons.menus.block.AutoBuilderMenu;
import net.oktawia.insaneae2addons.menus.block.AutoEnchanterMenu;
import net.oktawia.insaneae2addons.menus.block.BrokenPatternProviderMenu;
import net.oktawia.insaneae2addons.menus.block.EntropyCradleControllerMenu;
import net.oktawia.insaneae2addons.menus.block.ResearchPedestalMenu;
import net.oktawia.insaneae2addons.menus.block.ResearchStationMenu;
import net.oktawia.insaneae2addons.menus.block.ResearchUnitMenu;
import net.oktawia.insaneae2addons.logic.viewcell.ViewCellHost;
import net.oktawia.insaneae2addons.menus.item.BuilderPatternMenu;
import net.oktawia.insaneae2addons.menus.item.BuilderPatternSubMenu;
import net.oktawia.insaneae2addons.menus.item.DataDriveMenu;
import net.oktawia.insaneae2addons.menus.item.NbtViewCellMenu;
import net.oktawia.insaneae2addons.menus.part.EntityTickerMenu;
import net.oktawia.insaneae2addons.menus.part.NbtExportBusMenu;
import net.oktawia.insaneae2addons.menus.part.NbtStorageBusMenu;
import net.oktawia.insaneae2addons.parts.EntityTickerPart;
import net.oktawia.insaneae2addons.parts.NbtExportBusPart;
import net.oktawia.insaneae2addons.parts.NbtStorageBusPart;

public class InsaneMenuRegistrar {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, InsaneAddons.MODID);

    private static <C extends AEBaseMenu, I> RegistryObject<MenuType<C>> reg(
            String id, MenuTypeBuilder.MenuFactory<C, I> factory, Class<I> host) {

        return MENU_TYPES.register(id,
                () -> MenuTypeBuilder.create(factory, host).build(id));
    }

    public static final RegistryObject<MenuType<AmpereMeterMenu>> AMPERE_METER_MENU =
            IsModLoaded.GTCEU
                    ? reg("ampere_meter", AmpereMeterMenu::new, GTAmpereMeterBE.class)
                    : reg("ampere_meter", AmpereMeterMenu::new, AmpereMeterBE.class);

    public static final RegistryObject<MenuType<AutoBuilderMenu>> AUTO_BUILDER_MENU =
            reg("auto_builder_menu", AutoBuilderMenu::new, AutoBuilderBE.class);

    public static final RegistryObject<MenuType<AutoEnchanterMenu>> AUTO_ENCHANTER_MENU =
            reg("auto_enchanter_menu", AutoEnchanterMenu::new, AutoEnchanterBE.class);

    public static final RegistryObject<MenuType<BuilderPatternMenu>> BUILDER_PATTERN_MENU =
            reg("builder_pattern_menu", BuilderPatternMenu::new, BuilderPatternHost.class);

    public static final RegistryObject<MenuType<BuilderPatternSubMenu>> BUILDER_PATTERN_SUBMENU =
            reg("builder_pattern_submenu", BuilderPatternSubMenu::new, BuilderPatternHost.class);

    public static final RegistryObject<MenuType<BrokenPatternProviderMenu>> BROKEN_PATTERN_PROVIDER_MENU =
            reg("broken_pattern_provider_menu", BrokenPatternProviderMenu::new, BrokenPatternProviderBE.class);

    public static final RegistryObject<MenuType<ResearchStationMenu>> RESEARCH_STATION_MENU =
            reg("research_station_menu", ResearchStationMenu::new, ResearchStationBE.class);

    public static final RegistryObject<MenuType<ResearchUnitMenu>> RESEARCH_UNIT_MENU =
            reg("research_unit_menu", ResearchUnitMenu::new, ResearchUnitBE.class);

    public static final RegistryObject<MenuType<ResearchPedestalMenu>> RESEARCH_PEDESTAL_MENU =
            reg("research_pedestal_menu", ResearchPedestalMenu::new, ResearchPedestalBottomBE.class);

    public static final RegistryObject<MenuType<EntropyCradleControllerMenu>> ENTROPY_CRADLE_CONTROLLER_MENU =
            reg("entropy_cradle_controller_menu", EntropyCradleControllerMenu::new, EntropyCradleControllerBE.class);

    public static final RegistryObject<MenuType<DataDriveMenu>> DATA_DRIVE_MENU =
            reg("data_drive_menu", DataDriveMenu::new, DataHost.class);

    public static final RegistryObject<MenuType<NbtViewCellMenu>> NBT_VIEW_CELL_MENU =
            reg("nbt_view_cell_menu", NbtViewCellMenu::new, ViewCellHost.class);

    public static final RegistryObject<MenuType<NbtExportBusMenu>> NBT_EXPORT_BUS_MENU =
            reg("nbt_export_bus_menu", NbtExportBusMenu::new, NbtExportBusPart.class);

    public static final RegistryObject<MenuType<NbtStorageBusMenu>> NBT_STORAGE_BUS_MENU =
            reg("nbt_storage_bus_menu", NbtStorageBusMenu::new, NbtStorageBusPart.class);

    public static final RegistryObject<MenuType<EntityTickerMenu>> ENTITY_TICKER_MENU =
            reg("entity_ticker_menu", EntityTickerMenu::new, EntityTickerPart.class);

    private InsaneMenuRegistrar() {
    }
}
