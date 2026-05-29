package net.oktawia.crazyae2addons.network.packets;

import appeng.api.stacks.GenericStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.oktawia.crazyae2addons.client.screens.part.ResourceTrackingTerminalScreen;
import net.oktawia.crazyae2addons.menus.part.ResourceTrackingTerminalMenu;
import net.oktawia.crazyae2addons.tracking.ResourceSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record ResourceListPacket(List<ResourceSummary> summaries) {

    public static void encode(ResourceListPacket pkt, FriendlyByteBuf buf) {
        buf.writeVarInt(pkt.summaries().size());
        for (ResourceSummary s : pkt.summaries()) {
            buf.writeItem(GenericStack.wrapInItemStack(new GenericStack(s.key(), 1)));
            buf.writeLong(s.totalConsumed());
            buf.writeLong(s.perMinute());
        }
    }

    public static ResourceListPacket decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<ResourceSummary> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ItemStack stack = buf.readItem();
            long total = buf.readLong();
            long perMin = buf.readLong();
            GenericStack gs = GenericStack.fromItemStack(stack);
            if (gs != null) {
                list.add(new ResourceSummary(gs.what(), total, perMin));
            }
        }
        return new ResourceListPacket(list);
    }

    public static void handle(ResourceListPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var screen = Minecraft.getInstance().screen;
            if (screen instanceof ResourceTrackingTerminalScreen<?> s) {
                s.applyList(pkt);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
