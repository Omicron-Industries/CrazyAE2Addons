package net.oktawia.insaneae2addons.mobstorage;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class MobKey extends AEKey {

    public static final MapCodec<MobKey> MAP_CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    ResourceLocation.CODEC
                            .fieldOf("mob")
                            .forGetter(key -> ForgeRegistries.ENTITY_TYPES.getKey(key.entityType))
            ).apply(inst, mobId -> new MobKey(ForgeRegistries.ENTITY_TYPES.getValue(mobId)))
    );

    public static final Codec<MobKey> CODEC = MAP_CODEC.codec();

    private final EntityType<?> entityType;

    private MobKey(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    @Nullable
    public static MobKey of(EntityType<?> entityType) {
        return entityType == null ? null : new MobKey(entityType);
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    @Override
    public AEKeyType getType() {
        return MobKeyType.TYPE;
    }

    @Override
    public AEKey dropSecondary() {
        return this;
    }

    @Override
    public CompoundTag toTag() {
        DataResult<Tag> result = CODEC.encodeStart(NbtOps.INSTANCE, this);
        Tag raw = result.resultOrPartial(err -> {}).orElse(new CompoundTag());
        return raw instanceof CompoundTag ct ? ct : new CompoundTag();
    }

    @Nullable
    public static MobKey fromTag(CompoundTag tag) {
        return CODEC.decode(NbtOps.INSTANCE, tag)
                .resultOrPartial(err -> {})
                .map(pair -> pair.getFirst())
                .orElse(null);
    }

    @Override
    public Object getPrimaryKey() {
        return ForgeRegistries.ENTITY_TYPES.getKey(entityType).getPath();
    }

    @Override
    public ResourceLocation getId() {
        return ForgeRegistries.ENTITY_TYPES.getKey(entityType);
    }

    @Override
    public void writeToPacket(FriendlyByteBuf data) {
        DataResult<Tag> result = CODEC.encodeStart(NbtOps.INSTANCE, this);
        data.writeNbt((CompoundTag) result.result().orElse(new CompoundTag()));
    }

    @Override
    protected Component computeDisplayName() {
        return entityType.getDescription();
    }

    @Override
    public void addDrops(long amount, List<ItemStack> drops, Level level, BlockPos pos) {}

    @Override
    public boolean isTagged(TagKey<?> tag) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof MobKey mk) && mk.entityType == entityType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityType);
    }

    @Override
    public String toString() {
        return "MobKey{" + entityType.toShortString() + '}';
    }
}
