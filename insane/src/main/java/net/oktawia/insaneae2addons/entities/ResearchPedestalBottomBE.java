package net.oktawia.insaneae2addons.entities;

import appeng.blockentity.AEBaseBlockEntity;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.oktawia.crazyae2addons.util.IMenuOpeningBlockEntity;
import net.oktawia.insaneae2addons.blocks.ICableMachine;
import net.oktawia.insaneae2addons.blocks.ResearchCableBlock;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.logic.research.ResearchStatus;
import net.oktawia.insaneae2addons.menus.block.ResearchPedestalMenu;

import java.util.Collections;
import java.util.List;

public class ResearchPedestalBottomBE extends AEBaseBlockEntity
        implements ICableMachine, MenuProvider, IMenuOpeningBlockEntity {

    public ResearchPedestalBottomBE(BlockPos pos, BlockState blockState) {
        super(InsaneBlockEntityRegistrar.RESEARCH_PEDESTAL_BOTTOM_BE.get(), pos, blockState);
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        MenuOpener.open(InsaneMenuRegistrar.RESEARCH_PEDESTAL_MENU.get(), player, locator);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ResearchPedestalMenu(id, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
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

    public ResearchStatus getNodeStatus() {
        ResearchUnitBE unit = getConnectedUnit();
        return unit != null ? unit.getNodeStatus() : ResearchStatus.STRUCTURE_INCOMPLETE;
    }
}
