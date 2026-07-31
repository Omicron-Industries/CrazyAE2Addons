package net.oktawia.crazyae2addons.logic.wireless;

import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import lombok.Getter;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.menu.ISubMenu;
import appeng.util.ConfigInventory;

import net.oktawia.crazyae2addons.CrazyConfig;
import net.oktawia.crazyae2addons.defs.regs.CrazyItemRegistrar;

public class WirelessNotificationTerminalItemLogicHost extends WTMenuHost implements IUpgradeableObject {

    public static final String NBT_CONFIG = "config";

    private final IUpgradeInventory upgrades = UpgradeInventories.forItem(this.getItemStack(), 2);

    @Getter
    private final ConfigInventory config = ConfigInventory.configTypes(getConfiguredSlotCount(), this::onConfigChanged);

    public WirelessNotificationTerminalItemLogicHost(
            Player player,
            @Nullable Integer slot,
            ItemStack itemStack,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(player, slot, itemStack, returnToMainMenu);
        readFromNbt();
    }

    public static int getConfiguredSlotCount() {
        return Math.max(0, CrazyConfig.COMMON.WIRELESS_NOTIFICATION_TERMINAL_CONFIG_SLOT.get());
    }

    private void onConfigChanged() {
        if (!getPlayer().level().isClientSide()) {
            config.writeToChildTag(getItemStack().getOrCreateTag(), NBT_CONFIG);
        }
    }

    @Override
    public void readFromNbt() {
        super.readFromNbt();

        CompoundTag tag = getItemStack().getOrCreateTag();
        if (tag.contains(NBT_CONFIG)) {
            config.readFromChildTag(tag, NBT_CONFIG);
        }
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(CrazyItemRegistrar.WIRELESS_NOTIFICATION_TERMINAL.get());
    }
}
