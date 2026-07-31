package net.oktawia.insaneae2addons.entities.penrose;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockFrameBE;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;

public class PenroseCoilBE extends AbstractMultiblockFrameBE<PortablePenroseSphereControllerBE> {

    public PenroseCoilBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.PENROSE_COIL_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.PENROSE_COIL_BLOCK.get()),
                2.0F);
    }

    @Override
    protected Class<PortablePenroseSphereControllerBE> controllerClass() {
        return PortablePenroseSphereControllerBE.class;
    }

    @Override
    protected void onControllerChanged(@Nullable PortablePenroseSphereControllerBE newController) {
        if (newController != null) {
            connectToControllerGrid();
        } else {
            disconnectFromControllerGrid();
        }
    }
}
