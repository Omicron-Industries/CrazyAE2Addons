package net.oktawia.crazyae2addons.tracking;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.GlobalPos;

import appeng.api.stacks.AEKey;

public record UsageTarget(UsageType type, @Nullable AEKey output, @Nullable GlobalPos pos) {

    public static UsageTarget crafting(AEKey output) {
        return new UsageTarget(UsageType.CRAFTING, output, null);
    }

    public static UsageTarget machine(GlobalPos pos) {
        return new UsageTarget(UsageType.MACHINE, null, pos);
    }

    public static UsageTarget interfaceAt(GlobalPos pos) {
        return new UsageTarget(UsageType.INTERFACE, null, pos);
    }
}
