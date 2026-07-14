package net.oktawia.insaneae2addons.defs.recipes;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemRecipes {

    public record RecipeDef(
            String id,
            @Nullable String pattern,
            Map<Character, Item> keys,
            List<Item> shapelessIngredients,
            Item output,
            int count
    ) {}

    private static final List<RecipeDef> RECIPES = new ArrayList<>();

    public static List<RecipeDef> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    public static Builder recipe(String id) {
        return new Builder(id);
    }

    public static void registerRecipes() {
        recipe("xpshard_to_experience_bottle")
                .shapeless(Items.GLASS_BOTTLE,
                        InsaneItemRegistrar.XP_SHARD.get(),
                        InsaneItemRegistrar.XP_SHARD.get())
                .output(Items.EXPERIENCE_BOTTLE)
                .register();

        recipe("nbt_view_cell")
                .shaped("DI")
                .define('D', AEItems.VIEW_CELL.asItem())
                .define('I', Items.NAME_TAG)
                .output(InsaneItemRegistrar.NBT_VIEW_CELL.get())
                .register();

        recipe("nbt_export_bus")
                .shaped("ET/TL")
                .define('E', AEParts.EXPORT_BUS.asItem())
                .define('T', Items.NAME_TAG)
                .define('L', AEItems.LOGIC_PROCESSOR.asItem())
                .output(InsaneItemRegistrar.NBT_EXPORT_BUS.get())
                .register();

        recipe("nbt_storage_bus")
                .shaped("ET/TL")
                .define('E', AEParts.STORAGE_BUS.asItem())
                .define('T', Items.NAME_TAG)
                .define('L', AEItems.LOGIC_PROCESSOR.asItem())
                .output(InsaneItemRegistrar.NBT_STORAGE_BUS.get())
                .register();

        recipe("player_upgrade_card")
                .shaped("CE")
                .define('C', AEItems.ADVANCED_CARD.asItem())
                .define('E', Items.IRON_PICKAXE)
                .output(InsaneItemRegistrar.PLAYER_UPGRADE_CARD.get())
                .register();

        recipe("automation_upgrade_card")
                .shaped("CE")
                .define('C', AEItems.ADVANCED_CARD.asItem())
                .define('E', AEBlocks.CRAFTING_UNIT.asItem())
                .output(InsaneItemRegistrar.AUTOMATION_UPGRADE_CARD.get())
                .register();
    }

    public static class Builder {
        private final String id;
        private String pattern = null;
        private final Map<Character, Item> keys = new LinkedHashMap<>();
        private final List<Item> shapelessIngredients = new ArrayList<>();
        private Item output = null;
        private int count = 1;

        private Builder(String id) {
            this.id = id;
        }

        public Builder shaped(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder define(char key, ItemLike item) {
            keys.put(key, item.asItem());
            return this;
        }

        public Builder shapeless(ItemLike... ingredients) {
            for (var i : ingredients) shapelessIngredients.add(i.asItem());
            return this;
        }

        public Builder output(ItemLike item) {
            this.output = item.asItem();
            return this;
        }

        public Builder output(ItemLike item, int count) {
            this.output = item.asItem();
            this.count = count;
            return this;
        }

        public void register() {
            RECIPES.add(new RecipeDef(id, pattern, Map.copyOf(keys), List.copyOf(shapelessIngredients), output, count));
        }
    }
}
