package net.oktawia.insaneae2addons.parts.mobstorage;

import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;

import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import appeng.api.util.AECableType;
import appeng.items.parts.PartModels;
import appeng.me.helpers.MachineSource;
import appeng.parts.AEBasePart;
import appeng.parts.automation.PlaneConnectionHelper;
import appeng.parts.automation.PlaneConnections;
import appeng.parts.automation.PlaneModelData;
import appeng.parts.automation.PlaneModels;

import net.oktawia.insaneae2addons.mobstorage.MobKey;

public class MobAnnihilationPlanePart extends AEBasePart implements IGridTickable {

    private static final PlaneModels MODELS = new PlaneModels("part/annihilation_plane",
            "part/annihilation_plane_on");

    @PartModels
    public static List<IPartModel> getModels() {
        return MODELS.getModels();
    }

    private final IActionSource actionSource = new MachineSource(this);
    private final PlaneConnectionHelper connectionHelper = new PlaneConnectionHelper(this);

    public MobAnnihilationPlanePart(IPartItem<?> partItem) {
        super(partItem);
        getMainNode().addService(IGridTickable.class, this);
        getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL);
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        if (bch.isBBCollision()) {
            bch.addBox(0, 0, 14, 16, 16, 15.5);
            return;
        }
        connectionHelper.getBoxes(bch);
    }

    public PlaneConnections getConnections() {
        return connectionHelper.getConnections();
    }

    @Override
    public void onNeighborChanged(BlockGetter level, BlockPos pos, BlockPos neighbor) {
        if (pos.relative(getSide()).equals(neighbor) && !isClientSide()) {
            wakeDevice();
        }
    }

    @Override
    public void onUpdateShape(Direction side) {
        if (side.equals(getSide())) {
            if (!isClientSide()) {
                wakeDevice();
            }
        } else if (getSide().getAxis() != side.getAxis()) {
            connectionHelper.updateConnections();
        }
    }

    private void wakeDevice() {
        getMainNode().ifPresent((grid, node) -> grid.getTickManager().alertDevice(node));
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 1;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(5, 5, false, true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        var pos = getBlockEntity().getBlockPos().relative(getSide());
        var mob = getMobAt(getLevel(), pos);
        if (mob == null) {
            return TickRateModulation.IDLE;
        }

        var key = MobKey.of(mob.getType());
        if (insertIntoGrid(key, 1, Actionable.MODULATE) <= 0) {
            return TickRateModulation.IDLE;
        }

        double x = mob.getX();
        double y = mob.getY();
        double z = mob.getZ();
        mob.remove(Entity.RemovalReason.DISCARDED);
        if (getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FIREWORK, x, y + 1, z, 20, 0.5, 0.5, 0.5, 0.01);
        }
        return TickRateModulation.URGENT;
    }

    private long insertIntoGrid(AEKey what, long amount, Actionable mode) {
        var grid = getMainNode().getGrid();
        if (grid == null) {
            return 0;
        }
        return StorageHelper.poweredInsert(grid.getEnergyService(), grid.getStorageService().getInventory(),
                what, amount, actionSource, mode);
    }

    private static @Nullable Mob getMobAt(Level level, BlockPos pos) {
        var box = new AABB(pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).inflate(0.001);
        var center = Vec3.atCenterOf(pos);
        return level.getEntitiesOfClass(Mob.class, box, m -> m.isAlive() && !m.isRemoved())
                .stream()
                .min(Comparator.comparingDouble(m -> m.distanceToSqr(center)))
                .orElse(null);
    }

    @Override
    public IPartModel getStaticModels() {
        return MODELS.getModel(isPowered(), isActive());
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(PlaneModelData.CONNECTIONS, getConnections())
                .build();
    }
}
