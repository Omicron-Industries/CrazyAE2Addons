package net.oktawia.insaneae2addons.logic.penrose;

import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public interface PenroseEnergyExport {

    List<String> portBlockIds();

    long push(BlockEntity target, long available);

    static void set(PenroseEnergyExport export) {
        Holder.instance = export;
    }

    static @Nullable PenroseEnergyExport get() {
        return Holder.instance;
    }

    final class Holder {
        private static @Nullable PenroseEnergyExport instance;

        private Holder() {
        }
    }
}
