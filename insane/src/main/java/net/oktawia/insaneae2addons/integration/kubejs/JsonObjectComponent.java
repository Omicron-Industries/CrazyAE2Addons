package net.oktawia.insaneae2addons.integration.kubejs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.util.MapJS;

public class JsonObjectComponent implements RecipeComponent<JsonObject> {

    public static final JsonObjectComponent INSTANCE = new JsonObjectComponent();

    @Override
    public String componentType() {
        return "json_object";
    }

    @Override
    public Class<?> componentClass() {
        return JsonObject.class;
    }

    @Override
    public JsonElement write(RecipeJS recipe, JsonObject value) {
        return value;
    }

    @Override
    public JsonObject read(RecipeJS recipe, Object from) {
        if (from instanceof JsonObject object) {
            return object;
        }
        return MapJS.json(from);
    }
}
