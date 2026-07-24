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
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_RESEARCH_UNIT_COMPUTATION_DIVISOR, cfg.RESEARCH_UNIT_COMPUTATION_DIVISOR.get(), 16, 1,
                            cfg.RESEARCH_UNIT_COMPUTATION_DIVISOR::set,
                            LangDefs.CONFIG_DESC_RESEARCH_UNIT_COMPUTATION_DIVISOR
                    ));
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_RESEARCH_UNIT_COMPUTATION_POWER_COST, cfg.RESEARCH_UNIT_COMPUTATION_POWER_COST.get(), 64, 0,
                            cfg.RESEARCH_UNIT_COMPUTATION_POWER_COST::set,
                            LangDefs.CONFIG_DESC_RESEARCH_UNIT_COMPUTATION_POWER_COST
                    ));
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_RESEARCH_UNIT_FLUID_DIVISOR, cfg.RESEARCH_UNIT_FLUID_DIVISOR.get(), 4, 1,
                            cfg.RESEARCH_UNIT_FLUID_DIVISOR::set,
                            LangDefs.CONFIG_DESC_RESEARCH_UNIT_FLUID_DIVISOR
                    ));
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_RESEARCH_UNIT_FLUID_BUFFER, cfg.RESEARCH_UNIT_FLUID_BUFFER.get(), 64_000, 1,
                            cfg.RESEARCH_UNIT_FLUID_BUFFER::set,
                            LangDefs.CONFIG_DESC_RESEARCH_UNIT_FLUID_BUFFER
                    ));
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_RESEARCH_UNIT_POWER_BUFFER, cfg.RESEARCH_UNIT_POWER_BUFFER.get(), 200_000, 1,
                            cfg.RESEARCH_UNIT_POWER_BUFFER::set,
                            LangDefs.CONFIG_DESC_RESEARCH_UNIT_POWER_BUFFER
                    ));
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_RESEARCH_STATION_PEDESTAL_RANGE, cfg.RESEARCH_STATION_PEDESTAL_RANGE.get(), 3, 1,
                            cfg.RESEARCH_STATION_PEDESTAL_RANGE::set,
                            LangDefs.CONFIG_DESC_RESEARCH_STATION_PEDESTAL_RANGE
                    ));
                },
                LangDefs.CONFIG_SECTION_RESEARCH_DESC
        );

        addSection(root, eb, LangDefs.CONFIG_SECTION_ENTROPY_CRADLE, entries -> {
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_CRADLE_CAPACITY, cfg.CRADLE_CAPACITY.get(), 600_000_000, 1,
                            cfg.CRADLE_CAPACITY::set,
                            LangDefs.CONFIG_DESC_CRADLE_CAPACITY
                    ));
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_CRADLE_CHARGING_SPEED, cfg.CRADLE_CHARGING_SPEED.get(), 10_000_000, 1,
                            cfg.CRADLE_CHARGING_SPEED::set,
                            LangDefs.CONFIG_DESC_CRADLE_CHARGING_SPEED
                    ));
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_CRADLE_COST, cfg.CRADLE_COST.get(), 600_000_000, 1,
                            cfg.CRADLE_COST::set,
                            LangDefs.CONFIG_DESC_CRADLE_COST
                    ));
                },
                LangDefs.CONFIG_SECTION_ENTROPY_CRADLE_DESC
        );

        addSection(root, eb, LangDefs.CONFIG_SECTION_PENROSE, entries -> {
                    entries.add(bool(eb, LangDefs.CONFIG_ENTRY_PENROSE_ENABLED, cfg.PENROSE_SPHERE_ENABLED.get(), true,
                            cfg.PENROSE_SPHERE_ENABLED::set,
                            LangDefs.CONFIG_DESC_PENROSE_ENABLED
                    ));
                },
                LangDefs.CONFIG_SECTION_PENROSE_DESC
        );

        addSection(root, eb, LangDefs.CONFIG_SECTION_MOB_FARM, entries -> {
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_MOB_FARM_BASE_SPEED, cfg.MOB_FARM_BASE_SPEED.get(), 16, 1,
                            cfg.MOB_FARM_BASE_SPEED::set,
                            LangDefs.CONFIG_DESC_MOB_FARM_BASE_SPEED
                    ));
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_MOB_FARM_SPEED_PER_CARD, cfg.MOB_FARM_SPEED_PER_CARD.get(), 12, 0,
                            cfg.MOB_FARM_SPEED_PER_CARD::set,
                            LangDefs.CONFIG_DESC_MOB_FARM_SPEED_PER_CARD
                    ));
                },
                LangDefs.CONFIG_SECTION_MOB_FARM_DESC
        );

        addSection(root, eb, LangDefs.CONFIG_SECTION_SPAWNER_EXTRACTOR, entries -> {
                    entries.add(bool(eb, LangDefs.CONFIG_ENTRY_SPAWNER_EXTRACTOR_PEACEFUL, cfg.SPAWNER_EXTRACTOR_PEACEFUL.get(), true,
                            cfg.SPAWNER_EXTRACTOR_PEACEFUL::set,
                            LangDefs.CONFIG_DESC_SPAWNER_EXTRACTOR_PEACEFUL
                    ));
                },
                LangDefs.CONFIG_SECTION_SPAWNER_EXTRACTOR_DESC
        );

        addSection(root, eb, LangDefs.CONFIG_SECTION_ENTITY_TICKER, entries -> {
                    entries.add(bool(eb, LangDefs.CONFIG_ENTRY_ENTITY_TICKER_ENABLED, cfg.ENTITY_TICKER_ENABLED.get(), true,
                            cfg.ENTITY_TICKER_ENABLED::set,
                            LangDefs.CONFIG_DESC_ENTITY_TICKER_ENABLED
                    ));
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_ENTITY_TICKER_COST, cfg.ENTITY_TICKER_COST.get(), 512, 0,
                            cfg.ENTITY_TICKER_COST::set,
                            LangDefs.CONFIG_DESC_ENTITY_TICKER_COST
                    ));
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_ENTITY_TICKER_MAX_SPEED_CARDS, cfg.ENTITY_TICKER_MAX_SPEED_CARDS.get(), 8, 0,
                            cfg.ENTITY_TICKER_MAX_SPEED_CARDS::set,
                            LangDefs.CONFIG_DESC_ENTITY_TICKER_MAX_SPEED_CARDS
                    ));
                    entries.add(stringList(eb, LangDefs.CONFIG_ENTRY_ENTITY_TICKER_BLACKLIST,
                            cfg.ENTITY_TICKER_BLACKLIST.get(),
                            cfg.ENTITY_TICKER_BLACKLIST::set,
                            LangDefs.CONFIG_DESC_ENTITY_TICKER_BLACKLIST
                    ));
                },
                LangDefs.CONFIG_SECTION_ENTITY_TICKER_DESC
        );

        addSection(root, eb, LangDefs.CONFIG_SECTION_NBT_TOOLS, entries -> {
                    entries.add(bool(eb, LangDefs.CONFIG_ENTRY_NBT_VIEW_CELL, cfg.NBT_VIEW_CELL_ENABLED.get(), true,
                            cfg.NBT_VIEW_CELL_ENABLED::set,
                            LangDefs.CONFIG_DESC_NBT_VIEW_CELL
                    ));
                    entries.add(bool(eb, LangDefs.CONFIG_ENTRY_NBT_STORAGE_BUS, cfg.NBT_STORAGE_BUS_ENABLED.get(), true,
                            cfg.NBT_STORAGE_BUS_ENABLED::set,
                            LangDefs.CONFIG_DESC_NBT_STORAGE_BUS
                    ));
                    entries.add(bool(eb, LangDefs.CONFIG_ENTRY_NBT_EXPORT_BUS, cfg.NBT_EXPORT_BUS_ENABLED.get(), true,
                            cfg.NBT_EXPORT_BUS_ENABLED::set,
                            LangDefs.CONFIG_DESC_NBT_EXPORT_BUS
                    ));
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_NBT_EXPORT_BUS_TRANSFER_FACTOR, cfg.NBT_EXPORT_BUS_TRANSFER_FACTOR.get(), 4, 1,
                            cfg.NBT_EXPORT_BUS_TRANSFER_FACTOR::set,
                            LangDefs.CONFIG_DESC_NBT_EXPORT_BUS_TRANSFER_FACTOR
                    ));
                },
                LangDefs.CONFIG_SECTION_NBT_TOOLS_DESC
        );

        addSection(root, eb, LangDefs.CONFIG_SECTION_AMPERE_METER, entries -> {
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_AMPERE_METER_INACTIVITY_RESET, cfg.AMPERE_METER_INACTIVITY_RESET_TICKS.get(), 10, 0,
                            cfg.AMPERE_METER_INACTIVITY_RESET_TICKS::set,
                            LangDefs.CONFIG_DESC_AMPERE_METER_INACTIVITY_RESET
                    ));
                },
                LangDefs.CONFIG_SECTION_AMPERE_METER_DESC
        );

        addSection(root, eb, LangDefs.CONFIG_SECTION_BROKEN_PATTERN_PROVIDER, entries -> {
                    entries.add(bool(eb, LangDefs.CONFIG_ENTRY_BROKEN_PATTERN_PROVIDER_ENABLED, cfg.BROKEN_PATTERN_PROVIDER_ENABLED.get(), true,
                            cfg.BROKEN_PATTERN_PROVIDER_ENABLED::set,
                            LangDefs.CONFIG_DESC_BROKEN_PATTERN_PROVIDER_ENABLED
                    ));
                },
                LangDefs.CONFIG_SECTION_BROKEN_PATTERN_PROVIDER_DESC
        );

        addSection(root, eb, LangDefs.CONFIG_SECTION_PROVIDER_CARDS, entries -> {
                    entries.add(bool(eb, LangDefs.CONFIG_ENTRY_PROVIDER_CARDS_ENABLED, cfg.PROVIDER_CARDS_ENABLED.get(), true,
                            cfg.PROVIDER_CARDS_ENABLED::set,
                            LangDefs.CONFIG_DESC_PROVIDER_CARDS_ENABLED
                    ));
                },
                LangDefs.CONFIG_SECTION_PROVIDER_CARDS_DESC
        );

        addSection(root, eb, LangDefs.CONFIG_SECTION_AUTO_ENCHANTER, entries -> {
                    entries.add(bool(eb, LangDefs.CONFIG_ENTRY_AUTO_ENCHANTER_ENABLED, cfg.AUTO_ENCHANTER_ENABLED.get(), true,
                            cfg.AUTO_ENCHANTER_ENABLED::set,
                            LangDefs.CONFIG_DESC_AUTO_ENCHANTER_ENABLED
                    ));
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_AUTO_ENCHANTER_COST, cfg.AUTO_ENCHANTER_COST.get(), 10, 0,
                            cfg.AUTO_ENCHANTER_COST::set,
                            LangDefs.CONFIG_DESC_AUTO_ENCHANTER_COST
                    ));
                },
                LangDefs.CONFIG_SECTION_AUTO_ENCHANTER_DESC
        );

        addSection(root, eb, LangDefs.CONFIG_SECTION_ENERGY_STORAGE, entries -> {
                    entries.add(bool(eb, LangDefs.CONFIG_ENTRY_ENERGY_STORAGE_ENABLED, cfg.ENERGY_STORAGE_ENABLED.get(), true,
                            cfg.ENERGY_STORAGE_ENABLED::set,
                            LangDefs.CONFIG_DESC_ENERGY_STORAGE_ENABLED
                    ));
                    entries.add(integer(eb, LangDefs.CONFIG_ENTRY_ENERGY_STORAGE_CAPACITY_MULTIPLIER, cfg.ENERGY_STORAGE_CAPACITY_MULTIPLIER.get(), 1, 1,
                            cfg.ENERGY_STORAGE_CAPACITY_MULTIPLIER::set,
                            LangDefs.CONFIG_DESC_ENERGY_STORAGE_CAPACITY_MULTIPLIER
                    ));
                },
                LangDefs.CONFIG_SECTION_ENERGY_STORAGE_DESC
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
