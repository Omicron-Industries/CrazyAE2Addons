package net.oktawia.insaneae2addons;

import net.minecraftforge.fml.ModList;

public final class IsModLoaded {

    public static final boolean APOTHEOSIS = ModList.get().isLoaded("apotheosis");

    private IsModLoaded() {
    }
}
