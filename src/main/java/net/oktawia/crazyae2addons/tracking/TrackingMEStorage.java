package net.oktawia.crazyae2addons.tracking;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.helpers.InterfaceLogicHost;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public class TrackingMEStorage implements MEStorage {

    private final MEStorage delegate;
    private final IGrid grid;
    private final InterfaceLogicHost interfaceHost;
    private final UsageTarget target;
    private final String description;

    public TrackingMEStorage(MEStorage delegate, IGrid grid, InterfaceLogicHost interfaceHost, Level level, BlockPos interfacePos) {
        this.delegate = delegate;
        this.grid = grid;
        this.interfaceHost = interfaceHost;
        this.target = UsageTarget.interfaceAt(GlobalPos.of(level.dimension(), interfacePos.immutable()));
        this.description = "interface at " + interfacePos.getX() + " " + interfacePos.getY() + " " + interfacePos.getZ();
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        return delegate.insert(what, amount, mode, source);
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        long extracted = delegate.extract(what, amount, mode, source);
        if (extracted > 0 && mode == Actionable.MODULATE && !isReplenished(what)) {
            var svc = grid.getService(IResourceTrackingService.class);
            if (svc != null) svc.trackConsumption(what, extracted, target, description, null);
        }
        return extracted;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        delegate.getAvailableStacks(out);
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        return delegate.isPreferredStorageFor(what, source);
    }

    @Override
    public Component getDescription() {
        return delegate.getDescription();
    }

    private boolean isReplenished(AEKey what) {
        var config = interfaceHost.getConfig();
        for (int i = 0; i < config.size(); i++) {
            if (what.equals(config.getKey(i))) return true;
        }
        return false;
    }
}
