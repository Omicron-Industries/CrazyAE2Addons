package net.oktawia.crazyae2addons.items.wireless;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import de.mari_023.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;

import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.api.networking.IGrid;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEKey;
import appeng.api.util.IConfigManager;
import appeng.menu.locator.MenuLocators;
import appeng.util.ConfigInventory;

import net.oktawia.crazyae2addons.CrazyConfig;
import net.oktawia.crazyae2addons.defs.regs.CrazyMenuRegistrar;
import net.oktawia.crazyae2addons.logic.wireless.WirelessNotificationTerminalItemLogicHost;
import net.oktawia.crazyae2addons.menus.item.WirelessNotificationTerminalMenu;
import net.oktawia.crazyae2addons.network.NetworkHandler;
import net.oktawia.crazyae2addons.network.packets.NotificationHudPacket;

public class WirelessNotificationTerminalItem extends ItemWT implements IUniversalWirelessTerminalItem {

    private static int SLOTS = -1;
    private static final String NBT_CONFIG = WirelessNotificationTerminalItemLogicHost.NBT_CONFIG;
    private static final String NBT_ROOT = "crazy_notification_monitor";
    private static final String NBT_THRESHOLDS = "thresholds";
    private static final String NBT_HUD_X = "hudX";
    private static final String NBT_HUD_Y = "hudY";
    private static final String NBT_HIDE_ABOVE = "hideAbove";
    private static final String NBT_HIDE_BELOW = "hideBelow";
    private static final String NBT_HUD_SCALE = "hudscale";

    @Override
    public @NotNull MenuType<?> getMenuType(@NotNull ItemStack stack) {
        return CrazyMenuRegistrar.WIRELESS_NOTIFICATION_TERMINAL_MENU.get();
    }

    @Override
    public @NotNull IConfigManager getConfigManager(@NotNull ItemStack target) {
        IConfigManager cm = super.getConfigManager(target);
        cm.registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        cm.registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        return cm;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level w, Player player, InteractionHand hand) {
        ItemStack is = player.getItemInHand(hand);
        if (!CrazyConfig.COMMON.WIRELESS_NOTIFICATION_TERMINAL_ENABLED.get()) {
            return new InteractionResultHolder(InteractionResult.FAIL, is);
        }
        if (this.checkUniversalPreconditions(is, player)) {
            this.open(player, is, MenuLocators.forHand(player, hand), false);
            return new InteractionResultHolder(InteractionResult.SUCCESS, is);
        } else {
            return new InteractionResultHolder(InteractionResult.FAIL, is);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (!CrazyConfig.COMMON.WIRELESS_NOTIFICATION_TERMINAL_ENABLED.get()) {
            if (!level.isClientSide && entity instanceof ServerPlayer player && (player.tickCount % 20) == 0) {
                NetworkHandler.sendToPlayer(
                        player,
                        new NotificationHudPacket(List.of(), (byte) 100, (byte) 0, (byte) 100));
            }
            return;
        }

        if (level.isClientSide) {
            return;
        }
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }
        if ((player.tickCount % 20) != 0) {
            return;
        }
        if (player.containerMenu instanceof WirelessNotificationTerminalMenu) {
            return;
        }

        var menuHost = this.getMenuHost(player, MenuLocators.forInventorySlot(slotId), stack);
        if (menuHost instanceof WTMenuHost wtMenuHost && !wtMenuHost.rangeCheck()) {
            return;
        }

        sendHudUpdate(player, stack);
    }

    private void sendHudUpdate(ServerPlayer player, ItemStack terminalStack) {
        if (SLOTS == -1 || SLOTS != CrazyConfig.COMMON.WIRELESS_NOTIFICATION_TERMINAL_CONFIG_SLOT.get()) {
            SLOTS = CrazyConfig.COMMON.WIRELESS_NOTIFICATION_TERMINAL_CONFIG_SLOT.get();
        }

        IGrid grid = this.getLinkedGrid(terminalStack, player.level(), player);
        if (grid == null) {
            return;
        }

        IStorageService storage = grid.getStorageService();
        var cached = storage.getCachedInventory();

        CompoundTag tag = terminalStack.getOrCreateTag();
        CompoundTag root = tag.contains(NBT_ROOT) ? tag.getCompound(NBT_ROOT) : new CompoundTag();

        long[] thresholds = Arrays.copyOf(root.getLongArray(NBT_THRESHOLDS), SLOTS);

        int hudX = clampPercent(root.getInt(NBT_HUD_X), 100);
        int hudY = clampPercent(root.getInt(NBT_HUD_Y), 0);
        int hudScale = clampPercent(root.contains(NBT_HUD_SCALE) ? root.getInt(NBT_HUD_SCALE) : 100, 100);

        boolean hideAbove = root.getBoolean(NBT_HIDE_ABOVE);
        boolean hideBelow = root.getBoolean(NBT_HIDE_BELOW);

        ConfigInventory cfg = ConfigInventory.configTypes(SLOTS, () -> {
        });
        if (tag.contains(NBT_CONFIG)) {
            cfg.readFromChildTag(tag, NBT_CONFIG);
        }

        var inv = cfg.createMenuWrapper();
        var out = new ArrayList<NotificationHudPacket.Entry>(SLOTS);

        for (int i = 0; i < SLOTS; i++) {
            long threshold = thresholds[i];
            if (threshold <= 0) {
                continue;
            }

            ItemStack icon = inv.getStackInSlot(i);
            if (icon.isEmpty()) {
                continue;
            }

            AEKey key = cfg.getKey(i);
            if (key == null) {
                continue;
            }

            long amount = cached.get(key);
            boolean above = amount >= threshold;

            if (hideAbove && above) {
                continue;
            }
            if (hideBelow && !above) {
                continue;
            }

            ItemStack sendIcon = icon.copy();
            sendIcon.setCount(1);

            out.add(new NotificationHudPacket.Entry(sendIcon, amount, threshold));
        }

        NetworkHandler.sendToPlayer(
                player,
                new NotificationHudPacket(out, (byte) hudX, (byte) hudY, (byte) hudScale));
    }

    private static int clampPercent(int value, int fallback) {
        return value < 0 || value > 100 ? fallback : value;
    }
}
