package net.oktawia.crazyae2addons.util;

import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.concurrent.*;

public class Utils {

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    public static final int DEFAULT_TOOLTIP_MAX_CHARS = 30;

    public static void asyncDelay(Runnable function, float delay) {
        long delayInMillis = (long) (delay * 1000);
        SCHEDULER.schedule(function, delayInMillis, TimeUnit.MILLISECONDS);
    }

    private static final Map<Double, String> SHORTEN_THRESHOLDS;
    static {
        SHORTEN_THRESHOLDS = new LinkedHashMap<>();
        SHORTEN_THRESHOLDS.put(1e18, "E");
        SHORTEN_THRESHOLDS.put(1e15, "P");
        SHORTEN_THRESHOLDS.put(1e12, "T");
        SHORTEN_THRESHOLDS.put(1e9,  "G");
        SHORTEN_THRESHOLDS.put(1e6,  "M");
        SHORTEN_THRESHOLDS.put(1e3,  "K");
    }

    public static String shortenNumber(double number) {
        return shortenNumber(number, 2);
    }

    public static String shortenNumber(double number, int decimals) {
        double abs = Math.abs(number);

        for (Map.Entry<Double, String> entry : SHORTEN_THRESHOLDS.entrySet()) {
            double threshold = entry.getKey();
            String suffix = entry.getValue();

            if (abs >= threshold) {
                return formatDecimal(number / threshold, decimals) + suffix;
            }
        }

        return formatDecimal(number, decimals);
    }

    private static String formatDecimal(double value, int decimals) {
        String s = String.format(Locale.ROOT, "%." + decimals + "f", value);

        if (s.indexOf('.') >= 0) {
            while (s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.endsWith(".")) {
                s = s.substring(0, s.length() - 1);
            }
        }

        return s;
    }

    public static String toTitle(String id) {
        StringBuilder out = new StringBuilder();

        for (String part : id.split("_")) {
            if (part.isEmpty()) continue;

            if (part.chars().anyMatch(Character::isDigit)) {
                out.append(part.toUpperCase());
            } else {
                out.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1).toLowerCase());
            }
            out.append(' ');
        }
        return out.toString().trim();
    }

    public static List<Component> wrapTooltip(Component component) {
        return wrapTooltip(component, DEFAULT_TOOLTIP_MAX_CHARS);
    }

    public static List<Component> wrapTooltip(Component component, int maxChars) {
        List<Component> lines = new ArrayList<>();
        addWrappedTooltipLines(lines, component, maxChars);
        return lines;
    }

    public static void addWrappedTooltipLines(List<Component> lines, Component component) {
        addWrappedTooltipLines(lines, component, DEFAULT_TOOLTIP_MAX_CHARS);
    }

    public static void addWrappedTooltipLines(List<Component> lines, Component component, int maxChars) {
        if (component == null) {
            return;
        }

        String text = component.getString().trim();

        if (text.isBlank()) {
            return;
        }

        int max = Math.max(8, maxChars);
        String remaining = text;

        while (remaining.length() > max) {
            int split = remaining.lastIndexOf(' ', max);

            if (split <= 0) {
                split = remaining.indexOf(' ', max);

                if (split <= 0) {
                    break;
                }
            }

            String line = remaining.substring(0, split).trim();

            if (!line.isBlank()) {
                lines.add(Component.literal(line).withStyle(component.getStyle()));
            }

            remaining = remaining.substring(split + 1).trim();
        }

        if (!remaining.isBlank()) {
            lines.add(Component.literal(remaining).withStyle(component.getStyle()));
        }
    }
}