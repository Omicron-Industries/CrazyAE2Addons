package net.oktawia.insaneae2addons.entities.cradle;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockFrameBE;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import org.jetbrains.annotations.Nullable;

public class EntropyCradleWallBE extends AbstractMultiblockFrameBE<EntropyCradleControllerBE> {

    public EntropyCradleWallBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.ENTROPY_CRADLE_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.ENTROPY_CRADLE_BLOCK.get()),
                1.0F
        );
    }

    @Override
    protected Class<EntropyCradleControllerBE> controllerClass() {
        return EntropyCradleControllerBE.class;
    }

    @Override
    protected void onControllerChanged(@Nullable EntropyCradleControllerBE newController) {
        if (newController != null) {
            connectToControllerGrid();
        } else {
            disconnectFromControllerGrid();
        }
    }
}
