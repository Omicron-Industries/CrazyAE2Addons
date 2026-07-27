package net.oktawia.insaneae2addons.entities.penrose;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.StorageCells;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.oktawia.crazyae2addons.multiblock.AbstractMultiblockControllerBE;
import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.blocks.penrose.PortablePenroseSphereControllerBlock;
import net.oktawia.insaneae2addons.blocks.penrose.PenroseFrameBlock;
import net.oktawia.insaneae2addons.defs.InsaneMultiblocks;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.entities.research.ResearchPedestalTopBE;
import net.oktawia.insaneae2addons.events.PenroseSphereFormedEvent;
import net.oktawia.insaneae2addons.logic.penrose.AccretionDisk;
import net.oktawia.insaneae2addons.logic.penrose.PenroseCurves;
import net.oktawia.insaneae2addons.logic.penrose.PenroseEnergyExport;
import net.oktawia.insaneae2addons.menus.block.PortablePenroseSphereControllerMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PortablePenroseSphereControllerBE extends AbstractMultiblockControllerBE {

    private static final long LASER_ARM_TICKS = 8L;
    private static final double BLACK_HOLE_DETECT_RADIUS = 3.0;

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER =
            new ManagedFieldHolder(PortablePenroseSphereControllerBE.class);

    @Persisted
    @DescSynced
    @Getter
    private boolean blackHoleActive;

    @DescSynced
    @Getter
    private long blackHoleMass;

    @DescSynced
    @Getter
    private double heat;

    @Persisted
    @DescSynced
    @Getter
    private long storedEnergy;

    @Persisted
    @DescSynced
    @Getter
    private long storedEnergyInDisk;

    @Persisted
    @DescSynced
    @Getter
    private long lastGeneratedFePerTick;

    @Persisted
    @DescSynced
    @Getter
    private long lastConsumedFePerTick;

    @Persisted
    @DescSynced
    @Getter
    private long lastSecondMassDelta;

    @Persisted
    @DescSynced
    @Getter
    private int lastAccretionSingu;

    @Persisted
    @DescSynced
    @Getter
    private int lastFeedSingu;

    @Persisted
    @DescSynced
    @Getter
    private double diskMass;

    private double blackHoleMassRemainder;

    private AccretionDisk disk = new AccretionDisk();

    @Persisted
    private int ventingLockTicks;

    private final Set<PenrosePeripheralBE> peripherals = new HashSet<>();

    @Nullable
    private PenroseBlackHoleEntity activeHole;

    private int pendingFeedSingu;
    private double pendingCooling;
    private long pendingEvaporation;

    private int secondTickCounter;
    private long secondMassDeltaAccumulator;

    @Nullable
    private ServerPlayer placer;

    private LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(SphereEnergyOutput::new);

    public PortablePenroseSphereControllerBE(BlockPos pos, BlockState blockState) {
        super(
                InsaneBlockEntityRegistrar.PORTABLE_PENROSE_SPHERE_CONTROLLER_BE.get(),
                pos,
                blockState,
                new ItemStack(InsaneBlockRegistrar.PORTABLE_PENROSE_SPHERE_CONTROLLER_BLOCK.get()),
                2.0F
        );
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected MultiblockDefinition getMultiblockDefinition() {
        return InsaneMultiblocks.penroseSphere();
    }

    @Override
    protected char frameSymbol() {
        return 'A';
    }

    @Override
    protected MenuType<?> getMenuType() {
        return InsaneMenuRegistrar.PORTABLE_PENROSE_SPHERE_CONTROLLER_MENU.get();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new PortablePenroseSphereControllerMenu(id, inventory, this);
    }

    public void setPlacer(@Nullable ServerPlayer placer) {
        this.placer = placer;
    }

    @Override
    protected void afterFormed() {
        if (getLevel() instanceof ServerLevel serverLevel) {
            MinecraftForge.EVENT_BUS.post(
                    new PenroseSphereFormedEvent(serverLevel, getBlockPos(), this, this.placer));
        }
    }

    @Override
    protected void afterDisformed() {
        this.pendingFeedSingu = 0;
        this.pendingCooling = 0.0;
        this.pendingEvaporation = 0L;
    }

    public void attachPeripheral(PenrosePeripheralBE peripheral) {
        this.peripherals.add(peripheral);
    }

    public void detachPeripheral(PenrosePeripheralBE peripheral) {
        this.peripherals.remove(peripheral);
    }

    private List<PenrosePeripheralBE> livePeripherals() {
        if (this.peripherals.isEmpty()) {
            return List.of();
        }

        this.peripherals.removeIf(be -> be.isRemoved() || be.getLevel() != getLevel());
        return List.copyOf(this.peripherals);
    }

    private <T extends PenrosePeripheralBE> List<T> livePeripherals(Class<T> type) {
        return livePeripherals().stream().filter(type::isInstance).map(type::cast).toList();
    }

    private void tickPeripherals() {
        for (PenrosePeripheralBE peripheral : livePeripherals()) {
            peripheral.onControllerTick();
            peripheral.defaultServerTick();
        }
        defaultServerTick();
    }

    public boolean isVentingLocked() {
        return this.ventingLockTicks > 0 || this.pendingEvaporation > 0L;
    }

    public void requestVentingLock(int ticks) {
        this.ventingLockTicks = Math.max(this.ventingLockTicks, Math.max(1, ticks));
    }

    public void addFeed(int singularities) {
        if (singularities <= 0 || isVentingLocked()) {
            return;
        }
        this.pendingFeedSingu = (int) Math.min(Integer.MAX_VALUE, (long) this.pendingFeedSingu + singularities);
    }

    public void addCooling(double heatGK) {
        if (heatGK <= 0.0 || Double.isNaN(heatGK) || Double.isInfinite(heatGK)) {
            return;
        }
        this.pendingCooling += heatGK;
    }

    public void addEvaporation(long massMu) {
        if (massMu <= 0L) {
            return;
        }
        this.pendingEvaporation = (this.pendingEvaporation > Long.MAX_VALUE - massMu)
                ? Long.MAX_VALUE
                : this.pendingEvaporation + massMu;
        requestVentingLock(2);
    }

    public long takeStoredEnergy(long wanted) {
        if (wanted <= 0L || this.storedEnergy <= 0L) {
            return 0L;
        }
        long taken = Math.min(this.storedEnergy, wanted);
        this.storedEnergy -= taken;
        return taken;
    }

    public long getInitialMass() {
        return Math.max(0L, InsaneConfig.COMMON.PENROSE_INITIAL_MASS_MU.get());
    }

    public long getMaxMass() {
        long initial = getInitialMass();
        long window = Math.max(0L, InsaneConfig.COMMON.PENROSE_MASS_WINDOW_MU.get());
        return (initial > Long.MAX_VALUE - window) ? Long.MAX_VALUE : initial + window;
    }

    public long getSweetSpotMass() {
        return getInitialMass() + Math.max(0L, InsaneConfig.COMMON.PENROSE_MASS_WINDOW_MU.get()) / 2L;
    }

    public double getMaxHeat() {
        return InsaneConfig.COMMON.PENROSE_MAX_HEAT_GK.get();
    }

    public long getDiskMassSingu() {
        return Math.max(0L, Math.round(this.diskMass));
    }

    public int getOrbitDelaySeconds() {
        return AccretionDisk.MEAN_TICKS / 20;
    }

    public int getDiskFlowSinguPerTick() {
        return (int) Math.max(0L, Math.min(Integer.MAX_VALUE, Math.round(this.disk.flowPerTick())));
    }

    private void resetTransient() {
        this.diskMass = 0.0;

        this.pendingFeedSingu = 0;
        this.pendingCooling = 0.0;
        this.pendingEvaporation = 0L;

        this.lastAccretionSingu = 0;
        this.lastFeedSingu = 0;
        this.lastSecondMassDelta = 0L;
        this.secondTickCounter = 0;
        this.secondMassDeltaAccumulator = 0L;

        this.storedEnergyInDisk = 0L;
        this.lastGeneratedFePerTick = 0L;
        this.lastConsumedFePerTick = 0L;
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        super.tickingRequest(node, ticksSinceLastCall);

        Level level = getLevel();
        if (level == null || level.isClientSide() || !InsaneConfig.COMMON.PENROSE_SPHERE_ENABLED.get()) {
            return TickRateModulation.IDLE;
        }

        int ticks = Math.max(1, ticksSinceLastCall);
        if (this.ventingLockTicks > 0) {
            this.ventingLockTicks = Math.max(0, this.ventingLockTicks - ticks);
        }

        PenroseBlackHoleEntity hole = (this.multiblockState.isFormed() && level instanceof ServerLevel serverLevel)
                ? findBlackHole(serverLevel)
                : null;
        if (hole != null) {
            if (!this.blackHoleActive || this.activeHole != hole) {
                adopt(hole);
            }
        } else if (this.blackHoleActive) {
            deactivate();
        }

        if (!this.blackHoleActive) {
            this.pendingFeedSingu = 0;
            this.pendingCooling = 0.0;
            this.pendingEvaporation = 0L;
            tickPeripherals();
            return TickRateModulation.IDLE;
        }

        boolean ioEnabled = this.multiblockState.isFormed() && getMainNode().getGrid() != null;
        if (!ioEnabled) {
            this.pendingFeedSingu = 0;
            this.pendingCooling = 0.0;
            this.pendingEvaporation = 0L;
        }

        List<PenroseHeatVentBE> vents =
                ioEnabled ? livePeripherals(PenroseHeatVentBE.class) : List.<PenroseHeatVentBE>of();
        List<PenroseHawkingVentBE> hawkingVents =
                ioEnabled ? livePeripherals(PenroseHawkingVentBE.class) : List.<PenroseHawkingVentBE>of();

        if (ioEnabled) {
            if (isVentingLocked()) {
                this.pendingFeedSingu = 0;
            } else {
                for (PenroseInjectionPortBE port : livePeripherals(PenroseInjectionPortBE.class)) {
                    port.pullFuel(ticks);
                }
            }
        }

        double coolingPerTick = this.pendingCooling / ticks;
        long evaporationPerTick = this.pendingEvaporation / ticks;
        long evaporationRemainder = this.pendingEvaporation % ticks;
        this.pendingCooling = 0.0;
        this.pendingEvaporation = 0L;

        long generatedAccumulator = 0L;
        long consumedAccumulator = 0L;

        for (int step = 0; step < ticks; step++) {
            long massBefore = this.blackHoleMass;

            int feed = 0;
            if (ioEnabled && !isVentingLocked()) {
                feed = Math.min(Math.max(0, InsaneConfig.COMMON.PENROSE_MAX_FEED_PER_TICK.get()), this.pendingFeedSingu);
                this.pendingFeedSingu -= feed;
            } else {
                this.pendingFeedSingu = 0;
            }
            this.lastFeedSingu = feed;

            advanceDisk(feed);

            double flow = this.disk.flowPerTick();
            double massFactor = computeMassFactor();

            this.heat += InsaneConfig.COMMON.PENROSE_HEAT_PER_SINGU_FLOW.get() * flow * massFactor;
            this.heat = Math.max(0.0, this.heat - coolingPerTick);

            if (this.heat >= getMaxHeat()) {
                triggerMeltdown();
                return TickRateModulation.IDLE;
            }

            long evaporation = evaporationPerTick + (step < evaporationRemainder ? 1L : 0L);
            if (evaporation > 0L) {
                applyEvaporation(evaporation);
            }

            long generated = computeGeneratedFe(flow, massFactor);
            generatedAccumulator += generated;

            for (PenroseHeatVentBE vent : vents) {
                runHeatVent(vent);
            }

            long budget = generated;
            for (PenroseHawkingVentBE vent : hawkingVents) {
                VentPayment payment = runHawkingVent(vent, budget);
                budget = payment.remainingBudget();
                consumedAccumulator += payment.paid();
            }

            if (this.heat >= getMaxHeat()) {
                triggerMeltdown();
                return TickRateModulation.IDLE;
            }

            if (this.blackHoleMass >= getMaxMass()) {
                triggerMeltdown();
                return TickRateModulation.IDLE;
            }

            addStoredEnergy(budget);

            this.secondMassDeltaAccumulator += this.blackHoleMass - massBefore;
            if (++this.secondTickCounter >= 20) {
                this.lastSecondMassDelta = this.secondMassDeltaAccumulator;
                this.secondMassDeltaAccumulator = 0L;
                this.secondTickCounter = 0;
            }
        }

        this.lastGeneratedFePerTick = generatedAccumulator / ticks;
        this.lastConsumedFePerTick = consumedAccumulator / ticks;
        writeBackToHole();
        exportStoredEnergy(level);
        recomputeDiskEnergy();
        tickPeripherals();

        setChanged();
        return TickRateModulation.IDLE;
    }

    public @Nullable Vec3 sphereCenter() {
        List<BlockPos> ports = getMultiblockDefinition().getOffsetsBySymbol(getPreviewFacing(), 'P');
        if (ports.isEmpty()) {
            return null;
        }

        BlockPos origin = getBlockPos();
        double cx = 0.0;
        double cy = 0.0;
        double cz = 0.0;
        for (BlockPos port : ports) {
            cx += origin.getX() + port.getX() + 0.5;
            cy += origin.getY() + port.getY() + 0.5;
            cz += origin.getZ() + port.getZ() + 0.5;
        }
        return new Vec3(cx / ports.size(), cy / ports.size(), cz / ports.size());
    }

    private @Nullable PenroseBlackHoleEntity findBlackHole(ServerLevel level) {
        Vec3 center = sphereCenter();
        if (center == null) {
            return null;
        }
        double radius = BLACK_HOLE_DETECT_RADIUS;
        AABB box = new AABB(center.x - radius, center.y - radius, center.z - radius,
                center.x + radius, center.y + radius, center.z + radius);
        PenroseBlackHoleEntity nearest = null;
        double nearestSq = Double.MAX_VALUE;
        for (PenroseBlackHoleEntity hole : level.getEntitiesOfClass(PenroseBlackHoleEntity.class, box)) {
            double distSq = hole.position().distanceToSqr(center);
            if (distSq <= radius * radius && distSq < nearestSq) {
                nearest = hole;
                nearestSq = distSq;
            }
        }
        return nearest;
    }

    public void onLaserFired(long gameTime) {
        if (this.blackHoleActive
                || !(getLevel() instanceof ServerLevel level)
                || !this.multiblockState.isFormed()) {
            return;
        }

        List<BlockPos> offsets = getMultiblockDefinition().getOffsetsBySymbol(getPreviewFacing(), 'L');
        if (offsets.size() != 4) {
            return;
        }

        BlockPos origin = getBlockPos();
        for (BlockPos offset : offsets) {
            if (!(level.getBlockEntity(origin.offset(offset)) instanceof PenroseLaserBE laser)) {
                return;
            }
            long firedAt = laser.getFiredChargedTick();
            if (firedAt < 0 || gameTime - firedAt >= LASER_ARM_TICKS) {
                return;
            }
        }

        Vec3 center = sphereCenter();
        if (center == null) {
            return;
        }
        BlockPos centerPos = BlockPos.containing(center);
        if (!(level.getBlockEntity(centerPos) instanceof ResearchPedestalTopBE pedestal)
                || !diskHasEnoughSingularities(pedestal.getStoredStack())) {
            return;
        }

        pedestal.setStoredStack(ItemStack.EMPTY);
        level.destroyBlock(centerPos, false);
        spawnBlackHole(level, center, getInitialMass());
    }

    private static boolean diskHasEnoughSingularities(ItemStack disk) {
        if (disk.isEmpty()) {
            return false;
        }
        var cellInventory = StorageCells.getCellInventory(disk, null);
        if (cellInventory == null) {
            return false;
        }
        long have = cellInventory.getAvailableStacks().get(AEItemKey.of(InsaneItemRegistrar.SUPER_SINGULARITY.get()));
        return have >= InsaneConfig.COMMON.PENROSE_CREATION_COST_SINGU.get();
    }

    private static void spawnBlackHole(ServerLevel level, Vec3 center, long initialMass) {
        PenroseBlackHoleEntity hole = InsaneEntityRegistrar.PENROSE_BLACK_HOLE.get().create(level);
        if (hole == null) {
            return;
        }
        hole.setPos(center.x, center.y, center.z);
        hole.initState(initialMass);
        level.addFreshEntity(hole);
    }

    private void adopt(PenroseBlackHoleEntity hole) {
        this.blackHoleActive = true;
        this.activeHole = hole;
        this.disk = hole.getDisk();
        this.blackHoleMass = hole.getMass();
        this.blackHoleMassRemainder = hole.getMassRemainder();
        this.heat = hole.getHeat();
        this.ventingLockTicks = 0;
        resetTransient();
        this.diskMass = this.disk.getMass();
        setChanged();
    }

    private void writeBackToHole() {
        if (this.activeHole != null && !this.activeHole.isRemoved()) {
            this.activeHole.storeState(this.blackHoleMass, this.blackHoleMassRemainder, this.heat);
        }
    }

    private void deactivate() {
        this.blackHoleActive = false;
        this.activeHole = null;
        this.disk = new AccretionDisk();
        this.blackHoleMass = 0L;
        this.blackHoleMassRemainder = 0.0;
        this.heat = 0.0;
        this.ventingLockTicks = 0;
        resetTransient();
        setChanged();
    }

    public static void deactivateNearControllers(ServerLevel level, BlockPos pos) {
        int radius = InsaneMultiblocks.penroseCoreRadius() + 8;
        int radiusSq = radius * radius;
        int minCx = (pos.getX() - radius) >> 4;
        int maxCx = (pos.getX() + radius) >> 4;
        int minCz = (pos.getZ() - radius) >> 4;
        int maxCz = (pos.getZ() + radius) >> 4;

        for (int cx = minCx; cx <= maxCx; cx++) {
            for (int cz = minCz; cz <= maxCz; cz++) {
                if (!level.hasChunk(cx, cz)) {
                    continue;
                }
                for (BlockEntity be : level.getChunk(cx, cz).getBlockEntities().values()) {
                    if (be instanceof PortablePenroseSphereControllerBE controller
                            && controller.blackHoleActive
                            && be.getBlockPos().distSqr(pos) <= radiusSq) {
                        controller.deactivate();
                    }
                }
            }
        }
    }

    private void exportStoredEnergy(Level level) {
        if (this.storedEnergy <= 0L || !this.multiblockState.isFormed()) {
            return;
        }

        boolean feOutput = InsaneConfig.COMMON.PENROSE_FE_OUTPUT_ENABLED.get();
        PenroseEnergyExport export = InsaneConfig.COMMON.PENROSE_EU_OUTPUT_ENABLED.get()
                ? PenroseEnergyExport.get()
                : null;

        if (!feOutput && export == null) {
            return;
        }

        for (BlockPos portPos : this.multiblockState.getBlocksBySymbol('P')) {
            if (this.storedEnergy <= 0L) {
                return;
            }

            BlockEntity be = level.getBlockEntity(portPos);
            if (be == null || be.isRemoved()) {
                continue;
            }

            if (be instanceof PenrosePortBE port) {
                if (feOutput) {
                    takeStoredEnergy(port.pushToNeighbors(this.storedEnergy));
                }
            } else if (export != null) {
                takeStoredEnergy(export.push(be, this.storedEnergy));
            }
        }
    }

    private record VentPayment(long remainingBudget, long paid) {
    }

    private void runHeatVent(PenroseHeatVentBE vent) {
        double requested = vent.getDesiredCooling();
        if (requested <= 0.0 || !vent.isArmed()) {
            return;
        }

        double applied = vent.drawCoolantFor(requested);
        if (applied > 0.0) {
            this.heat = Math.max(0.0, this.heat - applied);
        }
    }

    private VentPayment runHawkingVent(PenroseHawkingVentBE vent, long budget) {
        double requested = vent.getDesiredEvaporation();
        if (requested <= 0.0 || !vent.isArmed()) {
            return new VentPayment(budget, 0L);
        }

        long cost = PenroseCurves.hawkingVentCost(requested);
        if (cost <= 0L) {
            return new VentPayment(budget, 0L);
        }

        long fromBudget = Math.min(budget, cost);
        long outstanding = cost - fromBudget;

        long fromBuffer = (outstanding > 0L) ? takeStoredEnergy(outstanding) : 0L;
        outstanding -= fromBuffer;

        long fromVent = (outstanding > 0L) ? vent.drawEnergy(outstanding) : 0L;

        long paid = fromBudget + fromBuffer + fromVent;
        if (paid <= 0L) {
            return new VentPayment(budget, 0L);
        }

        long evaporated = Math.round(requested * Math.min(1.0, (double) paid / cost));
        if (evaporated > 0L) {
            applyEvaporation(evaporated);
        }

        return new VentPayment(budget - fromBudget, paid);
    }

    private void advanceDisk(int injectedSingularities) {
        this.blackHoleMassRemainder += this.disk.advance(injectedSingularities);
        if (this.blackHoleMassRemainder >= 1.0) {
            long whole = (long) Math.floor(this.blackHoleMassRemainder);
            this.blackHoleMass += whole;
            this.blackHoleMassRemainder -= whole;
        }

        this.diskMass = this.disk.getMass();
        this.lastAccretionSingu = this.disk.getLastAccretion();
    }

    private double computeMassFactor() {
        return PenroseCurves.massFactor(
                this.blackHoleMass + this.blackHoleMassRemainder,
                getSweetSpotMass(),
                Math.max(0L, InsaneConfig.COMMON.PENROSE_MASS_WINDOW_MU.get()) / 2.0,
                InsaneConfig.COMMON.PENROSE_MASS_FACTOR_MAX.get());
    }

    private double computeHeatEfficiency() {
        return PenroseCurves.heatEfficiency(this.heat, InsaneConfig.COMMON.PENROSE_HEAT_PEAK_GK.get());
    }

    private long computeGeneratedFe(double flow, double massFactor) {
        return this.disk.isEmpty() ? 0L : energyFor(flow, massFactor);
    }

    private void recomputeDiskEnergy() {
        this.storedEnergyInDisk = (!this.blackHoleActive || this.disk.isEmpty())
                ? 0L
                : energyFor(this.disk.getMass(), computeMassFactor());
    }

    private long energyFor(double flow, double massFactor) {
        return PenroseCurves.generatedFe(
                flow,
                computeHeatEfficiency(),
                massFactor,
                InsaneConfig.COMMON.PENROSE_DUTY_COMPENSATION.get(),
                InsaneConfig.COMMON.PENROSE_FE_BASE_PER_SINGU_FLOW.get());
    }

    private void applyEvaporation(long massMu) {
        double exactMass = Math.max(getInitialMass(), this.blackHoleMass + this.blackHoleMassRemainder - massMu);

        this.blackHoleMass = (long) Math.floor(exactMass);
        this.blackHoleMassRemainder = exactMass - this.blackHoleMass;
        requestVentingLock(2);
    }

    private void addStoredEnergy(long amount) {
        if (amount <= 0L) {
            return;
        }
        this.storedEnergy = (this.storedEnergy > Long.MAX_VALUE - amount)
                ? Long.MAX_VALUE
                : this.storedEnergy + amount;
    }

    private void triggerMeltdown() {
        if (this.activeHole != null && !this.activeHole.isRemoved()) {
            this.activeHole.meltdown();
        }

        this.blackHoleActive = false;
        this.activeHole = null;
        this.disk = new AccretionDisk();
        this.blackHoleMass = 0L;
        this.blackHoleMassRemainder = 0.0;
        this.heat = 0.0;
        this.storedEnergy = 0L;
        this.ventingLockTicks = 0;

        resetTransient();
        tickPeripherals();
        setChanged();
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY && InsaneConfig.COMMON.PENROSE_FE_OUTPUT_ENABLED.get()) {
            return this.energyCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        rebuildCaps();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        rebuildCaps();
    }

    private void rebuildCaps() {
        this.energyCap.invalidate();
        this.energyCap = isRemoved() ? LazyOptional.empty() : LazyOptional.of(SphereEnergyOutput::new);
    }

    @Override
    protected void setOwnFormedState(boolean formed) {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = getBlockState();
        if (state.hasProperty(PortablePenroseSphereControllerBlock.FORMED)
                && state.getValue(PortablePenroseSphereControllerBlock.FORMED) != formed) {
            level.setBlock(getBlockPos(), state.setValue(PortablePenroseSphereControllerBlock.FORMED, formed), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void setMemberFormedState(BlockPos pos, boolean formed) {
        setFrameFormed(pos, formed);
    }

    public void unformMembersForRemoval() {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        for (BlockPos pos : this.multiblockState.getBlocksBySymbol(frameSymbol())) {
            setFrameFormed(pos, false);
        }
    }

    private void setFrameFormed(BlockPos pos, boolean formed) {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(PenroseFrameBlock.FORMED) && state.getValue(PenroseFrameBlock.FORMED) != formed) {
            level.setBlock(pos, state.setValue(PenroseFrameBlock.FORMED, formed), Block.UPDATE_CLIENTS);
        }
    }

    private final class SphereEnergyOutput implements IEnergyStorage {

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (maxExtract <= 0 || storedEnergy <= 0L) {
                return 0;
            }

            int extracted = (int) Math.min(maxExtract, Math.min(storedEnergy, Integer.MAX_VALUE));
            if (!simulate) {
                storedEnergy -= extracted;
            }
            return extracted;
        }

        @Override
        public int getEnergyStored() {
            return (int) Math.min(storedEnergy, Integer.MAX_VALUE);
        }

        @Override
        public int getMaxEnergyStored() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    }
}
