package net.oktawia.insaneae2addons.menus.block;

import appeng.menu.AEBaseMenu;
import lombok.Getter;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.insaneae2addons.entities.penrose.PenroseEmitterBE;

public class PenroseEmitterMenu extends AEBaseMenu {

    private static final String SET_ON = "set_on";
    private static final String SET_OFF = "set_off";

    @Getter
    private final PenroseEmitterBE host;

    public PenroseEmitterMenu(int id, Inventory playerInventory, PenroseEmitterBE host) {
        super(host.getMenuType(), id, playerInventory, host);
        this.host = host;

        registerClientAction(SET_ON, Double.class, this::setOnPercent);
        registerClientAction(SET_OFF, Double.class, this::setOffPercent);
        createPlayerInventorySlots(playerInventory);
    }

    public void setOnPercent(double onPercent) {
        this.host.setThresholds(onPercent, this.host.getOffPercent());
        if (isClientSide()) {
            sendClientAction(SET_ON, onPercent);
        }
    }

    public void setOffPercent(double offPercent) {
        this.host.setThresholds(this.host.getOnPercent(), offPercent);
        if (isClientSide()) {
            sendClientAction(SET_OFF, offPercent);
        }
    }
}
