package net.oktawia.insaneae2addons.client.screens.item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import appeng.api.client.AEKeyRendering;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.Scrollbar;

import net.oktawia.insaneae2addons.menus.item.MobKeySelectorMenu;
import net.oktawia.insaneae2addons.mobstorage.MobKey;

public class MobKeySelectorScreen<C extends MobKeySelectorMenu> extends AEBaseScreen<C> {

    private static final int LABEL_WIDTH = 112;
    private static final int ICON_LEFT = 12;
    private static final int ICON_TOP = 36;
    private static final int ROW_HEIGHT = 20;

    private record MobEntry(String id, MobKey key, Component name, String modName, String searchText) {
    }

    private static List<MobEntry> cachedMobs;

    private Scrollbar scroll;
    private final List<Button> btns = new ArrayList<>();
    private final List<MobEntry> allMobs;
    private List<MobEntry> filtered;
    private int lastScroll = -1;
    private boolean initialized;

    public MobKeySelectorScreen(C menu, Inventory inv, Component title, ScreenStyle style) {
        super(menu, inv, title, style);

        this.allMobs = mobEntries();
        this.filtered = allMobs;

        AETextField search = new AETextField(this.style, Minecraft.getInstance().font, 0, 0, 0, 0);
        search.setPlaceholder(Component.translatable("gui.insaneae2addons.mob_key_search"));
        search.setResponder(query -> {
            String s = query.toLowerCase(Locale.ROOT).trim();
            this.filtered = s.isEmpty()
                    ? allMobs
                    : allMobs.stream().filter(e -> e.searchText().contains(s)).toList();
            scroll.setCurrentScroll(0);
            refreshPage();
        });
        search.setBordered(false);
        this.widgets.add("search", search);

        this.scroll = new Scrollbar();
        this.widgets.add("scroll", this.scroll);
        this.scroll.setRange(0, maxScroll(), 1);

        for (int i = 0; i < 6; i++) {
            int idx = i;
            Button b = Button.builder(Component.empty(), btn -> onPress(idx)).pos(0, 0).size(0, 0).build();
            this.widgets.add("b" + idx, b);
            btns.add(b);
        }
    }

    private static List<MobEntry> mobEntries() {
        if (cachedMobs != null) {
            return cachedMobs;
        }
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }

        List<MobEntry> entries = new ArrayList<>();
        for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES.getValues()) {
            if (!isMob(type, level)) {
                continue;
            }
            ResourceLocation rl = ForgeRegistries.ENTITY_TYPES.getKey(type);
            if (rl == null) {
                continue;
            }
            MobKey key = MobKey.of(type);
            Component name = key.getDisplayName();
            String modName = modName(rl.getNamespace());
            String searchText = (rl + " " + name.getString() + " " + modName).toLowerCase(Locale.ROOT);
            entries.add(new MobEntry(rl.toString(), key, name, modName, searchText));
        }
        entries.sort(Comparator.comparing((MobEntry e) -> e.name().getString()).thenComparing(MobEntry::id));

        cachedMobs = List.copyOf(entries);
        return cachedMobs;
    }

    private static String modName(String namespace) {
        return ModList.get().getModContainerById(namespace)
                .map(container -> container.getModInfo().getDisplayName())
                .orElse(namespace);
    }

    private static boolean isMob(EntityType<?> type, Level level) {
        try {
            return type.create(level) instanceof Mob;
        } catch (Exception e) {
            return false;
        }
    }

    private Component buildLabel(MobEntry entry) {
        String name = entry.name().getString();
        if (font.width(name) <= LABEL_WIDTH) {
            return Component.literal(name);
        }
        return Component.literal(font.plainSubstrByWidth(name, LABEL_WIDTH - font.width("..")) + "..");
    }

    private int maxScroll() {
        return Math.max(0, filtered.size() - btns.size());
    }

    private void onPress(int visibleIndex) {
        int idx = scroll.getCurrentScroll() + visibleIndex;
        if (idx < 0 || idx >= filtered.size()) {
            return;
        }
        menu.choose(filtered.get(idx).id());
        refreshPage();
    }

    private void refreshPage() {
        int offset = scroll.getCurrentScroll();
        String selected = menu.getSelectedKey();
        for (int i = 0; i < btns.size(); i++) {
            Button b = btns.get(i);
            int idx = offset + i;
            if (idx < filtered.size()) {
                MobEntry entry = filtered.get(idx);
                b.active = true;
                b.setMessage(buildLabel(entry));

                var tooltip = Component.empty()
                        .append(entry.name())
                        .append("\n")
                        .append(Component.literal(entry.id()).withStyle(ChatFormatting.DARK_GRAY))
                        .append("\n")
                        .append(Component.literal(entry.modName()).withStyle(ChatFormatting.BLUE));
                if (entry.id().equals(selected)) {
                    tooltip.append("\n").append(Component.translatable("gui.insaneae2addons.mob_key_selected"));
                }
                b.setTooltip(Tooltip.create(tooltip));
            } else {
                b.active = false;
                b.setMessage(Component.empty());
                b.setTooltip(null);
            }
        }
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        int offset = scroll.getCurrentScroll();
        for (int i = 0; i < btns.size(); i++) {
            int idx = offset + i;
            if (idx >= filtered.size()) {
                break;
            }
            AEKeyRendering.drawInGui(minecraft, guiGraphics, ICON_LEFT, ICON_TOP + i * ROW_HEIGHT,
                    filtered.get(idx).key());
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        int max = maxScroll();
        if (scroll.getCurrentScroll() > max) {
            scroll.setCurrentScroll(max);
        }
        scroll.setRange(0, max, 1);

        int cur = scroll.getCurrentScroll();
        if (cur != lastScroll) {
            lastScroll = cur;
            refreshPage();
        }

        if (!initialized) {
            initialized = true;
            refreshPage();
        }
    }
}
