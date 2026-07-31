package net.oktawia.insaneae2addons.client.screens.part;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Settings;
import appeng.api.config.StorageFilter;
import appeng.api.config.YesNo;
import appeng.client.gui.Icon;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;

import net.oktawia.crazyae2addons.client.misc.IconButton;
import net.oktawia.crazyae2addons.client.misc.MultilineTextFieldWidget;
import net.oktawia.insaneae2addons.client.utils.NbtMatcherHighlight;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.logic.nbt.NBTMatcher;
import net.oktawia.insaneae2addons.menus.part.NbtStorageBusMenu;
import net.oktawia.insaneae2addons.util.NbtFormatter;

public class NbtStorageBusScreen<C extends NbtStorageBusMenu> extends UpgradeableScreen<C> {

    private final MultilineTextFieldWidget textEditor;
    private final IconButton confirmBtn;
    private final IconButton loadBtn;
    private final IconButton formatBtn;
    private final SettingToggleButton<AccessRestriction> rwMode;
    private final SettingToggleButton<StorageFilter> storageFilter;
    private final SettingToggleButton<YesNo> filterOnExtract;

    private boolean initialized = false;

    public NbtStorageBusScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        textEditor = new MultilineTextFieldWidget(Minecraft.getInstance().font, 0, 0, 205, 135, Component.empty());
        textEditor.setDefaultTextColor(0xFFFFFFFF);
        textEditor.setHighlightRules(NbtMatcherHighlight.rules());
        widgets.add("data", textEditor);

        confirmBtn = new IconButton(Icon.COPY_MODE_ON, btn -> save());
        confirmBtn.setTooltip(Tooltip.create(Component.translatable(LangDefs.NBT_STORAGE_CONFIRM.getTranslationKey())));
        widgets.add("confirm", confirmBtn);

        loadBtn = new IconButton(Icon.ENTER, btn -> {
            String loaded = getMenu().loadNBT();
            if (loaded != null) {
                textEditor.setValue(loaded);
            }
        });
        loadBtn.setTooltip(Tooltip.create(Component.translatable(LangDefs.NBT_STORAGE_LOAD.getTranslationKey())));
        widgets.add("load", loadBtn);

        formatBtn = new IconButton(Icon.WRENCH, btn -> format());
        formatBtn.setTooltip(Tooltip.create(Component.translatable(LangDefs.NBT_STORAGE_FORMAT.getTranslationKey())));
        widgets.add("format", formatBtn);

        widgets.addOpenPriorityButton();

        this.rwMode = new ServerSettingToggleButton<>(Settings.ACCESS, AccessRestriction.READ_WRITE);
        this.storageFilter = new ServerSettingToggleButton<>(Settings.STORAGE_FILTER, StorageFilter.EXTRACTABLE_ONLY);
        this.filterOnExtract = new ServerSettingToggleButton<>(Settings.FILTER_ON_EXTRACT, YesNo.YES);

        this.addToLeftToolbar(this.storageFilter);
        this.addToLeftToolbar(this.filterOnExtract);
        this.addToLeftToolbar(this.rwMode);
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
            confirmBtn
                    .setTooltip(Tooltip.create(Component.translatable(LangDefs.NBT_FILTER_SAVED.getTranslationKey())));
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
        this.storageFilter.set(getMenu().getStorageFilter());
        this.rwMode.set(getMenu().getReadWriteMode());
        this.filterOnExtract.set(getMenu().getFilterOnExtract());
    }

    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (textEditor.mouseScrolled(x, y, delta)) {
            return true;
        }
        return super.mouseScrolled(x, y, delta);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (textEditor.mouseClicked(x, y, button)) {
            setFocused(textEditor);
            return true;
        }
        return super.mouseClicked(x, y, button);
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (textEditor.mouseDragged(x, y, button, dx, dy)) {
            return true;
        }
        return super.mouseDragged(x, y, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        if (textEditor.mouseReleased(x, y, button)) {
            return true;
        }
        return super.mouseReleased(x, y, button);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (textEditor.isFocused() && textEditor.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int key, int sc, int mod) {
        if (textEditor.isFocused() && minecraft != null) {
            if (minecraft.options.keyInventory.matches(key, sc)
                    || minecraft.options.keyDrop.matches(key, sc)) {
                return true;
            }
            if (textEditor.keyPressed(key, sc, mod)) {
                return true;
            }
        }
        return super.keyPressed(key, sc, mod);
    }
}
