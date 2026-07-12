package net.oktawia.insaneae2addons.defs.regs;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.oktawia.crazyae2addons.IsModLoaded;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.compat.GregTech.GTAmpereMeterBE;
import net.oktawia.insaneae2addons.entities.AmpereMeterBE;
import net.oktawia.insaneae2addons.entities.AutoBuilderBE;
import net.oktawia.insaneae2addons.entities.AutoBuilderCreativeSupplyBE;
import net.oktawia.insaneae2addons.entities.BrokenPatternProviderBE;
import net.oktawia.insaneae2addons.entities.ResearchPedestalBottomBE;
import net.oktawia.insaneae2addons.entities.ResearchPedestalTopBE;
import net.oktawia.insaneae2addons.entities.ResearchStationBE;
import net.oktawia.insaneae2addons.entities.ResearchUnitBE;
import net.oktawia.insaneae2addons.entities.ResearchUnitFrameBE;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class InsaneBlockEntityRegistrar {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, InsaneAddons.MODID);

    private static final List<Runnable> BLOCK_ENTITY_SETUP = new ArrayList<>();

    private static <T extends AEBaseBlockEntity> RegistryObject<BlockEntityType<T>> reg(
            String id,
            RegistryObject<? extends AEBaseEntityBlock<?>> block,
            BlockEntityType.BlockEntitySupplier<T> factory,
            Class<T> blockEntityClass
    ) {
        return BLOCK_ENTITIES.register(id, () -> {
            var blk = block.get();
            var type = BlockEntityType.Builder.of(factory, blk).build(null);

            BLOCK_ENTITY_SETUP.add(() -> blk.setBlockEntity(
                    (Class) blockEntityClass, (BlockEntityType) type, null, null
            ));

            return type;
        });
    }

    private static <T extends AEBaseBlockEntity, S extends T> RegistryObject<BlockEntityType<T>> regConditional(
            String id,
            Supplier<? extends AEBaseEntityBlock<?>> block,
            BooleanSupplier condition,
            Supplier<BlockEntityType.BlockEntitySupplier<S>> trueFactory,
            Supplier<Class<S>> trueClass,
            BlockEntityType.BlockEntitySupplier<T> falseFactory,
            Class<T> falseClass
    ) {
        return BLOCK_ENTITIES.register(id, () -> {
            var blk = block.get();

            if (condition.getAsBoolean()) {
                BlockEntityType<S> type = BlockEntityType.Builder.of(trueFactory.get(), blk).build(null);
                BLOCK_ENTITY_SETUP.add(() -> blk.setBlockEntity(
                        (Class) trueClass.get(),
                        (BlockEntityType) type,
                        null,
                        null
                ));
                return (BlockEntityType<T>) type;
            } else {
                BlockEntityType<T> type = BlockEntityType.Builder.of(falseFactory, blk).build(null);
                BLOCK_ENTITY_SETUP.add(() -> blk.setBlockEntity(
                        (Class) falseClass,
                        (BlockEntityType) type,
                        null,
                        null
                ));
                return type;
            }
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

    public static final RegistryObject<BlockEntityType<AmpereMeterBE>> AMPERE_METER_BE =
            regConditional(
                    "ampere_meter_be",
                    InsaneBlockRegistrar.AMPERE_METER_BLOCK,
                    () -> IsModLoaded.GTCEU,
                    () -> GTAmpereMeterBE::new,
                    () -> AmpereMeterBE.class,
                    AmpereMeterBE::new,
                    AmpereMeterBE.class
            );

    public static final RegistryObject<BlockEntityType<AutoBuilderBE>> AUTO_BUILDER_BE =
            reg("auto_builder_be", InsaneBlockRegistrar.AUTO_BUILDER_BLOCK, AutoBuilderBE::new, AutoBuilderBE.class);

    public static final RegistryObject<BlockEntityType<AutoBuilderCreativeSupplyBE>> AUTO_BUILDER_CREATIVE_SUPPLY_BE =
            reg("auto_builder_creative_supply_be", InsaneBlockRegistrar.AUTO_BUILDER_CREATIVE_SUPPLY_BLOCK, AutoBuilderCreativeSupplyBE::new, AutoBuilderCreativeSupplyBE.class);

    public static final RegistryObject<BlockEntityType<BrokenPatternProviderBE>> BROKEN_PATTERN_PROVIDER_BE =
            reg("broken_pattern_provider_be", InsaneBlockRegistrar.BROKEN_PATTERN_PROVIDER_BLOCK, BrokenPatternProviderBE::new, BrokenPatternProviderBE.class);

    public static final RegistryObject<BlockEntityType<ResearchUnitBE>> RESEARCH_UNIT_BE =
            reg("research_unit_be", InsaneBlockRegistrar.RESEARCH_UNIT_BLOCK, ResearchUnitBE::new, ResearchUnitBE.class);

    public static final RegistryObject<BlockEntityType<ResearchUnitFrameBE>> RESEARCH_UNIT_FRAME_BE =
            reg("research_unit_frame_be", InsaneBlockRegistrar.RESEARCH_UNIT_FRAME_BLOCK, ResearchUnitFrameBE::new, ResearchUnitFrameBE.class);

    public static final RegistryObject<BlockEntityType<ResearchPedestalTopBE>> RESEARCH_PEDESTAL_TOP_BE =
            reg("research_pedestal_top_be", InsaneBlockRegistrar.RESEARCH_PEDESTAL_TOP_BLOCK, ResearchPedestalTopBE::new, ResearchPedestalTopBE.class);

    public static final RegistryObject<BlockEntityType<ResearchPedestalBottomBE>> RESEARCH_PEDESTAL_BOTTOM_BE =
            reg("research_pedestal_bottom_be", InsaneBlockRegistrar.RESEARCH_PEDESTAL_BOTTOM_BLOCK, ResearchPedestalBottomBE::new, ResearchPedestalBottomBE.class);

    public static final RegistryObject<BlockEntityType<ResearchStationBE>> RESEARCH_STATION_BE =
            reg("research_station_be", InsaneBlockRegistrar.RESEARCH_STATION_BLOCK, ResearchStationBE::new, ResearchStationBE.class);

    private InsaneBlockEntityRegistrar() {
    }
}
