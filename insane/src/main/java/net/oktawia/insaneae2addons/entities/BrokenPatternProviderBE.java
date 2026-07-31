package net.oktawia.insaneae2addons.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.stacks.AEItemKey;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;

import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;

public class BrokenPatternProviderBE extends PatternProviderBlockEntity {

    public BrokenPatternProviderBE(BlockPos pos, BlockState blockState) {
        super(InsaneBlockEntityRegistrar.BROKEN_PATTERN_PROVIDER_BE.get(), pos, blockState);
        this.getMainNode().setVisualRepresentation(InsaneBlockRegistrar.BROKEN_PATTERN_PROVIDER_BLOCK.get().asItem());
    }

    @Override
    public PatternProviderLogic createLogic() {
        return new PatternProviderLogic(this.getMainNode(), this, 1);
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        MenuOpener.open(InsaneMenuRegistrar.BROKEN_PATTERN_PROVIDER_MENU.get(), player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(InsaneMenuRegistrar.BROKEN_PATTERN_PROVIDER_MENU.get(), player, subMenu.getLocator());
    }

    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(getBlockState().getBlock().asItem());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return getBlockState().getBlock().asItem().getDefaultInstance();
    }
}
