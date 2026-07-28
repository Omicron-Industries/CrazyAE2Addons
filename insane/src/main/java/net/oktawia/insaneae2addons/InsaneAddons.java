package net.oktawia.insaneae2addons;

import appeng.api.features.GridLinkables;
import appeng.api.stacks.AEKeyTypes;
import net.oktawia.insaneae2addons.items.MultiblockBuilderItem;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import net.oktawia.crazyae2addons.client.textures.ConnectedTextures;
import net.oktawia.crazyae2addons.integration.ResearchDiskHooks;
import net.oktawia.insaneae2addons.integration.ResearchDiskHookImpl;
import net.oktawia.insaneae2addons.client.InsaneConnectedTextures;
import net.oktawia.insaneae2addons.client.renderer.ResearchPedestalTopRenderer;
import net.oktawia.insaneae2addons.client.screens.InsaneConfigScreen;
import net.oktawia.insaneae2addons.defs.InsaneFeatureGates;
import net.oktawia.insaneae2addons.defs.Screens;
import net.oktawia.insaneae2addons.defs.UpgradeCards;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneCreativeTabRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneFluidRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneRecipes;
import net.oktawia.insaneae2addons.client.renderer.EntityTypeRenderer;
import net.oktawia.crazyae2addons.logic.display.keytypes.DisplayKeyCompatRegistry;
import net.oktawia.insaneae2addons.mobstorage.MobKeyContainerStrategy;
import net.oktawia.insaneae2addons.mobstorage.MobKeyResolver;
import net.oktawia.crazyae2addons.IsModLoaded;
import net.oktawia.insaneae2addons.compat.CC.InsaneCCCompat;
import net.oktawia.insaneae2addons.compat.GregTech.GTAmpereMeterCompat;
import net.oktawia.insaneae2addons.compat.GregTech.GTPenroseEnergyExport;
import net.oktawia.insaneae2addons.mobstorage.MobKeyType;
import net.oktawia.insaneae2addons.network.NetworkHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(InsaneAddons.MODID)
public class InsaneAddons {
    public static final String MODID = "insaneae2addons";
    public static final Logger LOGGER = LogUtils.getLogger();

    public InsaneAddons() {
        LOGGER.info("Loading Insane AE2 Addons");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, InsaneConfig.COMMON_SPEC);
        modEventBus.addListener(this::commonSetup);

        InsaneItemRegistrar.ITEMS.register(modEventBus);
        InsaneBlockRegistrar.BLOCKS.register(modEventBus);
        InsaneBlockRegistrar.BLOCK_ITEMS.register(modEventBus);
        InsaneBlockEntityRegistrar.BLOCK_ENTITIES.register(modEventBus);
        InsaneEntityRegistrar.ENTITY_TYPES.register(modEventBus);
        InsaneMenuRegistrar.MENU_TYPES.register(modEventBus);
        InsaneFluidRegistrar.register(modEventBus);
        InsaneRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        InsaneRecipes.RECIPE_TYPES.register(modEventBus);

        modEventBus.addListener(this::registerCreativeTab);
        modEventBus.addListener((RegisterEvent event) -> {
            if (event.getRegistryKey().equals(Registries.BLOCK)) {
                AEKeyTypes.register(MobKeyType.TYPE);
            }
            if (event.getRegistryKey().equals(Registries.ITEM)) {
                InsaneItemRegistrar.registerPartModels();
            }
        });
    }

    public static @NotNull ResourceLocation makeId(String path) {
        return new ResourceLocation(MODID, path);
    }

    private void registerCreativeTab(final RegisterEvent evt) {
        if (evt.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
            evt.register(
                    Registries.CREATIVE_MODE_TAB,
                    InsaneCreativeTabRegistrar.ID,
                    () -> InsaneCreativeTabRegistrar.TAB
            );
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            InsaneBlockEntityRegistrar.setupBlockEntityTypes();
            GridLinkables.register(InsaneItemRegistrar.MULTIBLOCK_BUILDER.get(), MultiblockBuilderItem.LINKABLE_HANDLER);
            NetworkHandler.registerMessages();
            ResearchDiskHooks.register(new ResearchDiskHookImpl());
            InsaneFeatureGates.register();
            MobKeyContainerStrategy.register();
            if (IsModLoaded.GTCEU) {
                GTAmpereMeterCompat.register();
                GTPenroseEnergyExport.register();
            }
            if (IsModLoaded.COMPUTERCRAFT) {
                InsaneCCCompat.init();
            }
            DisplayKeyCompatRegistry.register(new MobKeyResolver());
        });
        new UpgradeCards(event);
    }

    @Mod.EventBusSubscriber(
            modid = InsaneAddons.MODID,
            bus = Mod.EventBusSubscriber.Bus.MOD,
            value = Dist.CLIENT
    )
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ModLoadingContext.get().registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> InsaneConfigScreen.create(parent))
            );
            Screens.register();
            InsaneConnectedTextures.register();
            EntityTypeRenderer.initialize();
        }

        @SubscribeEvent
        public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
            ConnectedTextures.onModifyBakingResult(event);
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(
                    InsaneBlockEntityRegistrar.RESEARCH_PEDESTAL_TOP_BE.get(),
                    ResearchPedestalTopRenderer::new
            );
        }
    }
}
