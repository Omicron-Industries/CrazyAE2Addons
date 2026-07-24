package net.oktawia.insaneae2addons.mobstorage;

import appeng.api.stacks.AEKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.oktawia.crazyae2addons.logic.display.keytypes.IDisplayKeyResolver;
import org.jetbrains.annotations.Nullable;

public class MobKeyResolver implements IDisplayKeyResolver {

    @Override
    public String getTypePrefix() {
        return "mob";
    }

    @Override
    public @Nullable AEKey resolve(String id) {
        if (!ResourceLocation.isValidResourceLocation(id)) {
            return null;
        }
        var entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(id));
        return entityType != null ? MobKey.of(entityType) : null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @Nullable ItemStack getIcon(String id) {
        AEKey key = resolve(id);
        return key != null ? key.wrapForDisplayOrFilter() : null;
    }
}
