package net.oktawia.insaneae2addons.menus.block;

import net.minecraft.world.entity.player.Inventory;

import lombok.Getter;

import appeng.client.gui.Icon;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.OutputSlot;

import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.entities.penrose.ReinforcedMatterCondenserBE;

public class ReinforcedMatterCondenserMenu extends AEBaseMenu implements IProgressProvider {

    private final ReinforcedMatterCondenserBE host;

    @Getter
    private final IProgressProvider cellProgress = new CellProgress();

    public ReinforcedMatterCondenserMenu(int id, Inventory playerInventory, ReinforcedMatterCondenserBE host) {
        super(InsaneMenuRegistrar.REINFORCED_MATTER_CONDENSER_MENU.get(), id, playerInventory, host);
        this.host = host;

        this.addSlot(new AppEngSlot(host.getInputInventory(), 0), SlotSemantics.MACHINE_INPUT);
        this.addSlot(new AppEngSlot(host.getComponentInventory(), 0), SlotSemantics.STORAGE_CELL);
        this.addSlot(new OutputSlot(host.getOutputInventory(), 0, Icon.CONDENSER_OUTPUT_SINGULARITY),
                SlotSemantics.MACHINE_OUTPUT);

        this.createPlayerInventorySlots(playerInventory);
    }

    @Override
    public int getCurrentProgress() {
        return this.host.getStoredSingularities();
    }

    @Override
    public int getMaxProgress() {
        return ReinforcedMatterCondenserBE.SINGULARITIES_PER_SUPER;
    }

    private final class CellProgress implements IProgressProvider {

        @Override
        public int getCurrentProgress() {
            return ReinforcedMatterCondenserMenu.this.host.getInstalledCellComponents();
        }

        @Override
        public int getMaxProgress() {
            return ReinforcedMatterCondenserBE.REQUIRED_CELL_COMPONENTS;
        }
    }
}
