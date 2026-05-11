package net.oktawia.crazyae2addons.logic.provider;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public final class CrazyProviderNbt {

    public static final String NBT_PROVIDER = "crazy_provider";
    public static final String NBT_STATE = "state";
    public static final String NBT_LOGIC = "logic";
    public static final String NBT_ADDED = "added";
    public static final String NBT_BLOCK_ENTITY_TAG = "BlockEntityTag";

    public static final String NBT_LEGACY_STATE = "crazy_state";
    public static final String NBT_LEGACY_PATTERNS = "crazy_patterns";
    public static final String NBT_PATTERNS = "patterns";

    private CrazyProviderNbt() {
    }

    public static CompoundTag saveState(int added) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(NBT_ADDED, added);
        return tag;
    }

    public static int loadAdded(CompoundTag stateTag, int fallback) {
        if (stateTag.contains(NBT_ADDED, Tag.TAG_ANY_NUMERIC)) {
            return stateTag.getInt(NBT_ADDED);
        }

        return fallback;
    }

    public static CompoundTag findProviderTag(CompoundTag rootTag) {
        if (rootTag.contains(NBT_PROVIDER, Tag.TAG_COMPOUND)) {
            return rootTag.getCompound(NBT_PROVIDER);
        }

        if (rootTag.contains(NBT_BLOCK_ENTITY_TAG, Tag.TAG_COMPOUND)) {
            CompoundTag blockEntityTag = rootTag.getCompound(NBT_BLOCK_ENTITY_TAG);

            if (blockEntityTag.contains(NBT_PROVIDER, Tag.TAG_COMPOUND)) {
                return blockEntityTag.getCompound(NBT_PROVIDER);
            }
        }

        return new CompoundTag();
    }

    public static int loadAddedFromAnyKnownFormat(CompoundTag rootTag, int fallback) {
        CompoundTag providerTag = findProviderTag(rootTag);

        if (!providerTag.isEmpty() && providerTag.contains(NBT_STATE, Tag.TAG_COMPOUND)) {
            return loadAdded(providerTag.getCompound(NBT_STATE), fallback);
        }

        if (rootTag.contains(NBT_LEGACY_STATE, Tag.TAG_COMPOUND)) {
            return loadAdded(rootTag.getCompound(NBT_LEGACY_STATE), fallback);
        }

        if (rootTag.contains(NBT_ADDED, Tag.TAG_ANY_NUMERIC)) {
            return rootTag.getInt(NBT_ADDED);
        }

        if (rootTag.contains(NBT_BLOCK_ENTITY_TAG, Tag.TAG_COMPOUND)) {
            CompoundTag blockEntityTag = rootTag.getCompound(NBT_BLOCK_ENTITY_TAG);

            CompoundTag nestedProviderTag = findProviderTag(blockEntityTag);
            if (!nestedProviderTag.isEmpty() && nestedProviderTag.contains(NBT_STATE, Tag.TAG_COMPOUND)) {
                return loadAdded(nestedProviderTag.getCompound(NBT_STATE), fallback);
            }

            if (blockEntityTag.contains(NBT_LEGACY_STATE, Tag.TAG_COMPOUND)) {
                return loadAdded(blockEntityTag.getCompound(NBT_LEGACY_STATE), fallback);
            }

            if (blockEntityTag.contains(NBT_ADDED, Tag.TAG_ANY_NUMERIC)) {
                return blockEntityTag.getInt(NBT_ADDED);
            }
        }

        return fallback;
    }

    public static boolean hasProviderDataOrLegacyAdded(CompoundTag rootTag) {
        if (!findProviderTag(rootTag).isEmpty()) {
            return true;
        }

        if (rootTag.contains(NBT_LEGACY_STATE, Tag.TAG_COMPOUND)) {
            return true;
        }

        if (rootTag.contains(NBT_ADDED, Tag.TAG_ANY_NUMERIC)) {
            return true;
        }

        if (rootTag.contains(NBT_BLOCK_ENTITY_TAG, Tag.TAG_COMPOUND)) {
            CompoundTag blockEntityTag = rootTag.getCompound(NBT_BLOCK_ENTITY_TAG);

            return !findProviderTag(blockEntityTag).isEmpty()
                    || blockEntityTag.contains(NBT_LEGACY_STATE, Tag.TAG_COMPOUND)
                    || blockEntityTag.contains(NBT_ADDED, Tag.TAG_ANY_NUMERIC);
        }

        return false;
    }

    public static CompoundTag buildProviderTagFromAnyKnownFormat(CompoundTag rootTag, int fallbackAdded) {
        CompoundTag providerTag = findProviderTag(rootTag);
        if (!providerTag.isEmpty()) {
            return providerTag.copy();
        }

        boolean hasLegacyState = hasProviderDataOrLegacyAdded(rootTag);
        CompoundTag legacyLogic = findLegacyLogicTag(rootTag);

        if (!hasLegacyState && legacyLogic.isEmpty()) {
            return new CompoundTag();
        }

        CompoundTag out = new CompoundTag();
        out.put(NBT_STATE, saveState(loadAddedFromAnyKnownFormat(rootTag, fallbackAdded)));

        if (!legacyLogic.isEmpty()) {
            out.put(NBT_LOGIC, legacyLogic);
        }

        return out;
    }

    private static CompoundTag findLegacyLogicTag(CompoundTag rootTag) {
        CompoundTag logicTag = new CompoundTag();

        copyPatternListIfPresent(rootTag, logicTag);

        if (!logicTag.isEmpty()) {
            return logicTag;
        }

        if (rootTag.contains(NBT_BLOCK_ENTITY_TAG, Tag.TAG_COMPOUND)) {
            CompoundTag blockEntityTag = rootTag.getCompound(NBT_BLOCK_ENTITY_TAG);
            copyPatternListIfPresent(blockEntityTag, logicTag);
        }

        return logicTag;
    }

    private static void copyPatternListIfPresent(CompoundTag source, CompoundTag logicTag) {
        Tag patterns = source.get(NBT_PATTERNS);
        if (patterns != null) {
            logicTag.put(NBT_PATTERNS, patterns.copy());
            return;
        }

        Tag legacyPatterns = source.get(NBT_LEGACY_PATTERNS);
        if (legacyPatterns != null) {
            logicTag.put(NBT_PATTERNS, legacyPatterns.copy());
        }
    }

    public static void writeProviderTagToItemRoot(CompoundTag rootTag, CompoundTag providerTag) {
        rootTag.put(NBT_PROVIDER, providerTag.copy());
    }

    public static void writeProviderTagToBlockEntityTag(CompoundTag rootTag, CompoundTag providerTag) {
        CompoundTag blockEntityTag = rootTag.contains(NBT_BLOCK_ENTITY_TAG, Tag.TAG_COMPOUND)
                ? rootTag.getCompound(NBT_BLOCK_ENTITY_TAG)
                : new CompoundTag();

        blockEntityTag.put(NBT_PROVIDER, providerTag.copy());
        rootTag.put(NBT_BLOCK_ENTITY_TAG, blockEntityTag);
    }
}