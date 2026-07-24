package net.oktawia.crazyae2addons.multiblock;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeService;
import appeng.api.networking.IManagedGridNode;
import appeng.api.stacks.AEItemKey;
import appeng.api.util.AEColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class LazyGridNode implements IManagedGridNode {

    private final IManagedGridNode delegate;

    private @Nullable Level pendingLevel;
    private @Nullable BlockPos pendingPos;
    private boolean destroyed;

    public LazyGridNode(IManagedGridNode delegate) {
        this.delegate = delegate;
    }

    @Override
    public void create(Level level, @Nullable BlockPos blockPos) {
        if (this.destroyed || this.delegate.getNode() != null) {
            return;
        }

        this.pendingLevel = level;
        this.pendingPos = blockPos;
    }

    public boolean materialize() {
        if (this.destroyed) {
            return false;
        }

        if (this.pendingLevel == null) {
            return this.delegate.getNode() != null;
        }

        Level level = this.pendingLevel;
        BlockPos pos = this.pendingPos;
        this.pendingLevel = null;
        this.pendingPos = null;

        this.delegate.create(level, pos);
        return this.delegate.getNode() != null;
    }

    @Override
    public void destroy() {
        this.destroyed = true;
        this.pendingLevel = null;
        this.pendingPos = null;
        this.delegate.destroy();
    }

    @Override
    public void loadFromNBT(CompoundTag nodeData) {
        this.delegate.loadFromNBT(nodeData);
    }

    @Override
    public void saveToNBT(CompoundTag nodeData) {
        this.delegate.saveToNBT(nodeData);
    }

    @Override
    public IManagedGridNode setFlags(GridFlags... flags) {
        this.delegate.setFlags(flags);
        return this;
    }

    @Override
    public IManagedGridNode setExposedOnSides(Set<Direction> directions) {
        if (!this.destroyed) {
            this.delegate.setExposedOnSides(directions);
        }
        return this;
    }

    @Override
    public IManagedGridNode setIdlePowerUsage(double usagePerTick) {
        this.delegate.setIdlePowerUsage(usagePerTick);
        return this;
    }

    @Override
    public IManagedGridNode setVisualRepresentation(@Nullable AEItemKey visualRepresentation) {
        this.delegate.setVisualRepresentation(visualRepresentation);
        return this;
    }

    @Override
    public IManagedGridNode setInWorldNode(boolean accessible) {
        this.delegate.setInWorldNode(accessible);
        return this;
    }

    @Override
    public IManagedGridNode setTagName(String tagName) {
        this.delegate.setTagName(tagName);
        return this;
    }

    @Override
    public IManagedGridNode setGridColor(AEColor gridColor) {
        this.delegate.setGridColor(gridColor);
        return this;
    }

    @Override
    public <T extends IGridNodeService> IManagedGridNode addService(Class<T> serviceClass, T service) {
        this.delegate.addService(serviceClass, service);
        return this;
    }

    @Override
    public boolean isReady() {
        return this.delegate.isReady();
    }

    @Override
    public boolean isActive() {
        return this.delegate.isActive();
    }

    @Override
    public boolean isOnline() {
        return this.delegate.isOnline();
    }

    @Override
    public boolean isPowered() {
        return this.delegate.isPowered();
    }

    @Override
    public boolean hasGridBooted() {
        return this.delegate.hasGridBooted();
    }

    @Override
    public void setOwningPlayerId(int ownerPlayerId) {
        this.delegate.setOwningPlayerId(ownerPlayerId);
    }

    @Override
    public void setOwningPlayer(Player ownerPlayer) {
        this.delegate.setOwningPlayer(ownerPlayer);
    }

    @Override
    public IGridNode getNode() {
        return this.delegate.getNode();
    }
}
