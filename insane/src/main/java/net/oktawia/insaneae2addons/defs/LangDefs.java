package net.oktawia.insaneae2addons.defs;

import appeng.core.localization.LocalizationEnum;

public enum LangDefs implements LocalizationEnum {

    MOD_NAME("itemGroup.insaneae2addons", "Insane AE2 Addons"),

    ALWAYS("gui.insaneae2addons.always", "Always"),
    EQUALS("gui.insaneae2addons.equals", "Equals"),
    NOT_EQUALS("gui.insaneae2addons.not_equals", "Not Equals"),
    PLACE("gui.insaneae2addons.place", "Place"),
    BREAK("gui.insaneae2addons.break", "Break"),

    LEFT("gui.insaneae2addons.left", "Left"),
    RIGHT("gui.insaneae2addons.right", "Right"),
    UP("gui.insaneae2addons.up", "Up"),
    DOWN("gui.insaneae2addons.down", "Down"),
    FORWARDS("gui.insaneae2addons.forwards", "Forwards"),
    BACKWARDS("gui.insaneae2addons.backwards", "Backwards"),
    BACK("gui.insaneae2addons.back", "Back"),

    MOVE_FORWARD("gui.insaneae2addons.move_forward", "Move forward"),
    MOVE_BACKWARD("gui.insaneae2addons.move_backward", "Move backward"),
    MOVE_LEFT("gui.insaneae2addons.move_left", "Move left"),
    MOVE_RIGHT("gui.insaneae2addons.move_right", "Move right"),
    MOVE_UP("gui.insaneae2addons.move_up", "Move up"),
    MOVE_DOWN("gui.insaneae2addons.move_down", "Move down"),

    CONFIRM("gui.insaneae2addons.confirm", "Confirm"),
    GENERATE("gui.insaneae2addons.generate", "Generate"),
    FLIP_HORIZONTAL("gui.insaneae2addons.flip_horizontal", "Flip Horizontal"),
    FLIP_VERTICAL("gui.insaneae2addons.flip_vertical", "Flip Vertical"),
    ROTATE_CW("gui.insaneae2addons.rotate_cw", "Rotate CW"),
    VISUAL_ASSISTANCE("gui.insaneae2addons.visual_assistance", "Visual Assistance - fill region helper"),

    SHOW_PREVIEW("gui.insaneae2addons.show_preview", "Show Preview"),
    HIDE_PREVIEW("gui.insaneae2addons.hide_preview", "Hide Preview"),

    CORNER_SET_A("gui.insaneae2addons.corner_set_a", "Corner A set"),
    CORNER_SET_B("gui.insaneae2addons.corner_set_b", "Corner B set (origin)"),
    CORNER_RESET("gui.insaneae2addons.corner_reset", "Corners reset"),

    PROGRAM_SAVED("gui.insaneae2addons.program_saved", "Pattern saved!"),
    PROGRAM_INVALID("gui.insaneae2addons.program_invalid", "Pattern is invalid"),
    PROGRAM_NO_CODE("gui.insaneae2addons.program_no_code", "No program loaded"),
    SYNTAX_ERROR("gui.insaneae2addons.syntax_error", "Syntax Error!"),

    BUILDER_PATTERN_SUBSCREEN_TITLE("gui.insaneae2addons.builder_pattern_subscreen_title", "Fill Region"),
    ACTION("gui.insaneae2addons.action", "Action"),
    CHECK("gui.insaneae2addons.check", "Check"),
    CONDITION("gui.insaneae2addons.condition", "Condition"),
    PATTERN("gui.insaneae2addons.pattern", "Pattern"),
    OFFSET("gui.insaneae2addons.offset", "Offset"),
    WIDTH("gui.insaneae2addons.width", "Width"),
    HEIGHT("gui.insaneae2addons.height", "Height"),
    DEPTH("gui.insaneae2addons.depth", "Depth"),

