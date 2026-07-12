package net.oktawia.insaneae2addons.client.screens;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.defs.LangDefs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InsaneConfigScreen {

    private InsaneConfigScreen() {
    }

    public static Screen create(Screen parent) {
        ConfigBuilder b = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(t(LangDefs.CONFIG_TITLE));

        b.setSavingRunnable(() -> InsaneConfig.COMMON_SPEC.save());

        ConfigEntryBuilder eb = b.entryBuilder();
        InsaneConfig.Common cfg = InsaneConfig.COMMON;

        ConfigCategory root = b.getOrCreateCategory(t(LangDefs.CONFIG_CATEGORY_SETTINGS));

        addSection(root, eb, LangDefs.CONFIG_SECTION_AUTOBUILDER, entries -> {
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_COST_MULTIPLIER, cfg.AUTOBUILDER_COST_MULT.get(), 5, 0,
                            cfg.AUTOBUILDER_COST_MULT::set,
                            LangDefs.CONFIG_DESC_COST_MULTIPLIER
                    ));
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_MINE_DELAY, cfg.AUTOBUILDER_MINE_DELAY.get(), 2, 0,
                            cfg.AUTOBUILDER_MINE_DELAY::set,
                            LangDefs.CONFIG_DESC_MINE_DELAY
                    ));
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_SPEED, cfg.AUTOBUILDER_SPEED.get(), 128, 0,
                            cfg.AUTOBUILDER_SPEED::set,
                            LangDefs.CONFIG_DESC_SPEED
                    ));
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_PREVIEW_LIMIT, cfg.AUTOBUILDER_PREVIEW_LIMIT.get(), 8192, 0,
                            cfg.AUTOBUILDER_PREVIEW_LIMIT::set,
                            LangDefs.CONFIG_DESC_PREVIEW_LIMIT
                    ));
                },
                LangDefs.CONFIG_SECTION_AUTOBUILDER_DESC
        );

        addSection(root, eb, LangDefs.CONFIG_SECTION_RESEARCH, entries -> {
                    entries.add(bool(eb, LangDefs.CONFIG_ENTRY_RESEARCH_REQUIRED, cfg.RESEARCH_REQUIRED.get(), true,
                            cfg.RESEARCH_REQUIRED::set,
                            LangDefs.CONFIG_DESC_RESEARCH_REQUIRED
                    ));
                    entries.add(stringList(eb, LangDefs.CONFIG_ENTRY_RESEARCH_UNIT_EXTRA_Q_BLOCKS,
                            cfg.RESEARCH_UNIT_EXTRA_Q_BLOCKS.get(),
                            cfg.RESEARCH_UNIT_EXTRA_Q_BLOCKS::set,
                            LangDefs.CONFIG_DESC_RESEARCH_UNIT_EXTRA_Q_BLOCKS
                    ));
                },
                LangDefs.CONFIG_SECTION_RESEARCH_DESC
        );

        return b.build();
    }

    private static void addSection(
            ConfigCategory root,
            ConfigEntryBuilder eb,
            LangDefs name,
            Consumer<List<AbstractConfigListEntry>> entriesBuilder,
            LangDefs... tooltip
    ) {
        List<AbstractConfigListEntry> entries = new ArrayList<>();
        entriesBuilder.accept(entries);

        root.addEntry(eb.startSubCategory(t(name), entries)
                .setTooltip(tooltip(tooltip))
                .setExpanded(false)
                .build());
    }

    private static AbstractConfigListEntry integer(
            ConfigEntryBuilder eb,
            LangDefs name,
            int value,
            int defaultValue,
            int min,
            Consumer<Integer> saveConsumer,
            LangDefs... tooltip
    ) {
        return eb.startIntField(t(name), value)
                .setDefaultValue(defaultValue)
                .setMin(min)
                .setTooltip(tooltip(tooltip))
                .setSaveConsumer(saveConsumer)
                .build();
    }

    private static AbstractConfigListEntry bool(
            ConfigEntryBuilder eb,
            LangDefs name,
            boolean value,
            boolean defaultValue,
            Consumer<Boolean> saveConsumer,
            LangDefs... tooltip
    ) {
        return eb.startBooleanToggle(t(name), value)
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip(tooltip))
                .setSaveConsumer(saveConsumer)
                .build();
    }

    private static AbstractConfigListEntry stringList(
            ConfigEntryBuilder eb,
            LangDefs name,
            List<? extends String> value,
            Consumer<List<String>> saveConsumer,
            LangDefs... tooltip
    ) {
        return eb.startStrList(t(name), new ArrayList<>(value))
                .setDefaultValue(new ArrayList<>())
                .setTooltip(tooltip(tooltip))
                .setSaveConsumer(saveConsumer)
                .build();
    }

    private static Component t(LangDefs def) {
        return Component.translatable(def.getTranslationKey());
    }

    private static Component[] tooltip(LangDefs... defs) {
        List<Component> out = new ArrayList<>();

        for (LangDefs def : defs) {
            addWrappedTooltipLine(out, t(def));
        }

        return out.toArray(Component[]::new);
    }

    private static void addWrappedTooltipLine(List<Component> out, Component component) {
        String text = component.getString();
        if (text == null || text.isBlank()) {
            out.add(Component.empty());
            return;
        }

        int maxWidth = getTooltipMaxWidth();
        var font = Minecraft.getInstance().font;

        StringBuilder line = new StringBuilder();

        for (String word : text.split(" ")) {
            if (word.isBlank()) {
                continue;
            }

            String candidate = line.length() == 0
                    ? word
                    : line + " " + word;

            if (font.width(candidate) <= maxWidth || line.length() == 0) {
                line.setLength(0);
                line.append(candidate);
                continue;
            }

            out.add(Component.literal(line.toString()));
            line.setLength(0);
            line.append(word);
        }

        if (line.length() > 0) {
            out.add(Component.literal(line.toString()));
        }
    }

    private static int getTooltipMaxWidth() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.screen == null) {
            return 240;
        }

        return Math.max(160, Math.min(240, minecraft.screen.width - 100));
    }
}
