package net.oktawia.crazyae2addons.multiblock;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.blockentity.grid.AENetworkBlockEntity;
import appeng.menu.locator.MenuLocator;
import appeng.menu.locator.MenuLocators;
import com.lowdragmc.lowdraglib.syncdata.IManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.oktawia.crazyae2addons.util.IManagedBEHelper;
import net.oktawia.crazyae2addons.util.IMenuOpeningBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;

public abstract class AbstractMultiblockFrameBE<C extends BlockEntity> extends AENetworkBlockEntity
        implements MultiblockCallback, IManagedBEHelper, IMenuOpeningBlockEntity {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER =
            new ManagedFieldHolder(AbstractMultiblockFrameBE.class);

    @Getter
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    protected @Nullable MultiblockState activeState;
    protected @Nullable C activeController;

    @Getter
    private final float frameIdlePowerUsage;

    @DescSynced
    @Persisted
    protected @Nullable BlockPos syncedControllerPos;

    protected AbstractMultiblockFrameBE(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState blockState,
            ItemStack visualRepresentation,
            float idlePowerUsage
    ) {
        super(type, pos, blockState);

        this.frameIdlePowerUsage = idlePowerUsage;

        this.getMainNode()
                .setIdlePowerUsage(0.0)
                .setVisualRepresentation(visualRepresentation);
    }

    @Override
    protected IManagedGridNode createMainNode() {
        return new LazyGridNode(super.createMainNode());
    }

    public static boolean isMultiblockMember(@Nullable BlockEntity be) {
        return be instanceof AbstractMultiblockFrameBE<?> || be instanceof AbstractMultiblockControllerBE;
    }

    @Override
    public @Nullable IGridNode getGridNode(Direction dir) {
        Level level = getLevel();
        if (level == null || getResolvedController() == null) {
            return null;
        }

        if (isMultiblockMember(level.getBlockEntity(getBlockPos().relative(dir)))) {
            return null;
        }

        materializeGridNode();
        return super.getGridNode(dir);
    }

    @Override
    public void onReady() {
        super.onReady();
        updateGridExposure(getResolvedController() != null);
    }

    private void updateGridExposure(boolean formed) {
        if (formed && hasExternalGridNeighbor()) {
            materializeGridNode();
        }

        getMainNode().setExposedOnSides(formed ? EnumSet.allOf(Direction.class) : EnumSet.noneOf(Direction.class));
    }

    private boolean hasExternalGridNeighbor() {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return false;
        }

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = getBlockPos().relative(dir);
            if (isMultiblockMember(level.getBlockEntity(neighborPos))) {
                continue;
            }

            if (GridHelper.getNodeHost(level, neighborPos) != null) {
                return true;
            }
        }

        return false;
    }

    @Override
    public @Nullable IGridNode getActionableNode() {
        IGridNode ownNode = getMainNode().getNode();
        if (ownNode != null) {
            return ownNode;
        }

        C controller = getResolvedController();
        if (controller instanceof AENetworkBlockEntity controllerAe) {
            return controllerAe.getMainNode().getNode();
        }

        return null;
    }

    protected void materializeGridNode() {
        if (getMainNode() instanceof LazyGridNode lazyNode && lazyNode.materialize()) {
            connectToControllerGrid();
        }
    }

    @Override
    public IManagedStorage getRootStorage() {
        return getSyncStorage();
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveManagedData(tag);
    }

    @Override
    public void loadTag(CompoundTag tag) {
        loadManagedData(tag);
        super.loadTag(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveManagedData(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        loadManagedData(tag);
        super.handleUpdateTag(tag);
    }

    @Override
    public void onChanged() {
        setChanged();
    }

    protected abstract Class<C> controllerClass();

    @Override
    public void setState(@Nullable MultiblockState state) {
        this.activeState = state;
        setChanged();
    }

    @Override
    public void setController(@Nullable BlockEntity controller) {
        C newController = controllerClass().isInstance(controller)
                ? controllerClass().cast(controller)
                : null;

        if (this.activeController == newController) {
            return;
        }

        this.activeController = newController;
        this.syncedControllerPos = newController != null ? newController.getBlockPos().immutable() : null;

        updateGridExposure(newController != null);
        onControllerChanged(newController);
        setChanged();
    }

    protected void onControllerChanged(@Nullable C newController) {
    }

    protected @Nullable C getResolvedController() {
        if (this.activeController != null && !this.activeController.isRemoved()) {
            return this.activeController;
        }

        if (this.syncedControllerPos == null) {
            return null;
        }

        Level level = getLevel();
        if (level == null) {
            return null;
        }

        BlockEntity be = level.getBlockEntity(this.syncedControllerPos);
        if (controllerClass().isInstance(be)) {
            return controllerClass().cast(be);
        }

        return null;
    }

    @Override
    public void unregister(MultiblockState state) {
        state.unregisterCallback(getBlockPos());
    }

    @Override
    public void setRemoved() {
        if (this.activeState != null) {
            unregister(this.activeState);
            this.activeState = null;
        }

        disconnectFromControllerGrid();
        this.activeController = null;
        this.syncedControllerPos = null;
        super.setRemoved();
    }

    public @Nullable C getActiveController() {
        return getResolvedController();
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        C controller = getResolvedController();
        if (controller instanceof IMenuOpeningBlockEntity opener) {
            opener.openMenu(player, MenuLocators.forBlockEntity(controller));
        }
    }

    @Override
    public boolean canOpenMenu() {
        return getResolvedController() != null;
    }

    public @Nullable MultiblockState getActiveState() {
        return this.activeState;
    }

    public void connectToControllerGrid() {
        IGridNode selfNode = this.getMainNode().getNode();
        if (selfNode == null || this.activeController == null) {
            return;
        }

        if (!(this.activeController instanceof AENetworkBlockEntity controllerAe)) {
            return;
        }

        IGridNode controllerNode = controllerAe.getMainNode().getNode();
        if (controllerNode == null) {
            return;
        }

        boolean alreadyLinked = selfNode.getConnections().stream()
                .anyMatch(connection -> connection.getOtherSide(selfNode) == controllerNode);

        if (!alreadyLinked) {
            GridHelper.createConnection(selfNode, controllerNode);
        }
    }

    public void disconnectFromControllerGrid() {
        IGridNode selfNode = this.getMainNode().getNode();
        if (selfNode == null) {
            return;
        }

        for (IGridConnection connection : new ArrayList<>(selfNode.getConnections())) {
            if (connection.isInWorld()) {
                continue;
            }

            IGridNode otherSide = connection.getOtherSide(selfNode);
            if (otherSide != null && controllerClass().isInstance(otherSide.getOwner())) {
                connection.destroy();
            }
        }
    }
}