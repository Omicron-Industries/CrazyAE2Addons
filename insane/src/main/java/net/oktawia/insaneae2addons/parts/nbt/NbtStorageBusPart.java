package net.oktawia.insaneae2addons.parts.nbt;

import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.parts.IPartItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.parts.storagebus.StorageBusPart;
import appeng.util.ConfigInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;

import java.util.List;

public class NbtStorageBusPart extends StorageBusPart {

    private static final String NBT_STATE = "state";
    private static final String NBT_UPGRADES = "nbtUpgrades";

    public final ConfigInventory inv = ConfigInventory.configTypes(1, () -> {});

    private final IUpgradeInventory nbtUpgrades = UpgradeInventories.forMachine(
            InsaneItemRegistrar.NBT_STORAGE_BUS.get(), 1, this::onNbtUpgradesChanged);

    private final NbtFilterState state = new NbtFilterState(this::markForSaveSafe);

    public NbtStorageBusPart(IPartItem<?> partItem) {
        super(partItem);
    }

    public String getData() {
        return state.getData();
    }

    @Override
    public MenuType<?> getMenuType() {
        return InsaneMenuRegistrar.NBT_STORAGE_BUS_MENU.get();
    }

    public void setFilter(String expression) {
        state.setData(expression);
        if (getHost() != null) {
            getHost().markForSave();
            getHost().markForUpdate();
        }
        onConfigurationChanged();
    }

    private void markForSaveSafe() {
        if (getHost() != null) {
            getHost().markForSave();
        }
    }

    private void onNbtUpgradesChanged() {
        markForSaveSafe();
        onConfigurationChanged();
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return nbtUpgrades;
    }

    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(ISegmentedInventory.UPGRADES)) {
            return nbtUpgrades;
        }
        return super.getSubInventory(id);
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
        super.addAdditionalDrops(drops, wrenched);
        for (ItemStack is : nbtUpgrades) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        nbtUpgrades.clear();
    }

    @Override
    public void readFromNBT(CompoundTag extra) {
        super.readFromNBT(extra);
        if (extra.contains(NBT_STATE, Tag.TAG_COMPOUND)) {
            state.loadPersisted(extra.getCompound(NBT_STATE));
        }
        nbtUpgrades.readFromNBT(extra, NBT_UPGRADES);
    }

    @Override
    public void writeToNBT(CompoundTag extra) {
        super.writeToNBT(extra);
        extra.put(NBT_STATE, state.savePersisted());
        nbtUpgrades.writeToNBT(extra, NBT_UPGRADES);
    }

    @Override
    public void writeToStream(FriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeNbt(state.saveSync(true));
    }

    @Override
    public boolean readFromStream(FriendlyByteBuf data) {
        boolean changed = super.readFromStream(data);
        CompoundTag tag = data.readNbt();
        if (tag != null) {
            state.loadSync(tag);
        }
        return changed;
    }
}
