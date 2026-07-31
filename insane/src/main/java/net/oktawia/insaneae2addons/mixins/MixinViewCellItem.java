package net.oktawia.insaneae2addons.mixins;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.ItemStack;

import appeng.api.storage.AEKeyFilter;
import appeng.items.storage.ViewCellItem;
import appeng.util.prioritylist.IPartitionList;

import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.items.nbt.NbtViewCellItem;
import net.oktawia.insaneae2addons.logic.nbt.NBTPriorityList;

@Mixin(value = ViewCellItem.class, remap = false)
public abstract class MixinViewCellItem {

    @Inject(method = "createFilter(Lappeng/api/storage/AEKeyFilter;Ljava/util/Collection;)Lappeng/util/prioritylist/IPartitionList;", at = @At("HEAD"), cancellable = true)
    private static void insaneae2addons$useNbtPriorityList(
            AEKeyFilter filter,
            Collection<ItemStack> list,
            CallbackInfoReturnable<IPartitionList> cir) {
        if (!InsaneConfig.COMMON.NBT_VIEW_CELL_ENABLED.get()) {
            return;
        }

        for (ItemStack stack : list) {
            if (stack == null || stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof NbtViewCellItem && stack.getOrCreateTag().contains("filter")) {
                String value = stack.getOrCreateTag().getString("filter");
                if (!value.isBlank()) {
                    cir.setReturnValue(new NBTPriorityList(value));
                    return;
                }
            }
        }
    }
}
