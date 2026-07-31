package net.oktawia.insaneae2addons.defs.regs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;

import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.entities.AmpereMeterBE;
import net.oktawia.insaneae2addons.entities.AutoEnchanterBE;
import net.oktawia.insaneae2addons.entities.BrokenPatternProviderBE;
import net.oktawia.insaneae2addons.entities.EnergyStorageBE;
import net.oktawia.insaneae2addons.entities.autobuilder.AutoBuilderBE;
import net.oktawia.insaneae2addons.entities.autobuilder.AutoBuilderCreativeSupplyBE;
import net.oktawia.insaneae2addons.entities.cradle.EntropyCradleCapacitorBE;
import net.oktawia.insaneae2addons.entities.cradle.EntropyCradleControllerBE;
import net.oktawia.insaneae2addons.entities.cradle.EntropyCradleWallBE;
import net.oktawia.insaneae2addons.entities.mobstorage.MobFarmControllerBE;
import net.oktawia.insaneae2addons.entities.mobstorage.MobFarmPartBE;
import net.oktawia.insaneae2addons.entities.mobstorage.SpawnerExtractorControllerBE;
import net.oktawia.insaneae2addons.entities.mobstorage.SpawnerExtractorWallBE;
import net.oktawia.insaneae2addons.entities.penrose.PenroseCoilBE;
import net.oktawia.insaneae2addons.entities.penrose.PenroseFrameBE;
import net.oktawia.insaneae2addons.entities.penrose.PenroseGlassBE;
import net.oktawia.insaneae2addons.entities.penrose.PenroseHawkingVentBE;
import net.oktawia.insaneae2addons.entities.penrose.PenroseHeatEmitterBE;
import net.oktawia.insaneae2addons.entities.penrose.PenroseHeatVentBE;
import net.oktawia.insaneae2addons.entities.penrose.PenroseInjectionPortBE;
import net.oktawia.insaneae2addons.entities.penrose.PenroseLaserBE;
import net.oktawia.insaneae2addons.entities.penrose.PenroseMassEmitterBE;
import net.oktawia.insaneae2addons.entities.penrose.PenrosePortBE;
import net.oktawia.insaneae2addons.entities.penrose.PortablePenroseSphereControllerBE;
import net.oktawia.insaneae2addons.entities.penrose.ReinforcedMatterCondenserBE;
import net.oktawia.insaneae2addons.entities.penrose.SuperSingularityBE;
import net.oktawia.insaneae2addons.entities.research.ResearchPedestalBottomBE;
import net.oktawia.insaneae2addons.entities.research.ResearchPedestalTopBE;
import net.oktawia.insaneae2addons.entities.research.ResearchStationBE;
import net.oktawia.insaneae2addons.entities.research.ResearchUnitBE;
import net.oktawia.insaneae2addons.entities.research.ResearchUnitFrameBE;

public class InsaneBlockEntityRegistrar {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
            .create(ForgeRegistries.BLOCK_ENTITY_TYPES, InsaneAddons.MODID);

    private static final List<Runnable> BLOCK_ENTITY_SETUP = new ArrayList<>();

    private static <T extends AEBaseBlockEntity> RegistryObject<BlockEntityType<T>> reg(
            String id,
            RegistryObject<? extends AEBaseEntityBlock<?>> block,
            BlockEntityType.BlockEntitySupplier<T> factory,
            Class<T> blockEntityClass) {
        return BLOCK_ENTITIES.register(id, () -> {
            var blk = block.get();
            var type = BlockEntityType.Builder.of(factory, blk).build(null);

            BLOCK_ENTITY_SETUP.add(() -> blk.setBlockEntity(
                    (Class) blockEntityClass, (BlockEntityType) type, null, null));

            return type;
        });
    }

