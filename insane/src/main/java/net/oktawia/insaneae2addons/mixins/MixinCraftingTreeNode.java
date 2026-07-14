package net.oktawia.insaneae2addons.mixins;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.crafting.ICraftingSimulationRequester;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.crafting.CraftingCalculation;
import appeng.crafting.CraftingTreeNode;
import appeng.me.service.CraftingService;
import net.oktawia.crazyae2addons.entities.CrazyPatternProviderBE;
import net.oktawia.crazyae2addons.parts.CrazyPatternProviderPart;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(value = CraftingTreeNode.class, remap = false)
public abstract class MixinCraftingTreeNode {

    @Shadow @Final private CraftingCalculation job;

    @Redirect(
            method = "buildChildPatterns",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/api/networking/crafting/ICraftingService;getCraftingFor(Lappeng/api/stacks/AEKey;)Ljava/util/Collection;"
            )
    )
    private Collection<IPatternDetails> insaneAE2Addons$filterPatterns(ICraftingService craftingService, AEKey what) {
        Collection<IPatternDetails> original = craftingService.getCraftingFor(what);
        if (original.isEmpty() || !InsaneConfig.COMMON.PROVIDER_CARDS_ENABLED.get()) {
            return original;
        }

        ICraftingSimulationRequester simRequester = ((CraftingCalculationAccessor) this.job).getSimRequester();
        if (simRequester == null) {
            return original;
        }
        IActionSource actionSource = simRequester.getActionSource();
        if (actionSource == null) {
            return original;
        }
        IGridNode gridNode = simRequester.getGridNode();
        if (gridNode == null) {
            return original;
        }
        IGrid grid = gridNode.getGrid();
        if (grid == null) {
            return original;
        }

        boolean isPlayer = actionSource.player().isPresent();
        boolean isMachine = actionSource.machine().isPresent();

        List<IPatternDetails> filtered = new ArrayList<>();
        for (IPatternDetails details : original) {
            if (insaneAE2Addons$allowed(details, craftingService, grid, isPlayer, isMachine)) {
                filtered.add(details);
            }
        }
        return filtered;
    }

    @Unique
    private boolean insaneAE2Addons$allowed(IPatternDetails details, ICraftingService craftingService,
                                            IGrid grid, boolean isPlayer, boolean isMachine) {
        if (!(craftingService instanceof CraftingService ae2Service)) {
            return true;
        }

        List<ICraftingProvider> providers = new ArrayList<>();
        ae2Service.getProviders(details).forEach(providers::add);
        if (providers.isEmpty()) {
            return false;
        }

        for (IGridNode node : grid.getNodes()) {
            ICraftingProvider providerOnNode = node.getService(ICraftingProvider.class);
            if (providerOnNode == null || !providers.contains(providerOnNode)) {
                continue;
            }

            IUpgradeInventory upgrades;
            if (node.getOwner() instanceof CrazyPatternProviderBE be) {
                upgrades = be.getUpgrades();
            } else if (node.getOwner() instanceof CrazyPatternProviderPart part) {
                upgrades = part.getUpgrades();
            } else {
                return true;
            }

            boolean hasAutomationCard = upgrades.isInstalled(InsaneItemRegistrar.AUTOMATION_UPGRADE_CARD.get());
            boolean hasPlayerCard = upgrades.isInstalled(InsaneItemRegistrar.PLAYER_UPGRADE_CARD.get());

            if (hasAutomationCard && !hasPlayerCard) {
                if (isMachine && !isPlayer) {
                    return true;
                }
            } else if (hasPlayerCard && !hasAutomationCard) {
                if (isPlayer) {
                    return true;
                }
            } else {
                return true;
            }
        }

        return false;
    }
}
