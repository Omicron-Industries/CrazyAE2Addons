package net.oktawia.insaneae2addons.entities.penrose;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import lombok.Getter;
import net.oktawia.insaneae2addons.InsaneConfig;
import net.oktawia.insaneae2addons.defs.InsaneMultiblocks;
import net.oktawia.insaneae2addons.events.BlackHoleFieldManager;
import net.oktawia.insaneae2addons.logic.penrose.AccretionDisk;

public class PenroseBlackHoleEntity extends Entity {

    private static final int DAMAGE_INTERVAL = 10;
    private static final double DAMAGE_CENTER = 40.0;
    private static final double DAMAGE_EDGE = 5.0;

    private static final double GRAVITY_STRENGTH = 1.0;
    private static final double GRAVITY_SOFTENING = 1.0;
    private static final double INTERACTION_RANGE = 32.0;

    private static final double BLACK_HOLE_DRAG = 1.0;

    private static final String INITIALIZED_TAG = "Initialized";
    private static final String MASS_TAG = "Mass";
    private static final String MASS_REMAINDER_TAG = "MassRemainder";
    private static final String HEAT_TAG = "Heat";
    private static final String DISK_TAG = "Disk";

    private int damageTimer;

    @Getter
    private boolean stateInitialized;

    @Getter
    private long mass;

    @Getter
    private double massRemainder;

    @Getter
    private double heat;

    @Getter
    private final AccretionDisk disk = new AccretionDisk();

    public PenroseBlackHoleEntity(EntityType<PenroseBlackHoleEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        setNoGravity(true);
    }

    public void initState(long initialMass) {
        if (this.stateInitialized) {
            return;
        }
        this.mass = Math.max(0L, initialMass);
        this.massRemainder = 0.0;
        this.heat = 0.0;
        this.disk.clear();
        this.stateInitialized = true;
    }

    public void storeState(long mass, double massRemainder, double heat) {
        this.mass = Math.max(0L, mass);
        this.massRemainder = massRemainder;
        this.heat = Math.max(0.0, heat);
    }

    public void meltdown() {
        if (level() instanceof ServerLevel serverLevel) {
            if (InsaneConfig.COMMON.PENROSE_MELTDOWN_EXPLOSIONS.get()) {
                serverLevel.explode(null, getX(), getY(), getZ(), 32.0F, true,
                        Level.ExplosionInteraction.TNT);
            }
            BlackHoleFieldManager.start(serverLevel, blockPosition(),
                    InsaneConfig.COMMON.PENROSE_MELTDOWN_FIELD_RADIUS.get());
        }
        discard();
    }

    public static double damageRadius() {
        return InsaneMultiblocks.penroseCoreRadius();
    }

    public static double pullRadius() {
        return INTERACTION_RANGE;
    }

    public static double gravityAccel(double distance) {
        double r = Math.max(distance, GRAVITY_SOFTENING);
        return GRAVITY_STRENGTH / (r * r);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) {
            applyGravity();
            applyDamage();
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(BLACK_HOLE_DRAG));
        }
    }

    private static AABB boxAround(Vec3 center, double radius) {
        return new AABB(
                center.x - radius, center.y - radius, center.z - radius,
                center.x + radius, center.y + radius, center.z + radius);
    }

    private void applyGravity() {
        double pullRadius = pullRadius();
        Vec3 center = position();

        for (Entity entity : level().getEntitiesOfClass(Entity.class, boxAround(center, pullRadius),
                e -> e != this && !(e instanceof Player))) {
            Vec3 toCenter = center.subtract(entity.position());
            double distance = toCenter.length();
            if (distance > pullRadius || distance < 1.0e-4) {
                continue;
            }
            double accel = gravityAccel(distance);
            entity.setDeltaMovement(entity.getDeltaMovement().add(toCenter.scale(accel / distance)));
            entity.hasImpulse = true;
        }
    }

    private void applyDamage() {
        this.damageTimer -= 1;
        if (this.damageTimer > 0) {
            return;
        }
        this.damageTimer = DAMAGE_INTERVAL;

        double radius = damageRadius();
        double falloff = DAMAGE_EDGE / DAMAGE_CENTER;
        float window = DAMAGE_INTERVAL / 20.0f;
        Vec3 center = position();

        for (LivingEntity living : level().getEntitiesOfClass(LivingEntity.class, boxAround(center, radius))) {
            if (living instanceof Player player && (player.isCreative() || player.isSpectator())) {
                continue;
            }
            double distance = center.distanceTo(living.getBoundingBox().getCenter());
            if (distance > radius) {
                continue;
            }
            double dps = DAMAGE_CENTER * Math.pow(falloff, distance / radius);
            living.hurt(damageSources().generic(), (float) (dps * window));
        }
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public void push(double x, double y, double z) {
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(4.0);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (level() instanceof ServerLevel serverLevel) {
            PortablePenroseSphereControllerBE.deactivateNearControllers(serverLevel, blockPosition());
        }
        super.remove(reason);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.stateInitialized = tag.getBoolean(INITIALIZED_TAG);
        this.mass = tag.getLong(MASS_TAG);
        this.massRemainder = tag.getDouble(MASS_REMAINDER_TAG);
        this.heat = tag.getDouble(HEAT_TAG);
        if (tag.contains(DISK_TAG)) {
            this.disk.deserializeNBT(tag.getCompound(DISK_TAG));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean(INITIALIZED_TAG, this.stateInitialized);
        tag.putLong(MASS_TAG, this.mass);
        tag.putDouble(MASS_REMAINDER_TAG, this.massRemainder);
        tag.putDouble(HEAT_TAG, this.heat);
        tag.put(DISK_TAG, this.disk.serializeNBT());
    }
}
