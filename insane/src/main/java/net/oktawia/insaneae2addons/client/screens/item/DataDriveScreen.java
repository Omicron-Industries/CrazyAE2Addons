package net.oktawia.insaneae2addons.client.screens.item;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.Scrollbar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.oktawia.insaneae2addons.defs.regs.InsaneRecipes;
import net.oktawia.insaneae2addons.items.DataDrive;
import net.oktawia.insaneae2addons.menus.item.DataDriveMenu;
import net.oktawia.insaneae2addons.recipes.ResearchRecipe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class DataDriveScreen<C extends DataDriveMenu> extends AEBaseScreen<C> {

    private static final int CARDS_PER_PAGE = 4;

    private final Scrollbar pageScroll;
    private final CardWidget[] cards = new CardWidget[CARDS_PER_PAGE];
    private final List<Entry> entries = new ArrayList<>();
    private boolean built = false;

    public DataDriveScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        pageScroll = new Scrollbar();
        this.widgets.add("page_scroll", pageScroll);

        for (int i = 0; i < CARDS_PER_PAGE; i++) {
            cards[i] = new CardWidget();
            this.widgets.add("card" + (i + 1), cards[i]);
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        if (!built) {
            buildEntries();
            built = true;
        }

        int max = Math.max(0, entries.size() - CARDS_PER_PAGE);
        pageScroll.setRange(0, max, 1);
        if (pageScroll.getCurrentScroll() > max) {
            pageScroll.setCurrentScroll(max);
        }

        int start = Math.min(pageScroll.getCurrentScroll(), max);
        for (int i = 0; i < CARDS_PER_PAGE; i++) {
            int idx = start + i;
            cards[i].set(idx < entries.size() ? entries.get(idx) : null);
        }
    }

    private void buildEntries() {
        entries.clear();

        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        ItemStack drive = getMenu().host.getItemStack();
        Set<ResourceLocation> unlocked = DataDrive.getUnlockedKeys(drive);

        for (ResearchRecipe recipe : level.getRecipeManager().getAllRecipesFor(InsaneRecipes.RESEARCH_TYPE.get())) {
            Entry entry = new Entry();
            entry.key = recipe.unlock.key;
            entry.label = recipe.unlock.label == null ? "" : recipe.unlock.label;
            entry.unlocked = unlocked.contains(entry.key);
            entries.add(entry);
        }

        entries.sort(Comparator
                .comparing((Entry e) -> !e.unlocked)
                .thenComparing(e -> e.label.isEmpty() ? e.key.toString() : e.label, String.CASE_INSENSITIVE_ORDER));
    }

    private static final class Entry {
        ResourceLocation key;
        String label;
        boolean unlocked;
    }

    private static final class CardWidget extends AbstractWidget {

        private String title = "";
        private int titleColor = 0xFFFFFFFF;
        private boolean empty = true;

        CardWidget() {
            super(0, 0, 0, 0, Component.empty());
        }

        void set(Entry entry) {
            if (entry == null) {
                empty = true;
                return;
            }

            empty = false;
            String name = entry.label.isEmpty() ? entry.key.toString() : entry.label;
            if (entry.unlocked) {
                title = "✔ " + name;
                titleColor = 0xFF20C020;
            } else {
                title = "✖ " + name;
                titleColor = 0xFFE04A4A;
            }
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int x1 = getX();
            int y1 = getY();
            int x2 = x1 + getWidth();
            int y2 = y1 + getHeight();

            graphics.fill(x1, y1, x2, y2, 0x7F101010);
            graphics.fill(x1, y1, x2, y1 + 1, 0xFF606060);
            graphics.fill(x1, y2 - 1, x2, y2, 0xFF606060);
            graphics.fill(x1, y1, x1 + 1, y2, 0xFF606060);
            graphics.fill(x2 - 1, y1, x2, y2, 0xFF606060);

            if (empty) {
                return;
            }

            graphics.drawString(Minecraft.getInstance().font, title, x1 + 6, y1 + 4, titleColor, false);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
        }
    }
}
