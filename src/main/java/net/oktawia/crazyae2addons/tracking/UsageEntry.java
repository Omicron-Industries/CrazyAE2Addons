package net.oktawia.crazyae2addons.tracking;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.GlobalPos;

import appeng.api.stacks.AEKey;

public record UsageEntry(String description, long totalAmount, @Nullable AEKey icon, @Nullable GlobalPos pos) {
}
