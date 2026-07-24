package net.oktawia.insaneae2addons.xei.common;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import net.oktawia.insaneae2addons.defs.InsaneMultiblocks;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneRecipes;
import net.oktawia.insaneae2addons.recipes.CradlePattern;
import net.oktawia.insaneae2addons.recipes.CradleRecipe;
import net.oktawia.insaneae2addons.recipes.ResearchRecipe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class InsaneXeiRecipes {

    private InsaneXeiRecipes() {
    }

    public static List<MultiblockEntry> getMultiblockEntries() {
        return List.of(
                new MultiblockEntry("research_unit",
                        new ItemStack(InsaneBlockRegistrar.RESEARCH_UNIT_BLOCK.get()),
                        InsaneMultiblocks.researchUnit(), true),
                new MultiblockEntry("entropy_cradle",
                        new ItemStack(InsaneBlockRegistrar.ENTROPY_CRADLE_CONTROLLER_BLOCK.get()),
                        InsaneMultiblocks.entropyCradle(), true),
                new MultiblockEntry("mob_farm",
                        new ItemStack(InsaneBlockRegistrar.MOB_FARM_CONTROLLER_BLOCK.get()),
                        InsaneMultiblocks.mobFarm(), true),
                new MultiblockEntry("spawner_extractor",
                        new ItemStack(InsaneBlockRegistrar.SPAWNER_EXTRACTOR_CONTROLLER_BLOCK.get()),
                        InsaneMultiblocks.spawnerExtractor(), true),
                new MultiblockEntry("portable_penrose_sphere",
                        new ItemStack(InsaneBlockRegistrar.PORTABLE_PENROSE_SPHERE_CONTROLLER_BLOCK.get()),
                        InsaneMultiblocks.penroseSphere(), true)
        );
    }

    public static List<CradleEntry> getCradleEntries() {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }

        return level.getRecipeManager()
                .getAllRecipesFor(InsaneRecipes.CRADLE_TYPE.get())
                .stream()
                .map(recipe -> new CradleEntry(
                        recipe.getId(),
                        buildInputsFromPattern(recipe.pattern()),
                        new ItemStack(recipe.resultBlock().asItem()),
                        recipe.description()
                ))
                .sorted(Comparator.comparing(entry ->
                        entry.output().getItem().builtInRegistryHolder().key().location().toString()))
                .toList();
    }

    public static List<ResearchEntry> getResearchEntries() {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }

        return level.getRecipeManager()
                .getAllRecipesFor(InsaneRecipes.RESEARCH_TYPE.get())
                .stream()
                .map(recipe -> new ResearchEntry(
                        recipe.getId(),
                        buildInputsFromResearch(recipe),
                        buildDriveOrOutput(recipe),
                        (recipe.unlock.label == null || recipe.unlock.label.isEmpty())
                                ? recipe.unlock.key.toString()
                                : recipe.unlock.label,
                        recipe.unlock.key
                ))
                .sorted(Comparator.comparing(entry -> entry.unlockKey().toString()))
                .toList();
    }

    private static List<ItemStack> buildInputsFromPattern(CradlePattern pattern) {
        Map<Block, Integer> counts = new LinkedHashMap<>();

        Map<String, List<Block>> symbols = pattern.symbolMap();
        List<String[][]> layers = pattern.layers();

        for (int y = 0; y < CradlePattern.SIZE; y++) {
            String[][] layer = layers.get(y);
            for (int z = 0; z < CradlePattern.SIZE; z++) {
                String[] row = layer[z];
                for (int x = 0; x < CradlePattern.SIZE; x++) {
                    String symbol = row[x];
                    if (symbol.equals(".")) {
                        continue;
                    }

                    List<Block> options = symbols.get(symbol);
                    if (options == null || options.isEmpty()) {
                        continue;
                    }

                    Block block = options.get(0);
                    if (block == Blocks.AIR) {
                        continue;
                    }

                    counts.merge(block, 1, Integer::sum);
                }
            }
        }

        List<ItemStack> stacks = new ArrayList<>();
        counts.forEach((block, count) -> {
            ItemStack stack = new ItemStack(block.asItem());
            stack.setCount(count);
            stacks.add(stack);
        });
        return stacks;
    }

    private static List<ItemStack> buildInputsFromResearch(ResearchRecipe recipe) {
        List<ItemStack> stacks = new ArrayList<>();
        for (ResearchRecipe.Consumable consumable : recipe.consumables) {
            stacks.add(new ItemStack(consumable.item).copyWithCount(Math.max(1, consumable.count)));
        }
        return stacks;
    }

    private static ItemStack buildDriveOrOutput(ResearchRecipe recipe) {
        ResourceLocation id = ResourceLocation.tryParse(recipe.unlock.item);
        if (id == null) {
            return ItemStack.EMPTY;
        }
        var item = ForgeRegistries.ITEMS.getValue(id);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }
}
