package net.oktawia.insaneae2addons.logic.penrose;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public final class BlackHoleField {

    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private static final int FLUSH_EVERY_TICKS = 2;
    private static final int PERSIST_EVERY_CHUNKS = 16;
    private static final int CHUNK_PICK_ATTEMPTS = 8;

    public record Snapshot(UUID id, BlockPos center, int radius, long[] processedChunks) {}

    @Getter
    private final ServerLevel level;
    @Getter
    private final UUID id;
    @Getter
    private final BlockPos center;
    @Getter
    private final int radius;

    private final long radiusSq;
    private final int[] sectionOrder;

    private final LongOpenHashSet targetChunks = new LongOpenHashSet();
    private final LongOpenHashSet processedChunks = new LongOpenHashSet();
    private final LongOpenHashSet queuedChunks = new LongOpenHashSet();
    private final ArrayDeque<ChunkPos> workQueue = new ArrayDeque<>();
    private final LongOpenHashSet dirtyChunks = new LongOpenHashSet();

    private final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

    @Nullable
    private LevelChunk currentChunk;
    @Nullable
    private ChunkPos currentChunkPos;
    private int sectionCursor;
    private boolean currentChunkModified;

    private int flushCountdown;
    private int processedSincePersist;

    @Getter
    private boolean done;
    private boolean persistDirty;

    public BlackHoleField(ServerLevel level, BlockPos center, int radius) {
        this(level, new Snapshot(UUID.randomUUID(), center, radius, null));
    }

    public BlackHoleField(ServerLevel level, Snapshot snapshot) {
        this.level = level;
        this.id = snapshot.id();
        this.center = snapshot.center().immutable();
        this.radius = snapshot.radius();
        this.radiusSq = (long) snapshot.radius() * snapshot.radius();
        this.sectionOrder = buildSectionOrder();

        if (snapshot.processedChunks() != null) {
            for (long key : snapshot.processedChunks()) {
                this.processedChunks.add(key);
            }
        }

        for (ChunkPos pos : buildChunkOrder()) {
            this.targetChunks.add(pos.toLong());
            if (!this.processedChunks.contains(pos.toLong())
                    && level.getChunkSource().getChunkNow(pos.x, pos.z) != null) {
                enqueue(pos);
            }
        }
    }

    public Snapshot snapshot() {
        return new Snapshot(this.id, this.center, this.radius, this.processedChunks.toLongArray());
    }

    public boolean consumePersistDirty() {
        boolean dirty = this.persistDirty;
        this.persistDirty = false;
        return dirty;
    }

    public void chunkLoaded(ChunkPos pos) {
        long key = pos.toLong();
        if (!this.targetChunks.contains(key) || this.processedChunks.contains(key)) {
            return;
        }
        if (this.currentChunkPos != null && this.currentChunkPos.toLong() == key) {
            return;
        }
        enqueue(pos);
    }

    public void tick(long budgetNanos) {
        long deadline = System.nanoTime() + budgetNanos;
        while (!this.done && System.nanoTime() < deadline) {
            if (this.processedChunks.size() >= this.targetChunks.size()) {
                this.done = true;
                this.persistDirty = true;
            } else if (!step()) {
                break;
            }
        }

        if (--this.flushCountdown <= 0 || this.done) {
            this.flushCountdown = FLUSH_EVERY_TICKS;
            flushDirtyChunks();
        }
    }

    private boolean step() {
        if (this.currentChunkPos != null && !isLoaded(this.currentChunkPos)) {
            requeueCurrentChunk();
            dropCurrentChunk();
            return true;
        }

        if (this.currentChunk == null) {
            return pickNextChunk();
        }

        while (this.sectionCursor < this.sectionOrder.length) {
            int sectionIndex = this.sectionOrder[this.sectionCursor++];
            LevelChunkSection section = this.currentChunk.getSection(sectionIndex);
            if (section == null) {
                continue;
            }

            int bottomY = this.level.getSectionYFromSectionIndex(sectionIndex);
            if (!intersectsSphere(this.currentChunkPos, bottomY)) {
                continue;
            }

            clearSection(section, bottomY);
            return true;
        }

        finishCurrentChunk();
        return true;
    }

    private boolean pickNextChunk() {
        for (int attempt = 0; attempt < CHUNK_PICK_ATTEMPTS && !this.workQueue.isEmpty(); attempt++) {
            ChunkPos pos = this.workQueue.pollFirst();
            long key = pos.toLong();
            this.queuedChunks.remove(key);

            if (this.processedChunks.contains(key)) {
                continue;
            }

            LevelChunk chunk = this.level.getChunkSource().getChunkNow(pos.x, pos.z);
            if (chunk == null) {
                enqueue(pos);
                continue;
            }

            this.currentChunk = chunk;
            this.currentChunkPos = pos;
            this.sectionCursor = 0;
            this.currentChunkModified = false;
            return true;
        }

        return false;
    }

    private void clearSection(LevelChunkSection section, int bottomY) {
        int baseX = this.currentChunkPos.x << 4;
        int baseZ = this.currentChunkPos.z << 4;

        for (int ly = 0; ly < 16; ly++) {
            long dy = (long) (bottomY + ly) - this.center.getY();
            long dySq = dy * dy;

            for (int lz = 0; lz < 16; lz++) {
                long dz = (long) (baseZ + lz) - this.center.getZ();
                long dyzSq = dySq + dz * dz;
                if (dyzSq > this.radiusSq) {
                    continue;
                }

                for (int lx = 0; lx < 16; lx++) {
                    long dx = (long) (baseX + lx) - this.center.getX();
                    if (dyzSq + dx * dx > this.radiusSq) {
                        continue;
                    }

                    BlockState state = section.getBlockState(lx, ly, lz);
                    if (state.isAir() || state.is(Blocks.BEDROCK)) {
                        continue;
                    }

                    if (state.hasBlockEntity()) {
                        this.cursor.set(baseX + lx, bottomY + ly, baseZ + lz);
                        this.currentChunk.removeBlockEntity(this.cursor);
                    }

                    section.setBlockState(lx, ly, lz, AIR, false);
                    this.currentChunkModified = true;
                }
            }
        }
    }

    private void finishCurrentChunk() {
        if (this.currentChunkModified) {
            Heightmap.primeHeightmaps(this.currentChunk, EnumSet.allOf(Heightmap.Types.class));
            this.dirtyChunks.add(this.currentChunkPos.toLong());
        }

        if (this.processedChunks.add(this.currentChunkPos.toLong())
                && ++this.processedSincePersist >= PERSIST_EVERY_CHUNKS) {
            this.processedSincePersist = 0;
            this.persistDirty = true;
        }

        dropCurrentChunk();
    }

    private void flushDirtyChunks() {
        if (this.dirtyChunks.isEmpty()) {
            return;
        }

        for (long key : this.dirtyChunks) {
            ChunkPos pos = new ChunkPos(key);
            LevelChunk chunk = this.level.getChunkSource().getChunkNow(pos.x, pos.z);
            if (chunk == null) {
                continue;
            }

            chunk.setUnsaved(true);
            chunk.setLightCorrect(false);

            ClientboundLevelChunkWithLightPacket packet =
                    new ClientboundLevelChunkWithLightPacket(chunk, this.level.getLightEngine(), null, null);
            this.level.getChunkSource().chunkMap
                    .getPlayers(pos, false)
                    .forEach(player -> player.connection.send(packet));
        }

        this.dirtyChunks.clear();
    }

    private void enqueue(ChunkPos pos) {
        if (this.queuedChunks.add(pos.toLong())) {
            this.workQueue.addLast(pos);
        }
    }

    private void requeueCurrentChunk() {
        if (this.currentChunkPos != null && !this.processedChunks.contains(this.currentChunkPos.toLong())) {
            enqueue(this.currentChunkPos);
        }
    }

    private void dropCurrentChunk() {
        this.currentChunk = null;
        this.currentChunkPos = null;
        this.currentChunkModified = false;
    }

    private boolean isLoaded(ChunkPos pos) {
        return this.level.getChunkSource().getChunkNow(pos.x, pos.z) != null;
    }

    private boolean intersectsSphere(ChunkPos pos, int bottomY) {
        int minX = pos.x << 4;
        int minZ = pos.z << 4;
        return distanceSqToBox(minX, bottomY, minZ, minX + 15, bottomY + 15, minZ + 15) <= this.radiusSq;
    }

    private long distanceSqToBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        long dx = axisDistance(this.center.getX(), minX, maxX);
        long dy = axisDistance(this.center.getY(), minY, maxY);
        long dz = axisDistance(this.center.getZ(), minZ, maxZ);
        return dx * dx + dy * dy + dz * dz;
    }

    private static long axisDistance(int value, int min, int max) {
        if (value < min) {
            return min - value;
        }
        return value > max ? value - max : 0L;
    }

    private int[] buildSectionOrder() {
        int minIndex = this.level.getSectionIndex(this.level.getMinBuildHeight());
        int maxIndex = this.level.getSectionIndex(this.level.getMaxBuildHeight() - 1);
        int centerIndex = this.level.getSectionIndex(this.center.getY());

        return IntStream.rangeClosed(minIndex, maxIndex)
                .boxed()
                .sorted(Comparator.comparingInt(index -> Math.abs(index - centerIndex)))
                .mapToInt(Integer::intValue)
                .toArray();
    }

    private List<ChunkPos> buildChunkOrder() {
        int centerChunkX = this.center.getX() >> 4;
        int centerChunkZ = this.center.getZ() >> 4;
        int chunkRadius = (this.radius + 15) >> 4;

        List<ChunkPos> order = new ArrayList<>();
        for (int cx = centerChunkX - chunkRadius; cx <= centerChunkX + chunkRadius; cx++) {
            for (int cz = centerChunkZ - chunkRadius; cz <= centerChunkZ + chunkRadius; cz++) {
                int minX = cx << 4;
                int minZ = cz << 4;
                long dx = axisDistance(this.center.getX(), minX, minX + 15);
                long dz = axisDistance(this.center.getZ(), minZ, minZ + 15);

                if (dx * dx + dz * dz <= this.radiusSq) {
                    order.add(new ChunkPos(cx, cz));
                }
            }
        }

        order.sort(Comparator.comparingLong(pos -> {
            long dx = ((long) pos.x << 4) + 8L - this.center.getX();
            long dz = ((long) pos.z << 4) + 8L - this.center.getZ();
            return dx * dx + dz * dz;
        }));

        return order;
    }
}
