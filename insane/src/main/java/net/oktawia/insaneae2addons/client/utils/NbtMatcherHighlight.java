package net.oktawia.insaneae2addons.client.utils;

import java.util.List;

import net.oktawia.crazyae2addons.client.misc.MultilineTextFieldWidget.HighlightRule;

public final class NbtMatcherHighlight {

    private NbtMatcherHighlight() {
    }

    public static List<HighlightRule> rules() {
        return List.of(
                new HighlightRule("\"(?:\\\\.|[^\"])*\"", 0xFF81C784),
                new HighlightRule("(?i)\\b(or|and|xor|nand|not)\\b", 0xFF64B5F6),
                new HighlightRule("\\|\\||&&|\\^\\^|!&|[|&^]", 0xFF64B5F6),
                new HighlightRule("!", 0xFFEF9A9A),
                new HighlightRule("\\*", 0xFFFFB74D),
                new HighlightRule("[A-Za-z_][A-Za-z0-9_]*(?=\\s*:)", 0xFFA5D6A7),
                new HighlightRule("-?\\d+(?:\\.\\d+)?[bslfdBSLFD]?", 0xFFFFF176),
                new HighlightRule("[{}()\\[\\],]", 0xFFB0BEC5));
    }
}
