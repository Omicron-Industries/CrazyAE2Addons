package net.oktawia.insaneae2addons.blocks.penrose;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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
import net.oktawia.insaneae2addons.entities.penrose.PortablePenroseSphereControllerBE;

public class PortablePenroseSphereControllerBlock extends AbstractMenuOpeningBlock<PortablePenroseSphereControllerBE> {

    public static final BooleanProperty FORMED = BooleanProperty.create("formed");

    public PortablePenroseSphereControllerBlock() {
        super(Properties.of().strength(4f).mapColor(MapColor.METAL).sound(SoundType.METAL));
        registerDefaultState(stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(FORMED, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PortablePenroseSphereControllerBE(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
        builder.add(FORMED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite())
                .setValue(FORMED, false);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide() || !(placer instanceof ServerPlayer player)) {
            return;
        }

        PortablePenroseSphereControllerBE be = getBlockEntity(level, pos);
        if (be != null) {
            be.setPlacer(player);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide() && state.getBlock() != newState.getBlock()) {
            PortablePenroseSphereControllerBE be = getBlockEntity(level, pos);
            if (be != null) {
                be.unformMembersForRemoval();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
