package net.oktawia.insaneae2addons.logic.penrose;

import java.util.Arrays;

import com.lowdragmc.lowdraglib.syncdata.IContentChangeAware;
import com.lowdragmc.lowdraglib.syncdata.ITagSerializable;

import net.minecraft.nbt.CompoundTag;

import lombok.Getter;
import lombok.Setter;

public final class AccretionDisk implements ITagSerializable<CompoundTag>, IContentChangeAware {

    public static final int WINDOW_TICKS = 20 * 120;
    public static final int MEAN_TICKS = WINDOW_TICKS / 2;

    private static final double SIGMA_TICKS = 400.0;
    private static final double NBT_SCALE = 1_000_000.0;

    private static final String MASS_BY_AGE_TAG = "massByAge";
    private static final String NEWEST_SLOT_TAG = "newestSlot";
    private static final String MASS_TAG = "mass";

    private static final double[] LEAVE_FRACTION = buildLeaveFractions();

    private final double[] massByAge = new double[WINDOW_TICKS];

    @Getter
    @Setter
    private Runnable onContentsChanged = () -> {
    };

    private int newestSlot;

    @Getter
    private double mass;

    @Getter
    private int lastAccretion;

    public double advance(double injected) {
        this.newestSlot--;
        if (this.newestSlot < 0) {
            this.newestSlot += WINDOW_TICKS;
        }

        this.massByAge[this.newestSlot] = injected;
        this.mass += injected;

        double leaving = 0.0;
        int slot = this.newestSlot;
        for (int age = 0; age < WINDOW_TICKS; age++) {
            leaving += this.massByAge[slot] * LEAVE_FRACTION[age];
            if (++slot >= WINDOW_TICKS) {
                slot = 0;
            }
        }

        leaving = Math.min(Math.max(0.0, leaving), this.mass);
        this.mass = Math.max(0.0, this.mass - leaving);
        this.lastAccretion = (int) Math.max(0L, Math.min(Integer.MAX_VALUE, Math.round(leaving)));
        this.onContentsChanged.run();

        return leaving;
    }

    public void clear() {
        Arrays.fill(this.massByAge, 0.0);
        this.newestSlot = 0;
        this.mass = 0.0;
        this.lastAccretion = 0;
        this.onContentsChanged.run();
    }

    public boolean isEmpty() {
        return this.mass <= 0.0;
    }

    public double flowPerTick() {
        return Math.max(0.0, this.mass / MEAN_TICKS);
    }

    @Override
    public CompoundTag serializeNBT() {
        long[] scaled = new long[WINDOW_TICKS];
        for (int i = 0; i < WINDOW_TICKS; i++) {
            scaled[i] = Math.round(this.massByAge[i] * NBT_SCALE);
        }

        CompoundTag tag = new CompoundTag();
        tag.putLongArray(MASS_BY_AGE_TAG, scaled);
        tag.putInt(NEWEST_SLOT_TAG, this.newestSlot);
        tag.putLong(MASS_TAG, Math.round(this.mass * NBT_SCALE));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        clear();

        long[] scaled = tag.getLongArray(MASS_BY_AGE_TAG);
        if (scaled.length == WINDOW_TICKS) {
            for (int i = 0; i < WINDOW_TICKS; i++) {
                this.massByAge[i] = scaled[i] / NBT_SCALE;
            }
        }

        int slot = tag.getInt(NEWEST_SLOT_TAG);
        this.newestSlot = (slot < 0 || slot >= WINDOW_TICKS) ? 0 : slot;
        this.mass = Math.max(0.0, tag.getLong(MASS_TAG) / NBT_SCALE);
    }

    private static double[] buildLeaveFractions() {
        double[] fractions = new double[WINDOW_TICKS];
        double mean = WINDOW_TICKS / 2.0;
        double scale = SIGMA_TICKS * Math.sqrt(2.0);

        double windowSpan = normalCdf(WINDOW_TICKS, mean, scale) - normalCdf(0.0, mean, scale);
        if (windowSpan <= 0.0) {
            windowSpan = 1.0;
        }

        double sum = 0.0;
        for (int age = 0; age < WINDOW_TICKS; age++) {
            double slice = (normalCdf(age + 1, mean, scale) - normalCdf(age, mean, scale)) / windowSpan;
            fractions[age] = Math.max(0.0, slice);
            sum += fractions[age];
        }

        if (sum > 0.0) {
            for (int age = 0; age < WINDOW_TICKS; age++) {
                fractions[age] /= sum;
            }
        }
        return fractions;
    }

    private static double normalCdf(double x, double mean, double scale) {
        return 0.5 * (1.0 + erf((x - mean) / scale));
    }

    private static double erf(double x) {
        int sign = (x < 0.0) ? -1 : 1;
        double abs = Math.abs(x);

        double t = 1.0 / (1.0 + 0.3275911 * abs);
        double poly = (((((1.061405429 * t - 1.453152027) * t) + 1.421413741) * t - 0.284496736) * t + 0.254829592) * t;

        return sign * (1.0 - poly * Math.exp(-abs * abs));
    }
}
