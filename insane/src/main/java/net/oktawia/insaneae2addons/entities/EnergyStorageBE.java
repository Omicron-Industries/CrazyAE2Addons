package net.oktawia.insaneae2addons.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import appeng.blockentity.networking.EnergyCellBlockEntity;

import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.blocks.EnergyStorageBlock;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;

public class EnergyStorageBE extends EnergyCellBlockEntity {

    public EnergyStorageBE(BlockPos pos, BlockState state) {
        super(InsaneBlockEntityRegistrar.ENERGY_STORAGE_BE.get(), pos, state);
    }

    @Override
    public double getAEMaxPower() {
        if (getBlockState().getBlock() instanceof EnergyStorageBlock esBlock) {
            return (double) esBlock.getMaxEnergy() * InsaneConfig.COMMON.ENERGY_STORAGE_CAPACITY_MULTIPLIER.get();
        }
        return super.getAEMaxPower();
    }
}
