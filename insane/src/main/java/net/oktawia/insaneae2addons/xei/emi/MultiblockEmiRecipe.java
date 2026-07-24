package net.oktawia.insaneae2addons.xei.emi;

import com.lowdragmc.lowdraglib.emi.ModularEmiRecipe;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.xei.common.MultiblockEntry;
import net.oktawia.insaneae2addons.xei.common.MultiblockStructurePreview;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MultiblockEmiRecipe extends ModularEmiRecipe<WidgetGroup> {

    private final EmiRecipeCategory category;
    private final MultiblockEntry entry;
    private final List<EmiIngredient> catalysts;

    public MultiblockEmiRecipe(MultiblockEntry entry, EmiRecipeCategory category) {
        super(() -> new MultiblockStructurePreview(entry));
        this.category = category;
        this.entry = entry;
        this.catalysts = collectCatalysts(entry);
    }

    private List<EmiIngredient> collectCatalysts(MultiblockEntry entry) {
        List<EmiIngredient> result = new ArrayList<>(super.getCatalysts());
        Set<Block> seen = new LinkedHashSet<>();

        Block controller = Block.byItem(entry.controller().getItem());
        if (controller != Blocks.AIR && seen.add(controller)) {
            result.add(EmiStack.of(controller));
        }

        MultiblockDefinition definition = entry.definition();
        Set<Character> symbols = new LinkedHashSet<>();
        for (MultiblockDefinition.PatternEntry patternEntry : definition.getEntries(Direction.NORTH)) {
            symbols.add(patternEntry.symbol());
        }

        for (char symbol : symbols) {
            MultiblockDefinition.SymbolDef symbolDef = definition.getSymbol(symbol);
            if (symbolDef == null) {
                continue;
            }
            for (Block block : symbolDef.blocks()) {
                if (block != Blocks.AIR && seen.add(block)) {
                    result.add(EmiStack.of(block));
                }
            }
        }

        return result;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return catalysts;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return category;
    }

    @Override
    public ResourceLocation getId() {
        return InsaneAddons.makeId("/multiblock/" + entry.id());
    }
}
