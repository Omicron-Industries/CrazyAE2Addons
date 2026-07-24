package net.oktawia.insaneae2addons.compat.CC;

import dan200.computercraft.api.ComputerCraftAPI;

public final class InsaneCCCompat {

    private InsaneCCCompat() {
    }

    public static void init() {
        ComputerCraftAPI.registerGenericSource(new PenroseFramePeripheral());
    }
}
