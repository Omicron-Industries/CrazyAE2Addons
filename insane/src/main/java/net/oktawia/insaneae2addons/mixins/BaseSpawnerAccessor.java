package net.oktawia.insaneae2addons.mixins;

import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.SpawnData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BaseSpawner.class)
public interface BaseSpawnerAccessor {

    @Accessor("spawnDelay")
    void setSpawnDelay(int spawnDelay);

    @Accessor("nextSpawnData")
    @Nullable
    SpawnData getNextSpawnData();
}
