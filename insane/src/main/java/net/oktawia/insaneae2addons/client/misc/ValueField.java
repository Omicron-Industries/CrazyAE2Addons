package net.oktawia.insaneae2addons.client.misc;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AETextField;
import net.minecraft.client.gui.Font;
import net.oktawia.crazyae2addons.util.MathParser;
import net.oktawia.crazyae2addons.util.Utils;
import net.oktawia.insaneae2addons.InsaneAddons;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public class ValueField extends AETextField {

    private static final String ALLOWED = "[0-9kKmMgGtTeE+\\-*/%().\\s]*";

    private final DoubleSupplier reader;
    private final DoubleConsumer writer;

    private boolean wasFocused;

    public ValueField(ScreenStyle style, Font font, int width, DoubleSupplier reader, DoubleConsumer writer) {
        super(style, font, 0, 0, width, 12);
        this.reader = reader;
        this.writer = writer;

        setBordered(false);
        setMaxLength(24);
        setFilter(s -> s.isEmpty() || s.matches(ALLOWED));
        setResponder(this::onChanged);
        setValue(format(reader.getAsDouble()));
    }

    public void syncFromHost() {
        double current = this.reader.getAsDouble();
        boolean focused = isFocused();

        if (focused && !this.wasFocused) {
            setValue(trimTrailingZeros(current));
        } else if (!focused) {
            String formatted = format(current);
            if (!formatted.equals(getValue())) {
                setValue(formatted);
            }
        }

        this.wasFocused = focused;
    }

    private void onChanged(String value) {
        if (!isFocused()) {
            return;
        }

        double parsed;
        try {
            parsed = parse(value);
        } catch (Exception e) {
            InsaneAddons.LOGGER.debug("invalid numeric input in penrose value field", e);
            return;
        }

        if (parsed != this.reader.getAsDouble()) {
            this.writer.accept(parsed);
        }
    }

    private static double parse(String value) {
        if (value == null || value.isBlank()) {
            return 0.0;
        }

        double parsed = MathParser.parse(value.trim());
        if (Double.isNaN(parsed) || Double.isInfinite(parsed)) {
            throw new IllegalArgumentException("Invalid numeric value: " + value);
        }
        return Math.max(0.0, parsed);
    }

    private static String format(double value) {
        if (value <= 0.0) {
            return "0";
        }
        return value >= 1000.0 ? Utils.shortenNumber(Math.round(value)) : trimTrailingZeros(value);
    }

    private static String trimTrailingZeros(double value) {
        double rounded = Math.round(value * 100.0) / 100.0;
        if (Math.abs(rounded - Math.rint(rounded)) < 1.0e-6) {
            return Long.toString(Math.round(rounded));
        }

        String text = String.format("%.2f", rounded);
        int end = text.length();
        while (text.charAt(end - 1) == '0') {
            end--;
        }
        if (text.charAt(end - 1) == '.') {
            end--;
        }
        return text.substring(0, end);
    }
}
