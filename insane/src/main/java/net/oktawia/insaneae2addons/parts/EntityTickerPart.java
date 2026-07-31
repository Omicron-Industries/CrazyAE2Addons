package net.oktawia.insaneae2addons.parts;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.definitions.AEItems;
import appeng.items.parts.PartModels;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.automation.UpgradeablePart;
import appeng.parts.p2p.P2PModels;

import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.menus.part.EntityTickerMenu;

public class EntityTickerPart extends UpgradeablePart implements IGridTickable, MenuProvider {

    private static final P2PModels MODELS = new P2PModels(
            new ResourceLocation(InsaneAddons.MODID, "part/entity_ticker_part_item"));

    @PartModels
    public static List<IPartModel> getModels() {
        return MODELS.getModels();
    }

    public EntityTickerPart(IPartItem<?> partItem) {
        super(partItem);
        getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .setIdlePowerUsage(1)
                .addService(IGridTickable.class, this);
        getConfigManager().registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        getConfigManager().registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
    }

    @Override
    protected int getUpgradeSlots() {
        return InsaneConfig.COMMON.ENTITY_TICKER_MAX_SPEED_CARDS.get();
    }

    public int getSpeedCards() {
        return getUpgrades().getInstalledUpgrades(AEItems.SPEED_CARD);
    }

    @Override
    public boolean onPartActivate(Player player, InteractionHand hand, Vec3 pos) {
        if (!InsaneConfig.COMMON.ENTITY_TICKER_ENABLED.get()) {
            return true;
        }
        if (!isClientSide()) {
            MenuOpener.open(InsaneMenuRegistrar.ENTITY_TICKER_MENU.get(), player, MenuLocators.forPart(this));
        }
        return true;
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(5, 5, 12, 11, 11, 13);
        bch.addBox(3, 3, 13, 13, 13, 14);
        bch.addBox(2, 2, 14, 14, 14, 16);
    }

    @Override
    public IPartModel getStaticModels() {
        return MODELS.getModel(isPowered(), isActive());
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(1, 1, false, true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (!InsaneConfig.COMMON.ENTITY_TICKER_ENABLED.get() || !isActive()) {
            return TickRateModulation.IDLE;
        }
        BlockEntity target = getLevel().getBlockEntity(getBlockEntity().getBlockPos().relative(getSide()));
        if (target != null) {
            tickTarget(target);
        }
        return TickRateModulation.IDLE;
    }

    private <T extends BlockEntity> void tickTarget(T target) {
        Level level = target.getLevel();
        if (level == null) {
            return;
        }
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(target.getBlockState().getBlock());
        if (blockId != null && InsaneConfig.COMMON.ENTITY_TICKER_BLACKLIST.get().contains(blockId.toString())) {
            return;
        }
        IGrid grid = getMainNode().getGrid();
        if (grid == null) {
            return;
        }
        BlockPos pos = target.getBlockPos();
        @SuppressWarnings("unchecked")
        BlockEntityTicker<T> ticker = level.getBlockState(pos).getTicker(level, (BlockEntityType<T>) target.getType());
        if (ticker == null) {
            return;
        }
        int speedCards = getSpeedCards();
        int powerDraw = (int) (InsaneConfig.COMMON.ENTITY_TICKER_COST.get() * Math.pow(4, speedCards)) / 2;
        if (grid.getEnergyService().extractAEPower(powerDraw, Actionable.MODULATE,
                PowerMultiplier.CONFIG) < powerDraw) {
            return;
        }
        int extraTicks = (int) Math.pow(2, speedCards + 1) - 1;
        for (int i = 0; i < extraTicks; i++) {
            ticker.tick(level, pos, target.getBlockState(), target);
        }
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new EntityTickerMenu(containerId, playerInventory, this);
    }

    @Override
    public Component getDisplayName() {
        return super.getDisplayName();
    }
}
