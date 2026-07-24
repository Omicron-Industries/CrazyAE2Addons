package net.oktawia.insaneae2addons.blocks.penrose;

import appeng.block.AEBaseEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.oktawia.insaneae2addons.entities.penrose.SuperSingularityBE;
import org.jetbrains.annotations.Nullable;

public class SuperSingularityBlock extends AEBaseEntityBlock<SuperSingularityBE> {

    public SuperSingularityBlock() {
        super(Properties.of().strength(4f).mapColor(MapColor.METAL).sound(SoundType.METAL));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SuperSingularityBE(pos, state);
    }
}
