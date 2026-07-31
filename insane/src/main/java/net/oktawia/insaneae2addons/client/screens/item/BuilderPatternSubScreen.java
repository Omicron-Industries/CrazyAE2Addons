package net.oktawia.insaneae2addons.client.screens.item;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.implementations.AESubScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AETextField;

import net.oktawia.crazyae2addons.client.misc.IconButton;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.menus.item.BuilderPatternSubMenu;

public class BuilderPatternSubScreen<C extends BuilderPatternSubMenu> extends AEBaseScreen<C> {

    private static final int NUM_W = 46;
    private static final int BLOCK_W = 100;
    private static final int FIELD_H = 18;

    private static final LangDefs[] ACTION_LABELS = { LangDefs.PLACE, LangDefs.BREAK };
    private static final LangDefs[] COND_LABELS = { LangDefs.ALWAYS, LangDefs.EQUALS, LangDefs.NOT_EQUALS };

    private final AETextField placeField;
    private final AETextField checkField;

    public BuilderPatternSubScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        Font font = Minecraft.getInstance().font;

        placeField = textField(style, font, () -> getMenu().placeBlock, getMenu()::setPlaceBlock);
        checkField = textField(style, font, () -> getMenu().checkBlock, getMenu()::setCheckBlock);

        widgets.add("width", numberField(style, font, () -> getMenu().width, getMenu()::setWidth));
        widgets.add("height", numberField(style, font, () -> getMenu().height, getMenu()::setHeight));
        widgets.add("depth", numberField(style, font, () -> getMenu().depth, getMenu()::setDepth));
        widgets.add("place", placeField);
        widgets.add("check", checkField);

        addToggle("widthDir", () -> getMenu().right, getMenu()::setRight, LangDefs.RIGHT, LangDefs.LEFT);
        addToggle("heightDir", () -> getMenu().up, getMenu()::setUp, LangDefs.UP, LangDefs.DOWN);
        addToggle("depthDir", () -> getMenu().forward, getMenu()::setForward, LangDefs.FORWARDS, LangDefs.BACKWARDS);

        addCycle("action", () -> getMenu().actionType, getMenu()::setActionType, ACTION_LABELS);
        addCycle("condition", () -> getMenu().condition, getMenu()::setCondition, COND_LABELS);

        widgets.addButton("generate", label(LangDefs.GENERATE), btn -> getMenu().generate());

        IconButton back = new IconButton(Icon.ARROW_LEFT, btn -> AESubScreen.goBack());
        back.setTooltip(Tooltip.create(label(LangDefs.BACK)));
        widgets.add("back", back);

        updateFieldVisibility();
    }

    private AETextField numberField(ScreenStyle style, Font font, IntSupplier current, IntConsumer setter) {
        AETextField field = new AETextField(style, font, 0, 0, NUM_W, FIELD_H);
        field.setBordered(false);
        field.setValue(Integer.toString(current.getAsInt()));
        field.setResponder(value -> {
            int parsed = parsePositiveInt(value, current.getAsInt());
            if (parsed != current.getAsInt()) {
                setter.accept(parsed);
            }
        });
        return field;
    }

    private AETextField textField(ScreenStyle style, Font font, Supplier<String> current, Consumer<String> setter) {
        AETextField field = new AETextField(style, font, 0, 0, BLOCK_W, FIELD_H);
        field.setBordered(false);
        field.setValue(current.get());
        field.setResponder(value -> {
            if (!value.equals(current.get())) {
                setter.accept(value);
            }
        });
        return field;
    }

    private void addToggle(String name, BooleanSupplier current, Consumer<Boolean> setter, LangDefs on, LangDefs off) {
        widgets.addButton(name, label(current.getAsBoolean() ? on : off), btn -> {
            boolean next = !current.getAsBoolean();
            setter.accept(next);
            btn.setMessage(label(next ? on : off));
        });
    }

    private void addCycle(String name, IntSupplier current, IntConsumer setter, LangDefs[] labels) {
        widgets.addButton(name, label(labels[current.getAsInt()]), btn -> {
            int next = (current.getAsInt() + 1) % labels.length;
            setter.accept(next);
            btn.setMessage(label(labels[next]));
            updateFieldVisibility();
        });
    }

    private void updateFieldVisibility() {
        placeField.setVisible(getMenu().actionType == 0);
        checkField.setVisible(getMenu().condition != 0);
    }

    private static Component label(LangDefs def) {
        return Component.translatable(def.getTranslationKey());
    }

    private static int parsePositiveInt(String value, int fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        try {
            return Math.max(1, Integer.parseInt(value.trim()));
        } catch (NumberFormatException e) {
            InsaneAddons.LOGGER.debug("invalid numeric input in builder pattern screen", e);
            return fallback;
        }
    }
}
