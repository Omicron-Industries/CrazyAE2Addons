package net.oktawia.insaneae2addons.entities.autobuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import appeng.blockentity.grid.AENetworkBlockEntity;

import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;

public class AutoBuilderCreativeSupplyBE extends AENetworkBlockEntity {

    public AutoBuilderCreativeSupplyBE(BlockPos pos, BlockState blockState) {
        super(InsaneBlockEntityRegistrar.AUTO_BUILDER_CREATIVE_SUPPLY_BE.get(), pos, blockState);
        this.getMainNode()
                .setIdlePowerUsage(0)
                .setVisualRepresentation(
                        new ItemStack(InsaneBlockRegistrar.AUTO_BUILDER_CREATIVE_SUPPLY_BLOCK.get().asItem()));
    }
}
