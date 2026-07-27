package net.oktawia.insaneae2addons.entities.penrose;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.core.definitions.AEItems;
import appeng.util.SettingsFrom;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.oktawia.crazyae2addons.util.IManagedBEHelper;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import org.jetbrains.annotations.Nullable;

public class PenroseInjectionPortBE extends PenrosePeripheralBE {

    public static final int MAX_RATE = 1024;

    private static final String NBT_DESIRED_RATE = "desired_rate";

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER =
            IManagedBEHelper.inheritedFieldHolder(PenroseInjectionPortBE.class);

    @Persisted
    @DescSynced
    @Getter
    private int desiredRate;

    public PenroseInjectionPortBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.PENROSE_INJECTION_PORT_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.PENROSE_INJECTION_PORT_BLOCK.get()),
                2.0F
        );
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public MenuType<?> getMenuType() {
        return InsaneMenuRegistrar.PENROSE_INJECTION_PORT_MENU.get();
    }

    public void setDesiredRate(int desiredRate) {
        this.desiredRate = Math.max(0, Math.min(MAX_RATE, desiredRate));
        setChanged();
    }

    @Override
    public void exportSettings(SettingsFrom mode, CompoundTag output, @Nullable Player player) {
        super.exportSettings(mode, output, player);

        if (mode == SettingsFrom.MEMORY_CARD) {
            output.putInt(NBT_DESIRED_RATE, this.desiredRate);
        }
    }

    @Override
    public void importSettings(SettingsFrom mode, CompoundTag input, @Nullable Player player) {
        super.importSettings(mode, input, player);

        if (mode == SettingsFrom.MEMORY_CARD && input.contains(NBT_DESIRED_RATE, Tag.TAG_INT)) {
            setDesiredRate(input.getInt(NBT_DESIRED_RATE));
        }
    }

    public void pullFuel(int ticks) {
        PortablePenroseSphereControllerBE controller = getActiveController();
        if (controller == null || !controller.isBlackHoleActive() || controller.isVentingLocked()) {
            return;
        }

        if (this.desiredRate <= 0 || !isArmed()) {
            return;
        }

        IGridNode node = getActionableNode();
        IGrid grid = node != null ? node.getGrid() : null;
        if (grid == null) {
            return;
        }

        long wanted = (long) Math.min(this.desiredRate, MAX_RATE) * Math.max(1, ticks);
        long extracted = grid.getStorageService().getInventory().extract(
                AEItemKey.of(AEItems.SINGULARITY),
                wanted,
                Actionable.MODULATE,
                IActionSource.ofMachine(this));

        while (extracted > 0L) {
            int chunk = (int) Math.min(Integer.MAX_VALUE, extracted);
            controller.addFeed(chunk);
            extracted -= chunk;
        }
    }
}
