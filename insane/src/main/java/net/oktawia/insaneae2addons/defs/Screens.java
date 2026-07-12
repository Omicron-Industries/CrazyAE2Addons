package net.oktawia.insaneae2addons.defs;

import appeng.init.client.InitScreens;
import net.oktawia.insaneae2addons.client.screens.block.AmpereMeterScreen;
import net.oktawia.insaneae2addons.client.screens.block.AutoBuilderScreen;
import net.oktawia.insaneae2addons.client.screens.block.BrokenPatternProviderScreen;
import net.oktawia.insaneae2addons.client.screens.block.ResearchPedestalScreen;
import net.oktawia.insaneae2addons.client.screens.block.ResearchStationScreen;
import net.oktawia.insaneae2addons.client.screens.block.ResearchUnitScreen;
import net.oktawia.insaneae2addons.client.screens.item.BuilderPatternScreen;
import net.oktawia.insaneae2addons.client.screens.item.BuilderPatternSubScreen;
import net.oktawia.insaneae2addons.client.screens.item.DataDriveScreen;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.menus.block.AmpereMeterMenu;
import net.oktawia.insaneae2addons.menus.block.AutoBuilderMenu;
import net.oktawia.insaneae2addons.menus.block.BrokenPatternProviderMenu;
import net.oktawia.insaneae2addons.menus.block.ResearchPedestalMenu;
import net.oktawia.insaneae2addons.menus.block.ResearchStationMenu;
import net.oktawia.insaneae2addons.menus.block.ResearchUnitMenu;
import net.oktawia.insaneae2addons.menus.item.BuilderPatternMenu;
import net.oktawia.insaneae2addons.menus.item.BuilderPatternSubMenu;
import net.oktawia.insaneae2addons.menus.item.DataDriveMenu;

public final class Screens {

    public static void register() {
        InitScreens.register(
                InsaneMenuRegistrar.AMPERE_METER_MENU.get(),
                AmpereMeterScreen<AmpereMeterMenu>::new,
                "/screens/ampere_meter.json"
        );
        InitScreens.register(
                InsaneMenuRegistrar.AUTO_BUILDER_MENU.get(),
                AutoBuilderScreen<AutoBuilderMenu>::new,
                "/screens/auto_builder.json"
        );
        InitScreens.register(
                InsaneMenuRegistrar.BUILDER_PATTERN_MENU.get(),
                BuilderPatternScreen<BuilderPatternMenu>::new,
                "/screens/builder_pattern.json"
        );
        InitScreens.register(
                InsaneMenuRegistrar.BUILDER_PATTERN_SUBMENU.get(),
                BuilderPatternSubScreen<BuilderPatternSubMenu>::new,
                "/screens/builder_pattern_subscreen.json"
        );
        InitScreens.register(
                InsaneMenuRegistrar.BROKEN_PATTERN_PROVIDER_MENU.get(),
                BrokenPatternProviderScreen<BrokenPatternProviderMenu>::new,
                "/screens/broken_pattern_provider.json"
        );
        InitScreens.register(
                InsaneMenuRegistrar.RESEARCH_STATION_MENU.get(),
                ResearchStationScreen<ResearchStationMenu>::new,
                "/screens/research_station.json"
        );
        InitScreens.register(
                InsaneMenuRegistrar.RESEARCH_UNIT_MENU.get(),
                ResearchUnitScreen<ResearchUnitMenu>::new,
                "/screens/research_unit.json"
        );
        InitScreens.register(
                InsaneMenuRegistrar.RESEARCH_PEDESTAL_MENU.get(),
                ResearchPedestalScreen<ResearchPedestalMenu>::new,
                "/screens/research_pedestal.json"
        );
        InitScreens.register(
                InsaneMenuRegistrar.DATA_DRIVE_MENU.get(),
                DataDriveScreen<DataDriveMenu>::new,
                "/screens/data_drive.json"
        );
    }

    private Screens() {
    }
}
