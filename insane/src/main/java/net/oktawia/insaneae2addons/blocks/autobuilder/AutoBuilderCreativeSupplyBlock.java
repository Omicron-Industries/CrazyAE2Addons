package net.oktawia.insaneae2addons.blocks.autobuilder;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.oktawia.insaneae2addons.entities.autobuilder.AutoBuilderCreativeSupplyBE;
import org.jetbrains.annotations.Nullable;

public class AutoBuilderCreativeSupplyBlock extends AEBaseEntityBlock<AutoBuilderCreativeSupplyBE> {

    public AutoBuilderCreativeSupplyBlock() {
        super(AEBaseBlock.metalProps());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AutoBuilderCreativeSupplyBE(pos, state);
    }
}