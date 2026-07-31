package net.oktawia.insaneae2addons.entities.research;

import java.util.List;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import lombok.Getter;

import appeng.blockentity.AEBaseBlockEntity;

import net.oktawia.crazyae2addons.util.IManagedBEHelper;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;

public class ResearchPedestalTopBE extends AEBaseBlockEntity implements IManagedBEHelper {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ResearchPedestalTopBE.class);

    @Getter
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Persisted
    @DescSynced
    private ItemStack storedStack = ItemStack.EMPTY;

    public ResearchPedestalTopBE(BlockPos pos, BlockState blockState) {
        super(InsaneBlockEntityRegistrar.RESEARCH_PEDESTAL_TOP_BE.get(), pos, blockState);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public boolean isEmpty() {
        return this.storedStack.isEmpty();
    }

    public ItemStack getStoredStack() {
        return this.storedStack;
    }

    public void setStoredStack(ItemStack stack) {
        this.storedStack = stack.copy();
        setChanged();
    }

    public ItemStack takeStoredStack() {
        ItemStack result = this.storedStack;
        this.storedStack = ItemStack.EMPTY;
        setChanged();
        return result;
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        if (!this.storedStack.isEmpty()) {
            drops.add(this.storedStack);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveManagedData(tag);
    }

    @Override
    public void loadTag(CompoundTag tag) {
        loadManagedData(tag);
        super.loadTag(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveManagedData(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        loadManagedData(tag);
        super.handleUpdateTag(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            handleUpdateTag(tag);
        }
    }
}
