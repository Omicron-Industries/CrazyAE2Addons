package net.oktawia.insaneae2addons.entities.mobstorage;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockFrameBE;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;

public class SpawnerExtractorWallBE extends AbstractMultiblockFrameBE<SpawnerExtractorControllerBE> {

    public SpawnerExtractorWallBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.SPAWNER_EXTRACTOR_WALL_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.SPAWNER_EXTRACTOR_WALL_BLOCK.get()),
                1.0F);
    }

    @Override
    protected Class<SpawnerExtractorControllerBE> controllerClass() {
        return SpawnerExtractorControllerBE.class;
    }

    @Override
    protected void onControllerChanged(@Nullable SpawnerExtractorControllerBE newController) {
        if (newController != null) {
            connectToControllerGrid();
        } else {
            disconnectFromControllerGrid();
        }
    }
}
