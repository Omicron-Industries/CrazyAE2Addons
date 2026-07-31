package net.oktawia.insaneae2addons.entities.penrose;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import lombok.Getter;

import appeng.util.SettingsFrom;

import net.oktawia.crazyae2addons.util.IManagedBEHelper;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;

public class PenroseHawkingVentBE extends PenrosePeripheralBE {

    private static final String NBT_DESIRED_EVAPORATION = "desired_evaporation";

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = IManagedBEHelper
            .inheritedFieldHolder(PenroseHawkingVentBE.class);

    @Persisted
    @DescSynced
    @Getter
    private double desiredEvaporation;

    @Persisted
    @DescSynced
    @Getter
    private int bufferedEnergy;

    private LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(VentEnergyBuffer::new);

    public PenroseHawkingVentBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.PENROSE_HAWKING_VENT_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.PENROSE_HAWKING_VENT_BLOCK.get()),
                1.0F);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public MenuType<?> getMenuType() {
        return InsaneMenuRegistrar.PENROSE_HAWKING_VENT_MENU.get();
    }

    public void setDesiredEvaporation(double desiredEvaporation) {
        this.desiredEvaporation = Math.max(0.0, desiredEvaporation);
        setChanged();
    }

    @Override
    public void exportSettings(SettingsFrom mode, CompoundTag output, @Nullable Player player) {
        super.exportSettings(mode, output, player);

        if (mode == SettingsFrom.MEMORY_CARD) {
            output.putDouble(NBT_DESIRED_EVAPORATION, this.desiredEvaporation);
        }
    }

    @Override
    public void importSettings(SettingsFrom mode, CompoundTag input, @Nullable Player player) {
        super.importSettings(mode, input, player);

        if (mode == SettingsFrom.MEMORY_CARD && input.contains(NBT_DESIRED_EVAPORATION, Tag.TAG_DOUBLE)) {
            setDesiredEvaporation(input.getDouble(NBT_DESIRED_EVAPORATION));
        }
    }

    public long drawEnergy(long wanted) {
        if (wanted <= 0L || this.bufferedEnergy <= 0) {
            return 0L;
        }

        int taken = (int) Math.min(this.bufferedEnergy, wanted);
        this.bufferedEnergy -= taken;
        setChanged();
        return taken;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
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
        this.energyCap = isRemoved() ? LazyOptional.empty() : LazyOptional.of(VentEnergyBuffer::new);
    }

    private final class VentEnergyBuffer implements IEnergyStorage {

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (maxReceive <= 0) {
                return 0;
            }

            int accepted = (int) Math.min(maxReceive, Integer.MAX_VALUE - (long) bufferedEnergy);
            if (!simulate && accepted > 0) {
                bufferedEnergy += accepted;
                setChanged();
            }
            return accepted;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (maxExtract <= 0 || bufferedEnergy <= 0) {
                return 0;
            }

            int extracted = Math.min(maxExtract, bufferedEnergy);
            if (!simulate) {
                bufferedEnergy -= extracted;
                setChanged();
            }
            return extracted;
        }

        @Override
        public int getEnergyStored() {
            return bufferedEnergy;
        }

        @Override
        public int getMaxEnergyStored() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    }
}
