package net.oktawia.crazyae2addons.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.ItemStack;

import appeng.items.storage.ViewCellItem;
import appeng.menu.slot.RestrictedInputSlot;

import net.oktawia.crazyae2addons.CrazyConfig;

@Mixin(value = RestrictedInputSlot.class, remap = false)
public abstract class MixinRestrictedInputSlot {

    @Shadow
    @Final
    private RestrictedInputSlot.PlacableItemType which;

    @Inject(method = "mayPlace(Lnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "FIELD", target = "Lappeng/menu/slot/RestrictedInputSlot;which:Lappeng/menu/slot/RestrictedInputSlot$PlacableItemType;", opcode = Opcodes.GETFIELD), cancellable = true, remap = true)
    private void crazyae2addons$acceptAnyViewCell(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.which != RestrictedInputSlot.PlacableItemType.VIEW_CELL) {
            return;
        }

        if (!CrazyConfig.COMMON.TAG_VIEW_CELL_ENABLED.get()) {
            return;
        }

        if (stack.getItem() instanceof ViewCellItem) {
            cir.setReturnValue(true);
        }
    }
}
