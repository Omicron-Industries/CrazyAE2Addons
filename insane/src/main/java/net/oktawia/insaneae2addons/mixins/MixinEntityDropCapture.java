package net.oktawia.insaneae2addons.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.oktawia.insaneae2addons.logic.mobstorage.MobDropCapture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntityDropCapture {

    @Inject(
            method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void insane$captureSimulatedDrop(ItemStack pStack, float pOffsetY, CallbackInfoReturnable<ItemEntity> cir) {
        if (MobDropCapture.capture(pStack)) {
            cir.setReturnValue(null);
        }
    }
}
