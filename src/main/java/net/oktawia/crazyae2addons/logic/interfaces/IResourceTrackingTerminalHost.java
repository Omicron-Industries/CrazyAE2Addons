package net.oktawia.crazyae2addons.logic.interfaces;

import java.util.List;

import appeng.api.stacks.AEKey;

import net.oktawia.crazyae2addons.tracking.ResourceSummary;
import net.oktawia.crazyae2addons.tracking.UsageEntry;

public interface IResourceTrackingTerminalHost {

    List<ResourceSummary> getSummaries();

    List<UsageEntry> getDetails(AEKey key);
}
