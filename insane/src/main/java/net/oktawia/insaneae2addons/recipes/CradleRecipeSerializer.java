package net.oktawia.insaneae2addons.recipes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class CradleRecipeSerializer implements RecipeSerializer<CradleRecipe> {

    public static final CradleRecipeSerializer INSTANCE = new CradleRecipeSerializer();
    private static final Gson GSON = new GsonBuilder().create();

    @Override
    public CradleRecipe fromJson(ResourceLocation id, JsonObject json) {
        CradlePattern pattern = CradlePattern.fromJson(json.getAsJsonObject("pattern"));

        Block resultBlock = ForgeRegistries.BLOCKS.getValue(
                new ResourceLocation(json.get("result_block").getAsString()));

        String description = json.has("description") ? json.get("description").getAsString() : "";

        return new CradleRecipe(id, pattern, resultBlock, description);
    }

    @Override
    public CradleRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        JsonObject patJson = JsonParser.parseString(buf.readUtf(32767)).getAsJsonObject();
        CradlePattern pattern = CradlePattern.fromJson(patJson);

        Block resultBlock = ForgeRegistries.BLOCKS.getValue(buf.readResourceLocation());
        String description = buf.readUtf();

        return new CradleRecipe(id, pattern, resultBlock, description);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, CradleRecipe recipe) {
        buf.writeUtf(GSON.toJson(recipe.pattern().toJson()));
        buf.writeResourceLocation(ForgeRegistries.BLOCKS.getKey(recipe.resultBlock()));
        buf.writeUtf(recipe.description());
    }
}
