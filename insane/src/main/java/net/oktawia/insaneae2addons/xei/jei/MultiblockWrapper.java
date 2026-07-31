package net.oktawia.insaneae2addons.xei.jei;

import com.lowdragmc.lowdraglib.jei.ModularWrapper;

import net.oktawia.insaneae2addons.xei.common.MultiblockEntry;
import net.oktawia.insaneae2addons.xei.common.MultiblockStructurePreview;

public class MultiblockWrapper extends ModularWrapper<MultiblockStructurePreview> {

    public final MultiblockEntry entry;

    public MultiblockWrapper(MultiblockEntry entry) {
        super(new MultiblockStructurePreview(entry));
        this.entry = entry;
    }
}
