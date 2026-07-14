package net.oktawia.insaneae2addons.parts;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.networking.IGrid;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.core.AppEng;
import appeng.core.settings.TickRates;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.parts.automation.IOBusPart;
import appeng.parts.automation.StackWorldBehaviors;
import appeng.util.ConfigInventory;
import appeng.util.prioritylist.DefaultPriorityList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.MenuType;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.logic.viewcell.NBTMatcher;
import org.jetbrains.annotations.Nullable;

public class NbtExportBusPart extends IOBusPart {

    public static final ResourceLocation MODEL_BASE = new ResourceLocation(AppEng.MOD_ID, "part/export_bus_base");

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE,
            new ResourceLocation(AppEng.MOD_ID, "part/export_bus_off"));
    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE,
            new ResourceLocation(AppEng.MOD_ID, "part/export_bus_on"));
    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE,
            new ResourceLocation(AppEng.MOD_ID, "part/export_bus_has_channel"));

    private static final int TRANSFER_FACTOR = 4;
    private static final String NBT_STATE = "state";

    public final ConfigInventory inv = ConfigInventory.configTypes(1, () -> {});

    private final NbtFilterState state = new NbtFilterState(this::markForSaveSafe);

    private NBTMatcher.Compiled matcher = NBTMatcher.compile("");
    private String matcherSource = "";

    @Nullable
    private StackExportStrategy exportStrategy;

    public NbtExportBusPart(IPartItem<?> partItem) {
        super(TickRates.ExportBus, StackWorldBehaviors.hasExportStrategyFilter(), partItem);
    }

    public String getData() {
        return state.getData();
    }

    public void setFilter(String expression) {
        state.setData(expression);
        if (getHost() != null) {
            getHost().markForSave();
            getHost().markForUpdate();
        }
    }

    private void markForSaveSafe() {
        if (getHost() != null) {
            getHost().markForSave();
        }
    }

    private NBTMatcher.Compiled matcher() {
        String data = state.getData();
        if (!matcherSource.equals(data)) {
            matcher = NBTMatcher.compile(data);
            matcherSource = data;
        }
        return matcher;
    }

    @Override
    protected MenuType<?> getMenuType() {
        return InsaneMenuRegistrar.NBT_EXPORT_BUS_MENU.get();
    }

    @Override
    protected boolean doBusWork(IGrid grid) {
        var storageService = grid.getStorageService();
        var context = new InsaneStackTransferContext(
                storageService, grid.getEnergyService(), this.source,
                getOperationsPerTick(), DefaultPriorityList.INSTANCE);

        var stacks = storageService.getInventory().getAvailableStacks();
        if (stacks.isEmpty()) {
            return false;
        }

        var compiled = matcher();
        boolean didWork = false;
        for (var entry : stacks) {
            AEKey key = entry.getKey();
            if (!(key instanceof AEItemKey itemKey) || !NBTMatcher.doesItemMatch(itemKey, compiled)) {
                continue;
            }

            long amount = (long) context.getOperationsRemaining() * TRANSFER_FACTOR;
            long transferred = getExportStrategy().transfer(context, itemKey, amount);
            if (transferred > 0) {
                context.reduceOperationsRemaining(Math.max(1, transferred / TRANSFER_FACTOR));
                didWork = true;
            }
            if (!context.hasOperationsLeft()) {
                break;
            }
        }
        return didWork;
    }

    private StackExportStrategy getExportStrategy() {
        if (exportStrategy == null) {
            var self = getHost().getBlockEntity();
            var fromPos = self.getBlockPos().relative(getSide());
            var fromSide = getSide().getOpposite();
            exportStrategy = StackWorldBehaviors.createExportFacade((ServerLevel) getLevel(), fromPos, fromSide);
        }
        return exportStrategy;
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(4, 4, 12, 12, 12, 14);
        bch.addBox(5, 5, 14, 11, 11, 15);
        bch.addBox(6, 6, 15, 10, 10, 16);
        bch.addBox(6, 6, 11, 10, 10, 12);
    }

    @Override
    public IPartModel getStaticModels() {
        if (isActive() && isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (isPowered()) {
            return MODELS_ON;
        }
        return MODELS_OFF;
    }

    @Override
    public void readFromNBT(CompoundTag extra) {
        super.readFromNBT(extra);
        if (extra.contains(NBT_STATE, Tag.TAG_COMPOUND)) {
            state.loadPersisted(extra.getCompound(NBT_STATE));
        }
    }

    @Override
    public void writeToNBT(CompoundTag extra) {
        super.writeToNBT(extra);
        extra.put(NBT_STATE, state.savePersisted());
    }

    @Override
    public void writeToStream(FriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeNbt(state.saveSync(true));
    }

    @Override
    public boolean readFromStream(FriendlyByteBuf data) {
        boolean changed = super.readFromStream(data);
        CompoundTag tag = data.readNbt();
        if (tag != null) {
            state.loadSync(tag);
        }
        return changed;
    }
}
