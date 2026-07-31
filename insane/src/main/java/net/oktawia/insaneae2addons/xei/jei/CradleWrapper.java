package net.oktawia.insaneae2addons.xei.jei;

import java.util.List;

import com.lowdragmc.lowdraglib.jei.ModularWrapper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.oktawia.insaneae2addons.xei.common.CradleEntry;
import net.oktawia.insaneae2addons.xei.common.CradlePreview;

public class CradleWrapper extends ModularWrapper<CradlePreview> {

    public final List<ItemStack> input;
    public final ItemStack output;
    public final ResourceLocation structure;

    public CradleWrapper(CradleEntry entry) {
        super(new CradlePreview(entry.structureId(), entry.inputs(), entry.output(), entry.description()));
        this.input = entry.inputs();
        this.output = entry.output();
        this.structure = entry.structureId();
    }
}
