package net.oktawia.crazyae2addons.logic.interfaces;

import appeng.api.stacks.AEKey;
import net.oktawia.crazyae2addons.tracking.ResourceSummary;
import net.oktawia.crazyae2addons.tracking.UsageEntry;

import java.util.List;

public interface IResourceTrackingTerminalHost {

    List<ResourceSummary> getSummaries();

    List<UsageEntry> getDetails(AEKey key);
}
