package net.oktawia.insaneae2addons.items.mobstorage;

import appeng.api.stacks.AEKey;
import appeng.items.storage.BasicStorageCell;
import appeng.items.storage.StorageTier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.oktawia.insaneae2addons.mobstorage.MobKey;
import net.oktawia.insaneae2addons.mobstorage.MobKeyType;

public class MobStorageCell extends BasicStorageCell {

    public MobStorageCell(Properties properties, StorageTier tier, ItemLike housingItem) {
        super(properties, tier.componentSupplier().get(), housingItem, tier.idleDrain(),
                tier.bytes() / 1024, tier.bytes() / 128, 5, MobKeyType.TYPE);
    }

    @Override
    public boolean isBlackListed(ItemStack cellItem, AEKey requestedAddition) {
        return !(requestedAddition instanceof MobKey);
    }
}
