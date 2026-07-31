package net.oktawia.crazyae2addons.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.defs.LangDefs;

@Mod.EventBusSubscriber(modid = CrazyAddons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class FeatureGates {

    private record Gate(String modid, BooleanSupplier disabled, Supplier<? extends ItemLike> item) {
    }

    private static final List<Gate> GATES = new ArrayList<>();

    private FeatureGates() {
    }

    public static void gate(String modid, BooleanSupplier disabled, Supplier<? extends ItemLike> item) {
        GATES.add(new Gate(modid, disabled, item));
    }

    public static boolean isDisabled(Item item) {
        for (Gate gate : GATES) {
            if (gate.disabled().getAsBoolean() && gate.item().get().asItem() == item) {
                return true;
            }
        }
        return false;
    }

    public static void forEachDisabled(String modid, Consumer<Item> action) {
        for (Gate gate : GATES) {
            if (gate.modid().equals(modid) && gate.disabled().getAsBoolean()) {
                action.accept(gate.item().get().asItem());
            }
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (isDisabled(event.getItemStack().getItem())) {
            event.getToolTip().add(Component.translatable(LangDefs.FEATURE_DISABLED.getTranslationKey())
                    .withStyle(ChatFormatting.RED));
            event.getToolTip().add(Component.translatable(LangDefs.FEATURE_DISABLED_CONFIG.getTranslationKey())
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
