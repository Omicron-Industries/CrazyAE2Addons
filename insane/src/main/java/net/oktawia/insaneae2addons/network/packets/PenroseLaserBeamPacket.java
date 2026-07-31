package net.oktawia.insaneae2addons.network.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import net.oktawia.insaneae2addons.client.renderer.PenroseLaserBeamRenderer;

public class PenroseLaserBeamPacket {

    private final BlockPos pos;
    private final Direction direction;
    private final float length;
    private final float intensity;

    public PenroseLaserBeamPacket(BlockPos pos, Direction direction, float length, float intensity) {
        this.pos = pos;
        this.direction = direction;
        this.length = length;
        this.intensity = intensity;
    }

    public static void encode(PenroseLaserBeamPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeByte(packet.direction.get3DDataValue());
        buf.writeFloat(packet.length);
        buf.writeFloat(packet.intensity);
    }

    public static PenroseLaserBeamPacket decode(FriendlyByteBuf buf) {
        return new PenroseLaserBeamPacket(
                buf.readBlockPos(),
                Direction.from3DDataValue(buf.readByte()),
                buf.readFloat(),
                buf.readFloat());
    }

    public static void handle(PenroseLaserBeamPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> PenroseLaserBeamRenderer.addBeam(
                packet.pos, packet.direction, packet.length, packet.intensity));
        ctx.setPacketHandled(true);
    }
}
