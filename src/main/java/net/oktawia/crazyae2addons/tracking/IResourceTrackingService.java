package net.oktawia.crazyae2addons.tracking;

import appeng.api.networking.IGridService;
import appeng.api.stacks.AEKey;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IResourceTrackingService extends IGridService {

    void trackConsumption(AEKey key, long amount, String description, @Nullable AEKey icon, @Nullable BlockPos pos);

    default void trackConsumption(AEKey key, long amount, String description, @Nullable AEKey icon) {
        trackConsumption(key, amount, description, icon, null);
    }

    List<ResourceSummary> getSummaries();

    List<UsageEntry> getDetails(AEKey key);
}
