package net.oktawia.insaneae2addons.xei.common;

import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SceneWidget;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.misc.ItemStackTransfer;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition;
import net.oktawia.crazyae2addons.multiblock.MultiblockMaterials;
import net.oktawia.insaneae2addons.defs.LangDefs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiblockStructurePreview extends WidgetGroup {

    private static final int WIDTH = 160;
    private static final int HEIGHT = 200;
    private static final int SLOT_SIZE = 18;
    private static final int SCENE_X = 5;
    private static final int SCENE_Y = 16;
    private static final int SCENE_WIDTH = WIDTH - 2 * SCENE_X;
    private static final int SCENE_HEIGHT = 104;
    private static final int MATERIALS_Y = 124;
    private static final int SURFACE_RENDER_DEPTH = 3;
    private static final int CYCLE_INTERVAL_TICKS = 20;
    private static final int CYCLE_LABEL_X = SCENE_X + 2 + SLOT_SIZE + 3;
    private static final int CYCLE_LABEL_MAX_WIDTH = SCENE_X + SCENE_WIDTH - 22 - CYCLE_LABEL_X - 2;

    private final TrackedDummyWorld world = new TrackedDummyWorld();
    private final SceneWidget sceneWidgetAll;
    private final SceneWidget sceneWidgetLayer;

    private final Map<BlockPos, List<Block>> optionsByPos = new HashMap<>();
    private List<Block> selectedOptions = List.of();
    private int cycleTick;
    private int cycleIndex;
    private ItemStackTransfer cycleTransfer;
    private SlotWidget cycleSlot;
    private LabelWidget cycleLabel;

    private Set<BlockPos> allPositions = new HashSet<>();
    private int layer = -1;
    private int minY;
    private int maxY;

    private static String multiblockTitle(MultiblockEntry entry) {
        String name = entry.controller().getHoverName().getString();
        String suffix = " Controller";
        if (name.regionMatches(true, name.length() - suffix.length(), suffix, 0, suffix.length())) {
            return name.substring(0, name.length() - suffix.length()).stripTrailing();
        }
        return name;
    }

    public MultiblockStructurePreview(MultiblockEntry entry) {
        super(0, 0, WIDTH, HEIGHT);
        setClientSideWidget();

        addWidget(new LabelWidget(5, 4, multiblockTitle(entry)));

        if (entry.showPreview()) {
            this.sceneWidgetAll = new SceneWidget(SCENE_X, SCENE_Y, SCENE_WIDTH, SCENE_HEIGHT, world)
                    .setRenderFacing(false)
                    .useCacheBuffer();
            this.sceneWidgetLayer = new SceneWidget(SCENE_X, SCENE_Y, SCENE_WIDTH, SCENE_HEIGHT, world)
                    .setRenderFacing(false);

            addWidget(sceneWidgetAll);
            addWidget(sceneWidgetLayer);

            sceneWidgetAll.setVisible(true);
            sceneWidgetLayer.setVisible(false);

            addWidget(new ButtonWidget(SCENE_X + SCENE_WIDTH - 22, SCENE_Y, 20, 20,
                    new TextTexture(tr(LangDefs.MULTIBLOCK_LAYER_ALL)).setSupplier(() -> layer >= 0
                            ? tr(LangDefs.MULTIBLOCK_LAYER_PREFIX) + layer
                            : tr(LangDefs.MULTIBLOCK_LAYER_ALL)),
                    button -> switchLayer())
                    .appendHoverTooltips(comp(LangDefs.MULTIBLOCK_LAYER_TOOLTIP)));

            this.cycleTransfer = new ItemStackTransfer(ItemStack.EMPTY);
            this.cycleSlot = new SlotWidget(cycleTransfer, 0, SCENE_X + 2, SCENE_Y + 2, false, false)
                    .setBackgroundTexture(SlotWidget.ITEM_SLOT_TEXTURE)
                    .setIngredientIO(IngredientIO.RENDER_ONLY);
            cycleSlot.setVisible(false);
            this.cycleLabel = new LabelWidget(CYCLE_LABEL_X, SCENE_Y + 7,
                    () -> selectedOptions.isEmpty()
                            ? ""
                            : fitName(new ItemStack(selectedOptions.get(cycleIndex % selectedOptions.size()))
                                    .getHoverName().getString()));
            cycleLabel.setDropShadow(true);
            cycleLabel.setVisible(false);
            addWidget(cycleSlot);
            addWidget(cycleLabel);

            loadStructure(entry);

            sceneWidgetAll.setOnSelected((pos, face) -> selectBlock(pos));
            sceneWidgetLayer.setOnSelected((pos, face) -> selectBlock(pos));
        } else {
            this.sceneWidgetAll = null;
            this.sceneWidgetLayer = null;

            addWidget(new LabelWidget(5, SCENE_Y + SCENE_HEIGHT / 2, tr(LangDefs.MULTIBLOCK_NO_PREVIEW)));
        }

        addMaterials(entry);
    }

    private void addMaterials(MultiblockEntry entry) {
        Map<Block, Integer> materials = new LinkedHashMap<>();
        materials.put(Block.byItem(entry.controller().getItem()), 1);
        materials.putAll(MultiblockMaterials.count(entry.definition()));

        addWidget(new LabelWidget(5, MATERIALS_Y, tr(LangDefs.MULTIBLOCK_MATERIALS)));

        var font = Minecraft.getInstance().font;

        int widest = SLOT_SIZE;
        for (Integer amount : materials.values()) {
            widest = Math.max(widest, font.width(String.valueOf(amount)));
        }

        int columnWidth = widest + 6;
        int available = WIDTH - 2 * SCENE_X;
        int perRow = Math.max(1, available / columnWidth);

        int index = 0;
        for (Map.Entry<Block, Integer> material : materials.entrySet()) {
            int column = index % perRow;
            int row = index / perRow;

            int columns = Math.min(perRow, materials.size() - row * perRow);
            int rowX = (WIDTH - columns * columnWidth) / 2;

            int x = rowX + column * columnWidth + (columnWidth - SLOT_SIZE) / 2;
            int y = MATERIALS_Y + 12 + row * (SLOT_SIZE + 14);

            ItemStack stack = new ItemStack(material.getKey());
            String amount = String.valueOf(material.getValue());

            addWidget(new SlotWidget(new ItemStackTransfer(stack), 0, x, y, false, false)
                    .setBackgroundTexture(SlotWidget.ITEM_SLOT_TEXTURE)
                    .setIngredientIO(IngredientIO.INPUT));
            addWidget(new LabelWidget(x + (SLOT_SIZE - font.width(amount)) / 2, y + 21, amount));

            index++;
        }
    }

    private void loadStructure(MultiblockEntry entry) {
        MultiblockDefinition definition = entry.definition();
        Map<BlockPos, BlockInfo> blocks = new HashMap<>();

        optionsByPos.clear();

        Block controller = Block.byItem(entry.controller().getItem());
        blocks.put(BlockPos.ZERO, BlockInfo.fromBlockState(controller.defaultBlockState()));
        optionsByPos.put(BlockPos.ZERO, List.of(controller));

        for (MultiblockDefinition.PatternEntry patternEntry : definition.getEntries(Direction.NORTH)) {
            List<Block> options = symbolOptions(definition, patternEntry.symbol());
            if (options.isEmpty()) {
                continue;
            }

            BlockPos pos = new BlockPos(patternEntry.relX(), patternEntry.relY(), patternEntry.relZ());
            blocks.put(pos, BlockInfo.fromBlockState(options.get(0).defaultBlockState()));
            optionsByPos.put(pos, options);
        }

        allPositions = new HashSet<>(blocks.keySet());
        minY = allPositions.stream().mapToInt(BlockPos::getY).min().orElse(0);
        maxY = allPositions.stream().mapToInt(BlockPos::getY).max().orElse(0);

        world.clear();
        world.addBlocks(blocks);
        sceneWidgetAll.setRenderedCore(visibleShell(blocks, SURFACE_RENDER_DEPTH), null);
        updateRenderedLayer();
    }

    private static String fitName(String name) {
        Font font = Minecraft.getInstance().font;
        if (font.width(name) <= CYCLE_LABEL_MAX_WIDTH) {
            return name;
        }
        String ellipsis = "...";
        String clipped = font.plainSubstrByWidth(name, CYCLE_LABEL_MAX_WIDTH - font.width(ellipsis));
        return clipped + ellipsis;
    }

    private static List<Block> symbolOptions(MultiblockDefinition definition, char symbol) {
        MultiblockDefinition.SymbolDef symbolDef = definition.getSymbol(symbol);
        if (symbolDef == null) {
            return List.of();
        }

        List<Block> options = new ArrayList<>();
        for (Block block : symbolDef.blocks()) {
            if (block != Blocks.AIR) {
                options.add(block);
            }
        }
        return options;
    }

    private void selectBlock(BlockPos pos) {
        List<Block> options = optionsByPos.get(pos);
        if (options == null || options.isEmpty()) {
            clearSelection();
            return;
        }

        selectedOptions = options;
        cycleIndex = 0;
        cycleTick = 0;
        cycleTransfer.setStackInSlot(0, new ItemStack(options.get(0)));
        cycleSlot.setVisible(true);
        cycleLabel.setVisible(true);
    }

    private void clearSelection() {
        selectedOptions = List.of();
        cycleTransfer.setStackInSlot(0, ItemStack.EMPTY);
        cycleSlot.setVisible(false);
        cycleLabel.setVisible(false);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (selectedOptions.size() <= 1) {
            return;
        }

        cycleTick++;
        if (cycleTick >= CYCLE_INTERVAL_TICKS) {
            cycleTick = 0;
            cycleIndex = (cycleIndex + 1) % selectedOptions.size();
            cycleTransfer.setStackInSlot(0, new ItemStack(selectedOptions.get(cycleIndex)));
        }
    }

    private static Set<BlockPos> visibleShell(Map<BlockPos, BlockInfo> blocks, int maxDepth) {
        if (blocks.isEmpty()) {
            return Set.of();
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (BlockPos pos : blocks.keySet()) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
        }
        minX--; minY--; minZ--;
        maxX++; maxY++; maxZ++;

        Map<BlockPos, Integer> bestDepth = new HashMap<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> shell = new HashSet<>();

        BlockPos start = new BlockPos(minX, minY, minZ);
        bestDepth.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            BlockPos node = queue.poll();
            int depth = bestDepth.get(node);

            for (Direction direction : Direction.values()) {
                BlockPos next = node.relative(direction);
                if (next.getX() < minX || next.getX() > maxX
                        || next.getY() < minY || next.getY() > maxY
                        || next.getZ() < minZ || next.getZ() > maxZ) {
                    continue;
                }

                BlockInfo info = blocks.get(next);
                if (info == null) {
                    Integer old = bestDepth.get(next);
                    if (old == null || old > depth) {
                        bestDepth.put(next, depth);
                        queue.add(next);
                    }
                    continue;
                }

                int nextDepth = depth + 1;
                if (nextDepth > maxDepth) {
                    continue;
                }

                shell.add(next);

                if (!info.getBlockState().canOcclude() && nextDepth < maxDepth) {
                    Integer old = bestDepth.get(next);
                    if (old == null || old > nextDepth) {
                        bestDepth.put(next, nextDepth);
                        queue.add(next);
                    }
                }
            }
        }

        return shell;
    }

    private void switchLayer() {
        layer++;
        if (layer > (maxY - minY)) {
            layer = -1;
        }
        clearSelection();
        updateRenderedLayer();
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    private void updateRenderedLayer() {
        if (layer == -1) {
            sceneWidgetAll.setVisible(true);
            sceneWidgetLayer.setVisible(false);
            return;
        }

        int targetY = minY + layer;
        Set<BlockPos> filtered = new HashSet<>();
        for (BlockPos pos : allPositions) {
            if (pos.getY() == targetY) {
                filtered.add(pos);
            }
        }

        sceneWidgetLayer.setRenderedCore(filtered, null);
        sceneWidgetAll.setVisible(false);
        sceneWidgetLayer.setVisible(true);
    }

    private static String tr(LangDefs def) {
        return Component.translatable(def.getTranslationKey()).getString();
    }

    private static Component comp(LangDefs def) {
        return Component.translatable(def.getTranslationKey());
    }
}
