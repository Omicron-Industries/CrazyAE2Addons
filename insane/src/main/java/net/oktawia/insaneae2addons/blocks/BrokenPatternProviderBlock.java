package net.oktawia.insaneae2addons.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import appeng.block.crafting.PatternProviderBlock;

import net.oktawia.insaneae2addons.entities.BrokenPatternProviderBE;

public class BrokenPatternProviderBlock extends PatternProviderBlock {

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BrokenPatternProviderBE(pos, state);
    }
}
