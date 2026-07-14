package net.oktawia.insaneae2addons.xei.emi;

import com.lowdragmc.lowdraglib.emi.ModularEmiRecipe;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.xei.common.ResearchEntry;
import net.oktawia.insaneae2addons.xei.common.ResearchPreview;

public class ResearchEmiRecipe extends ModularEmiRecipe<WidgetGroup> {

    private final EmiRecipeCategory category;
    private final ResearchEntry entry;

    public ResearchEmiRecipe(ResearchEntry entry, EmiRecipeCategory category) {
        super(() -> new ResearchPreview(
                entry.recipeId(), entry.inputs(), entry.driveOrOutput()
        ));
        this.category = category;
        this.entry = entry;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return category;
    }

    @Override
    public ResourceLocation getId() {
        return InsaneAddons.makeId("/research/" + entry.unlockKey().toString().replace(':', '/'));
    }
}
