package net.oktawia.insaneae2addons.menus.part;

import appeng.api.stacks.AEItemKey;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.slot.FakeSlot;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.parts.NbtExportBusPart;
import net.oktawia.insaneae2addons.util.NbtFormatter;
import org.jetbrains.annotations.Nullable;

public class NbtExportBusMenu extends AEBaseMenu {
    public static final String SEND_DATA = "SendData";

    public final NbtExportBusPart host;

    public NbtExportBusMenu(int id, Inventory playerInventory, NbtExportBusPart host) {
        super(InsaneMenuRegistrar.NBT_EXPORT_BUS_MENU.get(), id, playerInventory, host);
        registerClientAction(SEND_DATA, String.class, this::updateData);
        this.createPlayerInventorySlots(playerInventory);
        this.addSlot(new FakeSlot(host.inv.createMenuWrapper(), 0), SlotSemantics.CONFIG);
        this.host = host;
    }

    public void updateData(String data) {
        if (isClientSide()) {
            sendClientAction(SEND_DATA, data);
        } else {
            host.setFilter(data);
        }
    }

    @Nullable
    public String loadNBT() {
        if (host.inv.getKey(0) instanceof AEItemKey ik && ik.getTag() != null) {
            return NbtFormatter.format(ik.getTag().toString());
        }
        return null;
    }
}
