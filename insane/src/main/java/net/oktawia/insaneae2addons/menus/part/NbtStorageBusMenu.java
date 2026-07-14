package net.oktawia.insaneae2addons.menus.part;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Settings;
import appeng.api.config.StorageFilter;
import appeng.api.config.YesNo;
import appeng.api.stacks.AEItemKey;
import appeng.api.util.IConfigManager;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.slot.FakeSlot;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.parts.NbtStorageBusPart;
import net.oktawia.insaneae2addons.util.NbtFormatter;
import org.jetbrains.annotations.Nullable;

public class NbtStorageBusMenu extends UpgradeableMenu<NbtStorageBusPart> {
    public static final String SEND_DATA = "SendData";

    @GuiSync(3)
    @Getter
    public AccessRestriction readWriteMode = AccessRestriction.READ_WRITE;
    @GuiSync(4)
    @Getter
    public StorageFilter storageFilter = StorageFilter.EXTRACTABLE_ONLY;
    @GuiSync(7)
    @Getter
    public YesNo filterOnExtract = YesNo.YES;
    @GuiSync(8)
    @Nullable
    public Component connectedTo;

    public final NbtStorageBusPart host;

    public NbtStorageBusMenu(int id, Inventory playerInventory, NbtStorageBusPart host) {
        super(InsaneMenuRegistrar.NBT_STORAGE_BUS_MENU.get(), id, playerInventory, host);
        registerClientAction(SEND_DATA, String.class, this::updateData);
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

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        this.connectedTo = this.host.getConnectedToDescription();
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        this.setFuzzyMode(cm.getSetting(Settings.FUZZY_MODE));
        this.readWriteMode = cm.getSetting(Settings.ACCESS);
        this.storageFilter = cm.getSetting(Settings.STORAGE_FILTER);
        this.filterOnExtract = cm.getSetting(Settings.FILTER_ON_EXTRACT);
    }
}
