package net.oktawia.insaneae2addons.items;

import appeng.api.config.Actionable;
import appeng.api.features.IGridLinkableHandler;
import appeng.api.implementations.blockentities.IWirelessAccessPoint;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import appeng.items.AEBaseItem;
import appeng.util.Platform;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockControllerBE;
import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition;
import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition.PatternEntry;
import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition.SymbolDef;
import net.oktawia.insaneae2addons.defs.LangDefs;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MultiblockBuilderItem extends AEBaseItem {

    private static final String TAG_ACCESS_POINT = "accessPoint";

    public static final IGridLinkableHandler LINKABLE_HANDLER = new IGridLinkableHandler() {
        @Override
        public boolean canLink(ItemStack stack) {
            return stack.getItem() instanceof MultiblockBuilderItem;
        }

        @Override
        public void link(ItemStack stack, GlobalPos pos) {
            GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).result()
                    .ifPresent(tag -> stack.getOrCreateTag().put(TAG_ACCESS_POINT, tag));
        }

        @Override
        public void unlink(ItemStack stack) {
            stack.removeTagKey(TAG_ACCESS_POINT);
        }
    };

    public MultiblockBuilderItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Nullable
    public GlobalPos getLinkedPosition(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_ACCESS_POINT, Tag.TAG_COMPOUND)) {
            return null;
        }
        return GlobalPos.CODEC.decode(NbtOps.INSTANCE, tag.get(TAG_ACCESS_POINT)).result()
                .map(Pair::getFirst)
                .orElse(null);
    }

    @Nullable
    public IGrid getLinkedGrid(ItemStack stack, Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }
        GlobalPos linkedPos = getLinkedPosition(stack);
        if (linkedPos == null) {
            return null;
        }
        ServerLevel linkedLevel = serverLevel.getServer().getLevel(linkedPos.dimension());
        if (linkedLevel == null) {
            return null;
        }
        BlockEntity be = Platform.getTickingBlockEntity(linkedLevel, linkedPos.pos());
        if (!(be instanceof IWirelessAccessPoint accessPoint)) {
            return null;
        }
        return accessPoint.getGrid();
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Player player = ctx.getPlayer();
        Level level = ctx.getLevel();
        if (player == null || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        BlockEntity be = level.getBlockEntity(ctx.getClickedPos());
        if (!(be instanceof AbstractMultiblockControllerBE controller)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        build(controller, level, player, ctx.getItemInHand());
        return InteractionResult.CONSUME;
    }

    private void build(AbstractMultiblockControllerBE controller, Level level, Player player, ItemStack stack) {
        MultiblockDefinition definition = controller.getPreviewDefinition();
        BlockPos origin = controller.getPreviewOrigin();
        Direction facing = controller.getPreviewFacing();
        boolean creative = player.getAbilities().instabuild;

        IGrid grid = creative ? null : getLinkedGrid(stack, level);
        MEStorage meInventory = grid != null ? grid.getStorageService().getInventory() : null;
        IActionSource source = IActionSource.ofPlayer(player);

        int placed = 0;
        int missing = 0;
        for (PatternEntry entry : definition.getEntries(facing)) {
            BlockPos worldPos = origin.offset(entry.relX(), entry.relY(), entry.relZ());
            SymbolDef symbol = definition.getSymbol(entry.symbol());
            if (symbol == null) {
                continue;
            }

            BlockState current = level.getBlockState(worldPos);
            if (!current.isAir() && symbol.blocks().contains(current.getBlock())) {
                continue;
            }
            if (!current.canBeReplaced()) {
                continue;
            }

            if (creative) {
                level.setBlock(worldPos, controller.getPreviewState(entry, symbol), 3);
                placed++;
                continue;
            }

            BlockState target = pickAndConsume(controller, entry, symbol, player, meInventory, source);
            if (target == null) {
                missing++;
                continue;
            }

            level.setBlock(worldPos, target, 3);
            placed++;
        }

        player.displayClientMessage(
                Component.translatable(LangDefs.BUILDER_PLACED.getTranslationKey(), placed), true);
        if (missing > 0) {
            player.displayClientMessage(
                    Component.translatable(LangDefs.BUILDER_MISSING.getTranslationKey(), missing), true);
        }
    }

    @Nullable
    private static BlockState pickAndConsume(AbstractMultiblockControllerBE controller, PatternEntry entry,
                                             SymbolDef symbol, Player player, @Nullable MEStorage meInventory,
                                             IActionSource source) {
        List<Block> candidates = symbol.blocks();
        for (int i = 0; i < candidates.size(); i++) {
            Item item = candidates.get(i).asItem();
            if (item == Items.AIR) {
                continue;
            }
            if (consumeOne(player, meInventory, source, item)) {
                return i == 0 ? controller.getPreviewState(entry, symbol) : candidates.get(i).defaultBlockState();
            }
        }
        return null;
    }

    private static boolean consumeOne(Player player, @Nullable MEStorage meInventory, IActionSource source,
                                      Item item) {
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < 36; slot++) {
            ItemStack slotStack = inventory.getItem(slot);
            if (!slotStack.isEmpty() && slotStack.is(item)) {
                slotStack.shrink(1);
                return true;
            }
        }

        if (meInventory != null) {
            return meInventory.extract(AEItemKey.of(item), 1, Actionable.MODULATE, source) > 0;
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
        boolean linked = getLinkedPosition(stack) != null;
        String key = (linked ? LangDefs.BUILDER_LINKED : LangDefs.BUILDER_UNLINKED).getTranslationKey();
        lines.add(Component.translatable(key).withStyle(linked ? ChatFormatting.GREEN : ChatFormatting.RED));
    }
}