    @SafeVarargs
    private static <T extends AEBaseBlockEntity> RegistryObject<BlockEntityType<T>> regMulti(
            String id,
            BlockEntityType.BlockEntitySupplier<T> factory,
            Class<T> blockEntityClass,
            RegistryObject<? extends AEBaseEntityBlock<?>>... blocks) {
        return BLOCK_ENTITIES.register(id, () -> {
            Block[] resolved = Arrays.stream(blocks).map(RegistryObject::get).toArray(Block[]::new);
            var type = BlockEntityType.Builder.of(factory, resolved).build(null);

            for (Block blk : resolved) {
                AEBaseEntityBlock<?> aeBlk = (AEBaseEntityBlock<?>) blk;
                BLOCK_ENTITY_SETUP.add(() -> aeBlk.setBlockEntity(
                        (Class) blockEntityClass, (BlockEntityType) type, null, null));
            }

            return type;
        });
    }

    public static void setupBlockEntityTypes() {
        for (var runnable : BLOCK_ENTITY_SETUP) {
            runnable.run();
        }
    }

    public static List<? extends BlockEntityType<?>> getEntities() {
        return BLOCK_ENTITIES.getEntries().stream().map(RegistryObject::get).toList();
    }

    public static final RegistryObject<BlockEntityType<AmpereMeterBE>> AMPERE_METER_BE = reg("ampere_meter_be",
            InsaneBlockRegistrar.AMPERE_METER_BLOCK, AmpereMeterBE::new, AmpereMeterBE.class);

    public static final RegistryObject<BlockEntityType<AutoBuilderBE>> AUTO_BUILDER_BE = reg("auto_builder_be",
            InsaneBlockRegistrar.AUTO_BUILDER_BLOCK, AutoBuilderBE::new, AutoBuilderBE.class);

    public static final RegistryObject<BlockEntityType<AutoEnchanterBE>> AUTO_ENCHANTER_BE = reg("auto_enchanter_be",
            InsaneBlockRegistrar.AUTO_ENCHANTER_BLOCK, AutoEnchanterBE::new, AutoEnchanterBE.class);

    public static final RegistryObject<BlockEntityType<AutoBuilderCreativeSupplyBE>> AUTO_BUILDER_CREATIVE_SUPPLY_BE = reg(
            "auto_builder_creative_supply_be", InsaneBlockRegistrar.AUTO_BUILDER_CREATIVE_SUPPLY_BLOCK,
            AutoBuilderCreativeSupplyBE::new, AutoBuilderCreativeSupplyBE.class);

    public static final RegistryObject<BlockEntityType<BrokenPatternProviderBE>> BROKEN_PATTERN_PROVIDER_BE = reg(
            "broken_pattern_provider_be", InsaneBlockRegistrar.BROKEN_PATTERN_PROVIDER_BLOCK,
            BrokenPatternProviderBE::new, BrokenPatternProviderBE.class);

    public static final RegistryObject<BlockEntityType<ResearchUnitBE>> RESEARCH_UNIT_BE = reg("research_unit_be",
            InsaneBlockRegistrar.RESEARCH_UNIT_BLOCK, ResearchUnitBE::new, ResearchUnitBE.class);

    public static final RegistryObject<BlockEntityType<ResearchUnitFrameBE>> RESEARCH_UNIT_FRAME_BE = reg(
            "research_unit_frame_be", InsaneBlockRegistrar.RESEARCH_UNIT_FRAME_BLOCK, ResearchUnitFrameBE::new,
            ResearchUnitFrameBE.class);

    public static final RegistryObject<BlockEntityType<ResearchPedestalTopBE>> RESEARCH_PEDESTAL_TOP_BE = reg(
            "research_pedestal_top_be", InsaneBlockRegistrar.RESEARCH_PEDESTAL_TOP_BLOCK, ResearchPedestalTopBE::new,
            ResearchPedestalTopBE.class);

    public static final RegistryObject<BlockEntityType<ResearchPedestalBottomBE>> RESEARCH_PEDESTAL_BOTTOM_BE = reg(
            "research_pedestal_bottom_be", InsaneBlockRegistrar.RESEARCH_PEDESTAL_BOTTOM_BLOCK,
            ResearchPedestalBottomBE::new, ResearchPedestalBottomBE.class);

    public static final RegistryObject<BlockEntityType<ResearchStationBE>> RESEARCH_STATION_BE = reg(
            "research_station_be", InsaneBlockRegistrar.RESEARCH_STATION_BLOCK, ResearchStationBE::new,
            ResearchStationBE.class);

