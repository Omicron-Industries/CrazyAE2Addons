package net.oktawia.insaneae2addons.client.screens.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import appeng.client.gui.style.ScreenStyle;

import net.oktawia.crazyae2addons.client.screens.AbstractMultiblockControllerScreen;
import net.oktawia.insaneae2addons.blocks.penrose.PortablePenroseSphereControllerBlock;
import net.oktawia.insaneae2addons.client.misc.BlackHoleWidget;
import net.oktawia.insaneae2addons.client.misc.StructureIssuesWidget;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.entities.penrose.PortablePenroseSphereControllerBE;
import net.oktawia.insaneae2addons.logic.penrose.PenroseCurveModel;
import net.oktawia.insaneae2addons.menus.block.PortablePenroseSphereControllerMenu;

public class PortablePenroseSphereControllerScreen<C extends PortablePenroseSphereControllerMenu>
        extends AbstractMultiblockControllerScreen<C> {

    private final BlackHoleWidget blackHole;
    private final StructureIssuesWidget issues;

    public PortablePenroseSphereControllerScreen(C menu, Inventory playerInventory, Component title,
            ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.blackHole = new BlackHoleWidget();
        this.widgets.add("bh", this.blackHole);

        this.issues = new StructureIssuesWidget();
        this.issues.setTitle(Component.translatable(LangDefs.PENROSE_ISSUES_TITLE.getTranslationKey()));
        this.issues.setHint(Component.translatable(LangDefs.PENROSE_ISSUES_HINT.getTranslationKey()));
        this.widgets.add("issues", this.issues);
    }

    @Override
    protected Component previewTooltip(boolean previewEnabled) {
        return Component.translatable((previewEnabled
                ? LangDefs.HIDE_PREVIEW
                : LangDefs.SHOW_PREVIEW).getTranslationKey());
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        PortablePenroseSphereControllerBE host = getMenu().getHost();
        boolean active = host.isBlackHoleActive();

        BlockState state = host.getBlockState();
        boolean formed = state.hasProperty(PortablePenroseSphereControllerBlock.FORMED)
                && state.getValue(PortablePenroseSphereControllerBlock.FORMED);

        this.blackHole.visible = formed;
        this.issues.visible = !formed;

        if (formed) {
            this.blackHole.setView(buildView(host, active));
        } else {
            this.issues.setLines(decodeIssues(host.getStructureIssues()));
        }
    }

    private static List<Component> decodeIssues(String[] rawIssues) {
        List<Component> lines = new ArrayList<>(rawIssues.length);

        for (String raw : rawIssues) {
            String[] parts = raw.split("\\|");
            if (parts.length < 3) {
                continue;
            }

            LangDefs message = parts[0].equals(PortablePenroseSphereControllerBE.ISSUE_VENTS)
                    ? LangDefs.PENROSE_ISSUE_TOO_MANY_VENTS
                    : LangDefs.PENROSE_ISSUE_MISSING;

            lines.add(Component.translatable(message.getTranslationKey(), parts[2], blockName(parts[1])));
        }

        return lines;
    }

    private static Component blockName(String blockId) {
        Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(blockId));
        return block == null ? Component.literal(blockId) : block.getName();
    }

    private static BlackHoleWidget.View buildView(PortablePenroseSphereControllerBE host, boolean active) {
        long initial = host.getInitialMass();
        long max = host.getMaxMass();
        double maxHeat = host.getMaxHeat();

        double massPosition = max > initial ? (double) (host.getBlackHoleMass() - initial) / (max - initial) : 0.0;
        double heatPosition = maxHeat > 0.0 ? host.getHeat() / maxHeat : 0.0;

        return new BlackHoleWidget.View(
                active,
                host.getBlackHoleMass(),
                initial,
                max,
                host.getHeat(),
                maxHeat,
                host.getDiskMassSingu(),
                host.getLastGeneratedFePerTick(),
                host.getLastConsumedFePerTick(),
                host.getStoredEnergy(),
                host.getStoredEnergyInDisk(),
                host.getLastSecondMassDelta(),
                active ? PenroseCurveModel.mass().valueAt(massPosition) : 0.0,
                active ? PenroseCurveModel.heat().valueAt(heatPosition) : 0.0);
    }
}
