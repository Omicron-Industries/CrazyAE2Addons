package net.oktawia.crazyae2addons.client.screens.part;

import appeng.api.client.AEKeyRendering;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.me.common.StackSizeRenderer;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.Scrollbar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.client.misc.IconButton;
import net.oktawia.crazyae2addons.client.renderer.InterfaceHighlighter;
import net.oktawia.crazyae2addons.defs.LangDefs;
import net.oktawia.crazyae2addons.menus.part.ResourceTrackingTerminalMenu;
import net.oktawia.crazyae2addons.network.packets.ResourceDetailPacket;
import net.oktawia.crazyae2addons.network.packets.ResourceListPacket;
import net.oktawia.crazyae2addons.tracking.ResourceSummary;
import net.oktawia.crazyae2addons.tracking.UsageEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResourceTrackingTerminalScreen<C extends ResourceTrackingTerminalMenu> extends AEBaseScreen<C> {

    private static final int COLS = 9;
    private static final int VISIBLE_ROWS = 7;
    private static final int CELL = 18;
    private static final int GRID_LEFT = 7;
    private static final int GRID_TOP = 17;
    private static final int GRID_WIDTH = COLS * CELL;
    private static final int GRID_HEIGHT = VISIBLE_ROWS * CELL;

    private static final Blitter TERM_HEADER =
            Blitter.texture("guis/terminal.png").src(0, 0, 195, 17);
    private static final Blitter TERM_FIRST_ROW =
            Blitter.texture("guis/terminal.png").src(0, 17, 195, 18);
    private static final Blitter TERM_ROW =
            Blitter.texture("guis/terminal.png").src(0, 35, 195, 18);
    private static final Blitter TERM_LAST_ROW =
            Blitter.texture("guis/terminal.png").src(0, 53, 195, 18);
    private static final Blitter TERM_BOTTOM =
            Blitter.texture("guis/terminal.png").src(0, 71, 195, 97);

    private final Scrollbar scrollbar = new Scrollbar();
    private final IconButton backButton;
    private String searchText = "";
    private boolean showingDetail = false;
    private int hoveredIdx = -1;
    private AEKey hoveredDetailKey = null;

    private boolean frozen = false;
    private List<ResourceSummary> frozenSummaries = List.of();
    private List<UsageEntry> frozenDetails = List.of();
    private AEKey frozenDetailKey = null;

    public ResourceTrackingTerminalScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.widgets.add("scrollbar", this.scrollbar);

        backButton = new IconButton(Icon.ARROW_LEFT, btn -> exitDetailView());
        backButton.setMessage(Component.translatable(LangDefs.RESOURCE_TRACKING_BACK.getTranslationKey()));
        backButton.visible = false;
        this.widgets.add("back_button", backButton);

        AETextField searchField = new AETextField(this.style, Minecraft.getInstance().font, 0, 0, 0, 0);
        searchField.setBordered(false);
        searchField.setMaxLength(64);
        searchField.setPlaceholder(Component.translatable(LangDefs.SEARCH.getTranslationKey()));
        searchField.setResponder(text -> {
            searchText = text.toLowerCase();
            scrollbar.setCurrentScroll(0);
            updateScrollbar();
        });
        this.widgets.add("search", searchField);
    }

    public void applyList(ResourceListPacket pkt) {
        updateFreezeState();
        getMenu().applyList(pkt);
        if (!showingDetail && !frozen) updateScrollbar();
    }

    public void applyDetail(ResourceDetailPacket pkt) {
        boolean wasShowingDetail = showingDetail;

        updateFreezeState();

        getMenu().applyDetail(pkt);
        showingDetail = true;
        scrollbar.setCurrentScroll(0);
        backButton.visible = true;

        if (frozen && !wasShowingDetail) {
            frozenDetails = List.copyOf(getMenu().clientDetails);
            frozenDetailKey = getMenu().clientDetailKey;
        }

        updateScrollbar();
    }

    private void exitDetailView() {
        showingDetail = false;
        backButton.visible = false;
        scrollbar.setCurrentScroll(0);
        updateScrollbar();
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        updateFreezeState();
        updateScrollbar();
    }

    private void updateFreezeState() {
        boolean shouldFreeze = Screen.hasShiftDown();

        if (shouldFreeze && !frozen) {
            frozen = true;
            frozenSummaries = List.copyOf(getMenu().clientSummaries);
            frozenDetails = List.copyOf(getMenu().clientDetails);
            frozenDetailKey = getMenu().clientDetailKey;
            return;
        }

        if (!shouldFreeze && frozen) {
            frozen = false;
            frozenSummaries = List.of();
            frozenDetails = List.of();
            frozenDetailKey = null;
        }
    }

    private List<ResourceSummary> activeSummaries() {
        return frozen ? frozenSummaries : getMenu().clientSummaries;
    }

    private List<UsageEntry> activeDetails() {
        return frozen ? frozenDetails : getMenu().clientDetails;
    }

    private @Nullable AEKey activeDetailKey() {
        return frozen ? frozenDetailKey : getMenu().clientDetailKey;
    }

    private void updateScrollbar() {
        if (showingDetail) {
            int maxRows = (GRID_HEIGHT - CELL - 2) / 26;
            int total = activeDetails().size();
            scrollbar.setRange(0, Math.max(0, total - maxRows), 1);
        } else {
            int total = filteredSummaries().size();
            int totalRows = (total + COLS - 1) / COLS;
            scrollbar.setRange(0, Math.max(0, totalRows - VISIBLE_ROWS), 1);
        }
    }

    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        TERM_HEADER.copy().dest(offsetX, offsetY).blit(guiGraphics);
        int y = offsetY + 17;
        for (int row = 0; row < VISIBLE_ROWS; row++) {
            Blitter rowBlitter = (row == 0) ? TERM_FIRST_ROW : (row == VISIBLE_ROWS - 1) ? TERM_LAST_ROW : TERM_ROW;
            rowBlitter.copy().dest(offsetX, y).blit(guiGraphics);
            y += CELL;
        }
        TERM_BOTTOM.copy().dest(offsetX, y).blit(guiGraphics);
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        hoveredIdx = -1;
        hoveredDetailKey = null;
        int relMouseX = mouseX - offsetX;
        int relMouseY = mouseY - offsetY;

        if (showingDetail) {
            drawDetailView(guiGraphics, relMouseX, relMouseY);
        } else {
            drawGridView(guiGraphics, relMouseX, relMouseY);
        }
    }

    private List<ResourceSummary> filteredSummaries() {
        List<ResourceSummary> all = activeSummaries();
        if (searchText.isEmpty()) return all;
        return all.stream()
                .filter(s -> s.key().getDisplayName().getString().toLowerCase().contains(searchText))
                .toList();
    }

    private void drawGridView(GuiGraphics guiGraphics, int relMouseX, int relMouseY) {
        List<ResourceSummary> all = activeSummaries();
        List<ResourceSummary> summaries = filteredSummaries();

        if (all.isEmpty()) {
            guiGraphics.fill(GRID_LEFT, GRID_TOP, GRID_LEFT + GRID_WIDTH, GRID_TOP + GRID_HEIGHT, 0xC0000000);
            var lines = font.split(
                    Component.translatable(LangDefs.RESOURCE_TRACKING_NO_DATA.getTranslationKey()),
                    GRID_WIDTH - 16
            );
            int centerX = GRID_LEFT + GRID_WIDTH / 2;
            int totalH = lines.size() * (font.lineHeight + 2);
            int startY = GRID_TOP + (GRID_HEIGHT - totalH) / 2;
            for (int i = 0; i < lines.size(); i++) {
                var line = lines.get(i);
                guiGraphics.drawString(
                        font,
                        line,
                        centerX - font.width(line) / 2,
                        startY + i * (font.lineHeight + 2),
                        0xFFAAAAAA,
                        false
                );
            }
            return;
        }

        if (summaries.isEmpty()) return;

        int startRow = scrollbar.getCurrentScroll();
        int startIdx = startRow * COLS;

        for (int row = 0; row < VISIBLE_ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int idx = startIdx + row * COLS + col;
                if (idx >= summaries.size()) break;
                ResourceSummary s = summaries.get(idx);

                int x = GRID_LEFT + col * CELL + 1;
                int y = GRID_TOP + row * CELL + 1;

                if (relMouseX >= x && relMouseX < x + 16 && relMouseY >= y && relMouseY < y + 16) {
                    guiGraphics.fill(x, y, x + 16, y + 16, 0x80FFFFFF);
                    hoveredIdx = idx;
                }

                AEKeyRendering.drawInGui(minecraft, guiGraphics, x, y, s.key());
                StackSizeRenderer.renderSizeLabel(guiGraphics, font, x, y, formatAmount(s.perMinute(), s.key()));
            }
        }
    }

    private void drawDetailView(GuiGraphics guiGraphics, int relMouseX, int relMouseY) {
        int entryTop = GRID_TOP + CELL + 2;

        guiGraphics.fill(GRID_LEFT, entryTop - 4, GRID_LEFT + GRID_WIDTH, GRID_TOP + GRID_HEIGHT, 0xFF2A2A2A);
        guiGraphics.fill(GRID_LEFT, GRID_TOP, GRID_LEFT + 8 * CELL + 2, GRID_TOP + CELL, 0xFF2A2A2A);

        AEKey key = activeDetailKey();
        if (key != null) {
            AEKeyRendering.drawInGui(minecraft, guiGraphics, GRID_LEFT + 1, GRID_TOP + 1, key);
            guiGraphics.drawString(font, key.getDisplayName(), GRID_LEFT + 20, GRID_TOP + 5, 0xFFFFFFFF, false);
        }

        int offset = scrollbar.getCurrentScroll();
        List<UsageEntry> details = activeDetails();
        int rowH = 26;
        int maxRows = (GRID_HEIGHT - CELL - 2) / rowH;

        Component usedFor = Component.translatable(LangDefs.RESOURCE_TRACKING_USED_FOR.getTranslationKey());
        Component crafting = Component.translatable(LangDefs.RESOURCE_TRACKING_CRAFTING.getTranslationKey());

        for (int row = 0; row < maxRows; row++) {
            int idx = offset + row;
            if (idx >= details.size()) break;
            UsageEntry e = details.get(idx);
            int y = entryTop + row * rowH;

            guiGraphics.fill(GRID_LEFT + 1, y, GRID_LEFT + GRID_WIDTH - 1, y + rowH - 2, 0x40FFFFFF);

            String amount = formatAmount(e.totalAmount(), activeDetailKey());
            int x = GRID_LEFT + 3;

            if (e.icon() != null && e.pos() != null) {
                guiGraphics.drawString(font, amount, x, y + 2, 0xFFFFAA00, false);
                x += font.width(amount) + 6;
                guiGraphics.drawString(font, usedFor, x, y + 2, 0xFFFFAA00, false);
                x += font.width(usedFor) + 3;
                AEKeyRendering.drawInGui(minecraft, guiGraphics, x, y, e.icon());

                if (relMouseX >= x && relMouseX < x + 16 && relMouseY >= y && relMouseY < y + 16) {
                    hoveredDetailKey = e.icon();
                }

                guiGraphics.drawString(font, e.description(), GRID_LEFT + 3, y + 15, 0xFFAAAAAA, false);

                int btnX = GRID_LEFT + GRID_WIDTH - 20;
                int btnY = y - 1 + (rowH - 16) / 2;
                boolean hovered = relMouseX >= btnX && relMouseX < btnX + 16
                        && relMouseY >= btnY && relMouseY < btnY + 16;

                if (hovered) guiGraphics.fill(btnX, btnY, btnX + 16, btnY + 16, 0x40FFFFFF);
                Icon.PATTERN_TERMINAL_VISIBLE.getBlitter().dest(btnX, btnY).blit(guiGraphics);
            } else if (e.icon() != null) {
                int textY = y + (rowH - font.lineHeight) / 2;

                guiGraphics.drawString(font, amount, x, textY, 0xFFFFAA00, false);
                x += font.width(amount) + 6;

                guiGraphics.drawString(font, usedFor, x, textY, 0xFFFFAA00, false);
                x += font.width(usedFor) + 3;

                guiGraphics.drawString(font, crafting, x, textY, 0xFFAAAAAA, false);
                x += font.width(crafting) + 6;

                int iconY = y + (rowH - 16) / 2;
                AEKeyRendering.drawInGui(minecraft, guiGraphics, x, iconY, e.icon());

                if (relMouseX >= x && relMouseX < x + 16 && relMouseY >= iconY && relMouseY < iconY + 16) {
                    hoveredDetailKey = e.icon();
                }
            } else {
                guiGraphics.drawString(font, amount, x, y + 4, 0xFFFFAA00, false);
                x += font.width(amount) + 6;

                guiGraphics.drawString(font, usedFor, x, y + 4, 0xFFFFAA00, false);

                guiGraphics.drawString(font, e.description(), GRID_LEFT + 3, y + 14, 0xFFCCCCCC, false);

                BlockPos ifPos = parseInterfacePos(e.description());
                if (ifPos != null) {
                    int btnX = GRID_LEFT + GRID_WIDTH - 20;
                    int btnY = y - 1 + (rowH - 16) / 2;
                    boolean hovered = relMouseX >= btnX && relMouseX < btnX + 16
                            && relMouseY >= btnY && relMouseY < btnY + 16;

                    if (hovered) guiGraphics.fill(btnX, btnY, btnX + 16, btnY + 16, 0x40FFFFFF);
                    Icon.PATTERN_TERMINAL_VISIBLE.getBlitter().dest(btnX, btnY).blit(guiGraphics);
                }
            }
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        AEKey tooltipKey = null;

        if (!showingDetail && hoveredIdx >= 0) {
            List<ResourceSummary> summaries = filteredSummaries();
            if (hoveredIdx < summaries.size()) tooltipKey = summaries.get(hoveredIdx).key();
        } else if (showingDetail && hoveredDetailKey != null) {
            tooltipKey = hoveredDetailKey;
        }

        if (tooltipKey != null) {
            if (tooltipKey instanceof AEItemKey itemKey) {
                guiGraphics.renderTooltip(font, itemKey.getReadOnlyStack(), x, y);
            } else {
                guiGraphics.renderComponentTooltip(font, AEKeyRendering.getTooltip(tooltipKey), x, y);
            }
            return;
        }

        super.renderTooltip(guiGraphics, x, y);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        updateFreezeState();

        if (showingDetail) {
            int relX = (int) mouseX - leftPos;
            int relY = (int) mouseY - topPos;
            int entryTop = GRID_TOP + CELL + 2;
            int rowH = 26;
            int maxRows = (GRID_HEIGHT - CELL - 2) / rowH;
            int offset = scrollbar.getCurrentScroll();
            List<UsageEntry> details = activeDetails();
            int btnX = GRID_LEFT + GRID_WIDTH - 20;

            for (int row = 0; row < maxRows; row++) {
                int idx = offset + row;
                if (idx >= details.size()) break;

                UsageEntry e = details.get(idx);
                int y = entryTop + row * rowH;
                int btnY = y - 1 + (rowH - 16) / 2;

                if (relX >= btnX && relX < btnX + 16 && relY >= btnY && relY < btnY + 16) {
                    if (e.pos() != null) {
                        InterfaceHighlighter.highlight(e.pos());
                        return true;
                    }

                    if (e.icon() == null) {
                        BlockPos pos = parseInterfacePos(e.description());
                        if (pos != null) {
                            InterfaceHighlighter.highlight(pos);
                            return true;
                        }
                    }
                }
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        int relX = (int) mouseX - leftPos;
        int relY = (int) mouseY - topPos;

        if (relX >= GRID_LEFT && relY >= GRID_TOP && relY < GRID_TOP + VISIBLE_ROWS * CELL) {
            int col = (relX - GRID_LEFT) / CELL;
            int row = (relY - GRID_TOP) / CELL;

            if (col < COLS && row < VISIBLE_ROWS) {
                int idx = (scrollbar.getCurrentScroll() + row) * COLS + col;
                List<ResourceSummary> filtered = filteredSummaries();

                if (idx < filtered.size()) {
                    ResourceSummary selected = filtered.get(idx);
                    int absIdx = findCurrentSummaryIndex(selected.key());

                    if (absIdx >= 0) {
                        getMenu().requestDetail(absIdx);
                        return true;
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int findCurrentSummaryIndex(AEKey key) {
        List<ResourceSummary> summaries = getMenu().clientSummaries;

        for (int i = 0; i < summaries.size(); i++) {
            if (summaries.get(i).key().equals(key)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        return scrollbar.onMouseWheel(
                new Point((int) Math.round(x - leftPos), (int) Math.round(y - topPos)),
                delta
        );
    }

    private static String formatAmount(long amount, @Nullable AEKey key) {
        if (key == null) return Long.toString(amount);

        int perUnit = key.getAmountPerUnit();
        String unit = key.getUnitSymbol() != null ? key.getUnitSymbol() : "";
        double display = amount / (double) perUnit;

        if (display >= 1_000_000_000.0) return String.format("%.1fG%s", display / 1_000_000_000.0, unit);
        if (display >= 1_000_000.0) return String.format("%.1fM%s", display / 1_000_000.0, unit);
        if (display >= 1_000.0) return String.format("%.1fk%s", display / 1_000.0, unit);
        if (display < 1.0 && perUnit > 1) return amount + "m" + unit;

        return String.format(display % 1 == 0 ? "%.0f%s" : "%.1f%s", display, unit);
    }

    private static @Nullable BlockPos parseInterfacePos(String desc) {
        if (!desc.startsWith("interface at ")) return null;

        String[] parts = desc.substring(13).split(" ");
        if (parts.length != 3) return null;

        try {
            return new BlockPos(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }
}