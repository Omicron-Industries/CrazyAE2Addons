package net.oktawia.insaneae2addons.mixins;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;

@Mixin(BaseSpawner.class)
public interface BaseSpawnerAccessor {

    @Accessor("spawnDelay")
    void setSpawnDelay(int spawnDelay);

    @Accessor("nextSpawnData")
    @Nullable
    SpawnData getNextSpawnData();
}
