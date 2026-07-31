package net.oktawia.crazyae2addons.entities;

import java.util.List;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.UpdateListener;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import lombok.Getter;

import appeng.api.stacks.AEItemKey;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import appeng.util.SettingsFrom;
import appeng.util.inv.AppEngInternalInventory;

import net.oktawia.crazyae2addons.CrazyConfig;
import net.oktawia.crazyae2addons.defs.regs.CrazyBlockEntityRegistrar;
import net.oktawia.crazyae2addons.defs.regs.CrazyBlockRegistrar;
import net.oktawia.crazyae2addons.defs.regs.CrazyMenuRegistrar;
import net.oktawia.crazyae2addons.logic.interfaces.IProviderLogicResizable;
import net.oktawia.crazyae2addons.logic.provider.CrazyProviderNbt;
import net.oktawia.crazyae2addons.network.NetworkHandler;
import net.oktawia.crazyae2addons.network.packets.SyncBlockClientPacket;
import net.oktawia.crazyae2addons.util.IManagedBEHelper;

@Getter
public class CrazyPatternProviderBE extends PatternProviderBlockEntity implements IManagedBEHelper, IUpgradeableObject {

    private static final int BASE_SIZE = 8 * 9;
    private static final int ROW_SIZE = 9;

    private static final String NBT_LEGACY_PATTERNS = "crazy_patterns";

    private final IUpgradeInventory upgrades = UpgradeInventories.forMachine(
            CrazyBlockRegistrar.CRAZY_PATTERN_PROVIDER_BLOCK.get(),
            1,
            this::setChanged);

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            CrazyPatternProviderBE.class);

    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Persisted
    @DescSynced
    @UpdateListener(methodName = "onAddedSynced")
    private int added = 0;

    private boolean pendingPatternUpdate = false;

    public CrazyPatternProviderBE(BlockPos pos, BlockState blockState) {
        super(CrazyBlockEntityRegistrar.CRAZY_PATTERN_PROVIDER_BE.get(), pos, blockState);
        this.getMainNode().setVisualRepresentation(CrazyBlockRegistrar.CRAZY_PATTERN_PROVIDER_BLOCK.get().asItem());

        if (!CrazyConfig.COMMON.CRAZY_PATTERN_PROVIDER_BLOCK_ENABLED.get()) {
            this.getMainNode().destroy();
        }
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        onValid();

        if (level != null) {
            applySize();
        }
    }

    @Override
    public void onReady() {
        super.onReady();

        if (pendingPatternUpdate) {
            requestPatternUpdateWhenReady();
        }
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    @Override
    public void setRemoved() {
        onInValid();
        super.setRemoved();
    }

    private boolean canUpdatePatternsNow() {
        return level != null;
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

        providerTag.put(CrazyProviderNbt.NBT_STATE, CrazyProviderNbt.saveState(added));

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
            this.added = CrazyProviderNbt.loadAddedFromAnyKnownFormat(tag, this.added);
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
            this.added = CrazyProviderNbt.loadAdded(stateTag, this.added);
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
        if (CrazyProviderNbt.hasProviderDataOrLegacyAdded(tag)) {
            this.added = CrazyProviderNbt.loadAddedFromAnyKnownFormat(tag, this.added);
        } else {
            loadManagedData(tag);
        }

        applySize();

        if (tag.contains(NBT_LEGACY_PATTERNS)) {
            ((AppEngInternalInventory) getLogic().getPatternInv()).readFromNBT(tag, NBT_LEGACY_PATTERNS);
            pendingPatternUpdate = true;
        }

        return true;
    }

    private void writeProviderDataToMatchingDrops(List<ItemStack> drops) {
        CompoundTag providerTag = saveProviderData();

        for (ItemStack drop : drops) {
            if (drop.getItem() == CrazyBlockRegistrar.CRAZY_PATTERN_PROVIDER_BLOCK.get().asItem()) {
                CompoundTag rootTag = drop.getOrCreateTag();

                CrazyProviderNbt.writeProviderTagToItemRoot(rootTag, providerTag);
                CrazyProviderNbt.writeProviderTagToBlockEntityTag(rootTag, providerTag);
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        saveManagedData(tag);
        writeProviderDataTo(tag);
    }

    @Override
    public void loadTag(CompoundTag tag) {
        loadProviderStateBeforeLogic(tag);

        super.loadTag(tag);

        loadProviderLogicFromCommonTag(tag);
    }

    @Override
    public void exportSettings(SettingsFrom mode, CompoundTag output, @Nullable Player player) {
        super.exportSettings(mode, output, player);

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
            syncManaged();
            setChanged();
        }
    }

    public void setAdded(int newAdded) {
        if (this.added == newAdded) {
            return;
        }

        this.added = newAdded;
        applySize();
        requestPatternUpdateWhenReady();
        syncManaged();
    }

    private void onAddedSynced(int newValue, int oldValue) {
        if (level != null) {
            applySize();
            requestPatternUpdateWhenReady();
        }
    }

    private void applySize() {
        ((IProviderLogicResizable) getLogic()).crazyAE2Addons$setSize(BASE_SIZE + ROW_SIZE * added);
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        writeProviderDataToMatchingDrops(drops);
    }

    @Override
    protected PatternProviderLogic createLogic() {
        return new PatternProviderLogic(this.getMainNode(), this, BASE_SIZE);
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        if (CrazyConfig.COMMON.CRAZY_PATTERN_PROVIDER_BLOCK_ENABLED.get()) {
            MenuOpener.open(CrazyMenuRegistrar.CRAZY_PATTERN_PROVIDER_MENU.get(), player, locator);
        }
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        if (CrazyConfig.COMMON.CRAZY_PATTERN_PROVIDER_BLOCK_ENABLED.get()) {
            MenuOpener.returnTo(CrazyMenuRegistrar.CRAZY_PATTERN_PROVIDER_MENU.get(), player, subMenu.getLocator());
        }
    }

    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(getBlockState().getBlock().asItem().getDefaultInstance());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return getBlockState().getBlock().asItem().getDefaultInstance();
    }

    public void syncAddedToClients() {
        if (getLevel() == null || getLevel().isClientSide) {
            return;
        }
        NetworkHandler.sendToTrackingChunk(getLevel().getChunkAt(getBlockPos()),
                new SyncBlockClientPacket(getBlockPos(), added, Direction.NORTH));
    }
}
