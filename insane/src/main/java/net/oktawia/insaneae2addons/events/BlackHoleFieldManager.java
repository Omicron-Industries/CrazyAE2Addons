package net.oktawia.insaneae2addons.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.logic.penrose.BlackHoleField;
import net.oktawia.insaneae2addons.logic.penrose.BlackHoleFieldData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = InsaneAddons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BlackHoleFieldManager {

    private static final List<BlackHoleField> ACTIVE = new ArrayList<>();

    private BlackHoleFieldManager() {
    }

    public static void start(ServerLevel level, BlockPos center, int radius) {
        if (radius <= 0) {
            return;
        }

        if (!level.getServer().isSameThread()) {
            level.getServer().execute(() -> start(level, center, radius));
            return;
        }

        BlackHoleField field = new BlackHoleField(level, center, radius);
        ACTIVE.add(field);
        BlackHoleFieldData.of(level).put(field.snapshot());
        field.tick(budgetNanos());
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ACTIVE.removeIf(field -> field.getLevel() == level);
        for (BlackHoleField.Snapshot snapshot : BlackHoleFieldData.of(level).all()) {
            ACTIVE.add(new BlackHoleField(level, snapshot));
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        BlackHoleFieldData data = BlackHoleFieldData.of(level);
        Iterator<BlackHoleField> iterator = ACTIVE.iterator();

        while (iterator.hasNext()) {
            BlackHoleField field = iterator.next();
            if (field.getLevel() != level) {
                continue;
            }

            if (field.isDone()) {
                data.remove(field.getId());
            } else {
                data.put(field.snapshot());
            }
            iterator.remove();
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level) || ACTIVE.isEmpty()) {
            return;
        }

        if (!(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }

        for (BlackHoleField field : ACTIVE) {
            if (field.getLevel() == level) {
                field.chunkLoaded(chunk);
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ACTIVE.isEmpty()) {
            return;
        }

        long budget = budgetNanos() / ACTIVE.size();
        Iterator<BlackHoleField> iterator = ACTIVE.iterator();

        while (iterator.hasNext()) {
            BlackHoleField field = iterator.next();

            if (field.isDone()) {
                BlackHoleFieldData.of(field.getLevel()).remove(field.getId());
                iterator.remove();
                continue;
            }

            field.tick(budget);

            if (field.consumePersistDirty()) {
                BlackHoleFieldData.of(field.getLevel()).put(field.snapshot());
            }
        }
    }

    private static long budgetNanos() {
        return InsaneConfig.COMMON.PENROSE_MELTDOWN_FIELD_BUDGET_MICROS.get() * 1000L;
    }
}
