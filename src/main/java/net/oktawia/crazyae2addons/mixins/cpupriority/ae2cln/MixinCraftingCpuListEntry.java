package net.oktawia.crazyae2addons.mixins.cpupriority.ae2cln;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.FriendlyByteBuf;

import net.oktawia.crazyae2addons.logic.interfaces.ICpuListEntryInfo;
import net.oktawia.crazyae2addons.logic.interfaces.ICpuPrio;

@Mixin(targets = "appeng.menu.me.crafting.CraftingCpuListEntry", remap = false)
public abstract class MixinCraftingCpuListEntry implements ICpuPrio, ICpuListEntryInfo {

    @Unique
    private int crazyae2addons$prio;

    @Override
    public int getPrio() {
        return this.crazyae2addons$prio;
    }

    @Override
    public void setPrio(int prio) {
        this.crazyae2addons$prio = prio;
    }

    @Inject(method = "writeToPacket", at = @At("TAIL"))
    private void crazyae2addons$writePrio(FriendlyByteBuf buf, CallbackInfo ci) {
        buf.writeVarInt(this.crazyae2addons$prio);
    }

    @Inject(method = "readFromPacket", at = @At("TAIL"))
    private static void crazyae2addons$readPrio(FriendlyByteBuf buf, CallbackInfoReturnable<Object> cir) {
        ((ICpuPrio) cir.getReturnValue()).setPrio(buf.readVarInt());
    }
}
