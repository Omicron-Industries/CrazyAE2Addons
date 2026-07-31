package net.oktawia.insaneae2addons.items;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.items.AEBaseItem;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;

import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.logic.DataHost;

public class DataDrive extends AEBaseItem implements IMenuItem {

    private static final String TAG_ROOT = "InsaneDataDrive";
    private static final String TAG_KEYS = "keys";

    public DataDrive(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static Set<ResourceLocation> getUnlockedKeys(ItemStack stack) {
        Set<ResourceLocation> out = new LinkedHashSet<>();
        if (stack.isEmpty()) {
            return out;
        }

        CompoundTag root = stack.getTagElement(TAG_ROOT);
        if (root == null || !root.contains(TAG_KEYS, Tag.TAG_LIST)) {
            return out;
        }

        ListTag list = root.getList(TAG_KEYS, Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            ResourceLocation key = ResourceLocation.tryParse(list.getString(i));
            if (key != null) {
                out.add(key);
            }
        }
        return out;
    }

    public static boolean hasKey(ItemStack stack, ResourceLocation key) {
        if (stack.isEmpty()) {
            return false;
        }

        CompoundTag root = stack.getTagElement(TAG_ROOT);
        if (root == null || !root.contains(TAG_KEYS, Tag.TAG_LIST)) {
            return false;
        }

        String expected = key.toString();
        ListTag list = root.getList(TAG_KEYS, Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            if (expected.equals(list.getString(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean addKey(ItemStack stack, ResourceLocation key) {
        if (stack.isEmpty()) {
            return false;
        }

        CompoundTag root = stack.getOrCreateTagElement(TAG_ROOT);
        ListTag list = root.contains(TAG_KEYS, Tag.TAG_LIST)
                ? root.getList(TAG_KEYS, Tag.TAG_STRING)
                : new ListTag();

        String value = key.toString();
        for (int i = 0; i < list.size(); i++) {
            if (value.equals(list.getString(i))) {
                return false;
            }
        }

        list.add(StringTag.valueOf(value));
        root.put(TAG_KEYS, list);
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            MenuOpener.open(InsaneMenuRegistrar.DATA_DRIVE_MENU.get(), player, MenuLocators.forHand(player, hand));
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    @Override
    public @Nullable ItemMenuHost getMenuHost(Player player, int inventorySlot, ItemStack stack,
            @Nullable BlockPos pos) {
        return new DataHost(player, inventorySlot, stack);
    }
}
