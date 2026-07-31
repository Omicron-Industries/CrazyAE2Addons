package net.oktawia.crazyae2addons.client.renderer.preview.multiblock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class MultiblockPreviewInfo {
    private static final int VALIDATION_INTERVAL_TICKS = 5;

    public final BlockPos origin;
    public final Direction facing;
    public float lastTick;

    private final List<Section> sections;
    private final Map<BlockPos, BlockInfo> byPos;
    private final List<BlockPos> invalidBlocks = new ArrayList<>();
    private final Set<BlockPos> placed = new HashSet<>();
    private final Set<BlockPos> frontier = new HashSet<>();

    private final int minY;
    private final float[] alpha;

    private boolean validated;
    private long lastValidationTick;

    public MultiblockPreviewInfo(BlockPos origin, Direction facing, Collection<BlockInfo> blockInfos) {
        this.origin = origin.immutable();
        this.facing = facing;

        this.byPos = new HashMap<>(blockInfos.size() * 2);
        Map<Long, Section> bySection = new HashMap<>();

        int lowestY = Integer.MAX_VALUE;
        int highestY = Integer.MIN_VALUE;

        for (BlockInfo info : blockInfos) {
            this.byPos.put(info.pos(), info);
        }

        for (BlockInfo info : blockInfos) {
            BlockPos pos = info.pos();

            lowestY = Math.min(lowestY, pos.getY());
            highestY = Math.max(highestY, pos.getY());

            long key = SectionPos.asLong(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
            bySection.computeIfAbsent(key, Section::new).entries.add(info);
        }

        this.sections = List.copyOf(bySection.values());
        this.minY = lowestY == Integer.MAX_VALUE ? 0 : lowestY;
        this.alpha = new float[Math.max(1, highestY - this.minY + 1)];
        this.lastTick = 0.0f;
    }

    public boolean isStale(MultiblockPreviewHost host) {
        return !origin.equals(host.getPreviewOrigin()) || facing != host.getPreviewFacing();
    }

    public List<Section> sections() {
        return this.sections;
    }

    public List<BlockPos> invalidBlocks() {
        return this.invalidBlocks;
    }

    public @Nullable BlockInfo at(BlockPos pos) {
        return this.byPos.get(pos);
    }

    public void validate(Level level, long gameTime) {
        if (this.validated && gameTime - this.lastValidationTick < VALIDATION_INTERVAL_TICKS) {
            return;
        }
        this.validated = true;
        this.lastValidationTick = gameTime;

        this.invalidBlocks.clear();
        this.placed.clear();

        for (Section section : this.sections) {
            section.missing.clear();

            for (BlockInfo info : section.entries) {
                BlockPos pos = info.pos();
                if (!level.isLoaded(pos)) {
                    continue;
                }

                BlockState current = level.getBlockState(pos);
                if (info.allowedBlocks().contains(current.getBlock())) {
                    this.placed.add(pos);
                    continue;
                }

                if (!current.isAir()) {
                    this.invalidBlocks.add(pos);
                }
                section.missing.add(info);
            }
        }

        this.frontier.clear();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (Section section : this.sections) {
            for (BlockInfo info : section.missing) {
                for (Direction direction : Direction.values()) {
                    cursor.setWithOffset(info.pos(), direction);
                    if (this.placed.contains(cursor)) {
                        this.frontier.add(info.pos());
                        break;
                    }
                }
            }
        }
    }

    public Set<BlockPos> frontierBlocks() {
        return this.frontier;
    }

    public boolean isMissing(Level level, BlockInfo info) {
        BlockState current = level.getBlockState(info.pos());
        return current.isAir() || !info.allowedBlocks().contains(current.getBlock());
    }

    public void advanceAlpha(float deltaTick, float target, float step) {
        for (int i = 0; i < this.alpha.length; i++) {
            float current = this.alpha[i];
            if (current < target) {
                this.alpha[i] = Math.min(target, current + deltaTick * step);
            } else if (current > target) {
                this.alpha[i] = Math.max(target, current - deltaTick * step);
            }
        }
    }

    public float alphaAt(int worldY) {
        int index = worldY - this.minY;
        return (index < 0 || index >= this.alpha.length) ? 0.0f : this.alpha[index];
    }

    public record BlockInfo(BlockPos pos, BlockState state, Set<Block> allowedBlocks) {
    }

    public static final class Section {
        private final AABB bounds;
        private final List<BlockInfo> entries = new ArrayList<>();
        private final List<BlockInfo> missing = new ArrayList<>();

        private Section(long sectionKey) {
            int x = SectionPos.x(sectionKey) << 4;
            int y = SectionPos.y(sectionKey) << 4;
            int z = SectionPos.z(sectionKey) << 4;
            this.bounds = new AABB(x, y, z, x + 16, y + 16, z + 16);
        }

        public AABB bounds() {
            return this.bounds;
        }

        public List<BlockInfo> missing() {
            return this.missing;
        }
    }
}
