package net.oktawia.insaneae2addons.xei.emi;

import java.util.List;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;

public class CradleEmiRecipeHandler implements StandardRecipeHandler<AbstractContainerMenu> {

    @Override
    public List<Slot> getInputSources(AbstractContainerMenu handler) {
        return handler.slots;
    }

    @Override
    public List<Slot> getCraftingSlots(AbstractContainerMenu handler) {
        return handler.slots;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe instanceof CradleEmiRecipe;
    }
}
