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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.fluid.ResearchFluid;
import net.oktawia.insaneae2addons.fluid.ResearchFluidBlock;
import net.oktawia.insaneae2addons.fluid.ResearchFluidType;

public final class InsaneFluidRegistrar {
    private InsaneFluidRegistrar() {}

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, InsaneAddons.MODID);

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, InsaneAddons.MODID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, InsaneAddons.MODID);

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, InsaneAddons.MODID);

    public static final RegistryObject<FluidType> RESEARCH_FLUID_TYPE = FLUID_TYPES.register(
            "research_fluid_type",
            () -> new ResearchFluidType(
                    FluidType.Properties.create()
                            .density(300)
                            .viscosity(1000)
                            .canSwim(true)
                            .canDrown(true)
                            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            )
    );

    public static final RegistryObject<FlowingFluid> RESEARCH_FLUID_SOURCE = FLUIDS.register(
            "research_fluid",
            () -> new ResearchFluid.Source(ResearchFluid.PROPERTIES)
    );

    public static final RegistryObject<FlowingFluid> RESEARCH_FLUID_FLOWING = FLUIDS.register(
            "research_fluid_flowing",
            () -> new ResearchFluid.Flowing(ResearchFluid.PROPERTIES)
    );

    public static final RegistryObject<LiquidBlock> RESEARCH_FLUID_BLOCK = BLOCKS.register(
            "research_fluid_block",
            () -> new ResearchFluidBlock(() -> (FlowingFluid) RESEARCH_FLUID_SOURCE.get(),
                    BlockBehaviour.Properties.of()
                            .liquid()
                            .mapColor(MapColor.COLOR_LIGHT_BLUE)
                            .noLootTable()
                            .replaceable()
                            .noCollission()
                            .strength(20.0F)
                            .pushReaction(PushReaction.DESTROY))
    );

    public static final RegistryObject<Item> RESEARCH_FLUID_BUCKET = ITEMS.register(
            "research_fluid_bucket",
            () -> new BucketItem(RESEARCH_FLUID_SOURCE,
                    new Item.Properties()
                            .stacksTo(1)
                            .craftRemainder(Items.BUCKET)));

    public static void register(IEventBus modBus) {
        FLUID_TYPES.register(modBus);
        FLUIDS.register(modBus);
        ITEMS.register(modBus);
        BLOCKS.register(modBus);
    }
}
