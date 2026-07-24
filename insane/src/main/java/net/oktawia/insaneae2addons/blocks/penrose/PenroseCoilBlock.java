package net.oktawia.insaneae2addons.blocks.penrose;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.oktawia.crazyae2addons.util.AbstractMenuOpeningBlock;
import net.oktawia.insaneae2addons.entities.penrose.PenroseCoilBE;
import org.jetbrains.annotations.Nullable;

public class PenroseCoilBlock extends AbstractMenuOpeningBlock<PenroseCoilBE> {

    public PenroseCoilBlock() {
        super(Properties.of().strength(4f).mapColor(MapColor.METAL).sound(SoundType.METAL));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PenroseCoilBE(pos, state);
    }
}
