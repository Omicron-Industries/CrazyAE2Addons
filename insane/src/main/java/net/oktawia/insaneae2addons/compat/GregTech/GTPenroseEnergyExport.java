package net.oktawia.insaneae2addons.compat.GregTech;

import java.util.List;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.part.EnergyHatchPartMachine;
import com.gregtechceu.gtceu.config.ConfigHolder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

import net.oktawia.insaneae2addons.logic.penrose.PenroseEnergyExport;

public final class GTPenroseEnergyExport implements PenroseEnergyExport {

    private static final String NAMESPACE = "gtceu";

    private static final List<String> PATH_SUFFIXES = List.of(
            "_energy_output_hatch",
            "_energy_output_hatch_4a",
            "_energy_output_hatch_16a",
            "_substation_output_hatch_64a");

    public static void register() {
        PenroseEnergyExport.set(new GTPenroseEnergyExport());
    }

    @Override
    public List<String> portBlockIds() {
        return ForgeRegistries.BLOCKS.getKeys().stream()
                .filter(GTPenroseEnergyExport::isEnergyOutputHatch)
                .map(ResourceLocation::toString)
                .toList();
    }

    @Override
    public long push(BlockEntity target, long available) {
        if (available <= 0L
                || !(target instanceof MetaMachineBlockEntity machineBlockEntity)
                || !(machineBlockEntity.getMetaMachine() instanceof EnergyHatchPartMachine hatch)) {
            return 0L;
        }

        int feToEu = feToEuRatio();
        long room = Math.max(0L, hatch.energyContainer.getEnergyCanBeInserted());
        long wantedEu = Math.min(available / feToEu, room);
        if (wantedEu <= 0L) {
            return 0L;
        }

        return Math.max(0L, hatch.energyContainer.changeEnergy(wantedEu)) * feToEu;
    }

    private static int feToEuRatio() {
        return Math.max(1, ConfigHolder.INSTANCE.compat.energy.feToEuRatio);
    }

    private static boolean isEnergyOutputHatch(ResourceLocation id) {
        if (!NAMESPACE.equals(id.getNamespace())) {
            return false;
        }

        return PATH_SUFFIXES.stream().anyMatch(id.getPath()::endsWith);
    }
}
