package net.oktawia.insaneae2addons.entities.penrose;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockFrameBE;
import org.jetbrains.annotations.Nullable;

public abstract class PenrosePeripheralBE extends AbstractMultiblockFrameBE<PortablePenroseSphereControllerBE> {

    protected PenrosePeripheralBE(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState blockState,
            ItemStack visualRepresentation,
            float idlePowerUsage
    ) {
        super(type, pos, blockState, visualRepresentation, idlePowerUsage);
    }

    @Override
    protected Class<PortablePenroseSphereControllerBE> controllerClass() {
        return PortablePenroseSphereControllerBE.class;
    }

    @Nullable
    private PortablePenroseSphereControllerBE attachedController;

    @Override
    protected void onControllerChanged(@Nullable PortablePenroseSphereControllerBE newController) {
        detachFromController();

        if (newController == null) {
            disconnectFromControllerGrid();
            return;
        }

        connectToControllerGrid();
        newController.attachPeripheral(this);
        this.attachedController = newController;
    }

    @Override
    public void setRemoved() {
        detachFromController();
        super.setRemoved();
    }

    private void detachFromController() {
        if (this.attachedController == null) {
            return;
        }

        this.attachedController.detachPeripheral(this);
        this.attachedController = null;
        onDetached();
    }

    protected void onDetached() {
    }

    public void onControllerTick() {
    }

    public abstract MenuType<?> getMenuType();

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        MenuOpener.open(getMenuType(), player, locator);
    }

    @Override
    public boolean canOpenMenu() {
        return true;
    }

    public boolean isArmed() {
        var level = getLevel();
        return level != null && level.hasNeighborSignal(getBlockPos());
    }
}
