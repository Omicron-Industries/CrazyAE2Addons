package net.oktawia.insaneae2addons.menus.block;

import net.minecraft.world.entity.player.Inventory;

import lombok.Getter;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.slot.AppEngSlot;

import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.entities.AutoEnchanterBE;

public class AutoEnchanterMenu extends AEBaseMenu {

    private static final String SYNC_OPTION = "syncOption";
    private static final String TOGGLE_LAPIS = "toggleLapis";
    private static final String TOGGLE_BOOKS = "toggleBooks";

    @Getter
    private final AutoEnchanterBE host;

    public AutoEnchanterMenu(int id, Inventory ip, AutoEnchanterBE host) {
        super(InsaneMenuRegistrar.AUTO_ENCHANTER_MENU.get(), id, ip, host);
        this.host = host;

        addSlot(new AppEngSlot(host.getInternalInventory(), 0), SlotSemantics.MACHINE_INPUT);
        addSlot(new AppEngSlot(host.getInternalInventory(), 1), SlotSemantics.MACHINE_INPUT);
        addSlot(new AppEngSlot(host.getInternalInventory(), 2), SlotSemantics.MACHINE_OUTPUT);

        registerClientAction(SYNC_OPTION, Integer.class, this::syncOption);
        registerClientAction(TOGGLE_LAPIS, Boolean.class, this::toggleLapis);
        registerClientAction(TOGGLE_BOOKS, Boolean.class, this::toggleBooks);

        createPlayerInventorySlots(ip);
    }

    public void syncOption(int option) {
        host.setOption(option);
        if (isClientSide()) {
            sendClientAction(SYNC_OPTION, option);
        }
    }

    public void toggleLapis(boolean value) {
        host.setAutoSupplyLapis(value);
        if (isClientSide()) {
            sendClientAction(TOGGLE_LAPIS, value);
        }
    }

    public void toggleBooks(boolean value) {
        host.setAutoSupplyBooks(value);
        if (isClientSide()) {
            sendClientAction(TOGGLE_BOOKS, value);
        }
    }
}
