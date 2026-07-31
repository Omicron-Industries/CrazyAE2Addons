package net.oktawia.crazyae2addons.defs;

import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.CrazyConfig;
import net.oktawia.crazyae2addons.defs.regs.CrazyBlockRegistrar;
import net.oktawia.crazyae2addons.defs.regs.CrazyItemRegistrar;
import net.oktawia.crazyae2addons.util.FeatureGates;

public final class CrazyFeatureGates {

    private CrazyFeatureGates() {
    }

    public static void register() {
        CrazyConfig.Common c = CrazyConfig.COMMON;

        FeatureGates.gate(CrazyAddons.MODID, () -> !c.DISPLAY_ENABLED.get(), CrazyItemRegistrar.DISPLAY);
        FeatureGates.gate(CrazyAddons.MODID, () -> !c.EMITTER_TERMINAL_ENABLED.get(),
                CrazyItemRegistrar.EMITTER_TERMINAL);
        FeatureGates.gate(CrazyAddons.MODID,
                () -> !c.EMITTER_TERMINAL_ENABLED.get() || !c.WIRELESS_EMITTER_TERMINAL_ENABLED.get(),
                CrazyItemRegistrar.WIRELESS_EMITTER_TERMINAL);
        FeatureGates.gate(CrazyAddons.MODID, () -> !c.WIRELESS_NOTIFICATION_TERMINAL_ENABLED.get(),
                CrazyItemRegistrar.WIRELESS_NOTIFICATION_TERMINAL);
        FeatureGates.gate(CrazyAddons.MODID, () -> !c.MULTI_LEVEL_EMITTER_ENABLED.get(),
                CrazyItemRegistrar.MULTI_LEVEL_EMITTER);
        FeatureGates.gate(CrazyAddons.MODID, () -> !c.TAG_LEVEL_EMITTER_ENABLED.get(),
                CrazyItemRegistrar.TAG_LEVEL_EMITTER);
        FeatureGates.gate(CrazyAddons.MODID, () -> !c.REDSTONE_EMITTER_TERMINAL_ENABLED.get(),
                CrazyItemRegistrar.REDSTONE_TERMINAL);
        FeatureGates.gate(CrazyAddons.MODID, () -> !c.REDSTONE_EMITTER_TERMINAL_ENABLED.get(),
                CrazyItemRegistrar.REDSTONE_EMITTER);
        FeatureGates.gate(CrazyAddons.MODID,
                () -> !c.REDSTONE_EMITTER_TERMINAL_ENABLED.get() || !c.WIRELESS_REDSTONE_TERMINAL_ENABLED.get(),
                CrazyItemRegistrar.WIRELESS_REDSTONE_TERMINAL);
        FeatureGates.gate(CrazyAddons.MODID, () -> !c.WORMHOLE_ENABLED.get(), CrazyItemRegistrar.WORMHOLE);
        FeatureGates.gate(CrazyAddons.MODID, () -> !c.CPU_PRIORITIES_ENABLED.get(), CrazyItemRegistrar.CPU_PRIO_TUNER);
        FeatureGates.gate(CrazyAddons.MODID, () -> !c.RR_ITEM_P2P_ENABLED.get(), CrazyItemRegistrar.RR_ITEM_P2P);
        FeatureGates.gate(CrazyAddons.MODID, () -> !c.RR_FLUID_P2P_ENABLED.get(), CrazyItemRegistrar.RR_FLUID_P2P);
        FeatureGates.gate(CrazyAddons.MODID, () -> !c.TAG_VIEW_CELL_ENABLED.get(), CrazyItemRegistrar.TAG_VIEW_CELL);
        FeatureGates.gate(CrazyAddons.MODID, () -> !c.PATTERN_MULTIPLIER_ENABLED.get(),
                CrazyItemRegistrar.PATTERN_MULTIPLIER);
        FeatureGates.gate(CrazyAddons.MODID, () -> !c.CRAZY_PATTERN_PROVIDER_PART_ENABLED.get(),
                CrazyItemRegistrar.CRAZY_PATTERN_PROVIDER_PART);
        FeatureGates.gate(CrazyAddons.MODID, () -> !c.CRAZY_PATTERN_PROVIDER_BLOCK_ENABLED.get(),
                CrazyBlockRegistrar.CRAZY_PATTERN_PROVIDER_BLOCK);
        FeatureGates.gate(CrazyAddons.MODID,
                () -> !c.CRAZY_PATTERN_PROVIDER_BLOCK_ENABLED.get() && !c.CRAZY_PATTERN_PROVIDER_PART_ENABLED.get(),
                CrazyItemRegistrar.CRAZY_UPGRADE);
        FeatureGates.gate(CrazyAddons.MODID, () -> !c.EJECTOR_ENABLED.get(), CrazyBlockRegistrar.EJECTOR_BLOCK);
        FeatureGates.gate(CrazyAddons.MODID, () -> !c.RESOURCE_TRACKING_TERMINAL_ENABLED.get(),
                CrazyItemRegistrar.RESOURCE_TRACKING_TERMINAL);
    }
}
