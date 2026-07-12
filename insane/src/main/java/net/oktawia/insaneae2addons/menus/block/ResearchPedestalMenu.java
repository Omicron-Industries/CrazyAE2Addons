package net.oktawia.insaneae2addons.menus.block;

import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.entities.ResearchPedestalBottomBE;
import net.oktawia.insaneae2addons.logic.research.ResearchStatus;

public class ResearchPedestalMenu extends AEBaseMenu {

    private final ResearchPedestalBottomBE host;

    @GuiSync(881) public int computation;
    @GuiSync(882) public boolean validConnection;
    @GuiSync(883) public int statusOrdinal;

    public ResearchPedestalMenu(int id, Inventory playerInventory, ResearchPedestalBottomBE host) {
        super(InsaneMenuRegistrar.RESEARCH_PEDESTAL_MENU.get(), id, playerInventory, host);
        this.host = host;
        this.computation = host.getConnectedComputation();
        this.validConnection = host.isValidConnection();
        this.statusOrdinal = host.getNodeStatus().ordinal();

        this.createPlayerInventorySlots(playerInventory);
    }

    public ResearchStatus status() {
        return ResearchStatus.values()[Math.floorMod(statusOrdinal, ResearchStatus.values().length)];
    }

    @Override
    public void broadcastChanges() {
        if (!isClientSide()) {
            this.computation = host.getConnectedComputation();
            this.validConnection = host.isValidConnection();
            this.statusOrdinal = host.getNodeStatus().ordinal();
        }
        super.broadcastChanges();
    }
}
