package net.oktawia.insaneae2addons.entities.penrose;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockFrameBE;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;

public class PenrosePortBE extends AbstractMultiblockFrameBE<PortablePenroseSphereControllerBE> {

    public PenrosePortBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.PENROSE_PORT_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.PENROSE_PORT_BLOCK.get()),
                2.0F);
    }

    @Override
    protected Class<PortablePenroseSphereControllerBE> controllerClass() {
        return PortablePenroseSphereControllerBE.class;
    }

    @Override
    protected void onControllerChanged(@Nullable PortablePenroseSphereControllerBE newController) {
        if (newController != null) {
            connectToControllerGrid();
        } else {
            disconnectFromControllerGrid();
        }
    }

    public long pushToNeighbors(long available) {
        Level level = getLevel();
        if (level == null || available <= 0L) {
            return 0L;
        }

        long moved = 0L;
        for (Direction dir : Direction.values()) {
            long left = available - moved;
            if (left <= 0L) {
                break;
            }

            BlockPos neighborPos = getBlockPos().relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            if (neighbor == null || neighbor.isRemoved() || isMultiblockMember(neighbor)) {
                continue;
            }

            IEnergyStorage target = neighbor
                    .getCapability(ForgeCapabilities.ENERGY, dir.getOpposite())
                    .orElse(null);
            if (target == null || !target.canReceive()) {
                continue;
            }

            moved += target.receiveEnergy((int) Math.min(Integer.MAX_VALUE, left), false);
        }

        return moved;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        PortablePenroseSphereControllerBE controller = getActiveController();
        if (cap == ForgeCapabilities.ENERGY && controller != null) {
            return controller.getCapability(cap, side);
        }
        return super.getCapability(cap, side);
    }
}
