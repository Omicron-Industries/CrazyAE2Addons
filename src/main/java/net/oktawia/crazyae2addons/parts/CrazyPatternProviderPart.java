package net.oktawia.crazyae2addons.parts;

import java.util.List;

import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.IManaged;
import com.lowdragmc.lowdraglib.syncdata.IManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.accessor.IManagedAccessor;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.syncdata.payload.NbtTagPayload;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import lombok.Getter;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.AEItemKey;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.items.parts.PartModels;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import appeng.menu.locator.MenuLocators;
import appeng.parts.PartModel;
import appeng.parts.crafting.PatternProviderPart;
import appeng.util.SettingsFrom;
import appeng.util.inv.AppEngInternalInventory;

import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.CrazyConfig;
import net.oktawia.crazyae2addons.defs.LangDefs;
import net.oktawia.crazyae2addons.defs.regs.CrazyBlockRegistrar;
import net.oktawia.crazyae2addons.defs.regs.CrazyItemRegistrar;
import net.oktawia.crazyae2addons.defs.regs.CrazyMenuRegistrar;
import net.oktawia.crazyae2addons.logic.interfaces.IProviderLogicResizable;
import net.oktawia.crazyae2addons.logic.provider.CrazyProviderNbt;

public class CrazyPatternProviderPart extends PatternProviderPart implements IUpgradeableObject {

    private static final int BASE_SIZE = 8 * 9;
    private static final int ROW_SIZE = 9;

    private static final String NBT_LEGACY_STATE = "crazy_state";
    private static final String NBT_LEGACY_PATTERNS = "crazy_patterns";

    @PartModels
    public static final PartModel MODELS_OFF = new PartModel(
            CrazyAddons.makeId("part/crazy_pattern_provider_part"),
            new ResourceLocation("ae2", "part/interface_off"));

    @PartModels
    public static final PartModel MODELS_ON = new PartModel(
            CrazyAddons.makeId("part/crazy_pattern_provider_part"),
            new ResourceLocation("ae2", "part/interface_on"));

    @PartModels
    public static final PartModel MODELS_HAS_CHANNEL = new PartModel(
            CrazyAddons.makeId("part/crazy_pattern_provider_part"),
            new ResourceLocation("ae2", "part/interface_has_channel"));

    private final PartState state = new PartState(this);

    private final IUpgradeInventory upgrades = UpgradeInventories.forMachine(
            CrazyBlockRegistrar.CRAZY_PATTERN_PROVIDER_BLOCK.get(),
            1,
            () -> {
                if (getHost() != null) {
                    getHost().markForSave();
                }
            });

    private boolean pendingPatternUpdate = false;

