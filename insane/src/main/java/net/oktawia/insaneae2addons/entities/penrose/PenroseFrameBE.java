package net.oktawia.insaneae2addons.entities.penrose;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockFrameBE;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PenroseFrameBE extends AbstractMultiblockFrameBE<PortablePenroseSphereControllerBE> {

    public PenroseFrameBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.PENROSE_FRAME_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.PENROSE_FRAME_BLOCK.get()),
                2.0F
        );
    }

    @Override
    protected Class<PortablePenroseSphereControllerBE> controllerClass() {
        return PortablePenroseSphereControllerBE.class;
    }

    public @Nullable PortablePenroseSphereControllerBE getController() {
        return getResolvedController();
    }

    @Override
    protected void onControllerChanged(@Nullable PortablePenroseSphereControllerBE newController) {
        if (newController != null) {
            connectToControllerGrid();
        } else {
            disconnectFromControllerGrid();
        }
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        PortablePenroseSphereControllerBE controller = getResolvedController();
        if (cap == ForgeCapabilities.ENERGY && controller != null) {
            return controller.getCapability(cap, side);
        }
        return super.getCapability(cap, side);
    }
}
