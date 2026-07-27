package net.oktawia.crazyae2addons.network.packets;

import appeng.api.parts.IPartHost;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.oktawia.crazyae2addons.parts.Display;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.function.Supplier;

public record DisplaySyncPacket(BlockPos pos, Direction side, String packed) {

    public static void encode(DisplaySyncPacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeEnum(pkt.side);
        buf.writeUtf(pkt.packed, 65535);
    }

    public static DisplaySyncPacket decode(FriendlyByteBuf buf) {
        return new DisplaySyncPacket(
                buf.readBlockPos(),
                buf.readEnum(Direction.class),
                buf.readUtf(65535)
        );
    }

    public static void handle(DisplaySyncPacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();

        ctx.enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Client.handle(pkt))
        );

        ctx.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static final class Client {

        private static void handle(DisplaySyncPacket pkt) {
            var mc = Minecraft.getInstance();
            if (mc.level == null) {
                return;
            }

            var be = mc.level.getBlockEntity(pkt.pos);
            if (!(be instanceof IPartHost host)) {
                return;
            }

            if (!(host.getPart(pkt.side) instanceof Display part)) {
                return;
            }

            part.resolvedTokens.clear();
            unpackResolvedTokens(pkt.packed, part.resolvedTokens);
        }

        private static void unpackResolvedTokens(String packed, Map<String, String> out) {
            if (packed == null || packed.isEmpty()) {
                return;
            }

            Base64.Decoder decoder = Base64.getUrlDecoder();

            for (String entry : packed.split("\\|", -1)) {
                int eq = entry.indexOf('=');
                if (eq <= 0) {
                    continue;
                }

                String keyPart = entry.substring(0, eq);
                String valuePart = entry.substring(eq + 1);

                try {
                    String key = decodePackedPart(decoder, keyPart);
                    String value = decodePackedPart(decoder, valuePart);

                    if (!key.isEmpty()) {
                        out.put(key, value);
                    }
                } catch (Throwable ignored) {
                    int oldEq = entry.indexOf('=');
                    if (oldEq > 0) {
                        out.put(entry.substring(0, oldEq), entry.substring(oldEq + 1));
                    }
                }
            }
        }

        private static String decodePackedPart(Base64.Decoder decoder, String value) {
            return new String(decoder.decode(value), StandardCharsets.UTF_8);
        }
    }
}