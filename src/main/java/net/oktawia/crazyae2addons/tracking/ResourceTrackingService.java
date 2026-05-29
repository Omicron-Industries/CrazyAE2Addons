package net.oktawia.crazyae2addons.tracking;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridServiceProvider;
import appeng.api.stacks.AEKey;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceTrackingService implements IResourceTrackingService, IGridServiceProvider {

    private static final int MAX_KEYS = 10_000;
    private static final int MAX_DESCS_PER_KEY = 200;

    private final HashMap<AEKey, PerKeyData> data = new HashMap<>();

    public ResourceTrackingService(IGrid grid) {
    }

    @Override
    public void trackConsumption(AEKey key, long amount, String description, @Nullable AEKey icon, @Nullable BlockPos pos) {
        if (amount <= 0) return;
        PerKeyData d = data.get(key);
        if (d == null) {
            if (data.size() >= MAX_KEYS) return;
            d = new PerKeyData();
            data.put(key, d);
        }
        if (d.descAmounts.size() >= MAX_DESCS_PER_KEY && !d.descAmounts.containsKey(description)) return;
        d.record(amount, description, icon, pos, System.currentTimeMillis());
    }

    @Override
    public List<ResourceSummary> getSummaries() {
        long now = System.currentTimeMillis();
        List<ResourceSummary> result = new ArrayList<>(data.size());
        for (Map.Entry<AEKey, PerKeyData> entry : data.entrySet()) {
            long perMin = entry.getValue().perMinute(now);
            if (perMin > 0) {
                result.add(new ResourceSummary(entry.getKey(), perMin, perMin));
            }
        }
        result.sort(Comparator.comparingLong(ResourceSummary::perMinute).reversed());
        return result;
    }

    @Override
    public List<UsageEntry> getDetails(AEKey key) {
        PerKeyData d = data.get(key);
        if (d == null) return List.of();
        long now = System.currentTimeMillis();
        List<UsageEntry> entries = new ArrayList<>(d.descAmounts.size());
        for (String desc : d.descAmounts.keySet()) {
            long perMin = d.perMinuteDesc(desc, now);
            if (perMin > 0) {
                entries.add(new UsageEntry(desc, perMin, d.descIcons.get(desc), d.descPositions.get(desc)));
            }
        }
        entries.sort(Comparator.comparingLong(UsageEntry::totalAmount).reversed());
        return entries;
    }

    @Override
    public void onServerEndTick() {
        long now = System.currentTimeMillis();
        data.entrySet().removeIf(e -> e.getValue().perMinute(now) == 0);
    }
}