    public static final RegistryObject<BlockEntityType<EntropyCradleControllerBE>> ENTROPY_CRADLE_CONTROLLER_BE = reg(
            "entropy_cradle_controller_be", InsaneBlockRegistrar.ENTROPY_CRADLE_CONTROLLER_BLOCK,
            EntropyCradleControllerBE::new, EntropyCradleControllerBE.class);

    public static final RegistryObject<BlockEntityType<EntropyCradleWallBE>> ENTROPY_CRADLE_BE = reg(
            "entropy_cradle_be", InsaneBlockRegistrar.ENTROPY_CRADLE_BLOCK, EntropyCradleWallBE::new,
            EntropyCradleWallBE.class);

    public static final RegistryObject<BlockEntityType<EntropyCradleCapacitorBE>> ENTROPY_CRADLE_CAPACITOR_BE = reg(
            "entropy_cradle_capacitor_be", InsaneBlockRegistrar.ENTROPY_CRADLE_CAPACITOR_BLOCK,
            EntropyCradleCapacitorBE::new, EntropyCradleCapacitorBE.class);

    public static final RegistryObject<BlockEntityType<MobFarmControllerBE>> MOB_FARM_CONTROLLER_BE = reg(
            "mob_farm_controller_be", InsaneBlockRegistrar.MOB_FARM_CONTROLLER_BLOCK, MobFarmControllerBE::new,
            MobFarmControllerBE.class);

    public static final RegistryObject<BlockEntityType<MobFarmPartBE>> MOB_FARM_PART_BE = regMulti("mob_farm_part_be",
            MobFarmPartBE::new, MobFarmPartBE.class,
            InsaneBlockRegistrar.MOB_FARM_WALL_BLOCK,
            InsaneBlockRegistrar.MOB_FARM_COLLECTOR_BLOCK,
            InsaneBlockRegistrar.MOB_FARM_INPUT_BLOCK,
            InsaneBlockRegistrar.MOB_FARM_DAMAGE_BLOCK);

    public static final RegistryObject<BlockEntityType<SpawnerExtractorControllerBE>> SPAWNER_EXTRACTOR_CONTROLLER_BE = reg(
            "spawner_extractor_controller_be", InsaneBlockRegistrar.SPAWNER_EXTRACTOR_CONTROLLER_BLOCK,
            SpawnerExtractorControllerBE::new, SpawnerExtractorControllerBE.class);

    public static final RegistryObject<BlockEntityType<SpawnerExtractorWallBE>> SPAWNER_EXTRACTOR_WALL_BE = reg(
            "spawner_extractor_wall_be", InsaneBlockRegistrar.SPAWNER_EXTRACTOR_WALL_BLOCK, SpawnerExtractorWallBE::new,
            SpawnerExtractorWallBE.class);

    public static final RegistryObject<BlockEntityType<SuperSingularityBE>> SUPER_SINGULARITY_BE = reg(
            "super_singularity_be", InsaneBlockRegistrar.SUPER_SINGULARITY_BLOCK, SuperSingularityBE::new,
            SuperSingularityBE.class);

    public static final RegistryObject<BlockEntityType<ReinforcedMatterCondenserBE>> REINFORCED_MATTER_CONDENSER_BE = reg(
            "reinforced_matter_condenser_be", InsaneBlockRegistrar.REINFORCED_MATTER_CONDENSER_BLOCK,
            ReinforcedMatterCondenserBE::new, ReinforcedMatterCondenserBE.class);

    public static final RegistryObject<BlockEntityType<PortablePenroseSphereControllerBE>> PORTABLE_PENROSE_SPHERE_CONTROLLER_BE = reg(
            "portable_penrose_sphere_controller_be", InsaneBlockRegistrar.PORTABLE_PENROSE_SPHERE_CONTROLLER_BLOCK,
            PortablePenroseSphereControllerBE::new, PortablePenroseSphereControllerBE.class);

