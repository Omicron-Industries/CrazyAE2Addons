package net.oktawia.insaneae2addons.blocks.cradle;

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
import net.minecraft.world.level.material.MapColor;

import net.oktawia.crazyae2addons.util.AbstractMenuOpeningBlock;
import net.oktawia.insaneae2addons.entities.cradle.EntropyCradleControllerBE;

public class EntropyCradleControllerBlock extends AbstractMenuOpeningBlock<EntropyCradleControllerBE> {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");

    public EntropyCradleControllerBlock() {
        super(Properties.of().strength(2f).mapColor(MapColor.METAL).sound(SoundType.METAL));
        registerDefaultState(stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(FORMED, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EntropyCradleControllerBE(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
        builder.add(POWERED);
        builder.add(FORMED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite())
                .setValue(POWERED, false)
                .setValue(FORMED, false);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide() && state.getBlock() != newState.getBlock()) {
            EntropyCradleControllerBE be = getBlockEntity(level, pos);
            if (be != null) {
                be.unformMembersForRemoval();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
            boolean isMoving) {
        if (level.isClientSide()) {
            return;
        }

        boolean wasPowered = state.getValue(POWERED);
        boolean isPoweredNow = level.hasNeighborSignal(pos);

        if (!wasPowered && isPoweredNow) {
            EntropyCradleControllerBE be = getBlockEntity(level, pos);
            if (be != null) {
                be.onRedstonePulse();
            }
            level.setBlock(pos, state.setValue(POWERED, true), Block.UPDATE_CLIENTS);
        } else if (wasPowered && !isPoweredNow) {
            level.setBlock(pos, state.setValue(POWERED, false), Block.UPDATE_CLIENTS);
        }
    }
}
