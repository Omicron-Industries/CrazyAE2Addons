package net.oktawia.insaneae2addons.compat.GregTech;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import net.oktawia.insaneae2addons.entities.AmpereMeterBE;
import net.oktawia.insaneae2addons.util.InsaneUtils;

public final class GTAmpereMeterCompat implements AmpereMeterBE.EnergyCompat {

    public static void register() {
        AmpereMeterBE.setEnergyCompat(new GTAmpereMeterCompat());
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(AmpereMeterBE meter, Capability<T> capability,
            @Nullable Direction side) {
        if (capability != GTCapability.CAPABILITY_ENERGY_CONTAINER || side != meter.getInputSide()) {
            return LazyOptional.empty();
        }
        return bridgeOf(meter).cap().cast();
    }

    @Override
    public void invalidate(AmpereMeterBE meter) {
        if (meter.getEnergyCompatState() instanceof Bridge bridge) {
            bridge.invalidate();
        }
    }

    private static Bridge bridgeOf(AmpereMeterBE meter) {
        if (meter.getEnergyCompatState() instanceof Bridge bridge) {
            return bridge;
        }

        Bridge bridge = new Bridge(meter);
        meter.setEnergyCompatState(bridge);
        return bridge;
    }

    private static final class Bridge {

        private static final int BUFFER_SIZE = 5;

        private final AmpereMeterBE meter;
        private final ArrayDeque<Integer> recentAmpTransfers = new ArrayDeque<>(BUFFER_SIZE);
        private final IEnergyContainer container = new MeterEnergyContainer();

        private LazyOptional<IEnergyContainer> cap = LazyOptional.empty();

        private long lastTick = -1;
        private int tickAmps = 0;
        private long tickVolt = 0;

        private Bridge(AmpereMeterBE meter) {
            this.meter = meter;
        }

        private LazyOptional<IEnergyContainer> cap() {
            if (!this.cap.isPresent()) {
                this.cap = LazyOptional.of(() -> this.container);
            }
            return this.cap;
        }

        private void invalidate() {
            this.cap.invalidate();
        }

        private @Nullable BlockEntity outputTarget() {
            Level level = this.meter.getLevel();
            if (level == null) {
                return null;
            }

            BlockEntity target = level.getBlockEntity(this.meter.getBlockPos().relative(this.meter.getOutputSide()));
            return target instanceof AmpereMeterBE ? null : target;
        }

        private void recordTransfer(long gameTime, long volt, int amps) {
            if (gameTime == this.lastTick) {
                this.tickAmps += amps;
                this.tickVolt = Math.max(this.tickVolt, volt);
            } else {
                if (gameTime - this.lastTick > BUFFER_SIZE) {
                    this.recentAmpTransfers.clear();
                }
                this.lastTick = gameTime;
                this.tickAmps = amps;
                this.tickVolt = volt;
            }

            Map.Entry<Long, String> voltageTier = InsaneUtils.voltagesMap.ceilingEntry(this.tickVolt);
            String unitLabel = "A (%s)".formatted(voltageTier != null ? voltageTier.getValue() : "???");

            if (!Objects.equals(this.meter.getUnit(), unitLabel)) {
                this.recentAmpTransfers.clear();
            }

            if (this.recentAmpTransfers.size() >= BUFFER_SIZE) {
                this.recentAmpTransfers.removeFirst();
            }
            this.recentAmpTransfers.addLast(this.tickAmps);

            int max = this.recentAmpTransfers.stream().mapToInt(Integer::intValue).max().orElse(0);
            this.meter.setDisplayedTransfer(max, unitLabel, true);
        }

        private final class MeterEnergyContainer implements IEnergyContainer {

            @Override
            public long acceptEnergyFromNetwork(Direction side, long volt, long amp) {
                Level level = Bridge.this.meter.getLevel();
                if (level == null || amp <= 0) {
                    return 0;
                }

                Bridge.this.meter.markActive();

                BlockEntity target = outputTarget();
                if (target == null) {
                    return 0;
                }

                Direction outputSide = Bridge.this.meter.getOutputSide();
                long ampsToForward = Math.max(0L, amp - 1L);
                AtomicLong forwardedRef = new AtomicLong(0);

                target.getCapability(GTCapability.CAPABILITY_ENERGY_CONTAINER, outputSide.getOpposite())
                        .ifPresent(out -> forwardedRef.set(
                                out.acceptEnergyFromNetwork(outputSide.getOpposite(), volt, ampsToForward)));

                long forwarded = Math.max(0L, Math.min(ampsToForward, forwardedRef.get()));
                recordTransfer(level.getGameTime(), volt, (int) Math.min(Integer.MAX_VALUE, forwarded));

                return Math.min(amp, forwarded + 1L);
            }

            @Override
            public boolean inputsEnergy(Direction side) {
                return side == null || side == Bridge.this.meter.getInputSide();
            }

            @Override
            public long changeEnergy(long energyToAdd) {
                return 0;
            }

            @Override
            public long getEnergyStored() {
                return 0;
            }

            @Override
            public long getEnergyCapacity() {
                return Integer.MAX_VALUE;
            }

            @Override
            public long getInputAmperage() {
                BlockEntity target = outputTarget();
                if (target == null) {
                    return 0;
                }

                Direction outputSide = Bridge.this.meter.getOutputSide();
                AtomicLong amperage = new AtomicLong();
                target.getCapability(GTCapability.CAPABILITY_ENERGY_CONTAINER, outputSide.getOpposite())
                        .ifPresent(out -> amperage.set(out.getInputAmperage()));

                long downstream = amperage.get();
                return downstream <= 0 ? 1 : downstream + 1;
            }

            @Override
            public long getInputVoltage() {
                BlockEntity target = outputTarget();
                if (target == null) {
                    return 0;
                }

                Direction outputSide = Bridge.this.meter.getOutputSide();
                AtomicLong voltage = new AtomicLong();
                target.getCapability(GTCapability.CAPABILITY_ENERGY_CONTAINER, outputSide.getOpposite())
                        .ifPresent(out -> voltage.set(out.getInputVoltage()));

                return voltage.get();
            }
        }
    }
}
