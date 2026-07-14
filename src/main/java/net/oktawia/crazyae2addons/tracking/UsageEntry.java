package net.oktawia.crazyae2addons.tracking;

import appeng.api.stacks.AEKey;
import net.minecraft.core.GlobalPos;
import org.jetbrains.annotations.Nullable;

public record UsageEntry(String description, long totalAmount, @Nullable AEKey icon, @Nullable GlobalPos pos) {}
