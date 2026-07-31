package net.oktawia.insaneae2addons.defs;

import appeng.client.gui.implementations.IOBusScreen;
import appeng.init.client.InitScreens;

import net.oktawia.insaneae2addons.client.screens.block.AmpereMeterScreen;
import net.oktawia.insaneae2addons.client.screens.block.AutoBuilderScreen;
import net.oktawia.insaneae2addons.client.screens.block.AutoEnchanterScreen;
import net.oktawia.insaneae2addons.client.screens.block.BrokenPatternProviderScreen;
import net.oktawia.insaneae2addons.client.screens.block.EntropyCradleControllerScreen;
import net.oktawia.insaneae2addons.client.screens.block.MobFarmControllerScreen;
import net.oktawia.insaneae2addons.client.screens.block.PenroseEmitterScreen;
import net.oktawia.insaneae2addons.client.screens.block.PenroseHawkingVentScreen;
import net.oktawia.insaneae2addons.client.screens.block.PenroseHeatVentScreen;
import net.oktawia.insaneae2addons.client.screens.block.PenroseInjectionPortScreen;
import net.oktawia.insaneae2addons.client.screens.block.PenroseLaserScreen;
import net.oktawia.insaneae2addons.client.screens.block.PortablePenroseSphereControllerScreen;
import net.oktawia.insaneae2addons.client.screens.block.ReinforcedMatterCondenserScreen;
import net.oktawia.insaneae2addons.client.screens.block.ResearchPedestalScreen;
import net.oktawia.insaneae2addons.client.screens.block.ResearchStationScreen;
import net.oktawia.insaneae2addons.client.screens.block.ResearchUnitScreen;
import net.oktawia.insaneae2addons.client.screens.block.SpawnerExtractorControllerScreen;
import net.oktawia.insaneae2addons.client.screens.item.BuilderPatternScreen;
import net.oktawia.insaneae2addons.client.screens.item.BuilderPatternSubScreen;
import net.oktawia.insaneae2addons.client.screens.item.DataDriveScreen;
import net.oktawia.insaneae2addons.client.screens.item.MobKeySelectorScreen;
import net.oktawia.insaneae2addons.client.screens.item.NbtViewCellScreen;
import net.oktawia.insaneae2addons.client.screens.part.EntityTickerScreen;
import net.oktawia.insaneae2addons.client.screens.part.MobFormationPlaneScreen;
import net.oktawia.insaneae2addons.client.screens.part.NbtExportBusScreen;
import net.oktawia.insaneae2addons.client.screens.part.NbtStorageBusScreen;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.menus.block.AmpereMeterMenu;
import net.oktawia.insaneae2addons.menus.block.AutoBuilderMenu;
import net.oktawia.insaneae2addons.menus.block.BrokenPatternProviderMenu;
import net.oktawia.insaneae2addons.menus.block.EntropyCradleControllerMenu;
import net.oktawia.insaneae2addons.menus.block.MobFarmControllerMenu;
import net.oktawia.insaneae2addons.menus.block.PenroseEmitterMenu;
import net.oktawia.insaneae2addons.menus.block.PenroseHawkingVentMenu;
import net.oktawia.insaneae2addons.menus.block.PenroseHeatVentMenu;
import net.oktawia.insaneae2addons.menus.block.PenroseInjectionPortMenu;
import net.oktawia.insaneae2addons.menus.block.PenroseLaserMenu;
import net.oktawia.insaneae2addons.menus.block.PortablePenroseSphereControllerMenu;
import net.oktawia.insaneae2addons.menus.block.ReinforcedMatterCondenserMenu;
import net.oktawia.insaneae2addons.menus.block.ResearchPedestalMenu;
import net.oktawia.insaneae2addons.menus.block.ResearchStationMenu;
import net.oktawia.insaneae2addons.menus.block.ResearchUnitMenu;
import net.oktawia.insaneae2addons.menus.block.SpawnerExtractorControllerMenu;
import net.oktawia.insaneae2addons.menus.item.BuilderPatternMenu;
import net.oktawia.insaneae2addons.menus.item.BuilderPatternSubMenu;
import net.oktawia.insaneae2addons.menus.item.DataDriveMenu;
import net.oktawia.insaneae2addons.menus.item.MobKeySelectorMenu;
import net.oktawia.insaneae2addons.menus.item.NbtViewCellMenu;
import net.oktawia.insaneae2addons.menus.part.EntityTickerMenu;
import net.oktawia.insaneae2addons.menus.part.MobFormationPlaneMenu;
import net.oktawia.insaneae2addons.menus.part.NbtExportBusMenu;
import net.oktawia.insaneae2addons.menus.part.NbtStorageBusMenu;

public final class Screens {

