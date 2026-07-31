package net.oktawia.insaneae2addons.defs;

import com.glodblock.github.appflux.common.AFItemAndBlock;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;

import net.oktawia.crazyae2addons.IsModLoaded;
import net.oktawia.crazyae2addons.defs.regs.CrazyBlockRegistrar;
import net.oktawia.crazyae2addons.defs.regs.CrazyItemRegistrar;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;

public class UpgradeCards {
    public UpgradeCards(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Upgrades.add(AEItems.SPEED_CARD, InsaneBlockRegistrar.AUTO_BUILDER_BLOCK.get(), 6);
            Upgrades.add(AEItems.CRAFTING_CARD, InsaneBlockRegistrar.AUTO_BUILDER_BLOCK.get(), 1);
            Upgrades.add(AEItems.VOID_CARD, InsaneItemRegistrar.NBT_STORAGE_BUS.get(), 1);
            Upgrades.add(AEItems.SPEED_CARD, InsaneItemRegistrar.ENTITY_TICKER.get(),
                    InsaneConfig.COMMON.ENTITY_TICKER_MAX_SPEED_CARDS.get());

            Upgrades.add(AEItems.SPEED_CARD, InsaneBlockRegistrar.SPAWNER_EXTRACTOR_CONTROLLER_BLOCK.get(), 4);

            Upgrades.add(AEItems.SPEED_CARD, InsaneBlockRegistrar.MOB_FARM_CONTROLLER_BLOCK.get(), 4);
            Upgrades.add(InsaneItemRegistrar.LOOTING_UPGRADE_CARD.get(),
                    InsaneBlockRegistrar.MOB_FARM_CONTROLLER_BLOCK.get(), 4);
            Upgrades.add(InsaneItemRegistrar.EXPERIENCE_UPGRADE_CARD.get(),
                    InsaneBlockRegistrar.MOB_FARM_CONTROLLER_BLOCK.get(), 4);

            Upgrades.add(AEItems.CAPACITY_CARD, InsaneItemRegistrar.MOB_EXPORT_BUS.get(), 5);
            Upgrades.add(AEItems.REDSTONE_CARD, InsaneItemRegistrar.MOB_EXPORT_BUS.get(), 1);
            Upgrades.add(AEItems.SPEED_CARD, InsaneItemRegistrar.MOB_EXPORT_BUS.get(), 4);

            Upgrades.add(AEItems.CAPACITY_CARD, InsaneItemRegistrar.MOB_FORMATION_PLANE.get(), 5);
            Upgrades.add(AEItems.INVERTER_CARD, InsaneItemRegistrar.MOB_FORMATION_PLANE.get(), 1);
            Upgrades.add(AEItems.REDSTONE_CARD, InsaneItemRegistrar.MOB_FORMATION_PLANE.get(), 1);

            Upgrades.add(InsaneItemRegistrar.AUTOMATION_UPGRADE_CARD.get(),
                    CrazyBlockRegistrar.CRAZY_PATTERN_PROVIDER_BLOCK.get(), 1);
            Upgrades.add(InsaneItemRegistrar.PLAYER_UPGRADE_CARD.get(),
                    CrazyBlockRegistrar.CRAZY_PATTERN_PROVIDER_BLOCK.get(), 1);

            Upgrades.add(InsaneItemRegistrar.AUTOMATION_UPGRADE_CARD.get(),
                    CrazyItemRegistrar.CRAZY_PATTERN_PROVIDER_PART.get(), 1);
            Upgrades.add(InsaneItemRegistrar.PLAYER_UPGRADE_CARD.get(),
                    CrazyItemRegistrar.CRAZY_PATTERN_PROVIDER_PART.get(), 1);

            if (IsModLoaded.APP_FLUX) {
                Upgrades.add(AFItemAndBlock.INDUCTION_CARD, InsaneBlockRegistrar.BROKEN_PATTERN_PROVIDER_BLOCK.get(),
                        1);
            }
        });
    }
}
