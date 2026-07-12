package net.oktawia.insaneae2addons.client.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.logic.research.ResearchStatus;

public final class ResearchStatusText {

    private ResearchStatusText() {
    }

    public static Component of(ResearchStatus status) {
        LangDefs entry = LangDefs.valueOf("RESEARCH_STATUS_" + status.name());
        return Component.translatable(entry.getTranslationKey()).withStyle(colorFor(status));
    }

    private static ChatFormatting colorFor(ResearchStatus status) {
        return switch (status) {
            case READY, WORKING -> ChatFormatting.GREEN;
            case IDLE -> ChatFormatting.GRAY;
            case FLUID_LOW -> ChatFormatting.YELLOW;
            default -> ChatFormatting.RED;
        };
    }
}
