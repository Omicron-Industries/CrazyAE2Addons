package net.oktawia.insaneae2addons.entities.penrose;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.state.BlockState;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.logic.penrose.PenroseCurveModel;

public class PenroseHeatEmitterBE extends PenroseEmitterBE {

    public PenroseHeatEmitterBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.PENROSE_HEAT_EMITTER_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.PENROSE_HEAT_EMITTER_BLOCK.get())
        );
    }

    @Override
    public MenuType<?> getMenuType() {
        return InsaneMenuRegistrar.PENROSE_HEAT_EMITTER_MENU.get();
    }

    @Override
    public PenroseCurveModel curveModel() {
        return PenroseCurveModel.heat();
    }

    @Override
    protected double readNormalized(PortablePenroseSphereControllerBE controller) {
        double max = controller.getMaxHeat();
        return max <= 0.0 ? 0.0 : controller.getHeat() / max;
    }
}
