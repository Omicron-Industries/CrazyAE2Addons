package net.oktawia.insaneae2addons.mobstorage;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import appeng.api.behaviors.ContainerItemStrategies;
import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;

import net.oktawia.insaneae2addons.items.mobstorage.MobKeySelectorItem;

public final class MobKeyContainerStrategy {

    private MobKeyContainerStrategy() {
    }

    @Nullable
    private static MobKey keyFrom(ItemStack stack) {
        if (!(stack.getItem() instanceof MobKeySelectorItem)) {
            return null;
        }
        String id = MobKeySelectorItem.getSelectedKeyId(stack);
        if (id.isEmpty()) {
            return null;
        }
        ResourceLocation rl = ResourceLocation.tryParse(id);
        if (rl == null) {
            return null;
        }
        EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(rl);
        return type != null ? MobKey.of(type) : null;
    }

    public static void register() {
        ContainerItemStrategies.register(MobKeyType.TYPE, MobKey.class, new ContainerItemStrategy() {
            @Override
            public @Nullable GenericStack getContainedStack(ItemStack stack) {
                MobKey key = keyFrom(stack);
                return key != null ? new GenericStack(key, 1) : null;
            }

            @Override
            public @Nullable MobKey findCarriedContext(Player player, AbstractContainerMenu menu) {
                return keyFrom(player.containerMenu.getCarried());
            }

            @Override
            public @Nullable GenericStack getExtractableContent(Object context) {
                return null;
            }

            @Override
            public long insert(Object ctx, AEKey what, long amount, Actionable mode) {
                return 0;
            }

            @Override
            public long extract(Object ctx, AEKey what, long amount, Actionable mode) {
                return 0;
            }

            @Override
            public void playFillSound(Player player, AEKey what) {
            }

            @Override
            public void playEmptySound(Player player, AEKey what) {
            }
        });
    }
}
