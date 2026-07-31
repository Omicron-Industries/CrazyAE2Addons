package net.oktawia.crazyae2addons.menus.part;

import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.PacketDistributor;

import appeng.api.stacks.AEKey;
import appeng.menu.AEBaseMenu;

import net.oktawia.crazyae2addons.defs.regs.CrazyMenuRegistrar;
import net.oktawia.crazyae2addons.logic.interfaces.IResourceTrackingTerminalHost;
import net.oktawia.crazyae2addons.network.NetworkHandler;
import net.oktawia.crazyae2addons.network.packets.ResourceDetailPacket;
import net.oktawia.crazyae2addons.network.packets.ResourceListPacket;
import net.oktawia.crazyae2addons.parts.ResourceTrackingTerminalPart;
import net.oktawia.crazyae2addons.tracking.ResourceSummary;
import net.oktawia.crazyae2addons.tracking.UsageEntry;

public class ResourceTrackingTerminalMenu extends AEBaseMenu {

    private static final String ACTION_DETAIL = "resource_tracking.detail";
    private static final int SYNC_INTERVAL = 20;

    private final IResourceTrackingTerminalHost trackingHost;

    private int tickCounter = SYNC_INTERVAL;
    private int lastSummaryHash = Integer.MIN_VALUE;
    private List<ResourceSummary> lastSentSummaries = List.of();

    public List<ResourceSummary> clientSummaries = List.of();
    public List<UsageEntry> clientDetails = List.of();
    public AEKey clientDetailKey = null;

    public ResourceTrackingTerminalMenu(int id, Inventory ip, ResourceTrackingTerminalPart host) {
        this(CrazyMenuRegistrar.RESOURCE_TRACKING_TERMINAL_MENU.get(), id, ip, host);
    }

    protected ResourceTrackingTerminalMenu(MenuType<?> type, int id, Inventory ip, IResourceTrackingTerminalHost host) {
        super(type, id, ip, host);
        this.trackingHost = host;
        registerClientAction(ACTION_DETAIL, Integer.class, this::requestDetail);
        createPlayerInventorySlots(ip);
    }

    public void requestDetail(int index) {
        if (isClientSide()) {
            sendClientAction(ACTION_DETAIL, index);
            return;
        }
        if (!(getPlayer() instanceof ServerPlayer serverPlayer))
            return;
        if (index < 0 || index >= lastSentSummaries.size())
            return;

        AEKey key = lastSentSummaries.get(index).key();
        List<UsageEntry> details = trackingHost.getDetails(key);
        NetworkHandler.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> serverPlayer),
                new ResourceDetailPacket(key, details));
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (isClientSide())
            return;
        if (!(getPlayer() instanceof ServerPlayer serverPlayer))
            return;

        tickCounter++;
        if (tickCounter < SYNC_INTERVAL)
            return;
        tickCounter = 0;

        List<ResourceSummary> summaries = trackingHost.getSummaries();
        int hash = computeHash(summaries);
        if (hash == lastSummaryHash)
            return;
        lastSummaryHash = hash;
        lastSentSummaries = summaries;

        NetworkHandler.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> serverPlayer),
                new ResourceListPacket(summaries));
    }

    public void applyList(ResourceListPacket pkt) {
        this.clientSummaries = pkt.summaries();
    }

    public void applyDetail(ResourceDetailPacket pkt) {
        this.clientDetailKey = pkt.key();
        this.clientDetails = pkt.entries();
    }

    private static int computeHash(List<ResourceSummary> summaries) {
        int h = summaries.size();
        for (ResourceSummary s : summaries) {
            h = h * 31 + s.key().hashCode();
            h = h * 31 + Long.hashCode(s.perMinute());
        }
        return h;
    }
}
