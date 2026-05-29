package net.oktawia.crazyae2addons.parts;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.AEKey;
import appeng.items.parts.PartModels;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractDisplayPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.defs.regs.CrazyMenuRegistrar;
import net.oktawia.crazyae2addons.logic.interfaces.IResourceTrackingTerminalHost;
import net.oktawia.crazyae2addons.tracking.IResourceTrackingService;
import net.oktawia.crazyae2addons.tracking.ResourceSummary;
import net.oktawia.crazyae2addons.tracking.UsageEntry;

import java.util.List;

public class ResourceTrackingTerminalPart extends AbstractDisplayPart implements IResourceTrackingTerminalHost {

    @PartModels
    public static final ResourceLocation MODEL_OFF = CrazyAddons.makeId("part/resource_tracking_terminal_off");

    @PartModels
    public static final ResourceLocation MODEL_ON = CrazyAddons.makeId("part/resource_tracking_terminal_on");

    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    public ResourceTrackingTerminalPart(IPartItem<?> partItem) {
        super(partItem, false);
    }

    @Override
    public boolean onPartActivate(Player player, InteractionHand hand, Vec3 pos) {
        if (!super.onPartActivate(player, hand, pos) && !isClientSide()) {
            MenuOpener.open(CrazyMenuRegistrar.RESOURCE_TRACKING_TERMINAL_MENU.get(), player, MenuLocators.forPart(this));
        }
        return true;
    }

    @Override
    public IPartModel getStaticModels() {
        return selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }

    @Override
    public List<ResourceSummary> getSummaries() {
        var grid = getMainNode().getGrid();
        if (grid == null) return List.of();
        var svc = grid.getService(IResourceTrackingService.class);
        if (svc == null) return List.of();
        return svc.getSummaries();
    }

    @Override
    public List<UsageEntry> getDetails(AEKey key) {
        var grid = getMainNode().getGrid();
        if (grid == null) return List.of();
        var svc = grid.getService(IResourceTrackingService.class);
        if (svc == null) return List.of();
        return svc.getDetails(key);
    }
}
