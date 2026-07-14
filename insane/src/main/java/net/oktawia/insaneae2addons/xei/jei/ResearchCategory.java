package net.oktawia.insaneae2addons.xei.jei;

import com.lowdragmc.lowdraglib.jei.ModularUIRecipeCategory;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;
import org.jetbrains.annotations.Nullable;

public class ResearchCategory extends ModularUIRecipeCategory<ResearchWrapper> {

    public static final RecipeType<ResearchWrapper> TYPE =
            RecipeType.create("insaneae2addons", "research", ResearchWrapper.class);

    private final IDrawable background;
    private final IDrawable icon;

    public ResearchCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(160, 200);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(InsaneItemRegistrar.DATA_DRIVE.get()));
    }

    @Override
    public RecipeType<ResearchWrapper> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(LangDefs.RESEARCH_CATEGORY.getTranslationKey());
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
