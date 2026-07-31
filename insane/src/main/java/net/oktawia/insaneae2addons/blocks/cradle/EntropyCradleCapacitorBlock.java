package net.oktawia.insaneae2addons.blocks.cradle;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;

import net.oktawia.crazyae2addons.util.AbstractMenuOpeningBlock;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.entities.cradle.EntropyCradleCapacitorBE;

public class EntropyCradleCapacitorBlock extends AbstractMenuOpeningBlock<EntropyCradleCapacitorBE> {

    public static final BooleanProperty FORMED = BooleanProperty.create("formed");
    public static final BooleanProperty POWER = BooleanProperty.create("power");

    public EntropyCradleCapacitorBlock() {
        super(Properties.of().strength(2f).mapColor(MapColor.METAL).sound(SoundType.METAL));
        registerDefaultState(defaultBlockState().setValue(FORMED, false).setValue(POWER, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EntropyCradleCapacitorBE(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FORMED);
        builder.add(POWER);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof EntropyCradleCapacitorBE cap
                && cap.getStoredEnergy() >= InsaneConfig.COMMON.CRADLE_CAPACITY.get()) {
            return 15;
        }
        return 0;
    }
}
