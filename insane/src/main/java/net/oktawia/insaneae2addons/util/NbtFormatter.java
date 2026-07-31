package net.oktawia.insaneae2addons.util;

public final class NbtFormatter {

    private static final String INDENT = "  ";

    private NbtFormatter() {
    }

    public static String format(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        StringBuilder out = new StringBuilder(input.length() + 32);
        int depth = 0;
        boolean inString = false;
        char stringChar = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (inString) {
                out.append(c);
                if (c == '\\' && i + 1 < input.length()) {
                    out.append(input.charAt(++i));
                } else if (c == stringChar) {
                    inString = false;
                }
                continue;
            }

            switch (c) {
                case '"', '\'' -> {
                    inString = true;
                    stringChar = c;
                    out.append(c);
                }
                case '{', '[' -> {
                    out.append(c);
                    depth++;
                    newline(out, depth);
                }
                case '}', ']' -> {
                    depth = Math.max(0, depth - 1);
                    newline(out, depth);
                    out.append(c);
                }
                case ',' -> {
                    out.append(c);
                    newline(out, depth);
                }
                default -> {
                    if (!Character.isWhitespace(c)) {
                        out.append(c);
                    }
                }
            }
        }

        return out.toString();
    }

    private static void newline(StringBuilder out, int depth) {
        out.append('\n');
        out.append(INDENT.repeat(depth));
    }
}
