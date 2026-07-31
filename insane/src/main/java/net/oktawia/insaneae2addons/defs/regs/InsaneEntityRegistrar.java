package net.oktawia.insaneae2addons.defs.regs;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.entities.penrose.PenroseBlackHoleEntity;

public final class InsaneEntityRegistrar {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
            .create(ForgeRegistries.ENTITY_TYPES, InsaneAddons.MODID);

    public static final RegistryObject<EntityType<PenroseBlackHoleEntity>> PENROSE_BLACK_HOLE = ENTITY_TYPES.register(
            "penrose_black_hole",
            () -> EntityType.Builder.<PenroseBlackHoleEntity>of(PenroseBlackHoleEntity::new, MobCategory.MISC)
                    .sized(4.0f, 4.0f)
                    .clientTrackingRange(16)
                    .updateInterval(1)
                    .fireImmune()
                    .build("penrose_black_hole"));

    private InsaneEntityRegistrar() {
    }
}
