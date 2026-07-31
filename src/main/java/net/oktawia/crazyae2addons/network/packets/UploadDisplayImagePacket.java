package net.oktawia.crazyae2addons.network.packets;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import net.oktawia.crazyae2addons.menus.part.DisplayImagesSubMenu;

public record UploadDisplayImagePacket(
        String sourceName,
        byte[] pngBytes,
        int width,
        int height) {

    public static final int MAX_NAME_LEN = 256;
    public static final int MAX_IMAGE_BYTES = 1024 * 1024;
    public static final int MAX_IMAGE_DIM = 512;

    public static void encode(UploadDisplayImagePacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.sourceName, MAX_NAME_LEN);
        buf.writeByteArray(pkt.pngBytes);
        buf.writeVarInt(pkt.width);
        buf.writeVarInt(pkt.height);
    }

    public static UploadDisplayImagePacket decode(FriendlyByteBuf buf) {
        return new UploadDisplayImagePacket(
                buf.readUtf(MAX_NAME_LEN),
                buf.readByteArray(MAX_IMAGE_BYTES),
                buf.readVarInt(),
                buf.readVarInt());
    }

    public static void handle(UploadDisplayImagePacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();

        ctx.enqueueWork(() -> {
            var player = ctx.getSender();
            if (player == null) {
                return;
            }

            if (!(player.containerMenu instanceof DisplayImagesSubMenu menu)) {
                return;
            }

            if (pkt.sourceName() == null || pkt.sourceName().isBlank()) {
                return;
            }

            if (pkt.pngBytes() == null || pkt.pngBytes().length == 0 || pkt.pngBytes().length > MAX_IMAGE_BYTES) {
                return;
            }

            if (pkt.width() <= 0 || pkt.height() <= 0) {
                return;
            }

            if (pkt.width() > MAX_IMAGE_DIM || pkt.height() > MAX_IMAGE_DIM) {
                return;
            }

            if (!isPngWithinBounds(pkt.pngBytes())) {
                return;
            }

            menu.addImage(pkt.sourceName(), pkt.pngBytes(), pkt.width(), pkt.height());
        });

        ctx.setPacketHandled(true);
    }

    private static boolean isPngWithinBounds(byte[] bytes) {
        if (bytes.length < 24) {
            return false;
        }
        boolean signature = (bytes[0] & 0xFF) == 0x89 && bytes[1] == 'P' && bytes[2] == 'N' && bytes[3] == 'G'
                && (bytes[4] & 0xFF) == 0x0D && (bytes[5] & 0xFF) == 0x0A
                && (bytes[6] & 0xFF) == 0x1A && (bytes[7] & 0xFF) == 0x0A;
        if (!signature) {
            return false;
        }
        if (bytes[12] != 'I' || bytes[13] != 'H' || bytes[14] != 'D' || bytes[15] != 'R') {
            return false;
        }
        int realWidth = readBigEndianInt(bytes, 16);
        int realHeight = readBigEndianInt(bytes, 20);
        return realWidth > 0 && realHeight > 0 && realWidth <= MAX_IMAGE_DIM && realHeight <= MAX_IMAGE_DIM;
    }

    private static int readBigEndianInt(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 24)
                | ((bytes[offset + 1] & 0xFF) << 16)
                | ((bytes[offset + 2] & 0xFF) << 8)
                | (bytes[offset + 3] & 0xFF);
    }
}
