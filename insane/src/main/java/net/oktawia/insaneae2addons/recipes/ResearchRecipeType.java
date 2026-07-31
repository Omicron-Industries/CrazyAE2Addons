package net.oktawia.insaneae2addons.recipes;

import net.minecraft.world.item.crafting.RecipeType;

import net.oktawia.insaneae2addons.InsaneAddons;

public class ResearchRecipeType implements RecipeType<ResearchRecipe> {
    public static final ResearchRecipeType INSTANCE = new ResearchRecipeType();

    private ResearchRecipeType() {
    }

    @Override
    public String toString() {
        return InsaneAddons.makeId("research").toString();
    }
}
