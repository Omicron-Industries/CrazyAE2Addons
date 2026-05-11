package net.oktawia.crazyae2addons.recipes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.oktawia.crazyae2addons.defs.regs.CrazyBlockRegistrar;
import net.oktawia.crazyae2addons.defs.regs.CrazyItemRegistrar;
import net.oktawia.crazyae2addons.defs.regs.CrazyRecipes;
import net.oktawia.crazyae2addons.logic.provider.CrazyProviderNbt;

public class CrazyProviderConversionRecipe extends CustomRecipe {

    public CrazyProviderConversionRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        ItemStack provider = ItemStack.EMPTY;
        int nonEmpty = 0;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);

            if (stack.isEmpty()) {
                continue;
            }

            nonEmpty++;

            if (!isCrazyProvider(stack)) {
                return false;
            }

            provider = stack;
        }

        return nonEmpty == 1 && !provider.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
        ItemStack input = findInputProvider(inv);

        if (input.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack output = createOppositeProvider(input);

        if (output.isEmpty()) {
            return ItemStack.EMPTY;
        }

        copyProviderNbt(input, output);
        return output;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return CrazyBlockRegistrar.CRAZY_PATTERN_PROVIDER_BLOCK.get().asItem().getDefaultInstance();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CrazyRecipes.CRAZY_PROVIDER_CONVERSION.get();
    }

    private static ItemStack findInputProvider(CraftingContainer inv) {
        ItemStack found = ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);

            if (stack.isEmpty()) {
                continue;
            }

            if (!isCrazyProvider(stack)) {
                return ItemStack.EMPTY;
            }

            if (!found.isEmpty()) {
                return ItemStack.EMPTY;
            }

            found = stack;
        }

        return found;
    }

    private static boolean isCrazyProvider(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();

        return item == CrazyItemRegistrar.CRAZY_PATTERN_PROVIDER_PART.get().asItem()
                || item == CrazyBlockRegistrar.CRAZY_PATTERN_PROVIDER_BLOCK.get().asItem();
    }

    private static boolean isCrazyProviderBlock(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() == CrazyBlockRegistrar.CRAZY_PATTERN_PROVIDER_BLOCK.get().asItem();
    }

    private static ItemStack createOppositeProvider(ItemStack input) {
        Item item = input.getItem();

        if (item == CrazyItemRegistrar.CRAZY_PATTERN_PROVIDER_PART.get().asItem()) {
            return CrazyBlockRegistrar.CRAZY_PATTERN_PROVIDER_BLOCK.get().asItem().getDefaultInstance();
        }

        if (item == CrazyBlockRegistrar.CRAZY_PATTERN_PROVIDER_BLOCK.get().asItem()) {
            return CrazyItemRegistrar.CRAZY_PATTERN_PROVIDER_PART.get().asItem().getDefaultInstance();
        }

        return ItemStack.EMPTY;
    }

    private static void copyProviderNbt(ItemStack input, ItemStack output) {
        CompoundTag inputRootTag = input.getTag();

        if (inputRootTag == null) {
            return;
        }

        CompoundTag providerTag = CrazyProviderNbt.buildProviderTagFromAnyKnownFormat(inputRootTag, 0);

        if (providerTag.isEmpty()) {
            return;
        }

        CompoundTag outputRootTag = output.getOrCreateTag();

        CrazyProviderNbt.writeProviderTagToItemRoot(outputRootTag, providerTag);

        if (isCrazyProviderBlock(output)) {
            CrazyProviderNbt.writeProviderTagToBlockEntityTag(outputRootTag, providerTag);
        }
    }
}