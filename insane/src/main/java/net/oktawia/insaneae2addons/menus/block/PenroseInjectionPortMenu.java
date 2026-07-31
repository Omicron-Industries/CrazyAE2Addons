package net.oktawia.insaneae2addons.menus.block;

import net.minecraft.world.entity.player.Inventory;

import lombok.Getter;

import appeng.menu.AEBaseMenu;

import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.entities.penrose.PenroseInjectionPortBE;

public class PenroseInjectionPortMenu extends AEBaseMenu {

    private static final String SET_RATE = "set_rate";

    @Getter
    private final PenroseInjectionPortBE host;

    public PenroseInjectionPortMenu(int id, Inventory playerInventory, PenroseInjectionPortBE host) {
        super(InsaneMenuRegistrar.PENROSE_INJECTION_PORT_MENU.get(), id, playerInventory, host);
        this.host = host;

        registerClientAction(SET_RATE, Integer.class, this::setRate);
        createPlayerInventorySlots(playerInventory);
    }

    public void setRate(int rate) {
        this.host.setDesiredRate(rate);
        if (isClientSide()) {
            sendClientAction(SET_RATE, rate);
        }
    }
}
