package net.oktawia.crazyae2addons.network.packets;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;

import net.oktawia.crazyae2addons.client.screens.part.ResourceTrackingTerminalScreen;
import net.oktawia.crazyae2addons.tracking.UsageEntry;

public record ResourceDetailPacket(AEKey key, List<UsageEntry> entries) {

    public static void encode(ResourceDetailPacket pkt, FriendlyByteBuf buf) {
        buf.writeItem(GenericStack.wrapInItemStack(new GenericStack(pkt.key(), 1)));
        buf.writeVarInt(pkt.entries().size());
        for (UsageEntry e : pkt.entries()) {
            buf.writeUtf(e.description());
            buf.writeLong(e.totalAmount());
            boolean hasIcon = e.icon() != null;
            buf.writeBoolean(hasIcon);
            if (hasIcon) {
                buf.writeItem(GenericStack.wrapInItemStack(new GenericStack(e.icon(), 1)));
            }
            boolean hasPos = e.pos() != null;
            buf.writeBoolean(hasPos);
            if (hasPos) {
                buf.writeResourceLocation(e.pos().dimension().location());
                buf.writeBlockPos(e.pos().pos());
            }
        }
    }

    public static ResourceDetailPacket decode(FriendlyByteBuf buf) {
        ItemStack keyStack = buf.readItem();
        GenericStack gs = GenericStack.fromItemStack(keyStack);
        AEKey key = gs != null ? gs.what() : null;

        int size = buf.readVarInt();
        List<UsageEntry> entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String desc = buf.readUtf();
            long amount = buf.readLong();
            AEKey icon = null;
            if (buf.readBoolean()) {
                GenericStack iconGs = GenericStack.fromItemStack(buf.readItem());
                if (iconGs != null)
                    icon = iconGs.what();
            }
            GlobalPos pos = null;
            if (buf.readBoolean()) {
                ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION, buf.readResourceLocation());
                BlockPos blockPos = buf.readBlockPos();
                pos = GlobalPos.of(dim, blockPos);
            }
            entries.add(new UsageEntry(desc, amount, icon, pos));
        }
        return new ResourceDetailPacket(key, entries);
    }

    public static void handle(ResourceDetailPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var screen = Minecraft.getInstance().screen;
            if (screen instanceof ResourceTrackingTerminalScreen<?> s) {
                s.applyDetail(pkt);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
