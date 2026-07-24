package net.oktawia.insaneae2addons.client.screens.part;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.ScreenStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.client.misc.IconButton;
import net.oktawia.crazyae2addons.client.misc.MultilineTextFieldWidget;
import net.oktawia.insaneae2addons.client.utils.NbtMatcherHighlight;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.logic.nbt.NBTMatcher;
import net.oktawia.insaneae2addons.menus.part.NbtExportBusMenu;
import net.oktawia.insaneae2addons.util.NbtFormatter;
import org.lwjgl.glfw.GLFW;

public class NbtExportBusScreen<C extends NbtExportBusMenu> extends AEBaseScreen<C> {

    private final MultilineTextFieldWidget textEditor;
    private final IconButton confirmBtn;
    private final IconButton loadBtn;
    private final IconButton formatBtn;

    private boolean initialized = false;

    public NbtExportBusScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        textEditor = new MultilineTextFieldWidget(Minecraft.getInstance().font, 0, 0, 205, 135, Component.empty());
        textEditor.setDefaultTextColor(0xFFFFFFFF);
        textEditor.setHighlightRules(NbtMatcherHighlight.rules());
        widgets.add("data", textEditor);

        confirmBtn = new IconButton(Icon.COPY_MODE_ON, btn -> save());
        confirmBtn.setTooltip(Tooltip.create(Component.translatable(LangDefs.NBT_EXPORT_CONFIRM.getTranslationKey())));
        widgets.add("confirm", confirmBtn);

        loadBtn = new IconButton(Icon.ENTER, btn -> {
            String loaded = getMenu().loadNBT();
            if (loaded != null) {
                textEditor.setValue(loaded);
            }
        });
        loadBtn.setTooltip(Tooltip.create(Component.translatable(LangDefs.NBT_EXPORT_LOAD.getTranslationKey())));
        widgets.add("load", loadBtn);

        formatBtn = new IconButton(Icon.WRENCH, btn -> format());
        formatBtn.setTooltip(Tooltip.create(Component.translatable(LangDefs.NBT_EXPORT_FORMAT.getTranslationKey())));
        widgets.add("format", formatBtn);
    }

    private void format() {
        String text = textEditor.getValue();
        NBTMatcher.Compiled compiled = NBTMatcher.compile(text);
        if (compiled.isValid()) {
            textEditor.setValue(NbtFormatter.format(text));
        } else {
            formatBtn.setTooltip(Tooltip.create(
                    Component.translatable(LangDefs.NBT_SYNTAX_ERROR.getTranslationKey())
                            .append(" ")
                            .append(Component.literal(compiled.getError() == null ? "" : compiled.getError()))));
        }
    }

    private void save() {
        String text = textEditor.getValue();
        getMenu().updateData(text);
        NBTMatcher.Compiled compiled = NBTMatcher.compile(text);
        if (compiled.isValid()) {
            confirmBtn.setTooltip(Tooltip.create(Component.translatable(LangDefs.NBT_FILTER_SAVED.getTranslationKey())));
        } else {
            confirmBtn.setTooltip(Tooltip.create(
                    Component.translatable(LangDefs.NBT_SYNTAX_ERROR.getTranslationKey())
                            .append(" ")
                            .append(Component.literal(compiled.getError() == null ? "" : compiled.getError()))));
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        if (!initialized) {
            textEditor.setValue(getMenu().host.getData());
            initialized = true;
        }
    }

    @Override
    public boolean keyPressed(int key, int sc, int mod) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            if (textEditor.isFocused()) {
                textEditor.setFocused(false);
                setFocused(null);
                return true;
            }
            onClose();
            return true;
        }
        if (textEditor.isFocused() && minecraft != null) {
            if (minecraft.options.keyInventory.matches(key, sc)
                    || minecraft.options.keyDrop.matches(key, sc)
                    || minecraft.options.keySocialInteractions.matches(key, sc)
                    || minecraft.options.keySwapOffhand.matches(key, sc)
                    || (key >= GLFW.GLFW_KEY_1 && key <= GLFW.GLFW_KEY_9
                    && minecraft.options.keyHotbarSlots[key - GLFW.GLFW_KEY_1].matches(key, sc))) {
                return true;
            }
            if (textEditor.keyPressed(key, sc, mod)) {
                return true;
            }
        }
        return super.keyPressed(key, sc, mod);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (textEditor.isFocused() && textEditor.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (textEditor.mouseClicked(mouseX, mouseY, button)) {
            setFocused(textEditor);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (textEditor.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (textEditor.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (textEditor.mouseScrolled(mouseX, mouseY, scroll)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scroll);
    }
}
