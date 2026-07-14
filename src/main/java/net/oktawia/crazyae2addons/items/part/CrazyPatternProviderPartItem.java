package net.oktawia.crazyae2addons.items.part;

import appeng.items.parts.PartItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.oktawia.crazyae2addons.defs.LangDefs;
import net.oktawia.crazyae2addons.logic.provider.CrazyProviderTooltip;
import net.oktawia.crazyae2addons.parts.CrazyPatternProviderPart;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrazyPatternProviderPartItem extends PartItem<CrazyPatternProviderPart> {

    public CrazyPatternProviderPartItem(Properties properties) {
        super(properties, CrazyPatternProviderPart.class, CrazyPatternProviderPart::new);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
                                TooltipFlag advancedTooltips) {
        super.appendHoverText(stack, level, tooltip, advancedTooltips);


        CrazyProviderTooltip.Data data = CrazyProviderTooltip.read(stack);

        tooltip.add(Component.translatable(
                LangDefs.CRAZY_PROVIDER_CAPACITY_TOOLTIP.getTranslationKey(),
                data.totalSlots()
        ).withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.literal("(" + data.percent() + "%)").withStyle(ChatFormatting.AQUA));
    }
}