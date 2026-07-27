package net.oktawia.insaneae2addons.entities.penrose;

import appeng.util.SettingsFrom;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.oktawia.crazyae2addons.util.IManagedBEHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.logic.penrose.CoolantTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PenroseHeatVentBE extends PenrosePeripheralBE {

    private static final String NBT_DESIRED_COOLING = "desired_cooling";

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER =
            IManagedBEHelper.inheritedFieldHolder(PenroseHeatVentBE.class);

    @Persisted
    @DescSynced
    @Getter
    private double desiredCooling;

    @Persisted
    @DescSynced
    private final CoolantTank coolantTank = new CoolantTank(this::setChanged);

    private LazyOptional<IFluidHandler> fluidCap = LazyOptional.of(() -> this.coolantTank);

    public PenroseHeatVentBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.PENROSE_HEAT_VENT_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.PENROSE_HEAT_VENT_BLOCK.get()),
                1.0F
        );
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public MenuType<?> getMenuType() {
        return InsaneMenuRegistrar.PENROSE_HEAT_VENT_MENU.get();
    }

    public void setDesiredCooling(double desiredCooling) {
        this.desiredCooling = Math.max(0.0, desiredCooling);
        setChanged();
    }

    @Override
    public void exportSettings(SettingsFrom mode, CompoundTag output, @Nullable Player player) {
        super.exportSettings(mode, output, player);

        if (mode == SettingsFrom.MEMORY_CARD) {
            output.putDouble(NBT_DESIRED_COOLING, this.desiredCooling);
        }
    }

    @Override
    public void importSettings(SettingsFrom mode, CompoundTag input, @Nullable Player player) {
        super.importSettings(mode, input, player);

        if (mode == SettingsFrom.MEMORY_CARD && input.contains(NBT_DESIRED_COOLING, Tag.TAG_DOUBLE)) {
            setDesiredCooling(input.getDouble(NBT_DESIRED_COOLING));
        }
    }

    public int getCoolantAmount() {
        return this.coolantTank.getAmount();
    }

    public int getCoolantCapacity() {
        return CoolantTank.capacity();
    }

    public double drawCoolantFor(double requestedCooling) {
        if (requestedCooling <= 0.0) {
            return 0.0;
        }

        double mbPerGK = InsaneConfig.COMMON.PENROSE_COOLANT_MB_PER_GK.get();
        long needed = (long) Math.ceil(requestedCooling * mbPerGK);
        if (needed <= 0L) {
            return requestedCooling;
        }

        int drained = this.coolantTank.drain((int) Math.min(Integer.MAX_VALUE, needed));
        if (drained <= 0) {
            return 0.0;
        }

        return requestedCooling * Math.min(1.0, (double) drained / needed);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return this.fluidCap.cast();
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
        this.fluidCap.invalidate();
        this.fluidCap = isRemoved() ? LazyOptional.empty() : LazyOptional.of(() -> this.coolantTank);
    }
}
