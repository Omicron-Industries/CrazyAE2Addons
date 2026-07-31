package net.oktawia.insaneae2addons.datagen;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;

public class InsaneBlockLootTables extends BlockLootSubProvider {
    public InsaneBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        for (var block : InsaneBlockRegistrar.getBlocks()) {
            this.dropSelf(block);
        }
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return InsaneBlockRegistrar.getBlocks().stream()::iterator;
    }

}
