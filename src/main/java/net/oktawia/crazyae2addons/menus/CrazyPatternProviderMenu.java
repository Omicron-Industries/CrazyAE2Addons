package net.oktawia.crazyae2addons.menus;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import lombok.Getter;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.PatternProviderMenu;
import appeng.menu.slot.RestrictedInputSlot;

import net.oktawia.crazyae2addons.IsModLoaded;
import net.oktawia.crazyae2addons.client.screens.CrazyPatternProviderScreen;
import net.oktawia.crazyae2addons.defs.regs.CrazyMenuRegistrar;
import net.oktawia.crazyae2addons.entities.CrazyPatternProviderBE;
import net.oktawia.crazyae2addons.network.NetworkHandler;
import net.oktawia.crazyae2addons.network.packets.UpdatePatternsPacket;
import net.oktawia.crazyae2addons.parts.CrazyPatternProviderPart;

public class CrazyPatternProviderMenu extends PatternProviderMenu {

    private static final String SYNC = "patternSync";

    @Getter
    private final PatternProviderLogicHost host;

    @GuiSync(38)
    public Integer slotNum;

    public CrazyPatternProviderMenu(int id, Inventory ip, PatternProviderLogicHost host) {
        super(CrazyMenuRegistrar.CRAZY_PATTERN_PROVIDER_MENU.get(), id, ip, host);
        this.host = host;

        this.slotNum = host.getLogic().getPatternInv().size();
        registerClientAction(SYNC, Integer.class, this::handleRequestUpdate);

        if (!IsModLoaded.APP_FLUX) {
            IUpgradeInventory upgradeInv = null;
            if (host.getBlockEntity() instanceof CrazyPatternProviderBE crazyBE) {
                upgradeInv = crazyBE.getUpgrades();
            } else if (host instanceof CrazyPatternProviderPart crazyPart) {
                upgradeInv = crazyPart.getUpgrades();
            }

            if (upgradeInv != null && !upgradeInv.isEmpty()) {
                for (int i = 0; i < upgradeInv.size(); i++) {
                    var slot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.UPGRADES, upgradeInv, i);
                    slot.setNotDraggable();
                    this.addSlot(slot, SlotSemantics.UPGRADE);
                }
            }
        }
    }

    public void requestUpdate(int startRow) {
        if (isClientSide()) {
            sendClientAction(SYNC, startRow);
        } else {
            handleRequestUpdate(startRow);
        }
    }

    private void handleRequestUpdate(int startRow) {
        if (isClientSide()) {
            return;
        }
        int startIndex = Math.max(0, Math.min(slotNum - 1, startRow * CrazyPatternProviderScreen.COLS));
        int count = Math.min(CrazyPatternProviderScreen.VISIBLE_ROWS * CrazyPatternProviderScreen.COLS,
                Math.max(0, slotNum - startIndex));

        var inventory = this.host.getLogic().getPatternInv();
        List<ItemStack> visibleStacks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            visibleStacks.add(inventory.getStackInSlot(startIndex + i));
        }

        NetworkHandler.sendToPlayer((ServerPlayer) getPlayer(), new UpdatePatternsPacket(startIndex, visibleStacks));
    }
}
