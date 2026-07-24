package net.oktawia.crazyae2addons.multiblock;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.blockentity.grid.AENetworkBlockEntity;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.oktawia.crazyae2addons.client.renderer.preview.multiblock.MultiblockPreviewHost;
import net.oktawia.crazyae2addons.client.renderer.preview.multiblock.MultiblockPreviewInfo;
import net.oktawia.crazyae2addons.client.renderer.preview.multiblock.PreviewRegistry;
import net.oktawia.crazyae2addons.util.IManagedBEHelper;
import net.oktawia.crazyae2addons.util.IMenuOpeningBlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMultiblockControllerBE
        extends AENetworkBlockEntity
        implements IGridTickable, IManagedBEHelper, MenuProvider, IMenuOpeningBlockEntity, MultiblockPreviewHost {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER =
            new ManagedFieldHolder(AbstractMultiblockControllerBE.class);

    @Getter
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Getter
    protected final MultiblockState multiblockState;

    private final float baseIdlePowerUsage;

    protected AbstractMultiblockControllerBE(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState blockState,
            ItemStack visualRepresentation,
            float idlePowerUsage
    ) {
        super(type, pos, blockState);

        this.baseIdlePowerUsage = idlePowerUsage;

        this.getMainNode()
                .setIdlePowerUsage(idlePowerUsage)
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .addService(IGridTickable.class, this)
                .setVisualRepresentation(visualRepresentation);

        this.multiblockState = new MultiblockState(
                getMultiblockDefinition(),
                this,
                this::onFormedInternal,
                this::onDisformedInternal
        );
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

    protected abstract MultiblockDefinition getMultiblockDefinition();

    protected abstract MenuType<?> getMenuType();

    protected abstract char frameSymbol();

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        MenuOpener.open(getMenuType(), player, locator);
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    protected abstract void setOwnFormedState(boolean formed);

    protected abstract void setMemberFormedState(BlockPos pos, boolean formed);

    protected void afterFormed() {
    }

    protected void afterDisformed() {
    }

    protected void invalidateMemberCapabilities() {
        for (BlockPos pos : this.multiblockState.getBlocksBySymbol(frameSymbol())) {
            invalidateCapabilitiesAt(pos);
        }
    }

    protected final void invalidateOwnCapabilities() {
        invalidateCapabilitiesAt(getBlockPos());
    }

    protected final void invalidateCapabilitiesAt(BlockPos pos) {
        Level level = getLevel();
        if (level != null && !level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                be.invalidateCaps();
            }
        }
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(1, 1, false, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return TickRateModulation.IDLE;
        }

        this.multiblockState.tick(level, getBlockPos(), getBlockState());
        return TickRateModulation.IDLE;
    }

    @Override
    public void setRemoved() {
        unregisterPreviewHost();
        this.multiblockState.destroy();
        super.setRemoved();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        Level level = getLevel();
        if (level != null && level.isClientSide()) {
            PreviewRegistry.register(this);
        }
    }

    @Override
    public void onChunkUnloaded() {
        unregisterPreviewHost();
        super.onChunkUnloaded();
    }

    private void unregisterPreviewHost() {
        Level level = getLevel();
        if (level != null && level.isClientSide()) {
            PreviewRegistry.unregister(this);
        }
    }

    private boolean previewEnabled;
    private @Nullable MultiblockPreviewInfo previewInfo;

    @Override
    public boolean isPreviewEnabled() {
        return this.previewEnabled;
    }

    public void setPreviewEnabled(boolean previewEnabled) {
        this.previewEnabled = previewEnabled;
    }

    @Override
    public @Nullable MultiblockPreviewInfo getPreviewInfo() {
        return this.previewInfo;
    }

    @Override
    public void setPreviewInfo(@Nullable MultiblockPreviewInfo previewInfo) {
        this.previewInfo = previewInfo;
    }

    @Override
    public MultiblockDefinition getPreviewDefinition() {
        return getMultiblockDefinition();
    }

    @Override
    public BlockPos getPreviewOrigin() {
        return getBlockPos();
    }

    @Override
    public Direction getPreviewFacing() {
        BlockState state = getBlockState();
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            return state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
        }
        return Direction.NORTH;
    }

    @Override
    public BlockState getPreviewState(MultiblockDefinition.PatternEntry entry, MultiblockDefinition.SymbolDef symbol) {
        return symbol.blocks().get(0).defaultBlockState();
    }

    private void onFormedInternal() {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        setOwnFormedState(true);
        setAllMemberFormedStates(true);
        applyStructureIdlePowerUsage();
        afterFormed();
        invalidateMemberCapabilities();
        invalidateOwnCapabilities();
        setChanged();
    }

    private void onDisformedInternal() {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        setOwnFormedState(false);
        setAllMemberFormedStates(false);
        setNodeIdlePowerUsage(this.baseIdlePowerUsage);
        afterDisformed();
        invalidateMemberCapabilities();
        invalidateOwnCapabilities();
        setChanged();
    }

    private void applyStructureIdlePowerUsage() {
        double total = this.baseIdlePowerUsage;
        for (MultiblockCallback callback : this.multiblockState.getRegisteredCallbacks()) {
            if (callback instanceof AbstractMultiblockFrameBE<?> frame) {
                total += frame.getFrameIdlePowerUsage();
            }
        }

        setNodeIdlePowerUsage(total);
    }

    private void setNodeIdlePowerUsage(double usagePerTick) {
        if (getMainNode().getNode() != null) {
            getMainNode().setIdlePowerUsage(usagePerTick);
        }
    }

    @Override
    public @Nullable IGridNode getGridNode(Direction dir) {
        Level level = getLevel();
        if (level != null && AbstractMultiblockFrameBE.isMultiblockMember(level.getBlockEntity(getBlockPos().relative(dir)))) {
            return null;
        }

        return super.getGridNode(dir);
    }

    private void setAllMemberFormedStates(boolean formed) {
        for (BlockPos pos : this.multiblockState.getBlocksBySymbol(frameSymbol())) {
            setMemberFormedState(pos, formed);
        }
    }
}