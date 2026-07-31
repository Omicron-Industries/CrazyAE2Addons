package net.oktawia.insaneae2addons.client.renderer;

import net.minecraft.util.Mth;

public final class PenroseLaserBeamStyle {

    public record Tint(float red, float green, float blue, float alpha) {
        public Tint scaled(float factor) {
            return new Tint(this.red, this.green, this.blue, this.alpha * factor);
        }
    }

    public record Rgb(float red, float green, float blue) {
        public static Rgb of(int hex) {
            return new Rgb(
                    ((hex >> 16) & 0xFF) / 255.0f,
                    ((hex >> 8) & 0xFF) / 255.0f,
                    (hex & 0xFF) / 255.0f);
        }

        public Tint tint(float whiteness, float alpha) {
            return new Tint(
                    Mth.lerp(whiteness, this.red, 1.0f),
                    Mth.lerp(whiteness, this.green, 1.0f),
                    Mth.lerp(whiteness, this.blue, 1.0f),
                    alpha);
        }
    }

    public static final Rgb BEAM_COLOR = Rgb.of(0x00cdee);

    public static final float LIFETIME_TICKS = 18.0f;
    public static final int MAX_ACTIVE_BEAMS = 64;

    public static final float CORE_WHITENESS = 0.90f;
    public static final float GLOW_WHITENESS = 0.00f;
    public static final float FLARE_WHITENESS = 0.24f;
    public static final float HAZE_WHITENESS = 0.00f;
    public static final float MUZZLE_WHITENESS = 0.63f;
    public static final float IMPACT_WHITENESS = 0.41f;
    public static final float RING_WHITENESS = 0.13f;

    public static final float CORE_ALPHA = 0.95f;
    public static final float GLOW_ALPHA = 0.32f;
    public static final float FLARE_ALPHA = 0.42f;
    public static final float HAZE_ALPHA = 0.16f;
    public static final float MUZZLE_ALPHA = 0.75f;
    public static final float IMPACT_ALPHA = 0.85f;
    public static final float RING_ALPHA = 0.55f;

    public static final Tint CORE = BEAM_COLOR.tint(CORE_WHITENESS, CORE_ALPHA);
    public static final Tint GLOW = BEAM_COLOR.tint(GLOW_WHITENESS, GLOW_ALPHA);
    public static final Tint FLARE = BEAM_COLOR.tint(FLARE_WHITENESS, FLARE_ALPHA);
    public static final Tint HAZE = BEAM_COLOR.tint(HAZE_WHITENESS, HAZE_ALPHA);
    public static final Tint MUZZLE = BEAM_COLOR.tint(MUZZLE_WHITENESS, MUZZLE_ALPHA);
    public static final Tint IMPACT = BEAM_COLOR.tint(IMPACT_WHITENESS, IMPACT_ALPHA);
    public static final Tint RING = BEAM_COLOR.tint(RING_WHITENESS, RING_ALPHA);

    public static final float CORE_RADIUS = 0.05f;
    public static final float GLOW_RADIUS_FACTOR = 2.8f;
    public static final float FLARE_RADIUS_FACTOR = 6.0f;
    public static final float HAZE_RADIUS_FACTOR = 16.0f;
    public static final float MUZZLE_RADIUS_FACTOR = 3.4f;
    public static final float IMPACT_RADIUS_FACTOR = 2.6f;
    public static final float IMPACT_GROWTH = 5.0f;

    public static final float RING_START_RADIUS = 0.15f;
    public static final float RING_GROWTH = 2.6f;
    public static final float RING_THICKNESS = 0.28f;
    public static final float RING_SPREAD = 0.5f;

    public static final float CHARGE_FLOOR = 0.35f;
    public static final float CHARGE_HAZE_SCALING = 2.0f;
    public static final float CHARGE_RING_SCALING = 2.0f;

    public static final float AGE_THINNING = 0.5f;
    public static final float TIP_ALPHA = 0.65f;

    public static final float FLICKER_BASE = 0.82f;
    public static final float FLICKER_AMPLITUDE = 0.18f;
    public static final float FLICKER_SPEED_A = 2.7f;
    public static final float FLICKER_SPEED_B = 1.3f;
    public static final float MUZZLE_PULSE_AMPLITUDE = 0.35f;
    public static final float MUZZLE_PULSE_SPEED = 3.1f;

    public static final int CORE_SIDES = 6;
    public static final int GLOW_SIDES = 12;
    public static final int DISC_SIDES = 18;

    private PenroseLaserBeamStyle() {
    }

    public static float chargeScale(float intensity) {
        return CHARGE_FLOOR + (1.0f - CHARGE_FLOOR) * Mth.clamp(intensity, 0.0f, 1.0f);
    }

    public static float life(float age) {
        return Mth.clamp(age / LIFETIME_TICKS, 0.0f, 1.0f);
    }

    public static float fade(float life) {
        return (1.0f - life) * (1.0f - life);
    }

    public static float flicker(float age) {
        return FLICKER_BASE + FLICKER_AMPLITUDE * Mth.sin(age * FLICKER_SPEED_A) * Mth.cos(age * FLICKER_SPEED_B);
    }

    public static float coreRadius(float intensity, float life, float age) {
        float thinning = 1.0f - AGE_THINNING * life;
        return CORE_RADIUS * chargeScale(intensity) * thinning * flicker(age);
    }

    public static float muzzlePulse(float age) {
        return 1.0f + MUZZLE_PULSE_AMPLITUDE * Mth.sin(age * MUZZLE_PULSE_SPEED);
    }

    public static float impactRadius(float coreRadius, float life) {
        return coreRadius * GLOW_RADIUS_FACTOR * (IMPACT_RADIUS_FACTOR + IMPACT_GROWTH * life);
    }

    public static float ringInnerRadius(float intensity, float life) {
        return RING_START_RADIUS + RING_GROWTH * life * scaledCharge(intensity, CHARGE_RING_SCALING);
    }

    public static float ringOuterRadius(float intensity, float life) {
        return ringInnerRadius(intensity, life) + RING_THICKNESS + RING_SPREAD * life;
    }

    public static float hazeRadius(float coreRadius, float intensity) {
        return coreRadius * HAZE_RADIUS_FACTOR * scaledCharge(intensity, CHARGE_HAZE_SCALING);
    }

    private static float scaledCharge(float intensity, float weight) {
        return Math.max(0.0f, 1.0f - weight + weight * chargeScale(intensity));
    }
}
