package net.oktawia.insaneae2addons.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.entities.penrose.PenroseBlackHoleEntity;

@Mod.EventBusSubscriber(
        modid = InsaneAddons.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public final class PenrosePlayerGravity {

    private PenrosePlayerGravity() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.isPaused()) {
            return;
        }

        LocalPlayer player = mc.player;
        if (player == null || player.isSpectator()) {
            return;
        }

        double pullRadius = PenroseBlackHoleEntity.pullRadius();
        Vec3 pos = player.getBoundingBox().getCenter();
        AABB search = player.getBoundingBox().inflate(pullRadius);
        Vec3 pull = Vec3.ZERO;

        for (PenroseBlackHoleEntity hole : player.level().getEntitiesOfClass(PenroseBlackHoleEntity.class, search)) {
            Vec3 toCenter = hole.position().subtract(pos);
            double distance = toCenter.length();
            double accel = PenroseBlackHoleEntity.gravityAccel(distance);
            if (accel <= 0.0 || distance < 1.0e-4) {
                continue;
            }
            pull = pull.add(toCenter.scale(accel / distance));
        }

        if (pull.lengthSqr() > 0.0) {
            player.setDeltaMovement(player.getDeltaMovement().add(pull));
        }
    }
}
