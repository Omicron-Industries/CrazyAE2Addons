package net.oktawia.insaneae2addons.xei.jei;

import com.lowdragmc.lowdraglib.jei.ModularUIRecipeCategory;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;

import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;

public class CradleCategory extends ModularUIRecipeCategory<CradleWrapper> {

    public static final RecipeType<CradleWrapper> TYPE = RecipeType.create("insaneae2addons", "cradle",
            CradleWrapper.class);

    private final IDrawable background;
    private final IDrawable icon;

    public CradleCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(160, 200);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(InsaneBlockRegistrar.ENTROPY_CRADLE_CONTROLLER_BLOCK.get()));
    }

    @Override
    public RecipeType<CradleWrapper> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(LangDefs.CRADLE_CATEGORY.getTranslationKey());
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }
}
