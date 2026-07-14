package net.oktawia.insaneae2addons.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ResearchRecipeSerializer implements RecipeSerializer<ResearchRecipe> {

    public static final ResearchRecipeSerializer INSTANCE = new ResearchRecipeSerializer();

    @Override
    public ResearchRecipe fromJson(ResourceLocation id, JsonObject json) throws JsonSyntaxException {
        final int duration = reqInt(json, "duration");
        final int ept      = reqInt(json, "energy_per_tick");

        final List<ResearchRecipe.Consumable> consumables = new ArrayList<>();
        if (json.has("consumables")) {
            JsonArray cons = GsonHelper.getAsJsonArray(json, "consumables");
            for (JsonElement el : cons) {
                JsonObject o = el.getAsJsonObject();
                Item item = readItem(reqString(o, "item"));
                int count = GsonHelper.getAsInt(o, "count", 1);
                int computation = GsonHelper.getAsInt(o, "computation", 1);
                consumables.add(new ResearchRecipe.Consumable(item, Math.max(1, count), computation));
            }
        }

        JsonObject un = GsonHelper.getAsJsonObject(json, "unlock");
        final ResourceLocation unlockKey = new ResourceLocation(reqString(un, "key"));
        final String unlockLabel = GsonHelper.getAsString(un, "label", "");
        final String item = GsonHelper.getAsString(un, "item", "");

        return new ResearchRecipe(
                id,
                duration, ept,
                consumables,
                new ResearchRecipe.Unlock(unlockKey, unlockLabel, item)
        );
    }

    @Override
    public ResearchRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        int duration = buf.readVarInt();
        int ept      = buf.readVarInt();

        int consN = buf.readVarInt();
        List<ResearchRecipe.Consumable> consumables = new ArrayList<>(consN);
        for (int i = 0; i < consN; i++) {
            Item item = buf.readRegistryId();
            int count = buf.readVarInt();
            int computation = buf.readVarInt();
            consumables.add(new ResearchRecipe.Consumable(item, count, computation));
        }

        ResourceLocation unlockKey = buf.readResourceLocation();
        String unlockLabel = buf.readUtf(256);
        String item = buf.readUtf(256);

        return new ResearchRecipe(
                id,
                duration, ept,
                consumables,
                new ResearchRecipe.Unlock(unlockKey, unlockLabel, item)
        );
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, ResearchRecipe r) {
        buf.writeVarInt(r.duration);
        buf.writeVarInt(r.energyPerTick);

        buf.writeVarInt(r.consumables.size());
        for (ResearchRecipe.Consumable c : r.consumables) {
            buf.writeRegistryId(ForgeRegistries.ITEMS, c.item);
            buf.writeVarInt(c.count);
            buf.writeVarInt(c.computation);
        }

        buf.writeResourceLocation(r.unlock.key);
        buf.writeUtf(r.unlock.label == null ? "" : r.unlock.label);
        buf.writeUtf(r.unlock.item);
    }

    private static String reqString(JsonObject o, String key) {
        if (!o.has(key)) throw new JsonSyntaxException("Missing '" + key + "'");
        return o.get(key).getAsString();
    }

    private static int reqInt(JsonObject o, String key) {
        if (!o.has(key)) throw new JsonSyntaxException("Missing '" + key + "'");
        return o.get(key).getAsInt();
    }

    private static Item readItem(String id) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        if (item == null) throw new JsonSyntaxException("Unknown item: " + id);
        return item;
    }
}
