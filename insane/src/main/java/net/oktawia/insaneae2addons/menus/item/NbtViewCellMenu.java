package net.oktawia.insaneae2addons.menus.item;

import net.minecraft.world.entity.player.Inventory;

import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;

import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.logic.nbt.ViewCellHost;

public class NbtViewCellMenu extends AEBaseMenu {
    public static final String SEND_DATA = "SendData";

    @GuiSync(92)
    public boolean newData;
    @GuiSync(31)
    public String data;

    public ViewCellHost host;

    public NbtViewCellMenu(int id, Inventory playerInventory, ViewCellHost host) {
        super(InsaneMenuRegistrar.NBT_VIEW_CELL_MENU.get(), id, playerInventory, host);
        registerClientAction(SEND_DATA, String.class, this::updateData);
        this.createPlayerInventorySlots(playerInventory);
        this.host = host;
        var tag = host.getItemStack().getOrCreateTag();
        if (tag.contains("filter")) {
            this.data = tag.getString("filter");
        } else {
            this.data = "";
        }
        this.newData = false;
    }

    public void updateData(String data) {
        this.data = data;
        this.host.getItemStack().getOrCreateTag().putString("filter", data);
        if (isClientSide()) {
            sendClientAction(SEND_DATA, data);
        }
    }
}
