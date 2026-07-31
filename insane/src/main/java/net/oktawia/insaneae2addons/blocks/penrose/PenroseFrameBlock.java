package net.oktawia.insaneae2addons.blocks.penrose;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;

import net.oktawia.crazyae2addons.util.AbstractMenuOpeningBlock;
import net.oktawia.insaneae2addons.entities.penrose.PenroseFrameBE;

public class PenroseFrameBlock extends AbstractMenuOpeningBlock<PenroseFrameBE> {

    public static final BooleanProperty FORMED = BooleanProperty.create("formed");

    public PenroseFrameBlock() {
        super(Properties.of().strength(4f).mapColor(MapColor.METAL).sound(SoundType.METAL));
        registerDefaultState(defaultBlockState().setValue(FORMED, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PenroseFrameBE(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FORMED);
    }
}
