package net.oktawia.crazyae2addons.client.screens.block;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AETextField;

import net.oktawia.crazyae2addons.client.misc.AETextButton;
import net.oktawia.crazyae2addons.client.misc.DisplayDatabaseEntryListWidget;
import net.oktawia.crazyae2addons.defs.LangDefs;
import net.oktawia.crazyae2addons.menus.block.DisplayDatabaseMenu;

public class DisplayDatabaseScreen<C extends DisplayDatabaseMenu> extends AEBaseScreen<C> {

    private final AETextField keyInput;
    private final AETextField valueInput;
    private final AETextButton addButton;
    private final AETextButton clearButton;
    private final DisplayDatabaseEntryListWidget entries;

    private String selectedKey = null;

    public DisplayDatabaseScreen(C menu, Inventory inv, Component title, ScreenStyle style) {
        super(menu, inv, title, style);

        var font = Minecraft.getInstance().font;

        this.keyInput = new AETextField(style, font, 0, 0, 0, 0);
        this.keyInput.setBordered(false);
        this.keyInput.setMaxLength(128);
        this.keyInput.setHint(Component.translatable(LangDefs.DISPLAY_DATABASE_KEY.getTranslationKey()));

        this.valueInput = new AETextField(style, font, 0, 0, 0, 0);
        this.valueInput.setBordered(false);
        this.valueInput.setMaxLength(1024);
        this.valueInput.setHint(Component.translatable(LangDefs.DISPLAY_DATABASE_VALUE.getTranslationKey()));

        this.entries = new DisplayDatabaseEntryListWidget(
                () -> getMenu().getEntries(),
                () -> selectedKey,
                this::selectEntry,
                this::removeEntry);

        this.addButton = new AETextButton(
                Component.translatable(LangDefs.DISPLAY_DATABASE_ADD.getTranslationKey()),
                btn -> addEntry());
        this.addButton.setTooltip(Tooltip.create(
                Component.translatable(LangDefs.DISPLAY_DATABASE_ADD_TOOLTIP.getTranslationKey())));

        this.clearButton = new AETextButton(
                Component.translatable(LangDefs.DISPLAY_DATABASE_CLEAR.getTranslationKey()),
                btn -> {
                    getMenu().clearEntries();
                    selectedKey = null;
                    entries.clampScroll();
                });
        this.clearButton.setTooltip(Tooltip.create(
                Component.translatable(LangDefs.DISPLAY_DATABASE_CLEAR_TOOLTIP.getTranslationKey())));

        widgets.add("key", keyInput);
        widgets.add("value", valueInput);
        widgets.add("add", addButton);
        widgets.add("clear", clearButton);
        widgets.add("entries", entries);
    }

    private void addEntry() {
        String key = keyInput.getValue().trim();
        String value = valueInput.getValue();

        if (key.isEmpty()) {
            return;
        }

        getMenu().putEntry(key, value);
        selectedKey = key;

        keyInput.setValue("");
        valueInput.setValue("");
        keyInput.setFocused(true);
        this.setFocused(keyInput);

        entries.ensureVisible(key);
    }

    private void selectEntry(String key, String value) {
        selectedKey = key;

        keyInput.setValue(key);
        valueInput.setValue(value);
        valueInput.setFocused(true);
        this.setFocused(valueInput);
    }

    private void removeEntry(String key) {
        getMenu().removeEntry(key);

        if (key != null && key.equals(selectedKey)) {
            selectedKey = null;
        }

        entries.clampScroll();
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        entries.clampScroll();

        if (selectedKey != null && !getMenu().getEntries().containsKey(selectedKey)) {
            selectedKey = null;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (entries != null && entries.mouseScrolled(mouseX, mouseY, scroll)) {
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (keyInput.isMouseOver(mouseX, mouseY)) {
                keyInput.setValue("");
                selectedKey = null;
                keyInput.setFocused(true);
                this.setFocused(keyInput);
                return true;
            }

            if (valueInput.isMouseOver(mouseX, mouseY)) {
                valueInput.setValue("");
                valueInput.setFocused(true);
                this.setFocused(valueInput);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
            if (keyInput.isFocused() || valueInput.isFocused()) {
                addEntry();
                return true;
            }
        }

        if (key == GLFW.GLFW_KEY_ESCAPE) {
            if (keyInput.isFocused()) {
                keyInput.setFocused(false);
                this.setFocused(null);
                return true;
            }

            if (valueInput.isFocused()) {
                valueInput.setFocused(false);
                this.setFocused(null);
                return true;
            }
        }

        if (keyInput.keyPressed(key, scancode, modifiers)) {
            return true;
        }

        if (valueInput.keyPressed(key, scancode, modifiers)) {
            return true;
        }

        return super.keyPressed(key, scancode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (keyInput.charTyped(codePoint, modifiers)) {
            return true;
        }

        if (valueInput.charTyped(codePoint, modifiers)) {
            return true;
        }

        return super.charTyped(codePoint, modifiers);
    }
}
