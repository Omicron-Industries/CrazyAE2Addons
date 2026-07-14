package net.oktawia.insaneae2addons.entities.research;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.blockentity.grid.AENetworkInvBlockEntity;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.filter.IAEItemFilter;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.FieldManagedStorage;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import net.oktawia.crazyae2addons.util.IManagedBEHelper;
import net.oktawia.crazyae2addons.util.IMenuOpeningBlockEntity;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneRecipes;
import net.oktawia.insaneae2addons.items.DataDrive;
import net.oktawia.insaneae2addons.logic.research.ResearchStatus;
import net.oktawia.insaneae2addons.menus.block.ResearchStationMenu;
import net.oktawia.insaneae2addons.recipes.ResearchRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResearchStationBE extends AENetworkInvBlockEntity
        implements IGridTickable, IManagedBEHelper, MenuProvider, IMenuOpeningBlockEntity {

    private static final int PEDESTAL_RANGE = 3;
    private static final int[] PEDESTAL_Y_OFFSETS = {0, 1};

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER =
            new ManagedFieldHolder(ResearchStationBE.class);

    @Getter
    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    private final AppEngInternalInventory inv = new AppEngInternalInventory(this, 1);
    private final InternalInventory disk = inv.getSubInventory(0, 1);

    @Persisted
    private int progressTicks = 0;

    @DescSynced
    @Getter
    private ResearchStatus status = ResearchStatus.IDLE;

    @DescSynced
    @Getter
    private String[] diagnostics = new String[0];

    private int diagTick = 0;

    @Nullable
    private ResearchRecipe activeRecipe = null;
    private final List<PedestalUse> activePedestals = new ArrayList<>();

    public ResearchStationBE(BlockPos pos, BlockState blockState) {
        super(InsaneBlockEntityRegistrar.RESEARCH_STATION_BE.get(), pos, blockState);
        this.getMainNode().addService(IGridTickable.class, this);
        this.inv.setFilter(new IAEItemFilter() {
            @Override
            public boolean allowInsert(InternalInventory inventory, int slot, ItemStack stack) {
                return stack.getItem() == InsaneItemRegistrar.DATA_DRIVE.get();
            }
        });
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.inv;
    }

    @Override
    public void onChangeInventory(InternalInventory inventory, int slot) {
        hardReset();
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        saveManagedData(tag);
    }

    @Override
    public void loadTag(CompoundTag tag) {
        loadManagedData(tag);
        super.loadTag(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveManagedData(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        loadManagedData(tag);
        super.handleUpdateTag(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            handleUpdateTag(tag);
        }
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(1, 5, false, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (level == null || level.isClientSide()) {
            return TickRateModulation.IDLE;
        }

        if (++diagTick >= 10) {
            diagTick = 0;
            rebuildDiagnostics();
        }

        if (activeRecipe == null) {
            activeRecipe = findMatchingRecipe();
            progressTicks = 0;

            if (activeRecipe == null) {
                setStatus(diagnoseIdle());
                return TickRateModulation.IDLE;
            }
        }

        if (!canWork(activeRecipe)) {
            hardReset();
            return TickRateModulation.IDLE;
        }

        ResearchStatus pedestalProblem = firstPedestalProblem();
        if (pedestalProblem != null) {
            hardReset();
            setStatus(pedestalProblem);
            return TickRateModulation.IDLE;
        }

        if (!doPedestalsWork()) {
            hardReset();
            setStatus(ResearchStatus.NOT_ENOUGH_POWER);
            return TickRateModulation.IDLE;
        }

        if (!drainStationPower(activeRecipe, true)) {
            hardReset();
            setStatus(ResearchStatus.NOT_ENOUGH_POWER);
            return TickRateModulation.IDLE;
        }
        drainStationPower(activeRecipe, false);

        int tickComputation = getTickComputation();
        if (tickComputation <= 0) {
            hardReset();
            setStatus(ResearchStatus.NOT_ENOUGH_COMPUTATION);
            return TickRateModulation.IDLE;
        }

        progressTicks += tickComputation;
        setStatus(ResearchStatus.WORKING);

        if (progressTicks >= activeRecipe.duration) {
            ResearchRecipe done = activeRecipe;
            consumeInputs();
            writeKeyToDrive(done);
            finishedEffect();
            hardReset();
            setStatus(ResearchStatus.READY);
            return TickRateModulation.IDLE;
        }

        setChanged();
        return TickRateModulation.URGENT;
    }

    private void hardReset() {
        this.progressTicks = 0;
        this.activeRecipe = null;
        this.activePedestals.clear();
        setChanged();
    }

    private void setStatus(ResearchStatus newStatus) {
        if (this.status != newStatus) {
            this.status = newStatus;
        }
        setChanged();
    }

    private ItemStack getDrive() {
        return this.disk.getStackInSlot(0);
    }

    private boolean diskHasKeyFor(ResearchRecipe recipe) {
        ItemStack drive = getDrive();
        return !drive.isEmpty() && DataDrive.hasKey(drive, recipe.unlock.key);
    }

    private List<ItemStack> gatherPedestalStacks() {
        List<ItemStack> result = new ArrayList<>();
        if (level == null) {
            return result;
        }

        for (BlockPos topPos : scanPedestalTops()) {
            if (level.getBlockEntity(topPos) instanceof ResearchPedestalTopBE top && !top.isEmpty()) {
                result.add(top.getStoredStack().copy());
            }
        }

        return result;
    }

    private List<BlockPos> scanPedestalTops() {
        List<BlockPos> tops = new ArrayList<>();
        if (level == null) {
            return tops;
        }

        BlockPos base = this.worldPosition;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = -PEDESTAL_RANGE; dx <= PEDESTAL_RANGE; dx++) {
            for (int dz = -PEDESTAL_RANGE; dz <= PEDESTAL_RANGE; dz++) {
                for (int dy : PEDESTAL_Y_OFFSETS) {
                    pos.set(base.getX() + dx, base.getY() + dy, base.getZ() + dz);
                    if (level.getBlockEntity(pos) instanceof ResearchPedestalTopBE) {
                        tops.add(pos.immutable());
                    }
                }
            }
        }

        return tops;
    }

    @Nullable
    private ResearchRecipe findMatchingRecipe() {
        if (level == null) {
            return null;
        }

        List<ItemStack> stacks = gatherPedestalStacks();
        SimpleContainer container = new SimpleContainer(stacks.size());
        for (int i = 0; i < stacks.size(); i++) {
            container.setItem(i, stacks.get(i));
        }

        boolean hasDrive = !getDrive().isEmpty();

        for (ResearchRecipe recipe : level.getRecipeManager().getAllRecipesFor(InsaneRecipes.RESEARCH_TYPE.get())) {
            if (!recipe.matches(container, level)) {
                continue;
            }

            if (!hasDrive || diskHasKeyFor(recipe)) {
                continue;
            }

            List<PedestalUse> pedestals = allocatePedestals(recipe);
            if (pedestals == null) {
                continue;
            }

            this.activePedestals.clear();
            this.activePedestals.addAll(pedestals);
            return recipe;
        }

        return null;
    }

    @Nullable
    private List<PedestalUse> allocatePedestals(ResearchRecipe recipe) {
        if (level == null) {
            return null;
        }

        List<BlockPos> topPositions = new ArrayList<>();
        List<BlockPos> bottomPositions = new ArrayList<>();
        List<ItemStack> stacks = new ArrayList<>();
        List<Integer> computations = new ArrayList<>();

        for (BlockPos topPos : scanPedestalTops()) {
            if (!(level.getBlockEntity(topPos) instanceof ResearchPedestalTopBE top) || top.isEmpty()) {
                continue;
            }

            BlockPos bottomPos = topPos.below();
            if (!(level.getBlockEntity(bottomPos) instanceof ResearchPedestalBottomBE bottom)) {
                continue;
            }

            topPositions.add(topPos.immutable());
            bottomPositions.add(bottomPos.immutable());
            stacks.add(top.getStoredStack().copy());
            computations.add(bottom.getConnectedComputation());
        }

        if (topPositions.isEmpty()) {
            return null;
        }

        int count = topPositions.size();
        boolean[] used = new boolean[count];
        List<PedestalUse> result = new ArrayList<>();

        List<ResearchRecipe.Consumable> consumables = new ArrayList<>(recipe.consumables);
        consumables.sort(Comparator.comparingInt((ResearchRecipe.Consumable c) -> c.computation).reversed());

        for (ResearchRecipe.Consumable consumable : consumables) {
            if (consumable.count <= 0) {
                continue;
            }

            boolean assigned = false;
            for (int i = 0; i < count; i++) {
                if (used[i]) {
                    continue;
                }

                ItemStack stack = stacks.get(i);
                if (stack.getItem() != consumable.item || stack.getCount() < consumable.count) {
                    continue;
                }
                if (computations.get(i) < consumable.computation) {
                    continue;
                }

                used[i] = true;
                result.add(new PedestalUse(topPositions.get(i), bottomPositions.get(i), consumable.count));
                assigned = true;
                break;
            }

            if (!assigned) {
                return null;
            }
        }

        return result.isEmpty() ? null : result;
    }

    private boolean canWork(@Nullable ResearchRecipe recipe) {
        if (recipe == null || level == null) {
            return false;
        }

        if (getDrive().isEmpty() || diskHasKeyFor(recipe)) {
            return false;
        }

        List<ItemStack> stacks = gatherPedestalStacks();
        SimpleContainer container = new SimpleContainer(stacks.size());
        for (int i = 0; i < stacks.size(); i++) {
            container.setItem(i, stacks.get(i));
        }

        if (!recipe.matches(container, level)) {
            return false;
        }

        List<PedestalUse> pedestals = allocatePedestals(recipe);
        if (pedestals == null) {
            return false;
        }

        this.activePedestals.clear();
        this.activePedestals.addAll(pedestals);
        return true;
    }

    @Nullable
    private ResearchStatus firstPedestalProblem() {
        if (level == null) {
            return ResearchStatus.STRUCTURE_INCOMPLETE;
        }

        for (PedestalUse use : activePedestals) {
            if (!(level.getBlockEntity(use.bottomPos()) instanceof ResearchPedestalBottomBE bottom)) {
                return ResearchStatus.STRUCTURE_INCOMPLETE;
            }

            ResearchStatus nodeStatus = bottom.getNodeStatus();
            if (nodeStatus != ResearchStatus.READY && nodeStatus != ResearchStatus.FLUID_LOW) {
                return nodeStatus;
            }
        }

        return null;
    }

    private boolean doPedestalsWork() {
        if (level == null || activePedestals.isEmpty()) {
            return false;
        }

        for (PedestalUse use : activePedestals) {
            if (!(level.getBlockEntity(use.bottomPos()) instanceof ResearchPedestalBottomBE bottom) || !bottom.doWork()) {
                return false;
            }
        }

        return true;
    }

    private int getTickComputation() {
        if (level == null) {
            return 0;
        }

        int sum = 0;
        for (PedestalUse use : activePedestals) {
            if (level.getBlockEntity(use.bottomPos()) instanceof ResearchPedestalBottomBE bottom) {
                sum += Math.max(0, bottom.getConnectedComputation());
            }
        }
        return sum;
    }

    private boolean drainStationPower(ResearchRecipe recipe, boolean simulate) {
        int need = Math.max(0, recipe.energyPerTick);
        if (need == 0) {
            return true;
        }

        IGrid grid = getMainNode().getGrid();
        if (grid == null) {
            return false;
        }

        IEnergyService energy = grid.getEnergyService();
        Actionable mode = simulate ? Actionable.SIMULATE : Actionable.MODULATE;
        return energy.extractAEPower(need, mode, PowerMultiplier.CONFIG) >= need;
    }

    private void consumeInputs() {
        if (level == null) {
            return;
        }

        for (PedestalUse use : activePedestals) {
            if (use.count() <= 0) {
                continue;
            }
            if (!(level.getBlockEntity(use.topPos()) instanceof ResearchPedestalTopBE top)) {
                continue;
            }

            ItemStack stack = top.takeStoredStack();
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.getCount() > use.count()) {
                stack.shrink(use.count());
                top.setStoredStack(stack);
            }
        }

        setChanged();
    }

    private void writeKeyToDrive(ResearchRecipe recipe) {
        ItemStack drive = getDrive();
        if (drive.isEmpty()) {
            return;
        }

        if (DataDrive.addKey(drive, recipe.unlock.key)) {
            this.disk.setItemDirect(0, drive);
            setChanged();
        }
    }

    private ResearchStatus diagnoseIdle() {
        if (level == null) {
            return ResearchStatus.IDLE;
        }

        List<ItemStack> stacks = gatherPedestalStacks();
        if (stacks.isEmpty()) {
            return ResearchStatus.IDLE;
        }

        SimpleContainer container = new SimpleContainer(stacks.size());
        for (int i = 0; i < stacks.size(); i++) {
            container.setItem(i, stacks.get(i));
        }

        boolean hasDrive = !getDrive().isEmpty();
        boolean matchNeedsDrive = false;
        boolean matchAlreadyUnlocked = false;

        for (ResearchRecipe recipe : level.getRecipeManager().getAllRecipesFor(InsaneRecipes.RESEARCH_TYPE.get())) {
            if (!recipe.matches(container, level)) {
                continue;
            }
            if (!hasDrive) {
                matchNeedsDrive = true;
                continue;
            }
            if (diskHasKeyFor(recipe)) {
                matchAlreadyUnlocked = true;
                continue;
            }
            return ResearchStatus.NOT_ENOUGH_COMPUTATION;
        }

        if (matchNeedsDrive) {
            return ResearchStatus.NO_DATA_DRIVE;
        }
        if (matchAlreadyUnlocked) {
            return ResearchStatus.ALREADY_UNLOCKED;
        }
        return ResearchStatus.NO_MATCHING_RECIPE;
    }

    private void finishedEffect() {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 1.2, worldPosition.getZ() + 0.5,
                    8, 0.2, 0.2, 0.2, 0.01);
            serverLevel.playSound(null, worldPosition, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.4f, 1.2f);
        }
    }

    public int getProgressPct() {
        if (activeRecipe == null) {
            return 0;
        }
        int duration = Math.max(1, activeRecipe.duration);
        return Math.max(0, Math.min(1000, (int) Math.round(1000.0 * progressTicks / duration)));
    }

    public InternalInventory getDiskInventory() {
        return this.disk;
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        MenuOpener.open(InsaneMenuRegistrar.RESEARCH_STATION_MENU.get(), player, locator);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ResearchStationMenu(id, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    public void unlockAllToDisk() {
        if (level == null) {
            return;
        }

        ItemStack drive = getDrive();
        if (drive.isEmpty()) {
            return;
        }

        boolean changed = false;
        for (ResearchRecipe recipe : level.getRecipeManager().getAllRecipesFor(InsaneRecipes.RESEARCH_TYPE.get())) {
            changed |= DataDrive.addKey(drive, recipe.unlock.key);
        }

        if (changed) {
            this.disk.setItemDirect(0, drive);
            setChanged();
        }
    }

    private void rebuildDiagnostics() {
        List<String> out = new ArrayList<>();
        out.add(encodeDiagnostic(worldPosition, "STATION", this.status));

        Set<BlockPos> seenUnits = new HashSet<>();

        for (BlockPos topPos : scanPedestalTops()) {
            BlockPos bottomPos = topPos.below();
            if (!(level.getBlockEntity(bottomPos) instanceof ResearchPedestalBottomBE bottom)) {
                continue;
            }

            out.add(encodeDiagnostic(bottomPos, "PEDESTAL", bottom.getNodeStatus()));

            ResearchUnitBE unit = bottom.getConnectedUnit();
            if (unit != null && seenUnits.add(unit.getBlockPos())) {
                out.add(encodeDiagnostic(unit.getBlockPos(), "UNIT", unit.getNodeStatus()));
            }
        }

        String[] arr = out.toArray(new String[0]);
        if (!Arrays.equals(arr, this.diagnostics)) {
            this.diagnostics = arr;
            setChanged();
        }
    }

    private static String encodeDiagnostic(BlockPos pos, String role, ResearchStatus status) {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ() + "|" + role + "|" + status.name();
    }

    private record PedestalUse(BlockPos topPos, BlockPos bottomPos, int count) {
        PedestalUse(BlockPos topPos, BlockPos bottomPos, int count) {
            this.topPos = topPos.immutable();
            this.bottomPos = bottomPos.immutable();
            this.count = count;
        }
    }
}