    public static final RegistryObject<BlockEntityType<PenroseFrameBE>> PENROSE_FRAME_BE = reg("penrose_frame_be",
            InsaneBlockRegistrar.PENROSE_FRAME_BLOCK, PenroseFrameBE::new, PenroseFrameBE.class);

    public static final RegistryObject<BlockEntityType<PenroseGlassBE>> PENROSE_GLASS_BE = reg("penrose_glass_be",
            InsaneBlockRegistrar.PENROSE_GLASS_BLOCK, PenroseGlassBE::new, PenroseGlassBE.class);

    public static final RegistryObject<BlockEntityType<PenroseCoilBE>> PENROSE_COIL_BE = reg("penrose_coil_be",
            InsaneBlockRegistrar.PENROSE_COIL_BLOCK, PenroseCoilBE::new, PenroseCoilBE.class);

    public static final RegistryObject<BlockEntityType<PenroseLaserBE>> PENROSE_LASER_BE = reg("penrose_laser_be",
            InsaneBlockRegistrar.PENROSE_LASER_BLOCK, PenroseLaserBE::new, PenroseLaserBE.class);

    public static final RegistryObject<BlockEntityType<PenrosePortBE>> PENROSE_PORT_BE = reg("penrose_port_be",
            InsaneBlockRegistrar.PENROSE_PORT_BLOCK, PenrosePortBE::new, PenrosePortBE.class);

    public static final RegistryObject<BlockEntityType<PenroseInjectionPortBE>> PENROSE_INJECTION_PORT_BE = reg(
            "penrose_injection_port_be", InsaneBlockRegistrar.PENROSE_INJECTION_PORT_BLOCK, PenroseInjectionPortBE::new,
            PenroseInjectionPortBE.class);

    public static final RegistryObject<BlockEntityType<PenroseHeatVentBE>> PENROSE_HEAT_VENT_BE = reg(
            "penrose_heat_vent_be", InsaneBlockRegistrar.PENROSE_HEAT_VENT_BLOCK, PenroseHeatVentBE::new,
            PenroseHeatVentBE.class);

    public static final RegistryObject<BlockEntityType<PenroseHawkingVentBE>> PENROSE_HAWKING_VENT_BE = reg(
            "penrose_hawking_vent_be", InsaneBlockRegistrar.PENROSE_HAWKING_VENT_BLOCK, PenroseHawkingVentBE::new,
            PenroseHawkingVentBE.class);

    public static final RegistryObject<BlockEntityType<PenroseMassEmitterBE>> PENROSE_MASS_EMITTER_BE = reg(
            "penrose_mass_emitter_be", InsaneBlockRegistrar.PENROSE_MASS_EMITTER_BLOCK, PenroseMassEmitterBE::new,
            PenroseMassEmitterBE.class);

    public static final RegistryObject<BlockEntityType<PenroseHeatEmitterBE>> PENROSE_HEAT_EMITTER_BE = reg(
            "penrose_heat_emitter_be", InsaneBlockRegistrar.PENROSE_HEAT_EMITTER_BLOCK, PenroseHeatEmitterBE::new,
            PenroseHeatEmitterBE.class);

    public static final RegistryObject<BlockEntityType<EnergyStorageBE>> ENERGY_STORAGE_BE = regMulti(
            "energy_storage_be", EnergyStorageBE::new, EnergyStorageBE.class,
            InsaneBlockRegistrar.ENERGY_STORAGE_1K,
            InsaneBlockRegistrar.ENERGY_STORAGE_4K,
            InsaneBlockRegistrar.ENERGY_STORAGE_16K,
            InsaneBlockRegistrar.ENERGY_STORAGE_64K,
            InsaneBlockRegistrar.ENERGY_STORAGE_256K,
            InsaneBlockRegistrar.ENERGY_STORAGE_1M,
            InsaneBlockRegistrar.ENERGY_STORAGE_4M,
            InsaneBlockRegistrar.ENERGY_STORAGE_16M,
            InsaneBlockRegistrar.ENERGY_STORAGE_64M,
            InsaneBlockRegistrar.ENERGY_STORAGE_256M);

    private InsaneBlockEntityRegistrar() {
    }
}
