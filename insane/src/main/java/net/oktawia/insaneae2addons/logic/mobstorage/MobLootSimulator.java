package net.oktawia.insaneae2addons.logic.mobstorage;

import com.mojang.authlib.GameProfile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.oktawia.insaneae2addons.mixins.LivingEntityDropInvoker;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class MobLootSimulator {

    public record Result(List<ItemStack> drops, int experience) {
        public static final Result EMPTY = new Result(List.of(), 0);
    }

    private final ServerLevel level;
    private final Map<EntityType<?>, LivingEntity> samples = new HashMap<>();

    private @Nullable FakePlayer killer;

    public MobLootSimulator(ServerLevel level) {
        this.level = level;
    }

    public Result simulate(EntityType<?> type, ItemStack weapon, int extraLooting) {
        LivingEntity sample = sampleOf(type);
        if (sample == null) {
            return Result.EMPTY;
        }

        ItemStack tool = weapon.isEmpty() ? new ItemStack(Items.STICK) : weapon.copy();
        int looting = applyLooting(tool, extraLooting);

        Map<Item, Integer> collected = new HashMap<>();
        collectLootTableDrops(sample, type, tool, looting, collected);
        collectCustomDeathLoot(sample, looting, collected);

        List<ItemStack> drops = new ArrayList<>(collected.size());
        for (Map.Entry<Item, Integer> entry : collected.entrySet()) {
            drops.add(new ItemStack(entry.getKey(), entry.getValue()));
        }

        return new Result(drops, sample.getExperienceReward());
    }

    private void collectLootTableDrops(LivingEntity sample, EntityType<?> type, ItemStack tool, int looting,
                                       Map<Item, Integer> collected) {
        LootTable lootTable = lootTableOf(type);
        if (lootTable == null) {
            return;
        }

        LootParams params = lootParams(sample, tool);
        for (int roll = 0; roll <= looting; roll++) {
            for (ItemStack stack : lootTable.getRandomItems(params)) {
                merge(collected, stack);
            }
        }
    }

    private void collectCustomDeathLoot(LivingEntity sample, int looting, Map<Item, Integer> collected) {
        DamageSource source = this.level.damageSources().playerAttack(killer());
        List<ItemStack> captured = MobDropCapture.collect(() ->
                ((LivingEntityDropInvoker) sample).invokeDropCustomDeathLoot(source, looting, true));

        for (ItemStack stack : captured) {
            merge(collected, stack);
        }
    }

    private static void merge(Map<Item, Integer> collected, ItemStack stack) {
        if (!stack.isEmpty() && !stack.hasTag() && stack.getMaxStackSize() != 1) {
            collected.merge(stack.getItem(), stack.getCount(), Integer::sum);
        }
    }

    private static int applyLooting(ItemStack tool, int extraLooting) {
        Map<Enchantment, Integer> enchantments = new HashMap<>(EnchantmentHelper.getEnchantments(tool));
        int looting = extraLooting + enchantments.getOrDefault(Enchantments.MOB_LOOTING, 0);
        enchantments.put(Enchantments.MOB_LOOTING, looting);
        EnchantmentHelper.setEnchantments(enchantments, tool);
        return looting;
    }

    private LootParams lootParams(LivingEntity sample, ItemStack tool) {
        FakePlayer player = killer();
        return new LootParams.Builder(this.level)
                .withParameter(LootContextParams.THIS_ENTITY, sample)
                .withOptionalParameter(LootContextParams.KILLER_ENTITY, player)
                .withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, player)
                .withParameter(LootContextParams.DAMAGE_SOURCE, this.level.damageSources().playerAttack(player))
                .withParameter(LootContextParams.TOOL, tool)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(sample.blockPosition()))
                .create(LootContextParamSets.ENTITY);
    }

    private @Nullable LootTable lootTableOf(EntityType<?> type) {
        ResourceLocation id = type.getDefaultLootTable();
        return id == null ? null : this.level.getServer().getLootData().getLootTable(id);
    }

    private @Nullable LivingEntity sampleOf(EntityType<?> type) {
        LivingEntity cached = this.samples.get(type);
        if (cached != null) {
            return cached;
        }

        Entity created;
        try {
            created = type.create(this.level);
        } catch (Exception e) {
            return null;
        }

        if (!(created instanceof LivingEntity living)) {
            return null;
        }

        this.samples.put(type, living);
        return living;
    }

    private FakePlayer killer() {
        if (this.killer == null) {
            this.killer = FakePlayerFactory.get(this.level,
                    new GameProfile(UUID.randomUUID(), "[InsaneAE2Addons]"));
        }
        return this.killer;
    }
}
