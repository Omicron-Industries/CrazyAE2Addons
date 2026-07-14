package net.oktawia.insaneae2addons.xei.common;

import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.misc.ItemStackTransfer;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneRecipes;
import net.oktawia.insaneae2addons.recipes.ResearchRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResearchPreview extends WidgetGroup {

    private static final int WIDTH = 160;
    private static final int HEIGHT = 200;

    @Nullable
    private final ResourceLocation recipeId;

    public ResearchPreview(@Nullable ResourceLocation recipeId, List<ItemStack> inputs, ItemStack driveOrOutput) {
        super(0, 0, WIDTH, HEIGHT);
        setClientSideWidget();

        this.recipeId = recipeId;

        int centerX = WIDTH / 2;
        int centerY = 60;

        addStationIcon(centerX, centerY);
        addPedestalsCircle(centerX, centerY);
        addOutputPanel(driveOrOutput);
        addProcessInfoPanel();
    }

    private void addStationIcon(int centerX, int centerY) {
        ItemStack stationStack = new ItemStack(InsaneBlockRegistrar.RESEARCH_STATION_BLOCK.get().asItem());

        addWidget(new SlotWidget(new ItemStackTransfer(stationStack), 0,
                centerX - 9, centerY - 9, false, false)
                .setIngredientIO(IngredientIO.RENDER_ONLY));
    }

    private void addPedestalsCircle(int centerX, int centerY) {
        ResearchRecipe recipe = resolveRecipe();
        if (recipe == null || recipe.consumables == null || recipe.consumables.isEmpty()) {
            return;
        }

        int radius = 46;
        int maxSlots = 8;
        int count = Math.min(maxSlots, recipe.consumables.size());

        double angleStep = 2.0 * Math.PI / (double) count;
        double startAngle = -Math.PI / 2.0;

        var font = Minecraft.getInstance().font;

        for (int i = 0; i < count; i++) {
            ResearchRecipe.Consumable c = recipe.consumables.get(i);

            double angle = startAngle + angleStep * i;

            int slotCenterX = centerX + (int) Math.round(Math.cos(angle) * radius);
            int slotCenterY = centerY + (int) Math.round(Math.sin(angle) * radius);

            int slotX = slotCenterX - 9;
            int slotY = slotCenterY - 9;

            ItemStack stack = new ItemStack(c.item, Math.max(1, c.count));
            if (stack.getCount() > stack.getMaxStackSize()) {
                stack.setCount(stack.getMaxStackSize());
            }

            addWidget(new SlotWidget(new ItemStackTransfer(stack), 0,
                    slotX, slotY, false, false)
                    .setBackgroundTexture(SlotWidget.ITEM_SLOT_TEXTURE)
                    .setIngredientIO(IngredientIO.INPUT));

            String compText = Component.translatable(
                    LangDefs.RESEARCH_PEDESTAL_COMPACT.getTranslationKey(), c.computation
            ).getString();

            int textWidth = font.width(compText);
            addWidget(new LabelWidget(slotCenterX - textWidth / 2, slotY + 18 + 2, compText));
        }
    }

    private void addOutputPanel(ItemStack driveOrOutput) {
        int x = 5;
        int labelY = 135;
        int slotY = labelY + 10;

        addWidget(new LabelWidget(x, labelY, tr(LangDefs.RESEARCH_OUTPUT_LABEL)));

        addWidget(new SlotWidget(new ItemStackTransfer(driveOrOutput), 0,
                x, slotY, false, false)
                .setBackgroundTexture(SlotWidget.ITEM_SLOT_TEXTURE)
                .setIngredientIO(IngredientIO.OUTPUT));

        addWidget(new ButtonWidget(x + 20, slotY + 3, 12, 12,
                new TextTexture("?"), b -> {})
                .appendHoverTooltips(
                        comp(LangDefs.RESEARCH_DRIVE_TOOLTIP_1),
                        comp(LangDefs.RESEARCH_DRIVE_TOOLTIP_2)
                ));

        addWidget(new LabelWidget(x + 35, slotY + 4, tr(LangDefs.RESEARCH_OUTPUT_DISK_NOTE)));
    }

    private int getTotalPedestalComputation(ResearchRecipe recipe) {
        int sum = 0;
        if (recipe.consumables != null) {
            for (ResearchRecipe.Consumable c : recipe.consumables) {
                if (c != null && c.computation > 0) {
                    sum += c.computation;
                }
            }
        }
        return sum;
    }

    private void addProcessInfoPanel() {
        try {
            ResearchRecipe recipe = resolveRecipe();
            if (recipe == null) {
                return;
            }

            long requiredComputation = recipe.duration;
            int minCompPerTick = getTotalPedestalComputation(recipe);

            long seconds = 0;
            if (minCompPerTick > 0) {
                long ticksMin = (requiredComputation + minCompPerTick - 1L) / (long) minCompPerTick;
                seconds = ticksMin / 20L;
            }

            String line3 = Component
                    .translatable(LangDefs.RESEARCH_DURATION.getTranslationKey(), seconds)
                    .getString();

            String unlockName = (recipe.unlock.label == null || recipe.unlock.label.isEmpty())
                    ? recipe.unlock.key.toString()
                    : recipe.unlock.label;
            String line5 = Component
                    .translatable(LangDefs.RESEARCH_UNLOCKS.getTranslationKey(), unlockName)
                    .getString();

            int startY = 165;
            int lineH = Minecraft.getInstance().font.lineHeight + 2;
            int x = 5;

            addWidget(new LabelWidget(x, startY, line3));
            addWidget(new LabelWidget(x, startY + lineH, line5));
        } catch (Exception ignored) {
        }
    }

    @Nullable
    private ResearchRecipe resolveRecipe() {
        if (recipeId == null) {
            return null;
        }

        var level = Minecraft.getInstance().level;
        if (level == null) {
            LogUtils.getLogger().warn("No client level available to load research recipe {}", recipeId);
            return null;
        }
        return level.getRecipeManager()
                .getAllRecipesFor(InsaneRecipes.RESEARCH_TYPE.get())
                .stream()
                .filter(rr -> rr.getId().equals(recipeId))
                .findFirst()
                .orElse(null);
    }

    private static String tr(LangDefs def) {
        return Component.translatable(def.getTranslationKey()).getString();
    }

    private static Component comp(LangDefs def) {
        return Component.translatable(def.getTranslationKey());
    }
}
