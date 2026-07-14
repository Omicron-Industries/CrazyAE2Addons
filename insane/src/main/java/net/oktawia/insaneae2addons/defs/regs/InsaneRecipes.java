package net.oktawia.insaneae2addons.defs.regs;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.oktawia.insaneae2addons.recipes.CradleRecipe;
import net.oktawia.insaneae2addons.recipes.CradleRecipeSerializer;
import net.oktawia.insaneae2addons.recipes.CradleRecipeType;
import net.oktawia.insaneae2addons.recipes.ResearchRecipe;
import net.oktawia.insaneae2addons.recipes.ResearchRecipeSerializer;
import net.oktawia.insaneae2addons.recipes.ResearchRecipeType;

import static net.oktawia.insaneae2addons.InsaneAddons.MODID;

public final class InsaneRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);

    public static final RegistryObject<RecipeSerializer<ResearchRecipe>> RESEARCH_SERIALIZER =
            RECIPE_SERIALIZERS.register("research", () -> ResearchRecipeSerializer.INSTANCE);

    public static final RegistryObject<RecipeType<ResearchRecipe>> RESEARCH_TYPE =
            RECIPE_TYPES.register("research", () -> ResearchRecipeType.INSTANCE);

    public static final RegistryObject<RecipeSerializer<CradleRecipe>> CRADLE_SERIALIZER =
            RECIPE_SERIALIZERS.register("cradle", () -> CradleRecipeSerializer.INSTANCE);

    public static final RegistryObject<RecipeType<CradleRecipe>> CRADLE_TYPE =
            RECIPE_TYPES.register("cradle", () -> CradleRecipeType.INSTANCE);

    private InsaneRecipes() {
    }
}
