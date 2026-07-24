package net.oktawia.insaneae2addons.logic.penrose;

import net.oktawia.insaneae2addons.InsaneConfig;

import java.util.function.DoubleUnaryOperator;

public record PenroseCurveModel(DoubleUnaryOperator curve, double optimum) {

    public static PenroseCurveModel heat() {
        double max = InsaneConfig.COMMON.PENROSE_MAX_HEAT_GK.get();
        double peak = InsaneConfig.COMMON.PENROSE_HEAT_PEAK_GK.get();

        if (max <= 0.0) {
            return new PenroseCurveModel(x -> 0.0, -1.0);
        }
        return new PenroseCurveModel(x -> PenroseCurves.heatEfficiency(x * max, peak), Math.min(1.0, peak / max));
    }

    public static PenroseCurveModel mass() {
        double window = Math.max(0L, InsaneConfig.COMMON.PENROSE_MASS_WINDOW_MU.get());
        double maxFactor = InsaneConfig.COMMON.PENROSE_MASS_FACTOR_MAX.get();
        double span = maxFactor - 1.0;

        if (window <= 0.0 || span <= 0.0) {
            return new PenroseCurveModel(x -> 0.0, -1.0);
        }
        return new PenroseCurveModel(
                x -> (PenroseCurves.massFactor(x * window, window / 2.0, window / 2.0, maxFactor) - 1.0) / span,
                0.5);
    }

    public double valueAt(double position) {
        return position < 0.0 ? 0.0 : this.curve.applyAsDouble(Math.min(1.0, position));
    }
}
