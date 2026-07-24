package net.oktawia.insaneae2addons.logic.penrose;

import com.lowdragmc.lowdraglib.syncdata.IContentChangeAware;
import com.lowdragmc.lowdraglib.syncdata.ITagSerializable;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.oktawia.insaneae2addons.InsaneConfig;
import org.jetbrains.annotations.NotNull;

public final class CoolantTank implements IFluidHandler, ITagSerializable<CompoundTag>, IContentChangeAware {

    private static final String AMOUNT_TAG = "amount";

    @Getter
    @Setter
    private Runnable onContentsChanged;

    private int amount;

    public CoolantTank(Runnable onContentsChanged) {
        this.onContentsChanged = onContentsChanged;
    }

    public static int capacity() {
        return InsaneConfig.COMMON.PENROSE_COOLANT_VENT_CAPACITY.get();
    }

    public static boolean isCoolant(FluidStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        ResourceLocation configured = ResourceLocation.tryParse(InsaneConfig.COMMON.PENROSE_COOLANT_FLUID.get());
        return configured != null && configured.equals(ForgeRegistries.FLUIDS.getKey(stack.getFluid()));
    }

    public int getAmount() {
        return this.amount;
    }

    public int drain(int wanted) {
        if (wanted <= 0 || this.amount <= 0) {
            return 0;
        }

        int drained = Math.min(wanted, this.amount);
        this.amount -= drained;
        this.onContentsChanged.run();
        return drained;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (this.amount <= 0) {
            return FluidStack.EMPTY;
        }

        ResourceLocation configured = ResourceLocation.tryParse(InsaneConfig.COMMON.PENROSE_COOLANT_FLUID.get());
        if (configured == null) {
            return FluidStack.EMPTY;
        }

        var fluid = ForgeRegistries.FLUIDS.getValue(configured);
        return fluid == null ? FluidStack.EMPTY : new FluidStack(fluid, this.amount);
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return isCoolant(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (!isCoolant(resource)) {
            return 0;
        }

        int accepted = Math.min(resource.getAmount(), capacity() - this.amount);
        if (accepted <= 0) {
            return 0;
        }

        if (action.execute()) {
            this.amount += accepted;
            this.onContentsChanged.run();
        }
        return accepted;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        return isCoolant(resource) ? drain(resource.getAmount(), action) : FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack available = getFluidInTank(0);
        if (available.isEmpty() || maxDrain <= 0) {
            return FluidStack.EMPTY;
        }

        int drained = Math.min(maxDrain, this.amount);
        if (action.execute()) {
            this.amount -= drained;
            this.onContentsChanged.run();
        }

        FluidStack result = available.copy();
        result.setAmount(drained);
        return result;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(AMOUNT_TAG, this.amount);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.amount = Math.max(0, tag.getInt(AMOUNT_TAG));
        this.onContentsChanged.run();
    }
}
