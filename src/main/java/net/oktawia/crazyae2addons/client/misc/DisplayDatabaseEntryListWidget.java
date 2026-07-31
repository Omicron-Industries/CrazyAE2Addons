package net.oktawia.crazyae2addons.client.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import net.oktawia.crazyae2addons.defs.LangDefs;

public class DisplayDatabaseEntryListWidget extends AbstractWidget {

    private static final int ROW_H = 14;
    private static final int DELETE_W = 14;

    private final Supplier<Map<String, String>> entriesSupplier;
    private final Supplier<String> selectedKeySupplier;
    private final BiConsumer<String, String> selectAction;
    private final Consumer<String> removeAction;

    private int scrollOff = 0;

    public DisplayDatabaseEntryListWidget(
            Supplier<Map<String, String>> entriesSupplier,
            Supplier<String> selectedKeySupplier,
            BiConsumer<String, String> selectAction,
            Consumer<String> removeAction) {
        super(0, 0, 0, 0, Component.empty());
        this.entriesSupplier = entriesSupplier;
        this.selectedKeySupplier = selectedKeySupplier;
        this.selectAction = selectAction;
        this.removeAction = removeAction;
    }

    public int visibleRows() {
        return Math.max(0, (height - 4) / ROW_H);
    }

    public int maxScroll() {
        return Math.max(0, getEntries().size() - visibleRows());
    }

    public void clampScroll() {
        scrollOff = Mth.clamp(scrollOff, 0, maxScroll());
    }

    public void ensureVisible(String key) {
        if (key == null) {
            clampScroll();
            return;
        }

        List<Map.Entry<String, String>> entries = getEntries();
        int idx = -1;

        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getKey().equals(key)) {
                idx = i;
                break;
            }
        }

        if (idx < 0) {
            clampScroll();
            return;
        }

        int vis = visibleRows();
        if (vis <= 0) {
            scrollOff = 0;
            return;
        }

        if (idx < scrollOff) {
            scrollOff = idx;
        } else if (idx >= scrollOff + vis) {
            scrollOff = idx - vis + 1;
        }

        clampScroll();
    }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        clampScroll();

        Minecraft mc = Minecraft.getInstance();

        int x = getX();
        int y = getY();

        g.fill(x, y, x + width, y + height, 0xFF1E1E1E);
        g.fill(x, y, x + width, y + 1, 0xFF909090);
        g.fill(x, y + height - 1, x + width, y + height, 0xFF909090);
        g.fill(x, y, x + 1, y + height, 0xFF909090);
        g.fill(x + width - 1, y, x + width, y + height, 0xFF909090);

        List<Map.Entry<String, String>> entries = getEntries();

        if (entries.isEmpty()) {
            g.drawString(
                    mc.font,
                    Component.translatable(LangDefs.NO_ENTRIES.getTranslationKey()),
                    x + 4,
                    y + 4,
                    0xFF777777,
                    false);
            return;
        }

        String selectedKey = selectedKeySupplier.get();
        int vis = visibleRows();

        for (int row = 0; row < vis; row++) {
            int idx = row + scrollOff;
            if (idx >= entries.size()) {
                break;
            }

            int ry = y + 2 + row * ROW_H;
            Map.Entry<String, String> entry = entries.get(idx);

            boolean selected = selectedKey != null && entry.getKey().equals(selectedKey);
            boolean hovered = mouseX >= x + 1
                    && mouseX < x + width - 1
                    && mouseY >= ry
                    && mouseY < ry + ROW_H;

            if (selected) {
                g.fill(x + 1, ry, x + width - 1, ry + ROW_H, 0xFF3A1A1A);
            } else if (hovered) {
                g.fill(x + 1, ry, x + width - 1, ry + ROW_H, 0xFF2D2D2D);
            }

            int deleteX = getDeleteX();

            String label = entry.getKey() + " = " + entry.getValue();
            String visibleLabel = mc.font.plainSubstrByWidth(label, deleteX - x - 8);

            g.drawString(
                    mc.font,
                    visibleLabel,
                    x + 4,
                    ry + 3,
                    selected ? 0xFFFF5555 : 0xFFCCCCCC,
                    false);

            boolean deleteHovered = mouseX >= deleteX
                    && mouseX < deleteX + DELETE_W
                    && mouseY >= ry
                    && mouseY < ry + ROW_H;

            g.drawString(
                    mc.font,
                    "x",
                    deleteX + 4,
                    ry + 3,
                    deleteHovered ? 0xFFFF7777 : 0xFFAA5555,
                    false);
        }

        renderScrollbar(g, entries.size(), vis);
    }

    private void renderScrollbar(GuiGraphics g, int totalRows, int visibleRows) {
        if (totalRows <= visibleRows || visibleRows <= 0) {
            return;
        }

        int x = getX();
        int y = getY();

        int trackX1 = x + width - 3;
        int trackX2 = x + width - 1;
        int trackY1 = y + 2;
        int trackY2 = y + height - 2;
        int trackH = trackY2 - trackY1;

        if (trackH <= 0) {
            return;
        }

        int thumbH = Math.max(12, (int) ((visibleRows / (float) totalRows) * trackH));
        int maxScroll = maxScroll();

        int thumbY = trackY1;
        if (maxScroll > 0) {
            thumbY += (int) ((scrollOff / (float) maxScroll) * (trackH - thumbH));
        }

        g.fill(trackX1, trackY1, trackX2, trackY2, 0xFF2A2A2A);
        g.fill(trackX1, thumbY, trackX2, thumbY + thumbH, 0xFF777777);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }

        if (button != 0) {
            return true;
        }

        int mx = (int) mouseX;
        int my = (int) mouseY;

        List<Map.Entry<String, String>> entries = getEntries();
        int localY = my - getY() - 2;
        int row = localY / ROW_H;
        int idx = row + scrollOff;

        if (row < 0 || idx < 0 || idx >= entries.size()) {
            return true;
        }

        Map.Entry<String, String> entry = entries.get(idx);

        int rowY = getY() + 2 + row * ROW_H;
        int deleteX = getDeleteX();

        boolean clickedDelete = mx >= deleteX
                && mx < deleteX + DELETE_W
                && my >= rowY
                && my < rowY + ROW_H;

        if (clickedDelete) {
            removeAction.accept(entry.getKey());
            clampScroll();
            return true;
        }

        selectAction.accept(entry.getKey(), entry.getValue());
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }

        int delta = (int) Math.signum(scroll);
        if (delta == 0) {
            return false;
        }

        scrollOff = Mth.clamp(scrollOff - delta, 0, maxScroll());
        return true;
    }

    private int getDeleteX() {
        return getX() + width - DELETE_W - 5;
    }

    private List<Map.Entry<String, String>> getEntries() {
        Map<String, String> map = entriesSupplier.get();
        if (map == null || map.isEmpty()) {
            return List.of();
        }

        return new ArrayList<>(map.entrySet());
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput out) {
    }
}
