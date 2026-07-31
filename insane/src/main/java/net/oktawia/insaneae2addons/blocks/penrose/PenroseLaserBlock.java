package net.oktawia.insaneae2addons.blocks.penrose;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;

import net.oktawia.crazyae2addons.util.AbstractMenuOpeningBlock;
import net.oktawia.insaneae2addons.entities.penrose.PenroseLaserBE;

public class PenroseLaserBlock extends AbstractMenuOpeningBlock<PenroseLaserBE> {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");

    public PenroseLaserBlock() {
        super(Properties.of().strength(4f).mapColor(MapColor.METAL).sound(SoundType.METAL));
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(FORMED, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PenroseLaserBE(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
        builder.add(FORMED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection().getOpposite())
                .setValue(FORMED, false);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
            boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof PenroseLaserBE laser) {
            laser.updateRedstone();
        }
    }
}
