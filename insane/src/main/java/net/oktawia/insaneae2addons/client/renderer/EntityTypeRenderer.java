package net.oktawia.insaneae2addons.client.renderer;

import appeng.api.client.AEKeyRenderHandler;
import appeng.api.client.AEKeyRendering;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.oktawia.insaneae2addons.mobstorage.MobKey;
import net.oktawia.insaneae2addons.mobstorage.MobKeyType;

import java.util.HashMap;
import java.util.Map;

public class EntityTypeRenderer implements AEKeyRenderHandler<MobKey> {

    private static final Map<EntityType<?>, Entity> ENTITY_CACHE = new HashMap<>();
    private static final Map<EntityType<?>, Long> LAST_TICK = new HashMap<>();
    private static final float YAW_PER_TICK = 4.0f;

    public static void initialize() {
        AEKeyRendering.register(MobKeyType.TYPE, MobKey.class, new EntityTypeRenderer());
    }

    private static Entity cachedEntity(MobKey key, Level level) {
        return ENTITY_CACHE.computeIfAbsent(key.getEntityType(), type -> type.create(level));
    }

    private static void advance(Entity ent, EntityType<?> type, long now) {
        Long last = LAST_TICK.get(type);
        if (last != null && last == now) {
            return;
        }
        LAST_TICK.put(type, now);
        try {
            ent.tick();
        } catch (Exception ignored) {
        }
        if (ent instanceof LivingEntity le) {
            float next = le.yBodyRot + YAW_PER_TICK;
            le.yBodyRotO = le.yBodyRot;
            le.yHeadRotO = le.yHeadRot;
            le.yRotO = le.getYRot();
            le.yBodyRot = next;
            le.yHeadRot = next;
            le.setYRot(next);
        } else {
            ent.yRotO = ent.getYRot();
            ent.setYRot(ent.getYRot() + YAW_PER_TICK);
        }
    }

    @Override
    public void drawInGui(Minecraft mc, GuiGraphics gui, int x, int y, MobKey key) {
        if (mc.level == null) {
            return;
        }
        Entity ent = cachedEntity(key, mc.level);
        if (ent == null) {
            return;
        }
        advance(ent, key.getEntityType(), mc.level.getGameTime());

        float pt = mc.getFrameTime();
        float avail = 16f - 2f;
        float scale = Math.min(avail / ent.getBbWidth(), avail / ent.getBbHeight()) * 0.8f;

        PoseStack pose = gui.pose();
        pose.pushPose();
        pose.translate(x + 8.0, y + 14.0, 100.0);
        pose.mulPose(Axis.XP.rotationDegrees(-22.5f));
        pose.scale(scale, -scale, scale);

        int light = LightTexture.pack(12, 12);
        var buffers = mc.renderBuffers().bufferSource();
        mc.getEntityRenderDispatcher().render(ent, 0, 0, 0, ent.getYRot(), pt, pose, buffers, light);
        buffers.endBatch();
        pose.popPose();
    }

    @Override
    public void drawOnBlockFace(PoseStack poseStack, MultiBufferSource buffers, MobKey key,
                                float scale, int combinedLight, Level level) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        Entity ent = cachedEntity(key, mc.level);
        if (ent == null) {
            return;
        }
        advance(ent, key.getEntityType(), mc.level.getGameTime());

        float pt = mc.getFrameTime();
        float myScale = Math.min(16f / ent.getBbWidth(), 16f / ent.getBbHeight()) * 0.2f;

        poseStack.pushPose();
        poseStack.translate(0, -0.15, 0.01);
        poseStack.scale(myScale * 0.1f, myScale * 0.1f, 0.0001f);
        mc.getEntityRenderDispatcher().render(ent, 0, 0, 0, ent.getYRot(), pt, poseStack, buffers, combinedLight);
        poseStack.popPose();
    }

    @Override
    public Component getDisplayName(MobKey key) {
        return key.getEntityType().getDescription();
    }
}
