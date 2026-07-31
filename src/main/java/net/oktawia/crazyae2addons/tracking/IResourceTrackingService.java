package net.oktawia.crazyae2addons.tracking;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import appeng.api.networking.IGridService;
import appeng.api.stacks.AEKey;

public interface IResourceTrackingService extends IGridService {

    void trackConsumption(AEKey key, long amount, UsageTarget target, String description, @Nullable AEKey icon);

    List<ResourceSummary> getSummaries();

    List<UsageEntry> getDetails(AEKey key);
}
