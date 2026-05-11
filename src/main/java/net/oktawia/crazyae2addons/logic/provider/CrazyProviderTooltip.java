package net.oktawia.crazyae2addons.logic.provider;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public final class CrazyProviderTooltip {

    private static final int BASE_ROWS = 8;
    private static final int ROW_SIZE = 9;

    private static final String NBT_FILLED = "filled";
    private static final String NBT_PATTERNS = "patterns";
    private static final String NBT_LEGACY_PATTERNS = "crazy_patterns";
    private static final String NBT_LEGACY_MANAGED = "managed";

    private CrazyProviderTooltip() {
    }

    public record Data(int addedRows, int filled, int totalSlots, int percent) {
    }

    public static Data read(ItemStack stack) {
        int addedRows = 0;
        int filled = 0;

        CompoundTag tag = stack.getTag();

        if (tag != null) {
            addedRows = Math.max(0, CrazyProviderNbt.loadAddedFromAnyKnownFormat(tag, 0));
            filled = readFilledFromAnyKnownFormat(tag);
        }

        int totalSlots = (BASE_ROWS + addedRows) * ROW_SIZE;
        filled = Math.min(Math.max(0, filled), totalSlots);

        int percent = totalSlots > 0
                ? (int) Math.round(100.0D * filled / (double) totalSlots)
                : 0;

        return new Data(addedRows, filled, totalSlots, percent);
    }

    private static int readFilledFromAnyKnownFormat(CompoundTag rootTag) {
        int fromRoot = readFilledFromTag(rootTag);
        if (fromRoot >= 0) {
            return fromRoot;
        }

        CompoundTag providerTag = CrazyProviderNbt.findProviderTag(rootTag);
        int fromProvider = readFilledFromProviderTag(providerTag);
        if (fromProvider >= 0) {
            return fromProvider;
        }

        if (rootTag.contains(CrazyProviderNbt.NBT_BLOCK_ENTITY_TAG, Tag.TAG_COMPOUND)) {
            CompoundTag blockEntityTag = rootTag.getCompound(CrazyProviderNbt.NBT_BLOCK_ENTITY_TAG);

            int fromBlockEntityRoot = readFilledFromTag(blockEntityTag);
            if (fromBlockEntityRoot >= 0) {
                return fromBlockEntityRoot;
            }

            CompoundTag blockEntityProviderTag = CrazyProviderNbt.findProviderTag(blockEntityTag);
            int fromBlockEntityProvider = readFilledFromProviderTag(blockEntityProviderTag);
            if (fromBlockEntityProvider >= 0) {
                return fromBlockEntityProvider;
            }
        }

        return 0;
    }

    private static int readFilledFromProviderTag(CompoundTag providerTag) {
        if (providerTag == null || providerTag.isEmpty()) {
            return -1;
        }

        if (providerTag.contains(CrazyProviderNbt.NBT_LOGIC, Tag.TAG_COMPOUND)) {
            CompoundTag logicTag = providerTag.getCompound(CrazyProviderNbt.NBT_LOGIC);

            int fromLogic = readFilledFromTag(logicTag);
            if (fromLogic >= 0) {
                return fromLogic;
            }
        }

        return readFilledFromTag(providerTag);
    }

    private static int readFilledFromTag(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            return -1;
        }

        if (tag.contains(NBT_FILLED, Tag.TAG_INT)) {
            return Math.max(0, tag.getInt(NBT_FILLED));
        }

        if (tag.contains(NBT_PATTERNS, Tag.TAG_LIST)) {
            return countList(tag.getList(NBT_PATTERNS, Tag.TAG_COMPOUND));
        }

        if (tag.contains(NBT_LEGACY_PATTERNS, Tag.TAG_LIST)) {
            return countList(tag.getList(NBT_LEGACY_PATTERNS, Tag.TAG_COMPOUND));
        }

        if (tag.contains(NBT_LEGACY_MANAGED, Tag.TAG_COMPOUND)) {
            CompoundTag managed = tag.getCompound(NBT_LEGACY_MANAGED);

            if (managed.contains(NBT_FILLED, Tag.TAG_INT)) {
                return Math.max(0, managed.getInt(NBT_FILLED));
            }

            if (managed.contains(NBT_PATTERNS, Tag.TAG_LIST)) {
                return countList(managed.getList(NBT_PATTERNS, Tag.TAG_COMPOUND));
            }

            if (managed.contains(NBT_LEGACY_PATTERNS, Tag.TAG_LIST)) {
                return countList(managed.getList(NBT_LEGACY_PATTERNS, Tag.TAG_COMPOUND));
            }
        }

        return -1;
    }

    private static int countList(ListTag list) {
        return list == null ? 0 : list.size();
    }
}