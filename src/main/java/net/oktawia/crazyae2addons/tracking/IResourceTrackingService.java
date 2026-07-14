package net.oktawia.crazyae2addons.tracking;

import appeng.api.networking.IGridService;
import appeng.api.stacks.AEKey;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IResourceTrackingService extends IGridService {

    void trackConsumption(AEKey key, long amount, UsageTarget target, String description, @Nullable AEKey icon);

    List<ResourceSummary> getSummaries();

    List<UsageEntry> getDetails(AEKey key);
}
