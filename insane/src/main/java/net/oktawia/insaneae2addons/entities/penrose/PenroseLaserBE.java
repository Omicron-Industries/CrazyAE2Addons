package net.oktawia.insaneae2addons.entities.penrose;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import lombok.Getter;

import net.oktawia.crazyae2addons.util.IManagedBEHelper;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;

public class PenroseLaserBE extends PenrosePeripheralBE {

    public static final int CAPACITY = 2_100_000_000;

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = IManagedBEHelper
            .inheritedFieldHolder(PenroseLaserBE.class);

    @Persisted
    @DescSynced
    @Getter
    private int energy;

    @Persisted
    private boolean poweredLatch;

    private long firedChargedTick = Long.MIN_VALUE;

    private LazyOptional<IEnergyStorage> energyCap = LazyOptional.empty();

    public PenroseLaserBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.PENROSE_LASER_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.PENROSE_LASER_BLOCK.get()),
                2.0F);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public MenuType<?> getMenuType() {
        return InsaneMenuRegistrar.PENROSE_LASER_MENU.get();
    }

    @Override
    protected void onControllerChanged(@Nullable PortablePenroseSphereControllerBE newController) {
        super.onControllerChanged(newController);
        rebuildCaps();
    }

    public boolean isFormed() {
        return getResolvedController() != null;
    }

    public boolean isCharged() {
        return this.energy >= CAPACITY;
    }

    public long getFiredChargedTick() {
        return this.firedChargedTick;
    }

    public void updateRedstone() {
        if (!(getLevel() instanceof ServerLevel level)) {
            return;
        }
        boolean powered = level.hasNeighborSignal(getBlockPos());
        if (powered && !this.poweredLatch && isFormed()) {
            fire(level);
        }
        if (powered != this.poweredLatch) {
            this.poweredLatch = powered;
            setChanged();
        }
    }

    private void fire(ServerLevel level) {
        boolean charged = isCharged();
        this.energy = 0;
        setChanged();
        if (charged) {
            this.firedChargedTick = level.getGameTime();
        }
        PortablePenroseSphereControllerBE controller = getResolvedController();
        if (controller != null) {
            controller.onLaserFired(level.getGameTime());
        }
    }

    public void drain() {
        if (this.energy != 0) {
            this.energy = 0;
            setChanged();
        }
    }

    private int insertEnergy(int max, boolean simulate) {
        if (!isFormed() || max <= 0) {
            return 0;
        }
        int accepted = (int) Math.min((long) CAPACITY - this.energy, max);
        if (accepted <= 0) {
            return 0;
        }
        if (!simulate) {
            this.energy += accepted;
            setChanged();
        }
        return accepted;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY && isFormed()) {
            return this.energyCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        rebuildCaps();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        rebuildCaps();
    }

    private void rebuildCaps() {
        this.energyCap.invalidate();
        this.energyCap = (isRemoved() || !isFormed())
                ? LazyOptional.empty()
                : LazyOptional.of(LaserEnergy::new);
    }

    private final class LaserEnergy implements IEnergyStorage {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return insertEnergy(maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return PenroseLaserBE.this.energy;
        }

        @Override
        public int getMaxEnergyStored() {
            return CAPACITY;
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    }
}
