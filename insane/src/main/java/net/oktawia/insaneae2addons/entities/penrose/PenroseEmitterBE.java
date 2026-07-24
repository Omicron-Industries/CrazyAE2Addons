package net.oktawia.insaneae2addons.entities.penrose;

import appeng.util.Platform;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.oktawia.crazyae2addons.util.IManagedBEHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.oktawia.insaneae2addons.logic.penrose.PenroseCurveModel;

public abstract class PenroseEmitterBE extends PenrosePeripheralBE {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER =
            IManagedBEHelper.inheritedFieldHolder(PenroseEmitterBE.class);

    @Persisted
    @DescSynced
    @Getter
    private double onPercent = 1.0;

    @Persisted
    @DescSynced
    @Getter
    private double offPercent;

    @Persisted
    @DescSynced
    @Getter
    private boolean emitting;

    protected PenroseEmitterBE(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState blockState,
            ItemStack visualRepresentation
    ) {
        super(type, pos, blockState, visualRepresentation, 2.0F);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected void onDetached() {
        setEmitting(false);
    }

    @Override
    public void onControllerTick() {
        setEmitting(computeEmitting());
    }

    public void setThresholds(double onPercent, double offPercent) {
        this.onPercent = clampPercent(onPercent);
        this.offPercent = clampPercent(offPercent);
        setChanged();
    }

    private static double clampPercent(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    protected abstract double readNormalized(PortablePenroseSphereControllerBE controller);

    public abstract PenroseCurveModel curveModel();

    public double getCurvePosition() {
        PortablePenroseSphereControllerBE controller = getActiveController();
        return controller == null ? 0.0 : readNormalized(controller);
    }

    private void setEmitting(boolean emitting) {
        Level level = getLevel();
        if (level == null || level.isClientSide() || this.emitting == emitting) {
            return;
        }

        this.emitting = emitting;
        setChanged();
        Platform.notifyBlocksOfNeighbors(level, getBlockPos());
    }

    private boolean computeEmitting() {
        PortablePenroseSphereControllerBE controller = getActiveController();
        if (controller == null || !controller.isBlackHoleActive()) {
            return false;
        }

        double value = readNormalized(controller);
        return this.emitting ? value > this.offPercent : value >= this.onPercent;
    }
}
