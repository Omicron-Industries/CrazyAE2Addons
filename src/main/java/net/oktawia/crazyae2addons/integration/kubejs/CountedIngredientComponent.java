package net.oktawia.crazyae2addons.integration.kubejs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.ItemMatch;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;

public class CountedIngredientComponent implements RecipeComponent<InputItem> {

    public static final CountedIngredientComponent INSTANCE = new CountedIngredientComponent();

    @Override
    public String componentType() {
        return "counted_ingredient";
    }

    @Override
    public ComponentRole role() {
        return ComponentRole.INPUT;
    }

    @Override
    public Class<?> componentClass() {
        return InputItem.class;
    }

    @Override
    public boolean hasPriority(RecipeJS recipe, Object from) {
        return recipe.inputItemHasPriority(from);
    }

    @Override
    public InputItem read(RecipeJS recipe, Object from) {
        if (from instanceof JsonObject json && (json.has("item") || json.has("tag") || json.has("type"))) {
            return InputItem.of(Ingredient.fromJson(json), GsonHelper.getAsInt(json, "count", 1));
        }

        return recipe.readInputItem(from);
    }

    @Override
    public JsonElement write(RecipeJS recipe, InputItem value) {
        JsonElement ingredient = value.ingredient.toJson();

        if (!(ingredient instanceof JsonObject json)) {
            throw new RecipeExceptionJS("Fabrication input has to be a single item or tag, got " + ingredient);
        }

        JsonObject entry = json.deepCopy();
        entry.addProperty("count", value.count);
        return entry;
    }

    @Override
    public boolean isInput(RecipeJS recipe, InputItem value, ReplacementMatch match) {
        return match instanceof ItemMatch m && value.validForMatching() && m.contains(value.ingredient);
    }

    @Override
    public String checkEmpty(RecipeKey<InputItem> key, InputItem value) {
        if (value.isEmpty()) {
            return "Ingredient '" + key.name + "' can't be empty!";
        }

        return "";
    }

    @Override
    public String toString() {
        return componentType();
    }
}
