package net.oktawia.insaneae2addons.network;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.network.packets.PenroseLaserBeamPacket;
import net.oktawia.insaneae2addons.network.packets.SendLongStringToClientPacket;
import net.oktawia.insaneae2addons.network.packets.SendLongStringToServerPacket;

public final class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            InsaneAddons.makeId("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private static int nextId = 0;

    private NetworkHandler() {
    }

    public static void registerMessages() {
        CHANNEL.messageBuilder(SendLongStringToClientPacket.class, nextId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SendLongStringToClientPacket::encode)
                .decoder(SendLongStringToClientPacket::decode)
                .consumerMainThread(SendLongStringToClientPacket::handle)
                .add();

        CHANNEL.messageBuilder(SendLongStringToServerPacket.class, nextId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SendLongStringToServerPacket::encode)
                .decoder(SendLongStringToServerPacket::decode)
                .consumerMainThread(SendLongStringToServerPacket::handle)
                .add();

        CHANNEL.messageBuilder(PenroseLaserBeamPacket.class, nextId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(PenroseLaserBeamPacket::encode)
                .decoder(PenroseLaserBeamPacket::decode)
                .consumerMainThread(PenroseLaserBeamPacket::handle)
                .add();
    }

    public static void sendToNear(Level level, Vec3 center, double radius, Object packet) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                center.x, center.y, center.z, radius, serverLevel.dimension())), packet);
    }

    public static void sendToPlayer(ServerPlayer player, Object packet) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }
}
