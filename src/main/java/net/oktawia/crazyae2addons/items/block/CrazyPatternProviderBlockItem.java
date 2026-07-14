package net.oktawia.crazyae2addons.items.block;

import appeng.block.AEBaseBlockItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.oktawia.crazyae2addons.defs.LangDefs;
import net.oktawia.crazyae2addons.logic.provider.CrazyProviderTooltip;

import java.util.List;

public class CrazyPatternProviderBlockItem extends AEBaseBlockItem {

    public CrazyPatternProviderBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void addCheckedInformation(ItemStack stack, Level level, List<Component> tooltip,
                                      TooltipFlag advancedTooltips) {
        super.addCheckedInformation(stack, level, tooltip, advancedTooltips);


        CrazyProviderTooltip.Data data = CrazyProviderTooltip.read(stack);

        tooltip.add(Component.translatable(
                LangDefs.CRAZY_PROVIDER_CAPACITY_TOOLTIP.getTranslationKey(),
                data.totalSlots()
        ).withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.literal("(" + data.percent() + "%)").withStyle(ChatFormatting.AQUA));
    }
}