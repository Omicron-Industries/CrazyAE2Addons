package net.oktawia.crazyae2addons.datagen;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import net.oktawia.crazyae2addons.defs.regs.CrazyBlockRegistrar;

public class CrazyBlockLootTables extends BlockLootSubProvider {
    public CrazyBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        for (var block : CrazyBlockRegistrar.getBlocks()) {
            this.dropSelf(block);
        }
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return CrazyBlockRegistrar.getBlocks().stream()::iterator;
    }

}
