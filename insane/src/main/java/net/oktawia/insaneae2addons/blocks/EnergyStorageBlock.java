package net.oktawia.insaneae2addons.blocks;

import lombok.Getter;

import appeng.block.networking.EnergyCellBlock;

public class EnergyStorageBlock extends EnergyCellBlock {

    @Getter
    private final long maxEnergy;

    public EnergyStorageBlock(long maxEnergy) {
        super((double) maxEnergy, (double) maxEnergy, (int) (maxEnergy / 100));
        this.maxEnergy = maxEnergy;
    }
}
