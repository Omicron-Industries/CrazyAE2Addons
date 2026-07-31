package net.oktawia.crazyae2addons.multiblock;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface MultiblockCallback {
    void setController(@Nullable BlockEntity controller);

    void setState(@Nullable MultiblockState state);

    void unregister(MultiblockState state);
}
