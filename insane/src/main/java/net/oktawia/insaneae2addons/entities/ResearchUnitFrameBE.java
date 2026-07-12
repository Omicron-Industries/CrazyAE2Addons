package net.oktawia.insaneae2addons.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockFrameBE;
import net.oktawia.insaneae2addons.blocks.ICableMachine;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import org.jetbrains.annotations.Nullable;

public class ResearchUnitFrameBE extends AbstractMultiblockFrameBE<ResearchUnitBE> implements ICableMachine {

    public ResearchUnitFrameBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.RESEARCH_UNIT_FRAME_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.RESEARCH_UNIT_FRAME_BLOCK.get()),
                1.0F
        );
    }

    @Override
    protected Class<ResearchUnitBE> controllerClass() {
        return ResearchUnitBE.class;
    }

    @Override
    protected void onControllerChanged(@Nullable ResearchUnitBE newController) {
        if (newController != null) {
            connectToControllerGrid();
        } else {
            disconnectFromControllerGrid();
        }
    }
}
