package net.oktawia.insaneae2addons.defs;

import net.oktawia.crazyae2addons.util.FeatureGates;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;

public final class InsaneFeatureGates {

    private InsaneFeatureGates() {
    }

    public static void register() {
        InsaneConfig.Common c = InsaneConfig.COMMON;

        FeatureGates.gate(InsaneAddons.MODID, () -> !c.ENTITY_TICKER_ENABLED.get(), InsaneItemRegistrar.ENTITY_TICKER);
        FeatureGates.gate(InsaneAddons.MODID, () -> !c.NBT_VIEW_CELL_ENABLED.get(), InsaneItemRegistrar.NBT_VIEW_CELL);
        FeatureGates.gate(InsaneAddons.MODID, () -> !c.NBT_STORAGE_BUS_ENABLED.get(), InsaneItemRegistrar.NBT_STORAGE_BUS);
        FeatureGates.gate(InsaneAddons.MODID, () -> !c.NBT_EXPORT_BUS_ENABLED.get(), InsaneItemRegistrar.NBT_EXPORT_BUS);
        FeatureGates.gate(InsaneAddons.MODID, () -> !c.BROKEN_PATTERN_PROVIDER_ENABLED.get(),
                InsaneBlockRegistrar.BROKEN_PATTERN_PROVIDER_BLOCK_ITEM);
        FeatureGates.gate(InsaneAddons.MODID, () -> !c.PROVIDER_CARDS_ENABLED.get(), InsaneItemRegistrar.PLAYER_UPGRADE_CARD);
        FeatureGates.gate(InsaneAddons.MODID, () -> !c.PROVIDER_CARDS_ENABLED.get(), InsaneItemRegistrar.AUTOMATION_UPGRADE_CARD);
        FeatureGates.gate(InsaneAddons.MODID, () -> !c.AUTO_ENCHANTER_ENABLED.get(), InsaneBlockRegistrar.AUTO_ENCHANTER_BLOCK_ITEM);

        for (var block : InsaneBlockRegistrar.ENERGY_STORAGES) {
            FeatureGates.gate(InsaneAddons.MODID, () -> !c.ENERGY_STORAGE_ENABLED.get(), block);
        }
    }
}
