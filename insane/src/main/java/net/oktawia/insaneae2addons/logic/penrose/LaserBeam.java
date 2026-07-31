package net.oktawia.insaneae2addons.logic.penrose;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockControllerBE;
import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockFrameBE;
import net.oktawia.insaneae2addons.InsaneConfig;

public final class LaserBeam {

    private LaserBeam() {
    }

    public static float fire(ServerLevel level, BlockPos laserPos, Direction direction, long power) {
        if (power <= 0) {
            return 0.0f;
        }

        int range = InsaneConfig.COMMON.PENROSE_LASER_MAX_RANGE.get();
        long fePerHardness = InsaneConfig.COMMON.PENROSE_LASER_FE_PER_HARDNESS.get();
        long fePerDamage = InsaneConfig.COMMON.PENROSE_LASER_FE_PER_DAMAGE.get();
        double maxDamagePerEntity = InsaneConfig.COMMON.PENROSE_LASER_MAX_DAMAGE_PER_ENTITY.get();

        BlockPos start = laserPos.relative(direction);
        List<List<LivingEntity>> targets = bucketTargets(level, start, direction, range);
        DamageSource source = level.damageSources().magic();

        BlockPos.MutableBlockPos cursor = start.mutable();
        long remaining = power;

        for (int step = 0; step < range; step++) {
            if (step > 0) {
                cursor.move(direction);
            }
            if (level.isOutsideBuildHeight(cursor) || !level.isLoaded(cursor)) {
                return step;
            }

            remaining = hurtTargets(targets.get(step), source, remaining, fePerDamage, maxDamagePerEntity);
            if (remaining <= 0) {
                return step + 1;
            }

            BlockState state = level.getBlockState(cursor);
            if (state.isAir() || state.getBlock() instanceof LiquidBlock) {
                continue;
            }
            if (isMultiblockPart(level, cursor)) {
                return step;
            }

            float hardness = state.getDestroySpeed(level, cursor);
            if (hardness < 0.0f) {
                return step;
            }

            long cost = (long) Math.ceil(hardness * (double) fePerHardness);
            if (cost > remaining) {
                return step;
            }

            remaining -= cost;
            destroy(level, cursor.immutable(), state);
        }

        return range;
    }

    private static void destroy(ServerLevel level, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        List<ItemStack> drops = Block.getDrops(state, level, pos, blockEntity);

        level.destroyBlock(pos, false);

        for (ItemStack drop : drops) {
            if (drop.getItem().isFireResistant()) {
                Block.popResource(level, pos, drop);
            }
        }
    }

    private static long hurtTargets(List<LivingEntity> targets,
            DamageSource source,
            long remaining,
            long fePerDamage,
            double maxDamagePerEntity) {
        for (LivingEntity target : targets) {
            if (remaining <= 0) {
                return 0;
            }
            double affordable = Math.min((double) remaining / fePerDamage, maxDamagePerEntity);
            float before = target.getHealth() + target.getAbsorptionAmount();
            target.hurt(source, (float) affordable);
            float absorbed = before - (target.getHealth() + target.getAbsorptionAmount());
            if (absorbed > 0.0f) {
                remaining -= (long) Math.ceil(absorbed * (double) fePerDamage);
            }
        }
        return Math.max(remaining, 0L);
    }

    private static boolean isMultiblockPart(ServerLevel level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof AbstractMultiblockControllerBE) {
            return true;
        }
        return be instanceof AbstractMultiblockFrameBE<?> frame && frame.getActiveController() != null;
    }

    private static List<List<LivingEntity>> bucketTargets(ServerLevel level,
            BlockPos start,
            Direction direction,
            int range) {
        List<List<LivingEntity>> buckets = new ArrayList<>(range);
        for (int i = 0; i < range; i++) {
            buckets.add(List.of());
        }

        int reach = range - 1;
        AABB box = new AABB(start).expandTowards(
                direction.getStepX() * reach,
                direction.getStepY() * reach,
                direction.getStepZ() * reach);

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive)) {
            int step = Mth.clamp(stepIndex(entity.getBoundingBox(), start, direction), 0, reach);
            List<LivingEntity> bucket = buckets.get(step);
            if (bucket.isEmpty()) {
                bucket = new ArrayList<>(1);
                buckets.set(step, bucket);
            }
            bucket.add(entity);
        }

        return buckets;
    }

    private static int stepIndex(AABB box, BlockPos start, Direction direction) {
        return switch (direction) {
            case EAST -> Mth.floor(box.minX) - start.getX();
            case WEST -> start.getX() - Mth.floor(box.maxX);
            case UP -> Mth.floor(box.minY) - start.getY();
            case DOWN -> start.getY() - Mth.floor(box.maxY);
            case SOUTH -> Mth.floor(box.minZ) - start.getZ();
            case NORTH -> start.getZ() - Mth.floor(box.maxZ);
        };
    }
}
