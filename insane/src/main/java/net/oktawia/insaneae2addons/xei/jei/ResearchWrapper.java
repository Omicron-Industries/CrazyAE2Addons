package net.oktawia.insaneae2addons.xei.jei;

import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.oktawia.insaneae2addons.xei.common.ResearchEntry;
import net.oktawia.insaneae2addons.xei.common.ResearchPreview;

import java.util.List;

public class ResearchWrapper extends ModularWrapper<ResearchPreview> {

    public final List<ItemStack> inputs;
    public final ItemStack drive;
    public final ResourceLocation recipeId;

    public ResearchWrapper(ResearchEntry entry) {
        super(new ResearchPreview(entry.recipeId(), entry.inputs(), entry.driveOrOutput()));
        this.inputs = entry.inputs();
        this.drive = entry.driveOrOutput();
        this.recipeId = entry.recipeId();
    }
}
