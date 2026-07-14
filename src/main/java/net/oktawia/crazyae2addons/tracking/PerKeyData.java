package net.oktawia.crazyae2addons.tracking;

import appeng.api.stacks.AEKey;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

class PerKeyData {

    static final int NUM_BUCKETS = 12;
    static final long BUCKET_DURATION_MS = 5_000L;

    final long[] bucketAmounts = new long[NUM_BUCKETS];
    final long[] bucketStartMs = new long[NUM_BUCKETS];
    final HashMap<UsageTarget, long[]> targetAmounts = new HashMap<>();
    final HashMap<UsageTarget, long[]> targetStartMs = new HashMap<>();
    final HashMap<UsageTarget, AEKey> targetIcons = new HashMap<>();
    final HashMap<UsageTarget, String> targetDescriptions = new HashMap<>();

    void record(long amount, UsageTarget target, String description, @Nullable AEKey icon, long now) {
        int idx = bucketIdx(now);
        if (now - bucketStartMs[idx] >= BUCKET_DURATION_MS * NUM_BUCKETS) {
            bucketAmounts[idx] = 0;
            bucketStartMs[idx] = now;
        }
        bucketAmounts[idx] += amount;

        long[] da = targetAmounts.computeIfAbsent(target, k -> new long[NUM_BUCKETS]);
        long[] ds = targetStartMs.computeIfAbsent(target, k -> new long[NUM_BUCKETS]);
        if (now - ds[idx] >= BUCKET_DURATION_MS * NUM_BUCKETS) {
            da[idx] = 0;
            ds[idx] = now;
        }
        da[idx] += amount;

        if (icon != null) targetIcons.putIfAbsent(target, icon);
        if (description != null) targetDescriptions.putIfAbsent(target, description);
    }

    long perMinute(long now) {
        return sumBuckets(bucketAmounts, bucketStartMs, now);
    }

    long perMinuteTarget(UsageTarget target, long now) {
        long[] da = targetAmounts.get(target);
        long[] ds = targetStartMs.get(target);
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
