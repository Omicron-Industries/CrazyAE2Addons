package net.oktawia.crazyae2addons.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.oktawia.crazyae2addons.client.misc.DisplayImageClientCache;

import java.util.function.Supplier;

public record DisplayImageDataPacket(String imageId, byte[] pngBytes) {

    private static final int MAX_ID_LEN = 128;

    public static void encode(DisplayImageDataPacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.imageId == null ? "" : pkt.imageId, MAX_ID_LEN);
        buf.writeByteArray(pkt.pngBytes == null ? new byte[0] : pkt.pngBytes);
    }

    public static DisplayImageDataPacket decode(FriendlyByteBuf buf) {
        return new DisplayImageDataPacket(
                buf.readUtf(MAX_ID_LEN),
                buf.readByteArray(UploadDisplayImagePacket.MAX_IMAGE_BYTES)
        );
    }

    public static void handle(DisplayImageDataPacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();

        ctx.enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Client.handle(pkt))
        );

        ctx.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static final class Client {
        private static void handle(DisplayImageDataPacket pkt) {
            DisplayImageClientCache.accept(pkt.imageId(), pkt.pngBytes());
        }
    }
}
