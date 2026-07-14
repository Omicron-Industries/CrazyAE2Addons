package net.oktawia.insaneae2addons.mixins;

import appeng.api.networking.security.IActionSource;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.me.helpers.MachineSource;
import appeng.me.helpers.PlayerSource;
import net.oktawia.crazyae2addons.entities.CrazyPatternProviderBE;
import net.oktawia.crazyae2addons.parts.CrazyPatternProviderPart;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;
import net.oktawia.insaneae2addons.interfaces.IProviderSourceFilter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = PatternProviderLogic.class, remap = false)
public class MixinPatternProviderLogic implements IProviderSourceFilter {

    @Shadow @Final private PatternProviderLogicHost host;

    @Unique
    @Override
    public boolean insaneAE2Addons$allowSource(@Nullable IActionSource src) {
        if (src == null || !InsaneConfig.COMMON.PROVIDER_CARDS_ENABLED.get()) {
            return true;
        }
        IUpgradeInventory upgrades;
        if (host instanceof CrazyPatternProviderBE be) {
            upgrades = be.getUpgrades();
        } else if (host instanceof CrazyPatternProviderPart part) {
            upgrades = part.getUpgrades();
        } else {
            return true;
        }
        if (upgrades.isInstalled(InsaneItemRegistrar.AUTOMATION_UPGRADE_CARD.get())) {
            return src instanceof MachineSource;
        }
        if (upgrades.isInstalled(InsaneItemRegistrar.PLAYER_UPGRADE_CARD.get())) {
            return src instanceof PlayerSource;
        }
        return true;
    }
}
