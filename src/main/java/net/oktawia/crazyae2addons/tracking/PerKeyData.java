package net.oktawia.crazyae2addons.tracking;

import appeng.api.stacks.AEKey;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

class PerKeyData {

    static final int NUM_BUCKETS = 12;
    static final long BUCKET_DURATION_MS = 5_000L;

    final long[] bucketAmounts = new long[NUM_BUCKETS];
    final long[] bucketStartMs = new long[NUM_BUCKETS];
    final HashMap<String, long[]> descAmounts = new HashMap<>();
    final HashMap<String, long[]> descStartMs = new HashMap<>();
    final HashMap<String, AEKey> descIcons = new HashMap<>();
    final HashMap<String, BlockPos> descPositions = new HashMap<>();

    void record(long amount, String description, @Nullable AEKey icon, @Nullable BlockPos pos, long now) {
        int idx = bucketIdx(now);
        if (now - bucketStartMs[idx] >= BUCKET_DURATION_MS * NUM_BUCKETS) {
            bucketAmounts[idx] = 0;
            bucketStartMs[idx] = now;
        }
        bucketAmounts[idx] += amount;

        long[] da = descAmounts.computeIfAbsent(description, k -> new long[NUM_BUCKETS]);
        long[] ds = descStartMs.computeIfAbsent(description, k -> new long[NUM_BUCKETS]);
        if (now - ds[idx] >= BUCKET_DURATION_MS * NUM_BUCKETS) {
            da[idx] = 0;
            ds[idx] = now;
        }
        da[idx] += amount;

        if (icon != null) descIcons.putIfAbsent(description, icon);
        if (pos != null) descPositions.putIfAbsent(description, pos);
    }

    long perMinute(long now) {
        return sumBuckets(bucketAmounts, bucketStartMs, now);
    }

    long perMinuteDesc(String description, long now) {
        long[] da = descAmounts.get(description);
        long[] ds = descStartMs.get(description);
        if (da == null || ds == null) return 0;
        return sumBuckets(da, ds, now);
    }

    private static int bucketIdx(long now) {
        return (int) ((now / BUCKET_DURATION_MS) % NUM_BUCKETS);
    }

    private static long sumBuckets(long[] amounts, long[] starts, long now) {
        long cutoff = now - 60_000L;
        long sum = 0;
        for (int i = 0; i < NUM_BUCKETS; i++) {
            if (starts[i] > cutoff) sum += amounts[i];
        }
        return sum;
    }
}
