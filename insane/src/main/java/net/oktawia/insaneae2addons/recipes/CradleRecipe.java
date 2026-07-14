package net.oktawia.insaneae2addons.recipes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.oktawia.insaneae2addons.defs.regs.InsaneRecipes;

public class CradleRecipe implements Recipe<CradleContext> {

    private final ResourceLocation id;
    private final CradlePattern pattern;
    private final Block resultBlock;
    private final String description;

    public CradleRecipe(ResourceLocation id, CradlePattern pattern, Block resultBlock, String description) {
        this.id = id;
        this.pattern = pattern;
        this.resultBlock = resultBlock;
        this.description = description;
    }

    public CradlePattern pattern() { return pattern; }
    public Block resultBlock() { return resultBlock; }
    public String description() { return description; }

    @Override public boolean matches(CradleContext ctx, Level level) {
        return pattern.matches(level, ctx.origin(), ctx.facing());
    }

    @Override public ItemStack assemble(CradleContext ctx, RegistryAccess regs) { return ItemStack.EMPTY; }
    @Override public boolean canCraftInDimensions(int w, int h) { return true; }
    @Override public ItemStack getResultItem(RegistryAccess regs) { return ItemStack.EMPTY; }
    @Override public ResourceLocation getId() { return id; }
    @Override public RecipeSerializer<?> getSerializer() { return InsaneRecipes.CRADLE_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return InsaneRecipes.CRADLE_TYPE.get(); }
    @Override public boolean isSpecial() { return true; }
}
