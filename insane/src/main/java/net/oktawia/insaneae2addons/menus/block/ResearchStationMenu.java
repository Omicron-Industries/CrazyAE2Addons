package net.oktawia.insaneae2addons.menus.block;

import net.minecraft.world.entity.player.Inventory;

import lombok.Getter;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;

import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.entities.research.ResearchStationBE;

public class ResearchStationMenu extends AEBaseMenu {

    private static final String ACT_UNLOCK_ALL = "unlock_all";

    @Getter
    private final ResearchStationBE host;
    private final Inventory playerInv;

    public final RecipeProgress recipeBar = new RecipeProgress();

    public ResearchStationMenu(int id, Inventory playerInventory, ResearchStationBE host) {
        super(InsaneMenuRegistrar.RESEARCH_STATION_MENU.get(), id, playerInventory, host);
        this.host = host;
        this.playerInv = playerInventory;

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

    public class RecipeProgress implements IProgressProvider {
        @Override
        public int getCurrentProgress() {
            return host.getProgressPct();
        }

        @Override
        public int getMaxProgress() {
            return 1000;
        }
    }
}
