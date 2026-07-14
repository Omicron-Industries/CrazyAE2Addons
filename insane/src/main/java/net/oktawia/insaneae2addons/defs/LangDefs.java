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
    CONFIG_ENTRY_RESEARCH_UNIT_EXTRA_Q_BLOCKS("gui.insaneae2addons.config.entry.research_unit_extra_q_blocks", "Extra core blocks"),
    CONFIG_DESC_RESEARCH_UNIT_EXTRA_Q_BLOCKS("gui.insaneae2addons.config.desc.research_unit_extra_q_blocks", "Extra blocks allowed in the 3x3 core of the Research Unit and how much computation each grants per block. One entry per line as namespace:block=computation, e.g. minecraft:diamond_block=8. Built-in crafting storage blocks (1k-256k) are always allowed and counted separately. Leave empty for defaults only."),

    CONFIG_SECTION_NBT_TOOLS("gui.insaneae2addons.config.section.nbt_tools", "NBT tools"),
    CONFIG_SECTION_NBT_TOOLS_DESC("gui.insaneae2addons.config.section.nbt_tools.desc", "NBT view cell and NBT filtering buses match items by their NBT via a matcher expression."),
    CONFIG_ENTRY_NBT_VIEW_CELL("gui.insaneae2addons.config.entry.nbt_view_cell", "NBT view cell"),
    CONFIG_DESC_NBT_VIEW_CELL("gui.insaneae2addons.config.desc.nbt_view_cell", "Enables the NBT view cell and its recognition in AE2 view cell slots."),
    CONFIG_ENTRY_NBT_STORAGE_BUS("gui.insaneae2addons.config.entry.nbt_storage_bus", "NBT storage bus"),
    CONFIG_DESC_NBT_STORAGE_BUS("gui.insaneae2addons.config.desc.nbt_storage_bus", "Enables the NBT storage bus."),
    CONFIG_ENTRY_NBT_EXPORT_BUS("gui.insaneae2addons.config.entry.nbt_export_bus", "NBT export bus"),
    CONFIG_DESC_NBT_EXPORT_BUS("gui.insaneae2addons.config.desc.nbt_export_bus", "Enables the NBT export bus."),

    CONFIG_SECTION_ENTROPY_CRADLE("gui.insaneae2addons.config.section.entropy_cradle", "Entropy cradle"),
    CONFIG_SECTION_ENTROPY_CRADLE_DESC("gui.insaneae2addons.config.section.entropy_cradle.desc", "A multiblock that stores AE power as FE and transmutes a block pattern in its chamber on a redstone pulse."),
    CONFIG_ENTRY_CRADLE_CAPACITY("gui.insaneae2addons.config.entry.cradle_capacity", "Capacity"),
    CONFIG_DESC_CRADLE_CAPACITY("gui.insaneae2addons.config.desc.cradle_capacity", "Maximum FE the entropy cradle can store."),
    CONFIG_ENTRY_CRADLE_CHARGING_SPEED("gui.insaneae2addons.config.entry.cradle_charging_speed", "Charging speed"),
    CONFIG_DESC_CRADLE_CHARGING_SPEED("gui.insaneae2addons.config.desc.cradle_charging_speed", "Maximum AE the entropy cradle can pull from the network per tick."),
    CONFIG_ENTRY_CRADLE_COST("gui.insaneae2addons.config.entry.cradle_cost", "Cost"),
    CONFIG_DESC_CRADLE_COST("gui.insaneae2addons.config.desc.cradle_cost", "FE consumed by the entropy cradle per transmutation pulse."),

    NBT_VIEW_CELL_CONFIRM("gui.insaneae2addons.nbt_view_cell_confirm", "Save filter"),
    NBT_VIEW_CELL_INPUT("gui.insaneae2addons.nbt_view_cell_input", "NBT matcher expression"),
    NBT_EXPORT_CONFIRM("gui.insaneae2addons.nbt_export_confirm", "Save filter"),
    NBT_EXPORT_LOAD("gui.insaneae2addons.nbt_export_load", "Load NBT from the item in the slot"),
    NBT_STORAGE_CONFIRM("gui.insaneae2addons.nbt_storage_confirm", "Save filter"),
    NBT_STORAGE_LOAD("gui.insaneae2addons.nbt_storage_load", "Load NBT from the item in the slot"),
    NBT_FILTER_SAVED("gui.insaneae2addons.nbt_filter_saved", "Filter saved"),
    NBT_SYNTAX_ERROR("gui.insaneae2addons.nbt_syntax_error", "Syntax error:"),
    NBT_EXPORT_FORMAT("gui.insaneae2addons.nbt_export_format", "Format the expression"),
    NBT_STORAGE_FORMAT("gui.insaneae2addons.nbt_storage_format", "Format the expression"),

    RESEARCH_RECIPE_PROGRESS("gui.insaneae2addons.research_recipe_progress", "Research progress"),
    RESEARCH_STORED_FLUID("gui.insaneae2addons.research_stored_fluid", "Stored research fluid"),
    RESEARCH_STORED_POWER("gui.insaneae2addons.research_stored_power", "Stored power"),
    RESEARCH_LOADING("gui.insaneae2addons.research_loading", "Researching..."),
    RESEARCH_DEV_UNLOCK("gui.insaneae2addons.research_dev_unlock", "Unlock all researches to the drive"),
    RESEARCH_UNIT_COMPUTATION("gui.insaneae2addons.research_unit_computation", "Computation: "),
    RESEARCH_UNIT_COOLANT("gui.insaneae2addons.research_unit_coolant", "Fluid use: "),
    RESEARCH_UNIT_POWER("gui.insaneae2addons.research_unit_power", "Power use: "),
    RESEARCH_UNIT_BUFFER("gui.insaneae2addons.research_unit_buffer", "Power buffer: "),
    RESEARCH_PEDESTAL_COMPUTATION("gui.insaneae2addons.research_pedestal_computation", "Computation: "),
    RESEARCH_PEDESTAL_INVALID("gui.insaneae2addons.research_pedestal_invalid", "No unit connected"),
    RESEARCH_PEDESTAL_MULTIPLE_UNITS("gui.insaneae2addons.research_pedestal_multiple_units", "Multiple units connected"),
    RESEARCH_PEDESTAL_UNIT_SHARED("gui.insaneae2addons.research_pedestal_unit_shared", "Unit shared with another pedestal"),
    DATA_DRIVE_RESEARCHES("gui.insaneae2addons.data_drive_researches", "Researches"),
    RECIPE_FABRICATOR_RESEARCH_DISK("gui.insaneae2addons.recipe_fabricator_research_disk", "Research Disk"),

    RESEARCH_CATEGORY("emi.insaneae2addons.category_research", "Research Station"),
    CRADLE_CATEGORY("emi.insaneae2addons.category_cradle", "Entropy Cradle"),
    CRADLE_INPUTS("gui.insaneae2addons.cradle_inputs", "Inputs"),
    CRADLE_OUTPUT("gui.insaneae2addons.cradle_output", "Output"),
    CRADLE_LAYER_PREFIX("gui.insaneae2addons.cradle_layer_prefix", "Y"),
    CRADLE_LAYER_ALL("gui.insaneae2addons.cradle_layer_all", "All"),
    CRADLE_LAYER_TOOLTIP("gui.insaneae2addons.cradle_layer_tooltip", "Cycle through the chamber layers"),
    CRADLE_TOGGLE_LABEL("gui.insaneae2addons.cradle_toggle_label", "C"),
    CRADLE_ON("gui.insaneae2addons.cradle_on", "On"),
    CRADLE_OFF("gui.insaneae2addons.cradle_off", "Off"),
    CRADLE_TOGGLE_TOOLTIP("gui.insaneae2addons.cradle_toggle_tooltip", "Toggle the surrounding cradle structure"),
    RESEARCH_PEDESTAL_COMPACT("gui.insaneae2addons.research_pedestal_compact", "%s c"),
    RESEARCH_OUTPUT_LABEL("gui.insaneae2addons.research_output_label", "Unlocks"),
    RESEARCH_OUTPUT_DISK_NOTE("gui.insaneae2addons.research_output_disk_note", "written to Data Drive"),
    RESEARCH_DRIVE_TOOLTIP_1("gui.insaneae2addons.research_drive_tooltip_1", "Completing this research writes its key to a Data Drive."),
    RESEARCH_DRIVE_TOOLTIP_2("gui.insaneae2addons.research_drive_tooltip_2", "Insert that drive into the Recipe Fabricator to unlock gated recipes."),
    RESEARCH_DURATION("gui.insaneae2addons.research_duration", "Min. time: %s s"),
    RESEARCH_UNLOCKS("gui.insaneae2addons.research_unlocks", "Unlocks: %s"),
    RESEARCH_GATE_REQUIRES("gui.insaneae2addons.research_gate_requires", "Requires research: %s"),
    RESEARCH_GATE_HINT("gui.insaneae2addons.research_gate_hint", "Complete the research and insert its Data Drive into the Recipe Fabricator."),

    RESEARCH_STATUS_IDLE("gui.insaneae2addons.research_status.idle", "Idle"),
    RESEARCH_STATUS_READY("gui.insaneae2addons.research_status.ready", "Ready"),
    RESEARCH_STATUS_WORKING("gui.insaneae2addons.research_status.working", "Working"),
    RESEARCH_STATUS_NO_DATA_DRIVE("gui.insaneae2addons.research_status.no_data_drive", "No Data Drive"),
    RESEARCH_STATUS_NO_MATCHING_RECIPE("gui.insaneae2addons.research_status.no_matching_recipe", "No matching research"),
    RESEARCH_STATUS_MISSING_ITEMS("gui.insaneae2addons.research_status.missing_items", "Missing items"),
    RESEARCH_STATUS_NOT_ENOUGH_POWER("gui.insaneae2addons.research_status.not_enough_power", "Not enough power"),
    RESEARCH_STATUS_OUT_OF_RESEARCH_FLUID("gui.insaneae2addons.research_status.out_of_research_fluid", "Out of research fluid"),
    RESEARCH_STATUS_FLUID_LOW("gui.insaneae2addons.research_status.fluid_low", "Research fluid low"),
    RESEARCH_STATUS_NOT_ENOUGH_COMPUTATION("gui.insaneae2addons.research_status.not_enough_computation", "Not enough computation"),
    RESEARCH_STATUS_STRUCTURE_INCOMPLETE("gui.insaneae2addons.research_status.structure_incomplete", "Structure incomplete"),
    RESEARCH_STATUS_ALREADY_UNLOCKED("gui.insaneae2addons.research_status.already_unlocked", "Already unlocked");

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
