package net.oktawia.insaneae2addons.menus.item;

import appeng.menu.AEBaseMenu;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.items.mobstorage.MobKeySelectorItem;
import net.oktawia.insaneae2addons.logic.mobstorage.MobKeySelectorHost;

public class MobKeySelectorMenu extends AEBaseMenu {
    public static final String CHOOSE = "chooseMobKey";

    public final MobKeySelectorHost host;

    public MobKeySelectorMenu(int id, Inventory ip, MobKeySelectorHost host) {
        super(InsaneMenuRegistrar.MOB_KEY_SELECTOR_MENU.get(), id, ip, host);
        this.host = host;
        registerClientAction(CHOOSE, String.class, this::choose);
        createPlayerInventorySlots(ip);
    }

    public String getSelectedKey() {
        return MobKeySelectorItem.getSelectedKeyId(host.getItemStack());
    }

    public void choose(String keyId) {
        MobKeySelectorItem.setSelectedKeyId(host.getItemStack(), keyId);
        if (isClientSide()) {
            sendClientAction(CHOOSE, keyId);
        }
    }
}
