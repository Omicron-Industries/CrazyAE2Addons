package net.oktawia.insaneae2addons.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraftforge.data.loading.DatagenModLoader;

import dev.latvian.mods.kubejs.platform.forge.MiscForgeHelper;

@Mixin(value = MiscForgeHelper.class, remap = false)
public class MiscForgeHelperMixin {

    /**
     * @author Mqrius
     * @reason Forge ModLoader.isDataGenRunning() is broken.
     */
    @Overwrite
    public boolean isDataGen() {
        return DatagenModLoader.isRunningDataGen();
    }
}
