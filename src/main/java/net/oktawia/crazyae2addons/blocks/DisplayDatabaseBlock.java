package net.oktawia.crazyae2addons.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.oktawia.crazyae2addons.CrazyConfig;
import net.oktawia.crazyae2addons.entities.DisplayDatabaseBE;
import net.oktawia.crazyae2addons.util.AbstractMenuOpeningBlock;
import org.jetbrains.annotations.Nullable;

public class DisplayDatabaseBlock extends AbstractMenuOpeningBlock<DisplayDatabaseBE> {

    public DisplayDatabaseBlock() {
        super(Properties.of().strength(2f).mapColor(MapColor.METAL).sound(SoundType.METAL));
    }

    @Override
    public InteractionResult onActivated(Level level, BlockPos pos, Player player,
                                         InteractionHand hand, @Nullable ItemStack heldItem,
                                         BlockHitResult hit) {
        if (!CrazyConfig.COMMON.DISPLAY_DATABASE_ENABLED.get()) {
            return InteractionResult.PASS;
        }

        return super.onActivated(level, pos, player, hand, heldItem, hit);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DisplayDatabaseBE(pos, state);
    }
}