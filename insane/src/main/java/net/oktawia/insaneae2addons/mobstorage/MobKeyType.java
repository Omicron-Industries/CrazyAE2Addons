package net.oktawia.insaneae2addons.mobstorage;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.oktawia.insaneae2addons.InsaneAddons;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class MobKeyType extends AEKeyType {

    public static final AEKeyType TYPE = new MobKeyType();

    private MobKeyType() {
        super(InsaneAddons.makeId("mob"), MobKey.class,
                Component.translatable("gui.insaneae2addons.mob_key"));
    }

    @Nullable
    @Override
    public AEKey readFromPacket(FriendlyByteBuf input) {
        DataResult<MobKey> result = MobKey.CODEC.parse(NbtOps.INSTANCE, input.readNbt());
        return result.result().orElse(null);
    }

    @Nullable
    @Override
    public AEKey loadKeyFromTag(CompoundTag tag) {
        DataResult<MobKey> result = MobKey.CODEC.parse(NbtOps.INSTANCE, tag);
        return result.result().orElse(null);
    }

    @Override
    public Stream<TagKey<?>> getTagNames() {
        return Stream.empty();
    }

    @Override
    public String getUnitSymbol() {
        return "Mobs";
    }
}
