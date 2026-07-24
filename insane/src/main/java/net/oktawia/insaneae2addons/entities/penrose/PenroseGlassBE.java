package net.oktawia.insaneae2addons.entities.penrose;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockFrameBE;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import org.jetbrains.annotations.Nullable;

public class PenroseGlassBE extends AbstractMultiblockFrameBE<PortablePenroseSphereControllerBE> {

    public PenroseGlassBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.PENROSE_GLASS_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.PENROSE_GLASS_BLOCK.get()),
                2.0F
        );
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
