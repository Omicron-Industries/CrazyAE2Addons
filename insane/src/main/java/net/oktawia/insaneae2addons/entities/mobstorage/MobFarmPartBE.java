package net.oktawia.insaneae2addons.entities.mobstorage;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockFrameBE;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;

public class MobFarmPartBE extends AbstractMultiblockFrameBE<MobFarmControllerBE> {

    public MobFarmPartBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.MOB_FARM_PART_BE.get(),
                pos,
                blockState,
                new ItemStack(blockState.getBlock()),
                1.0F);
    }

    @Override
    protected Class<MobFarmControllerBE> controllerClass() {
        return MobFarmControllerBE.class;
    }

    @Override
    protected void onControllerChanged(@Nullable MobFarmControllerBE newController) {
        if (newController != null) {
            connectToControllerGrid();
        } else {
            disconnectFromControllerGrid();
        }
    }
}
