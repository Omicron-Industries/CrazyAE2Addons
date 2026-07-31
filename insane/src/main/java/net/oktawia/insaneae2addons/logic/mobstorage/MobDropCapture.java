package net.oktawia.insaneae2addons.logic.mobstorage;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.ItemStack;

public final class MobDropCapture {

    private static final ThreadLocal<List<ItemStack>> ACTIVE = new ThreadLocal<>();

    private MobDropCapture() {
    }

    public static List<ItemStack> collect(Runnable dropper) {
        List<ItemStack> captured = new ArrayList<>();
        ACTIVE.set(captured);
        try {
            dropper.run();
        } finally {
            ACTIVE.remove();
        }
        return captured;
    }

    public static boolean capture(@Nullable ItemStack stack) {
        List<ItemStack> target = ACTIVE.get();
        if (target == null) {
            return false;
        }

        if (stack != null && !stack.isEmpty()) {
            target.add(stack.copy());
        }
        return true;
    }
}