    public static void register() {
        InitScreens.register(
                InsaneMenuRegistrar.AMPERE_METER_MENU.get(),
                AmpereMeterScreen<AmpereMeterMenu>::new,
                "/screens/ampere_meter.json");
        InitScreens.register(
                InsaneMenuRegistrar.AUTO_BUILDER_MENU.get(),
                AutoBuilderScreen<AutoBuilderMenu>::new,
                "/screens/auto_builder.json");
        InitScreens.register(
                InsaneMenuRegistrar.AUTO_ENCHANTER_MENU.get(),
                AutoEnchanterScreen::new,
                "/screens/auto_enchanter.json");
        InitScreens.register(
                InsaneMenuRegistrar.BUILDER_PATTERN_MENU.get(),
                BuilderPatternScreen<BuilderPatternMenu>::new,
                "/screens/builder_pattern.json");
        InitScreens.register(
                InsaneMenuRegistrar.BUILDER_PATTERN_SUBMENU.get(),
                BuilderPatternSubScreen<BuilderPatternSubMenu>::new,
                "/screens/builder_pattern_subscreen.json");
        InitScreens.register(
                InsaneMenuRegistrar.BROKEN_PATTERN_PROVIDER_MENU.get(),
                BrokenPatternProviderScreen<BrokenPatternProviderMenu>::new,
                "/screens/broken_pattern_provider.json");
        InitScreens.register(
                InsaneMenuRegistrar.RESEARCH_STATION_MENU.get(),
                ResearchStationScreen<ResearchStationMenu>::new,
                "/screens/research_station.json");
        InitScreens.register(
                InsaneMenuRegistrar.RESEARCH_UNIT_MENU.get(),
                ResearchUnitScreen<ResearchUnitMenu>::new,
                "/screens/research_unit.json");
        InitScreens.register(
                InsaneMenuRegistrar.RESEARCH_PEDESTAL_MENU.get(),
                ResearchPedestalScreen<ResearchPedestalMenu>::new,
                "/screens/research_pedestal.json");
        InitScreens.register(
                InsaneMenuRegistrar.ENTROPY_CRADLE_CONTROLLER_MENU.get(),
                EntropyCradleControllerScreen<EntropyCradleControllerMenu>::new,
                "/screens/entropy_cradle_controller.json");
        InitScreens.register(
                InsaneMenuRegistrar.MOB_FARM_CONTROLLER_MENU.get(),
                MobFarmControllerScreen<MobFarmControllerMenu>::new,
                "/screens/mob_farm_controller.json");
        InitScreens.register(
                InsaneMenuRegistrar.SPAWNER_EXTRACTOR_CONTROLLER_MENU.get(),
                SpawnerExtractorControllerScreen<SpawnerExtractorControllerMenu>::new,
                "/screens/spawner_extractor_controller.json");
        InitScreens.register(
                InsaneMenuRegistrar.PORTABLE_PENROSE_SPHERE_CONTROLLER_MENU.get(),
                PortablePenroseSphereControllerScreen<PortablePenroseSphereControllerMenu>::new,
                "/screens/portable_penrose_sphere_controller.json");
        InitScreens.register(
                InsaneMenuRegistrar.PENROSE_INJECTION_PORT_MENU.get(),
                PenroseInjectionPortScreen<PenroseInjectionPortMenu>::new,
                "/screens/penrose_injection_port.json");
        InitScreens.register(
                InsaneMenuRegistrar.PENROSE_HEAT_VENT_MENU.get(),
                PenroseHeatVentScreen<PenroseHeatVentMenu>::new,
                "/screens/penrose_heat_vent.json");
        InitScreens.register(
                InsaneMenuRegistrar.PENROSE_LASER_MENU.get(),
                PenroseLaserScreen<PenroseLaserMenu>::new,
                "/screens/penrose_laser.json");
        InitScreens.register(
                InsaneMenuRegistrar.PENROSE_HAWKING_VENT_MENU.get(),
                PenroseHawkingVentScreen<PenroseHawkingVentMenu>::new,
                "/screens/penrose_hawking_vent.json");
        InitScreens.register(
                InsaneMenuRegistrar.PENROSE_MASS_EMITTER_MENU.get(),
                PenroseEmitterScreen<PenroseEmitterMenu>::new,
                "/screens/penrose_mass_emitter.json");
        InitScreens.register(
                InsaneMenuRegistrar.PENROSE_HEAT_EMITTER_MENU.get(),
                PenroseEmitterScreen<PenroseEmitterMenu>::new,
                "/screens/penrose_heat_emitter.json");
        InitScreens.register(
                InsaneMenuRegistrar.REINFORCED_MATTER_CONDENSER_MENU.get(),
                ReinforcedMatterCondenserScreen<ReinforcedMatterCondenserMenu>::new,
                "/screens/reinforced_matter_condenser.json");
        InitScreens.register(
                InsaneMenuRegistrar.DATA_DRIVE_MENU.get(),
                DataDriveScreen<DataDriveMenu>::new,
                "/screens/data_drive.json");
        InitScreens.register(
                InsaneMenuRegistrar.NBT_VIEW_CELL_MENU.get(),
                NbtViewCellScreen<NbtViewCellMenu>::new,
                "/screens/nbt_view_cell.json");
        InitScreens.register(
                InsaneMenuRegistrar.NBT_EXPORT_BUS_MENU.get(),
                NbtExportBusScreen<NbtExportBusMenu>::new,
                "/screens/nbt_export_bus.json");
        InitScreens.register(
                InsaneMenuRegistrar.NBT_STORAGE_BUS_MENU.get(),
                NbtStorageBusScreen<NbtStorageBusMenu>::new,
                "/screens/nbt_storage_bus.json");
        InitScreens.register(
                InsaneMenuRegistrar.ENTITY_TICKER_MENU.get(),
                EntityTickerScreen<EntityTickerMenu>::new,
                "/screens/entity_ticker.json");
        InitScreens.register(
                InsaneMenuRegistrar.MOB_KEY_SELECTOR_MENU.get(),
                MobKeySelectorScreen<MobKeySelectorMenu>::new,
                "/screens/mob_key_selector.json");
        InitScreens.register(
                InsaneMenuRegistrar.MOB_EXPORT_BUS_MENU.get(),
                IOBusScreen::new,
                "/screens/mob_export_bus.json");
        InitScreens.register(
                InsaneMenuRegistrar.MOB_FORMATION_PLANE_MENU.get(),
                MobFormationPlaneScreen<MobFormationPlaneMenu>::new,
                "/screens/mob_formation_plane.json");
    }

    private Screens() {
    }
}
