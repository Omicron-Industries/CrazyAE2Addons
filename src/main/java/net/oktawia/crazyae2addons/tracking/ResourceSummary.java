package net.oktawia.crazyae2addons.tracking;

import appeng.api.stacks.AEKey;

public record ResourceSummary(AEKey key, long totalConsumed, long perMinute) {}