    ENERGY_NEEDED("gui.insaneae2addons.energy_needed", "Energy needed: "),
    MISSING("gui.insaneae2addons.missing", "Missing:"),
    SKIP_MISSING_TOOLTIP("gui.insaneae2addons.skip_missing_tooltip", "Skip blocks that are missing from the network"),

    MIN("gui.insaneae2addons.min", "Min"),
    MAX("gui.insaneae2addons.max", "Max"),
    AMPERES("gui.insaneae2addons.amperes", "Amps"),
    FE_PER_TICK("gui.insaneae2addons.fe_per_tick", "FE/t"),
    AMPERE_METER_DIRECTION_LEFT_TO_RIGHT("gui.insaneae2addons.ampere_meter_direction_left_to_right", "Send power from left to right"),
    AMPERE_METER_DIRECTION_RIGHT_TO_LEFT("gui.insaneae2addons.ampere_meter_direction_right_to_left", "Send power from right to left"),
    AMPERE_METER_MIN_THRESHOLD("gui.insaneae2addons.ampere_meter_min_threshold", "Stop emitting redstone below this transfer rate"),
    AMPERE_METER_MAX_THRESHOLD("gui.insaneae2addons.ampere_meter_max_threshold", "Max out redstone above this transfer rate"),

    CONFIG_TITLE("gui.insaneae2addons.config.title", "Insane AE2 Addons"),
    CONFIG_CATEGORY_SETTINGS("gui.insaneae2addons.config.category.settings", "Settings"),
    CONFIG_SECTION_AUTOBUILDER("gui.insaneae2addons.config.section.autobuilder", "Autobuilder"),
    CONFIG_SECTION_AUTOBUILDER_DESC("gui.insaneae2addons.config.section.autobuilder.desc", "Places or breaks blocks in a region based on a builder pattern."),
    CONFIG_ENTRY_COST_MULTIPLIER("gui.insaneae2addons.config.entry.cost_multiplier", "Cost multiplier"),
    CONFIG_DESC_COST_MULTIPLIER("gui.insaneae2addons.config.desc.cost_multiplier", "FE cost multiplier for the autobuilder."),
    CONFIG_ENTRY_MINE_DELAY("gui.insaneae2addons.config.entry.mine_delay", "Mine delay"),
    CONFIG_DESC_MINE_DELAY("gui.insaneae2addons.config.desc.mine_delay", "Ticks to wait after each broken block."),
    CONFIG_ENTRY_SPEED("gui.insaneae2addons.config.entry.speed", "Speed"),
    CONFIG_DESC_SPEED("gui.insaneae2addons.config.desc.speed", "Operations per tick the autobuilder can perform."),
    CONFIG_ENTRY_PREVIEW_LIMIT("gui.insaneae2addons.config.entry.preview_limit", "Preview limit"),
    CONFIG_DESC_PREVIEW_LIMIT("gui.insaneae2addons.config.desc.preview_limit", "How many preview blocks the autobuilder can show at once."),

    CONFIG_SECTION_RESEARCH("gui.insaneae2addons.config.section.research", "Research"),
    CONFIG_SECTION_RESEARCH_DESC("gui.insaneae2addons.config.section.research.desc", "The research multiblock unlocks insane fabrication recipes onto a Data Drive."),
    CONFIG_ENTRY_RESEARCH_REQUIRED("gui.insaneae2addons.config.entry.research_required", "Research required"),
    CONFIG_DESC_RESEARCH_REQUIRED("gui.insaneae2addons.config.desc.research_required", "When on, insane fabrication recipes require the matching research unlock on a Data Drive."),
    CONFIG_ENTRY_RESEARCH_UNIT_EXTRA_Q_BLOCKS("gui.insaneae2addons.config.entry.research_unit_extra_q_blocks", "Unit extra Q blocks"),
    CONFIG_DESC_RESEARCH_UNIT_EXTRA_Q_BLOCKS("gui.insaneae2addons.config.desc.research_unit_extra_q_blocks", "Extra block ids accepted in the Q slots of the Research Unit structure.");

    private final String key;
    private final String value;

    LangDefs(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }

    @Override
    public String getEnglishText() {
        return value;
    }
}
