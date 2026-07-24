package net.oktawia.insaneae2addons.logic.penrose;

public final class PenroseCurves {

    private static final double VENT_REFERENCE_RATE = 16.0;
    private static final double HAWKING_EVAP_DIVISOR = 3.0;

    private PenroseCurves() {
    }

    public static long hawkingVentCost(double evaporation) {
        return ventCost(evaporation / HAWKING_EVAP_DIVISOR);
    }

    public static double hawkingBalancedInjection(double evaporation) {
        return evaporation / HAWKING_EVAP_DIVISOR;
    }

    public static double hawkingEvaporationForInjection(double injection) {
        return injection * HAWKING_EVAP_DIVISOR;
    }

    public static double massFactor(double mass, double sweetSpot, double halfWindow, double maxFactor) {
        if (halfWindow <= 0.0) {
            return 1.0;
        }

        double closeness = clamp(1.0 - Math.abs(mass - sweetSpot) / halfWindow, 0.0, 1.0);
        return 1.0 + (maxFactor - 1.0) * closeness;
    }

    public static double heatEfficiency(double heat, double peak) {
        if (peak <= 0.0) {
            return 0.0;
        }

        double x = heat / peak;
        return clamp(2.0 * x - x * x, 0.0, 1.0);
    }

    public static long generatedFe(double flow, double heatEfficiency, double massFactor,
                                   double dutyCompensation, double feBasePerFlow) {
        double generated = dutyCompensation * feBasePerFlow * flow * heatEfficiency * massFactor;

        if (Double.isNaN(generated) || generated <= 0.0) {
            return 0L;
        }
        return (generated >= (double) Long.MAX_VALUE) ? Long.MAX_VALUE : Math.round(generated);
    }

    public static long ventCost(double rate) {
        if (rate <= 0.0) {
            return 0L;
        }

        double cost = (double) (1L << 30) * Math.exp(2.0 * ((rate / VENT_REFERENCE_RATE) - 1.0));

        if (Double.isNaN(cost) || cost <= 0.0) {
            return 0L;
        }
        return (Double.isInfinite(cost) || cost > Long.MAX_VALUE) ? Long.MAX_VALUE : (long) Math.ceil(cost);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
