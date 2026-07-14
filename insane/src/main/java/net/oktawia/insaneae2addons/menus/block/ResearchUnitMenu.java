package net.oktawia.insaneae2addons.menus.block;

import appeng.menu.guisync.GuiSync;
import appeng.menu.interfaces.IProgressProvider;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockControllerMenu;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.entities.research.ResearchUnitBE;
import net.oktawia.insaneae2addons.logic.research.ResearchStatus;

public class ResearchUnitMenu extends AbstractMultiblockControllerMenu {

    private final ResearchUnitBE host;

    @GuiSync(880) public boolean formed;
    @GuiSync(881) public int computation;
    @GuiSync(882) public int fluidAmount;
    @GuiSync(883) public int storedPower;
    @GuiSync(884) public int statusOrdinal;

    public final FluidProgress fluidBar = new FluidProgress();
    public final PowerProgress powerBar = new PowerProgress();

    public ResearchUnitMenu(int id, Inventory playerInventory, ResearchUnitBE host) {
        super(InsaneMenuRegistrar.RESEARCH_UNIT_MENU.get(), id, playerInventory, host);
        this.host = host;
        this.formed = host.isFormed();
        this.computation = host.getComputation();
        this.fluidAmount = host.getFluidBuffer().getFluidAmount();
        this.storedPower = (int) host.getStoredPower();
        this.statusOrdinal = host.getNodeStatus().ordinal();

        this.createPlayerInventorySlots(playerInventory);
    }

    public ResearchStatus status() {
        return ResearchStatus.values()[Math.floorMod(statusOrdinal, ResearchStatus.values().length)];
    }

    @Override
    public void broadcastChanges() {
        if (!isClientSide()) {
            this.formed = host.isFormed();
            this.computation = host.getComputation();
            this.fluidAmount = host.getFluidBuffer().getFluidAmount();
            this.storedPower = (int) host.getStoredPower();
            this.statusOrdinal = host.getNodeStatus().ordinal();
        }
        super.broadcastChanges();
    }

    public class FluidProgress implements IProgressProvider {
        @Override
        public int getCurrentProgress() {
            return ResearchUnitMenu.this.fluidAmount;
        }

        @Override
        public int getMaxProgress() {
            return ResearchUnitBE.fluidBufferCapacity();
        }
    }

    public class PowerProgress implements IProgressProvider {
        @Override
        public int getCurrentProgress() {
            return ResearchUnitMenu.this.storedPower;
        }

        @Override
        public int getMaxProgress() {
            return (int) ResearchUnitBE.powerBufferCapacity();
        }
    }
}
