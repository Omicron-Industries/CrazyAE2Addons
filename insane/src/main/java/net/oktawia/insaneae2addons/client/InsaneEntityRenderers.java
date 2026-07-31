package net.oktawia.insaneae2addons.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.client.renderer.PenroseBlackHoleEntityRenderer;
import net.oktawia.insaneae2addons.defs.regs.InsaneEntityRegistrar;

@Mod.EventBusSubscriber(modid = InsaneAddons.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class InsaneEntityRenderers {

    private InsaneEntityRenderers() {
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(InsaneEntityRegistrar.PENROSE_BLACK_HOLE.get(),
                PenroseBlackHoleEntityRenderer::new);
    }
}
