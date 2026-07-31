package net.oktawia.insaneae2addons.items.mobstorage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.items.AEBaseItem;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;

import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.logic.mobstorage.MobKeySelectorHost;

public class MobKeySelectorItem extends AEBaseItem implements IMenuItem {
    public static final String NBT_MOBKEY = "mob_key";

    public MobKeySelectorItem(Item.Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
            @NotNull InteractionHand hand) {
        if (!level.isClientSide() && !player.isSecondaryUseActive()) {
            MenuOpener.open(InsaneMenuRegistrar.MOB_KEY_SELECTOR_MENU.get(), player,
                    MenuLocators.forHand(player, hand));
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    @Override
    public @Nullable ItemMenuHost getMenuHost(Player player, int slot, ItemStack stack, @Nullable BlockPos pos) {
        return new MobKeySelectorHost(player, slot, stack);
    }

    public static void setSelectedKeyId(ItemStack stack, String keyId) {
        var tag = stack.getOrCreateTag();
        if (keyId == null || keyId.isEmpty()) {
            tag.remove(NBT_MOBKEY);
        } else {
            tag.putString(NBT_MOBKEY, keyId);
        }
    }

    public static String getSelectedKeyId(ItemStack stack) {
        var tag = stack.getTag();
        return tag != null && tag.contains(NBT_MOBKEY) ? tag.getString(NBT_MOBKEY) : "";
    }
}
