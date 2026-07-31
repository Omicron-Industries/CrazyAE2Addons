package net.oktawia.crazyae2addons.client.screens.block;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.interfaces.IProgressProvider;

import net.oktawia.crazyae2addons.client.misc.GradientProgressBar;
import net.oktawia.crazyae2addons.client.misc.ResearchDiskPanel;
import net.oktawia.crazyae2addons.defs.LangDefs;
import net.oktawia.crazyae2addons.entities.RecipeFabricatorBE;
import net.oktawia.crazyae2addons.integration.ResearchDiskHook;
import net.oktawia.crazyae2addons.integration.ResearchDiskHooks;
import net.oktawia.crazyae2addons.menus.block.RecipeFabricatorMenu;

public class RecipeFabricatorScreen<C extends RecipeFabricatorMenu> extends AEBaseScreen<C> {

    private static final int PROGRESS_COLOR_FROM = 0xFF7A5A1E;
    private static final int PROGRESS_COLOR_TO = 0xFFFFD780;

    private final GradientProgressBar craftingProgress;

    public RecipeFabricatorScreen(C menu, Inventory inv, Component title, ScreenStyle style) {
        super(menu, inv, title, style);

        this.craftingProgress = new GradientProgressBar(new CraftingProgress(),
                PROGRESS_COLOR_FROM, PROGRESS_COLOR_TO, Component.empty());
        this.widgets.add("crafting_progress", this.craftingProgress);

        List<Slot> diskSlots = menu.getSlots(RecipeFabricatorMenu.RESEARCH_DISK);
        ResearchDiskHook hook = ResearchDiskHooks.get();
        List<Component> diskTooltip = hook != null ? hook.researchSlotTooltip() : List.of();
        this.widgets.add("research_disk", new ResearchDiskPanel(
                diskSlots.isEmpty() ? null : diskSlots.get(0),
                diskTooltip));
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        RecipeFabricatorBE host = getMenu().getHost();
        int progress = host != null ? host.getProgress() : 0;
        int duration = host != null ? host.getDuration() : 10;
        int pct = duration > 0 ? (int) Math.round(100.0 * progress / duration) : 0;

        this.craftingProgress.setFullMsg(Component.translatable(
                LangDefs.RECIPE_FABRICATOR_PROGRESS.getTranslationKey(), pct));
    }

    @Override
    public void drawBG(GuiGraphics gg, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        super.drawBG(gg, offsetX, offsetY, mouseX, mouseY, partialTicks);

        RecipeFabricatorBE host = getMenu().getHost();
        if (host == null) {
            return;
        }

        renderFluidSlot(gg, getInputFluid(host), getMenu().getFluidInSlot());
        renderFluidSlot(gg, getOutputFluid(host), getMenu().getFluidOutSlot());
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        super.render(gg, mouseX, mouseY, partialTick);

        RecipeFabricatorBE host = getMenu().getHost();
        if (host == null) {
            return;
        }

        if (isHovering(getMenu().getFluidInSlot().x, getMenu().getFluidInSlot().y, 16, 16, mouseX, mouseY)) {
            renderFluidTooltip(
                    gg,
                    getInputFluid(host),
                    mouseX,
                    mouseY,
                    Component.translatable(LangDefs.RECIPE_FABRICATOR_FLUID_IN.getTranslationKey()));
        }

        if (isHovering(getMenu().getFluidOutSlot().x, getMenu().getFluidOutSlot().y, 16, 16, mouseX, mouseY)) {
            renderFluidTooltip(
                    gg,
                    getOutputFluid(host),
                    mouseX,
                    mouseY,
                    Component.translatable(LangDefs.RECIPE_FABRICATOR_FLUID_OUT.getTranslationKey()));
        }
    }

    private final class CraftingProgress implements IProgressProvider {

        @Override
        public int getCurrentProgress() {
            RecipeFabricatorBE host = getMenu().getHost();
            return host != null ? host.getProgress() : 0;
        }

        @Override
        public int getMaxProgress() {
            RecipeFabricatorBE host = getMenu().getHost();
            return host != null ? Math.max(1, host.getDuration()) : 10;
        }
    }

    private static FluidStack getInputFluid(RecipeFabricatorBE host) {
        try {
            return host.getMenuInputHandler().getFluidInTank(0);
        } catch (Exception ignored) {
            return FluidStack.EMPTY;
        }
    }

    private static FluidStack getOutputFluid(RecipeFabricatorBE host) {
        try {
            return host.getMenuOutputHandler().getFluidInTank(0);
        } catch (Exception ignored) {
            return FluidStack.EMPTY;
        }
    }

    private void renderFluidTooltip(GuiGraphics gg, FluidStack fluidStack, int mouseX, int mouseY, Component title) {
        if (fluidStack == null || fluidStack.isEmpty()) {
            gg.renderComponentTooltip(
                    this.font,
                    List.of(
                            title,
                            Component.translatable(LangDefs.EMPTY.getTranslationKey())),
                    mouseX,
                    mouseY);
            return;
        }

        gg.renderComponentTooltip(
                this.font,
                List.of(
                        title,
                        fluidStack.getDisplayName(),
                        Component.translatable(LangDefs.MB_AMOUNT.getTranslationKey(), fluidStack.getAmount())),
                mouseX,
                mouseY);
    }

    private void renderFluidSlot(GuiGraphics gg, FluidStack fluidStack, Slot slot) {
        if (fluidStack == null || fluidStack.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        var ext = IClientFluidTypeExtensions.of(fluidStack.getFluid());

        ResourceLocation stillTexture = ext.getStillTexture();
        if (stillTexture == null) {
            return;
        }

        int tint = ext.getTintColor();
        TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);

        int x = this.leftPos + slot.x;
        int y = this.topPos + slot.y;

        float a = ((tint >> 24) & 0xFF) / 255f;
        float r = ((tint >> 16) & 0xFF) / 255f;
        float g = ((tint >> 8) & 0xFF) / 255f;
        float b = (tint & 0xFF) / 255f;

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(r, g, b, a == 0 ? 1f : a);

        gg.blit(x, y, 0, 16, 16, sprite);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }
}
