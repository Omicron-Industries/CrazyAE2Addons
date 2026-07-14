package net.oktawia.insaneae2addons.recipes;

import net.minecraft.world.item.crafting.RecipeType;
import net.oktawia.insaneae2addons.InsaneAddons;

public class CradleRecipeType implements RecipeType<CradleRecipe> {
    public static final CradleRecipeType INSTANCE = new CradleRecipeType();
    private CradleRecipeType() {}
    @Override public String toString() { return InsaneAddons.makeId("cradle").toString(); }
}
