package net.oktawia.insaneae2addons.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public interface LivingEntityDropInvoker {

    @Invoker("dropCustomDeathLoot")
    void invokeDropCustomDeathLoot(DamageSource source, int looting, boolean hitByPlayer);
}
