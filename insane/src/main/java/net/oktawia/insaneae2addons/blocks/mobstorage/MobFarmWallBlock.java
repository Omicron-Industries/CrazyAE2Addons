package net.oktawia.insaneae2addons.blocks.mobstorage;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class MobFarmWallBlock extends MobFarmPartBlock {

    public static final BooleanProperty FORMED = BooleanProperty.create("formed");

    public MobFarmWallBlock() {
        registerDefaultState(defaultBlockState().setValue(FORMED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FORMED);
    }
}