    public CrazyPatternProviderPart(IPartItem<?> partItem) {
        super(partItem);

        if (!CrazyConfig.COMMON.CRAZY_PATTERN_PROVIDER_PART_ENABLED.get()) {
            getMainNode().destroy();
        }
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    public int getAdded() {
        return state.getAdded();
    }

    @Override
    protected PatternProviderLogic createLogic() {
        return new PatternProviderLogic(this.getMainNode(), this, BASE_SIZE);
    }

    public void setAdded(int newAdded) {
        if (newAdded == state.getAdded()) {
            return;
        }

        state.setAdded(newAdded);
        applySize();
        requestPatternUpdateWhenReady();
        markForSync();
    }

    public void upgradeOnce() {
        setAdded(state.getAdded() + 1);
    }

    private void applySize() {
        ((IProviderLogicResizable) getLogic()).crazyAE2Addons$setSize(BASE_SIZE + ROW_SIZE * state.getAdded());
    }

    private void markForSync() {
        saveChanges();

        if (getHost() != null) {
            getHost().markForUpdate();
        }
    }

    private boolean canUpdatePatternsNow() {
        return getBlockEntity() != null && getBlockEntity().getLevel() != null;
    }

    private void requestPatternUpdateWhenReady() {
        if (canUpdatePatternsNow()) {
            pendingPatternUpdate = false;
            getLogic().updatePatterns();
        } else {
            pendingPatternUpdate = true;
        }
    }

    private CompoundTag saveProviderData() {
        CompoundTag providerTag = new CompoundTag();

        providerTag.put(CrazyProviderNbt.NBT_STATE, CrazyProviderNbt.saveState(state.getAdded()));

        CompoundTag logicTag = new CompoundTag();
        getLogic().writeToNBT(logicTag);
        providerTag.put(CrazyProviderNbt.NBT_LOGIC, logicTag);

        return providerTag;
    }

    private void writeProviderDataTo(CompoundTag tag) {
        tag.put(CrazyProviderNbt.NBT_PROVIDER, saveProviderData());
    }

    private boolean loadProviderStateBeforeLogic(CompoundTag tag) {
        if (CrazyProviderNbt.hasProviderDataOrLegacyAdded(tag)) {
            state.loadAdded(CrazyProviderNbt.loadAddedFromAnyKnownFormat(tag, state.getAdded()));
            applySize();
            return true;
        }

        applySize();
        return false;
    }

    private boolean loadProviderLogicFromCommonTag(CompoundTag tag) {
        CompoundTag providerTag = CrazyProviderNbt.findProviderTag(tag);

        if (providerTag.isEmpty() || !providerTag.contains(CrazyProviderNbt.NBT_LOGIC, Tag.TAG_COMPOUND)) {
            return false;
        }

        getLogic().readFromNBT(providerTag.getCompound(CrazyProviderNbt.NBT_LOGIC));
        pendingPatternUpdate = true;
        return true;
    }

    private boolean loadProviderDataFromCommonTag(CompoundTag tag) {
        CompoundTag providerTag = CrazyProviderNbt.findProviderTag(tag);

        if (providerTag.isEmpty()) {
            return false;
        }

        boolean loaded = false;

        if (providerTag.contains(CrazyProviderNbt.NBT_STATE, Tag.TAG_COMPOUND)) {
            CompoundTag stateTag = providerTag.getCompound(CrazyProviderNbt.NBT_STATE);
            state.loadAdded(CrazyProviderNbt.loadAdded(stateTag, state.getAdded()));
            applySize();
            loaded = true;
        } else {
            applySize();
        }

        if (providerTag.contains(CrazyProviderNbt.NBT_LOGIC, Tag.TAG_COMPOUND)) {
            getLogic().readFromNBT(providerTag.getCompound(CrazyProviderNbt.NBT_LOGIC));
            pendingPatternUpdate = true;
            loaded = true;
        }

        return loaded;
    }

    private boolean loadLegacyDismantleData(CompoundTag tag) {
        boolean loaded = false;

        if (tag.contains(NBT_LEGACY_STATE, Tag.TAG_COMPOUND)
                || tag.contains(CrazyProviderNbt.NBT_ADDED, Tag.TAG_ANY_NUMERIC)
                || tag.contains(CrazyProviderNbt.NBT_BLOCK_ENTITY_TAG, Tag.TAG_COMPOUND)) {
            state.loadAdded(CrazyProviderNbt.loadAddedFromAnyKnownFormat(tag, state.getAdded()));
            applySize();
            loaded = true;
        } else {
            applySize();
        }

        if (tag.contains(NBT_LEGACY_PATTERNS)) {
            ((AppEngInternalInventory) getLogic().getPatternInv()).readFromNBT(tag, NBT_LEGACY_PATTERNS);
            pendingPatternUpdate = true;
            loaded = true;
        }

        return loaded;
    }

    private void writeProviderDataToMatchingDrops(List<ItemStack> drops) {
        CompoundTag providerTag = saveProviderData();

        for (ItemStack drop : drops) {
            if (drop.getItem() == CrazyItemRegistrar.CRAZY_PATTERN_PROVIDER_PART.get().asItem()) {
                CompoundTag rootTag = drop.getOrCreateTag();
                CrazyProviderNbt.writeProviderTagToItemRoot(rootTag, providerTag);
            }
        }
    }

    @Override
    public void addToWorld() {
        super.addToWorld();

        if (pendingPatternUpdate) {
            requestPatternUpdateWhenReady();
        }
    }

    @Override
    public boolean onPartActivate(Player player, InteractionHand hand, Vec3 pos) {
        if (!CrazyConfig.COMMON.CRAZY_PATTERN_PROVIDER_PART_ENABLED.get()) {
            return true;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        if (heldItem.getItem() == CrazyItemRegistrar.CRAZY_UPGRADE.get().asItem()) {
            if (!player.level().isClientSide) {
                int maxAdd = CrazyConfig.COMMON.CRAZY_PROVIDER_MAX_UPGRADES.get();

                if (maxAdd != -1 && state.getAdded() >= maxAdd) {
                    player.displayClientMessage(
                            Component.translatable(LangDefs.PROVIDER_MAX.getTranslationKey()),
                            true);
                    return true;
                }

                heldItem.shrink(1);
                upgradeOnce();
            }

            return true;
        }

        if (!player.level().isClientSide) {
            MenuOpener.open(
                    CrazyMenuRegistrar.CRAZY_PATTERN_PROVIDER_MENU.get(),
                    player,
                    MenuLocators.forPart(this));
        }

        return true;
    }

    @Override
    public void writeToNBT(CompoundTag data) {
        super.writeToNBT(data);

        data.put(NBT_LEGACY_STATE, state.savePersisted());
        writeProviderDataTo(data);
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        loadProviderStateBeforeLogic(data);

        super.readFromNBT(data);

        loadProviderLogicFromCommonTag(data);
    }

    @Override
    public void writeToStream(FriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeNbt(state.saveSync(true));
    }

    @Override
    public boolean readFromStream(FriendlyByteBuf data) {
        boolean changed = super.readFromStream(data);

        int oldAdded = state.getAdded();
        CompoundTag syncTag = data.readNbt();

        if (syncTag != null) {
            state.loadSync(syncTag);
        }

        if (oldAdded != state.getAdded()) {
            applySize();
            requestPatternUpdateWhenReady();
        }

        return changed || oldAdded != state.getAdded();
    }

    @Override
    public void exportSettings(SettingsFrom mode, CompoundTag output) {
        super.exportSettings(mode, output);

        if (mode == SettingsFrom.DISMANTLE_ITEM) {
            writeProviderDataTo(output);
        }
    }

    @Override
    public void importSettings(SettingsFrom mode, CompoundTag input, @Nullable Player player) {
        super.importSettings(mode, input, player);

        if (mode == SettingsFrom.DISMANTLE_ITEM) {
            boolean loaded = loadProviderDataFromCommonTag(input);

            if (!loaded) {
                loadLegacyDismantleData(input);
            }

            requestPatternUpdateWhenReady();
            markForSync();
        }
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
        writeProviderDataToMatchingDrops(drops);
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        if (CrazyConfig.COMMON.CRAZY_PATTERN_PROVIDER_PART_ENABLED.get()) {
            MenuOpener.open(CrazyMenuRegistrar.CRAZY_PATTERN_PROVIDER_MENU.get(), player, locator);
        }
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        if (CrazyConfig.COMMON.CRAZY_PATTERN_PROVIDER_PART_ENABLED.get()) {
            MenuOpener.returnTo(CrazyMenuRegistrar.CRAZY_PATTERN_PROVIDER_MENU.get(), player, subMenu.getLocator());
        }
    }

    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(CrazyItemRegistrar.CRAZY_PATTERN_PROVIDER_PART.get());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return CrazyItemRegistrar.CRAZY_PATTERN_PROVIDER_PART.get().asItem().getDefaultInstance();
    }

    @Override
    public IPartModel getStaticModels() {
        if (isActive() && isPowered()) {
            return MODELS_HAS_CHANNEL;
        }

        if (isPowered()) {
            return MODELS_ON;
        }

        return MODELS_OFF;
    }

    private static final class PartState implements IManaged {
        private static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(PartState.class);

        private final CrazyPatternProviderPart owner;
        private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

        @Getter
        @Persisted
        @DescSynced
        private int added = 0;

        private PartState(CrazyPatternProviderPart owner) {
            this.owner = owner;
        }

        public void setAdded(int added) {
            this.added = added;
            markFieldDirty("added");
        }

        public void loadAdded(int added) {
            this.added = added;
        }

        public CompoundTag savePersisted() {
            return IManagedAccessor.readManagedFields(this, new CompoundTag());
        }

        public void loadPersisted(CompoundTag tag) {
            IManagedAccessor.writePersistedFields(tag, getSyncStorage().getPersistedFields());
        }

        public CompoundTag saveSync(boolean force) {
            return IManagedAccessor.readSyncedFields(this, new CompoundTag(), force);
        }

        public void loadSync(CompoundTag tag) {
            new IManagedAccessor().writeToReadonlyField(
                    AccessorOp.SYNCED,
                    this,
                    NbtTagPayload.of(tag));
        }

        private void markFieldDirty(String name) {
            getSyncStorage().getFieldByKey(getFieldHolder().getSyncedFieldIndex(name)).markAsDirty();
        }

        @Override
        public ManagedFieldHolder getFieldHolder() {
            return MANAGED_FIELD_HOLDER;
        }

        @Override
        public IManagedStorage getSyncStorage() {
            return syncStorage;
        }

        @Override
        public void onChanged() {
            owner.saveChanges();
        }
    }
}
