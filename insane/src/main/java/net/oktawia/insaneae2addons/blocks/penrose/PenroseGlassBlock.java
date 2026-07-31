package net.oktawia.insaneae2addons.blocks.penrose;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;

import net.oktawia.crazyae2addons.util.AbstractMenuOpeningBlock;
import net.oktawia.insaneae2addons.entities.penrose.PenroseGlassBE;

public class PenroseGlassBlock extends AbstractMenuOpeningBlock<PenroseGlassBE> {

    public static final BooleanProperty FORMED = BooleanProperty.create("formed");

    public PenroseGlassBlock() {
        super(Properties.of()
                .strength(4f)
                .mapColor(MapColor.NONE)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isViewBlocking((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false));
        registerDefaultState(defaultBlockState().setValue(FORMED, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PenroseGlassBE(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FORMED);
    }

    @Override
    public boolean skipRendering(@NotNull BlockState state, BlockState adjacentState, @NotNull Direction side) {
        return adjacentState.is(this) || super.skipRendering(state, adjacentState, side);
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter level,
            @NotNull BlockPos pos) {
        return true;
    }

    @Override
    public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 1.0F;
    }
}
