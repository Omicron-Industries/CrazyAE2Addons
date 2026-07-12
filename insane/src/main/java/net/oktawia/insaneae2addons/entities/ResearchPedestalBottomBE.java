package net.oktawia.insaneae2addons.entities;

import appeng.blockentity.AEBaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.oktawia.insaneae2addons.blocks.ICableMachine;
import net.oktawia.insaneae2addons.blocks.ResearchCableBlock;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;

import java.util.Collections;
import java.util.List;

public class ResearchPedestalBottomBE extends AEBaseBlockEntity implements ICableMachine {

    public ResearchPedestalBottomBE(BlockPos pos, BlockState blockState) {
        super(InsaneBlockEntityRegistrar.RESEARCH_PEDESTAL_BOTTOM_BE.get(), pos, blockState);
    }

    public List<BlockPos> getConnectedMachines() {
        if (this.level == null || this.level.isClientSide()) {
            return Collections.emptyList();
        }

        List<BlockPos> machines = ResearchCableBlock.findConnectedMachines(this.level, this.worldPosition);
        machines.remove(this.worldPosition);
        return machines;
    }

    public ResearchUnitBE getConnectedUnit() {
        if (this.level == null) {
            return null;
        }

        List<BlockPos> machines = getConnectedMachines();
        if (machines.size() != 1) {
            return null;
        }

        return this.level.getBlockEntity(machines.get(0)) instanceof ResearchUnitBE unit ? unit : null;
    }

    public boolean isValidConnection() {
        return getConnectedUnit() != null;
    }

    public int getConnectedComputation() {
        ResearchUnitBE unit = getConnectedUnit();
        return unit != null ? unit.getComputation() : 0;
    }

    public boolean doWork() {
        ResearchUnitBE unit = getConnectedUnit();
        return unit != null && unit.doWork();
    }
}
