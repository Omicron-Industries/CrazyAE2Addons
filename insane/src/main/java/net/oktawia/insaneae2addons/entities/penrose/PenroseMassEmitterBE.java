package net.oktawia.insaneae2addons.entities.penrose;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.logic.penrose.PenroseCurveModel;

public class PenroseMassEmitterBE extends PenroseEmitterBE {

    public PenroseMassEmitterBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.PENROSE_MASS_EMITTER_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.PENROSE_MASS_EMITTER_BLOCK.get()));
    }

    @Override
    public MenuType<?> getMenuType() {
        return InsaneMenuRegistrar.PENROSE_MASS_EMITTER_MENU.get();
    }

    @Override
    public PenroseCurveModel curveModel() {
        return PenroseCurveModel.mass();
    }

    @Override
    protected double readNormalized(PortablePenroseSphereControllerBE controller) {
        long initial = controller.getInitialMass();
        long span = controller.getMaxMass() - initial;
        if (span <= 0L) {
            return 0.0;
        }
        return (double) (controller.getBlackHoleMass() - initial) / span;
    }
}
