package net.oktawia.insaneae2addons.defs.regs;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.fluid.WaterBasedFluidType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class InsaneFluidRegistrar {
    private InsaneFluidRegistrar() {}

    public record FluidSet(
            String name,
            String displayName,
            String bucketTexture,
            RegistryObject<FluidType> type,
            RegistryObject<FlowingFluid> source,
            RegistryObject<FlowingFluid> flowing,
            RegistryObject<LiquidBlock> block,
            RegistryObject<Item> bucket
    ) {}

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, InsaneAddons.MODID);

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, InsaneAddons.MODID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, InsaneAddons.MODID);

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, InsaneAddons.MODID);

    private static final List<FluidSet> DEFINED = new ArrayList<>();

    public static final FluidSet RESEARCH_FLUID =
            define("research_fluid", "Research Fluid", "research_bucket", 0xFF47C7FF, MapColor.COLOR_LIGHT_BLUE);

    public static final FluidSet PENROSE_COOLANT =
            define("penrose_coolant", "Penrose Coolant", "penrose_coolant_bucket", 0xFFB8F2FF, MapColor.ICE);

    public static List<FluidSet> getFluids() {
        return Collections.unmodifiableList(DEFINED);
    }

    private static FluidSet define(String name, String displayName, String bucketTexture, int tint, MapColor mapColor) {
        FluidRefs refs = new FluidRefs();

        refs.type = FLUID_TYPES.register(name + "_type",
                () -> new WaterBasedFluidType(
                        FluidType.Properties.create()
                                .density(300)
                                .viscosity(1000)
                                .canSwim(true)
                                .canDrown(true)
                                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY),
                        tint));

        refs.source = FLUIDS.register(name, () -> new ForgeFlowingFluid.Source(refs.properties()));
        refs.flowing = FLUIDS.register(name + "_flowing", () -> new ForgeFlowingFluid.Flowing(refs.properties()));

        refs.block = BLOCKS.register(name + "_block",
                () -> new LiquidBlock(refs.source,
                        BlockBehaviour.Properties.of()
                                .liquid()
                                .mapColor(mapColor)
                                .noLootTable()
                                .replaceable()
                                .noCollission()
                                .strength(20.0F)
                                .pushReaction(PushReaction.DESTROY)));

        refs.bucket = ITEMS.register(name + "_bucket",
                () -> new BucketItem(refs.source,
                        new Item.Properties()
                                .stacksTo(1)
                                .craftRemainder(Items.BUCKET)));

        FluidSet set = new FluidSet(name, displayName, bucketTexture,
                refs.type, refs.source, refs.flowing, refs.block, refs.bucket);
        DEFINED.add(set);
        return set;
    }

    public static void register(IEventBus modBus) {
        FLUID_TYPES.register(modBus);
        FLUIDS.register(modBus);
        ITEMS.register(modBus);
        BLOCKS.register(modBus);
    }

    private static final class FluidRefs {
        private RegistryObject<FluidType> type;
        private RegistryObject<FlowingFluid> source;
        private RegistryObject<FlowingFluid> flowing;
        private RegistryObject<LiquidBlock> block;
        private RegistryObject<Item> bucket;

        private ForgeFlowingFluid.Properties properties() {
            return new ForgeFlowingFluid.Properties(this.type, this.source, this.flowing)
                    .bucket(this.bucket)
                    .block(this.block);
        }
    }
}
