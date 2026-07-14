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
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.oktawia.crazyae2addons.multiblock.MultiblockDefinition;
import net.oktawia.insaneae2addons.blocks.EntropyCradleBlock;
import net.oktawia.insaneae2addons.blocks.EntropyCradleCapacitorBlock;
import net.oktawia.insaneae2addons.blocks.EntropyCradleControllerBlock;
import net.oktawia.insaneae2addons.defs.InsaneMultiblocks;
import net.oktawia.insaneae2addons.defs.LangDefs;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneRecipes;
import net.oktawia.insaneae2addons.recipes.CradlePattern;
import net.oktawia.insaneae2addons.recipes.CradleRecipe;
import net.minecraft.core.Direction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CradlePreview extends WidgetGroup {

    private static final int WIDTH = 160;
    private static final int HEIGHT = 200;

    private final TrackedDummyWorld world = new TrackedDummyWorld();
    private final SceneWidget sceneWidgetAll;
    private final SceneWidget sceneWidgetLayer;
    private final ResourceLocation structureId;

    private int layer = -1;
    private int minY = 0;
    private int maxY = 0;
    private Set<BlockPos> allPositions = new HashSet<>();
    private boolean showCradle = false;
    private Map<BlockPos, BlockInfo> cradleBlocks = null;
    private final Map<BlockPos, BlockInfo> baseBlocks = new HashMap<>();

    public CradlePreview(ResourceLocation structureId, List<ItemStack> inputs, ItemStack output, String description) {
        super(0, 0, WIDTH, HEIGHT);
        setClientSideWidget();
        this.structureId = structureId;

        int sceneX = 5;
        int sceneY = 5;
        int sceneSize = 100;

        sceneWidgetAll = new SceneWidget(sceneX, sceneY, sceneSize, sceneSize, world).setRenderFacing(false);
        sceneWidgetLayer = new SceneWidget(sceneX, sceneY, sceneSize, sceneSize, world).setRenderFacing(false);

        addWidget(sceneWidgetAll);
        addWidget(sceneWidgetLayer);

        sceneWidgetAll.setVisible(true);
        sceneWidgetLayer.setVisible(false);

        addWidget(new ButtonWidget(sceneX + sceneSize - 25, sceneY, 20, 20,
                new TextTexture(tr(LangDefs.CRADLE_LAYER_PREFIX)).setSupplier(() -> layer >= 0
                        ? tr(LangDefs.CRADLE_LAYER_PREFIX) + layer
                        : tr(LangDefs.CRADLE_LAYER_ALL)),
                b -> switchLayer())
                .appendHoverTooltips(comp(LangDefs.CRADLE_LAYER_TOOLTIP)));

        addWidget(new ButtonWidget(sceneX + sceneSize - 50, sceneY, 20, 20,
                new TextTexture(tr(LangDefs.CRADLE_TOGGLE_LABEL))
                        .setSupplier(() -> showCradle
                                ? tr(LangDefs.CRADLE_ON)
                                : tr(LangDefs.CRADLE_OFF)),
                b -> toggleCradle())
                .appendHoverTooltips(comp(LangDefs.CRADLE_TOGGLE_TOOLTIP)));

        int inputX = 125;
        int inputY = 20;
        addWidget(new LabelWidget(inputX - 8, inputY - 15, tr(LangDefs.CRADLE_INPUTS)));

        for (ItemStack stack : inputs) {
            addWidget(new SlotWidget(new ItemStackTransfer(stack), 0, inputX, inputY, false, false)
                    .setBackgroundTexture(SlotWidget.ITEM_SLOT_TEXTURE)
                    .setIngredientIO(IngredientIO.INPUT));
            inputY += 22;
        }

        int outputSlotX = sceneX + sceneSize / 2 - 9;
        int outputSlotY = sceneY + sceneSize + 15;
        addWidget(new LabelWidget(outputSlotX - 8, outputSlotY - 12, tr(LangDefs.CRADLE_OUTPUT)));
        addWidget(new SlotWidget(new ItemStackTransfer(output), 0, outputSlotX, outputSlotY, false, false)
                .setBackgroundTexture(SlotWidget.ITEM_SLOT_TEXTURE)
                .setIngredientIO(IngredientIO.OUTPUT));

        loadStructure();

        var font = Minecraft.getInstance().font;
        int lineHeight = font.lineHeight;
        int spacing = 2;
        String[] lines = Component.translatable(description).getString().split("\n");
        int totalTextHeight = (lines.length * lineHeight) + ((lines.length - 1) * spacing);
        int cy = (HEIGHT - totalTextHeight) / 2 + 60;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int cx = (WIDTH - font.width(line)) / 2;
            int currentY = cy + (i * (lineHeight + spacing));
            addWidget(new LabelWidget(cx, currentY, line));
        }
    }

    private void toggleCradle() {
        float rotationPitch = sceneWidgetAll.getRotationPitch();
        float rotationYaw = sceneWidgetAll.getRotationYaw();
        float scale = sceneWidgetAll.getZoom();
        var center = sceneWidgetAll.getCenter();
        showCradle = !showCradle;

        Map<BlockPos, BlockInfo> combined = new HashMap<>(baseBlocks);
        if (showCradle && cradleBlocks != null) {
            combined.putAll(cradleBlocks);
        }
        world.clear();
        world.addBlocks(combined);

        Set<BlockPos> combinedPositions = new HashSet<>(baseBlocks.keySet());
        if (showCradle && cradleBlocks != null) {
            combinedPositions.addAll(cradleBlocks.keySet());
        }
        allPositions = combinedPositions;

        sceneWidgetAll.setRenderedCore(allPositions, null);
        sceneWidgetAll.setCameraYawAndPitch(rotationYaw, rotationPitch);
        sceneWidgetAll.setZoom(scale);
        sceneWidgetAll.setCenter(center);
        updateRenderedLayer();
    }

    private void switchLayer() {
        layer++;
        if (layer > (maxY - minY)) {
            layer = -1;
        }
        updateRenderedLayer();
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    private void updateRenderedLayer() {
        if (layer == -1) {
            sceneWidgetAll.setVisible(true);
            sceneWidgetLayer.setVisible(false);
        } else {
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
    }

    private void loadStructure() {
        try {
            var level = Minecraft.getInstance().level;
            if (level == null) {
                LogUtils.getLogger().warn("No client level available to load cradle recipe {}", structureId);
                return;
            }

            CradleRecipe recipe = level.getRecipeManager()
                    .getAllRecipesFor(InsaneRecipes.CRADLE_TYPE.get())
                    .stream()
                    .filter(cr -> cr.getId().equals(structureId))
                    .findFirst()
                    .orElse(null);

            if (recipe == null) {
                LogUtils.getLogger().warn("Cradle recipe not found for id {}", structureId);
                return;
            }

            Map<BlockPos, BlockInfo> innerBlockMap = buildBlocksFromPattern(recipe.pattern());

            baseBlocks.clear();
            baseBlocks.putAll(innerBlockMap);
            allPositions = new HashSet<>(innerBlockMap.keySet());
            recomputeBounds();

            world.clear();
            world.addBlocks(innerBlockMap);
            sceneWidgetAll.setRenderedCore(allPositions, null);
            updateRenderedLayer();
        } catch (Exception e) {
            LogUtils.getLogger().warn("Failed to load cradle pattern from recipe {}: {}", structureId, e.toString());
        }

        if (cradleBlocks == null) {
            cradleBlocks = buildCradleShell();
        }
    }

    private void recomputeBounds() {
        boolean first = true;
        for (BlockPos pos : baseBlocks.keySet()) {
            if (first) {
                minY = maxY = pos.getY();
                first = false;
            } else {
                minY = Math.min(minY, pos.getY());
                maxY = Math.max(maxY, pos.getY());
            }
        }
    }

    private Map<BlockPos, BlockInfo> buildBlocksFromPattern(CradlePattern pattern) {
        Map<BlockPos, BlockInfo> blockMap = new HashMap<>();

        Map<String, List<Block>> symbols = pattern.symbolMap();
        List<String[][]> layers = pattern.layers();

        for (int y = 0; y < CradlePattern.SIZE; y++) {
            String[][] layer = layers.get(y);
            for (int z = 0; z < CradlePattern.SIZE; z++) {
                String[] row = layer[z];
                for (int x = 0; x < CradlePattern.SIZE; x++) {
                    String symbol = row[x];
                    if (symbol.equals(".")) {
                        continue;
                    }

                    List<Block> options = symbols.get(symbol);
                    Block chosen = (options != null && !options.isEmpty()) ? options.get(0) : Blocks.AIR;
                    if (chosen == Blocks.AIR) {
                        continue;
                    }

                    BlockPos pos = new BlockPos(x - 2, y + 1, z - 7);
                    blockMap.put(pos, BlockInfo.fromBlockState(chosen.defaultBlockState()));
                }
            }
        }
        return blockMap;
    }

    private Map<BlockPos, BlockInfo> buildCradleShell() {
        Map<BlockPos, BlockInfo> shell = new HashMap<>();
        try {
            MultiblockDefinition definition = InsaneMultiblocks.entropyCradle();

            for (MultiblockDefinition.PatternEntry entry : definition.getEntries(Direction.NORTH)) {
                MultiblockDefinition.SymbolDef symbolDef = definition.getSymbol(entry.symbol());
                if (symbolDef == null || symbolDef.blocks().isEmpty()) {
                    continue;
                }

                BlockState state = formed(symbolDef.blocks().get(0).defaultBlockState());
                shell.put(new BlockPos(entry.relX(), entry.relY(), entry.relZ()), BlockInfo.fromBlockState(state));
            }

            BlockState controller = formed(InsaneBlockRegistrar.ENTROPY_CRADLE_CONTROLLER_BLOCK.get().defaultBlockState());
            shell.put(new BlockPos(0, 0, 0), BlockInfo.fromBlockState(controller));
        } catch (Exception e) {
            LogUtils.getLogger().warn("Failed to build cradle shell overlay: {}", e.toString());
        }
        return shell;
    }

    private static String tr(LangDefs def) {
        return Component.translatable(def.getTranslationKey()).getString();
    }

    private static Component comp(LangDefs def) {
        return Component.translatable(def.getTranslationKey());
    }

    private static BlockState formed(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof EntropyCradleBlock) {
            return state.setValue(EntropyCradleBlock.FORMED, true);
        }
        if (block instanceof EntropyCradleCapacitorBlock) {
            return state.setValue(EntropyCradleCapacitorBlock.FORMED, true);
        }
        if (block instanceof EntropyCradleControllerBlock) {
            return state.setValue(EntropyCradleControllerBlock.FORMED, true);
        }
        return state;
    }
}
