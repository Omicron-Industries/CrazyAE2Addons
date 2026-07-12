package net.oktawia.insaneae2addons.menus.block;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.entities.ResearchStationBE;
import net.oktawia.insaneae2addons.logic.research.ResearchStatus;

public class ResearchStationMenu extends AEBaseMenu {

    private static final String ACT_UNLOCK_ALL = "unlock_all";

    private final ResearchStationBE host;
    private final Inventory playerInv;

    @GuiSync(880) public int progressPct;
    @GuiSync(881) public int statusOrdinal;

    public final RecipeProgress recipeBar = new RecipeProgress();

    public ResearchStationMenu(int id, Inventory playerInventory, ResearchStationBE host) {
        super(InsaneMenuRegistrar.RESEARCH_STATION_MENU.get(), id, playerInventory, host);
        this.host = host;
        this.playerInv = playerInventory;
        this.progressPct = host.getProgressPct();
        this.statusOrdinal = host.getStatus().ordinal();

        this.addSlot(new AppEngSlot(host.getDiskInventory(), 0), SlotSemantics.MACHINE_OUTPUT);

        this.registerClientAction(ACT_UNLOCK_ALL, this::unlockAllClick);
        this.createPlayerInventorySlots(playerInventory);
    }

    public void unlockAllClick() {
        if (isClientSide()) {
            sendClientAction(ACT_UNLOCK_ALL);
        } else if (playerInv != null && playerInv.player.isCreative()) {
            host.unlockAllToDisk();
        }
    }

    public ResearchStatus status() {
        return ResearchStatus.values()[Math.floorMod(statusOrdinal, ResearchStatus.values().length)];
    }

    @Override
    public void broadcastChanges() {
        if (!isClientSide()) {
            this.progressPct = host.getProgressPct();
            this.statusOrdinal = host.getStatus().ordinal();
        }
        super.broadcastChanges();
    }

    public class RecipeProgress implements IProgressProvider {
        @Override
        public int getCurrentProgress() {
            return ResearchStationMenu.this.progressPct;
        }

        @Override
        public int getMaxProgress() {
            return 1000;
        }
    }
}
