package net.oktawia.insaneae2addons.network.packets;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import net.oktawia.insaneae2addons.client.screens.item.BuilderPatternScreen;

public class SendLongStringToClientPacket {
    private final String data;

    public SendLongStringToClientPacket(String data) {
        this.data = data;
    }

    public static void encode(SendLongStringToClientPacket packet, FriendlyByteBuf buf) {
        buf.writeByteArray(packet.data.getBytes(StandardCharsets.UTF_8));
    }

    public static SendLongStringToClientPacket decode(FriendlyByteBuf buf) {
        return new SendLongStringToClientPacket(
                new String(buf.readByteArray(), StandardCharsets.UTF_8));
    }

    public static void handle(SendLongStringToClientPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();

            if (mc.screen instanceof BuilderPatternScreen<?> screen) {
                screen.setProgram(packet.data);
            }
        });
        ctx.setPacketHandled(true);
    }
}
