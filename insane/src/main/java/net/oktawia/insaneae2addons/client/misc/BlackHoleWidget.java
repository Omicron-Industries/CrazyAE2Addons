package net.oktawia.insaneae2addons.client.misc;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

import lombok.Setter;

import appeng.client.gui.widgets.ITooltip;

import net.oktawia.crazyae2addons.util.Utils;
import net.oktawia.insaneae2addons.defs.LangDefs;

public class BlackHoleWidget extends AbstractWidget implements ITooltip {

    public record View(
            boolean active,
            long mass,
            long initialMass,
            long maxMass,
            double heat,
            double maxHeat,
            long diskMass,
            long generated,
            long consumed,
            long storedEnergy,
            long storedInDisk,
            long massDeltaPerSecond,
            double massEfficiency,
            double heatEfficiency) {
        public static final View EMPTY = new View(false, 0L, 0L, 1L, 0.0, 1.0, 0L, 0L, 0L, 0L, 0L, 0L, 0.0, 0.0);
    }

    private static final double DISK_FULL_AT_MU = 30_000.0;

    private static final int PAD = 6;
    private static final int BAR_HEIGHT = 14;
    private static final int BAR_GAP = 2;
    private static final int PREVIEW_TO_BARS_GAP = 4;

    private static final float BH_CENTER_X = 0.70f;
    private static final float BH_CENTER_Y = 0.50f;
    private static final float BH_HOLE_R_FACTOR = 0.46f / 3.0f;
    private static final float DISK_Y_SCALE = 0.86f;

    private static final int PANEL_BACKGROUND = 0xFF111217;
    private static final int PANEL_LIGHT = 0xFF2A2D3A;
    private static final int PANEL_DARK = 0xFF07080C;
    private static final int PREVIEW_BACKGROUND = 0xFF0B0C10;

    private static final int SEGMENTS = 320;
    private static final float RING_WIDTH = 1.8f;
    private static final float RING_OVERLAP = 0.55f;
    private static final int MAX_RINGS = 512;
    private static final float ARM_COUNT = 2.0f;
    private static final float TWIST_PER_PX = 0.95f;
    private static final float WARP_AMPLITUDE = 0.010f;
    private static final float RADIAL_HEAT_K = 6.2f;

    @Setter
    private View view = View.EMPTY;

    private int lastMouseX = Integer.MIN_VALUE;
    private int lastMouseY = Integer.MIN_VALUE;

    public BlackHoleWidget() {
        super(0, 0, 0, 0, Component.empty());
        this.active = true;
        this.visible = true;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible || this.width <= PAD * 2 || this.height <= PAD * 2) {
            return;
        }

        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;

        Layout layout = layout();

        drawPanel(graphics);

        graphics.fill(layout.previewX(), layout.previewY(),
                layout.previewX() + layout.previewW(), layout.previewY() + layout.previewH(), PREVIEW_BACKGROUND);
        graphics.fillGradient(layout.previewX(), layout.previewY(),
                layout.previewX() + layout.previewW(), layout.previewY() + layout.previewH(), 0x22000000, 0x88000000);
        graphics.fillGradient(layout.previewX(), layout.previewY(),
                layout.previewX() + layout.previewW(), layout.previewY() + layout.previewH() / 2, 0x11000000,
                0x00000000);

        Disk disk = disk(layout);
        drawAccretionDisk(graphics, disk, animationTime(partialTick));
        drawCircle(graphics, disk.centerX(), disk.centerY(), disk.shadowRadius(), 96, 0xFF000000);

        drawOverlayText(graphics, layout.previewX() + 8, layout.previewY() + 6);

        drawMassBar(graphics, layout, mouseX, mouseY);
        drawHeatBar(graphics, layout, mouseX, mouseY);

        if (contains(mouseX, mouseY, layout.previewX(), layout.previewY(), layout.previewW(), layout.previewH())) {
            graphics.fill(layout.previewX(), layout.previewY(),
                    layout.previewX() + layout.previewW(), layout.previewY() + 1, 0x55FFFFFF);
            graphics.fill(layout.previewX(), layout.previewY() + layout.previewH() - 1,
                    layout.previewX() + layout.previewW(), layout.previewY() + layout.previewH(), 0x22000000);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(getX(), getY(), this.width, this.height);
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return this.visible;
    }

    @Override
    public List<Component> getTooltipMessage() {
        if (this.lastMouseX == Integer.MIN_VALUE) {
            return List.of();
        }

        Layout layout = layout();

        if (contains(this.lastMouseX, this.lastMouseY, layout.barX(), layout.massBarY(), layout.barW(), BAR_HEIGHT)) {
            return massTooltip();
        }
        if (contains(this.lastMouseX, this.lastMouseY, layout.barX(), layout.heatBarY(), layout.barW(), BAR_HEIGHT)) {
            return heatTooltip();
        }
        if (!contains(this.lastMouseX, this.lastMouseY,
                layout.previewX(), layout.previewY(), layout.previewW(), layout.previewH())) {
            return List.of();
        }

        Disk disk = disk(layout);
        float dx = this.lastMouseX - disk.centerX();
        float dy = this.lastMouseY - disk.centerY();
        float distance = Mth.sqrt(dx * dx + dy * dy);

        if (distance <= disk.shadowRadius() * 1.03f) {
            return blackHoleTooltip();
        }
        if (distance >= disk.innerRadius() && distance <= disk.outerRadius()) {
            return diskTooltip();
        }
        return overviewTooltip();
    }

    private List<Component> blackHoleTooltip() {
        List<Component> lines = new ArrayList<>();
        lines.add(translate(LangDefs.PENROSE_TITLE_BLACK_HOLE).withStyle(ChatFormatting.GOLD));

        double ratio = this.view.active() && this.view.initialMass() > 0L
                ? (double) this.view.mass() / this.view.initialMass()
                : 0.0;
        lines.add(translate(LangDefs.PENROSE_BH_MASS_HEAT,
                String.format("%.4f", ratio), String.format("%.0f", this.view.heat())));

        double deltaRatio = this.view.active() && this.view.mass() > 0L
                ? (double) this.view.massDeltaPerSecond() / this.view.mass() * 100.0
                : 0.0;
        lines.add(translate(LangDefs.PENROSE_BH_MASS_DELTA,
                Utils.shortenNumber(this.view.massDeltaPerSecond()), String.format("%.4f%%", deltaRatio)));

        return lines;
    }

    private List<Component> diskTooltip() {
        List<Component> lines = new ArrayList<>();
        lines.add(translate(LangDefs.PENROSE_TITLE_ACCRETION_DISK).withStyle(ChatFormatting.GOLD));
        lines.add(translate(LangDefs.PENROSE_LINE_DISK_MASS,
                Utils.shortenNumber(this.view.diskMass())).withStyle(ChatFormatting.GRAY));
        lines.add(Component.empty());
        lines.add(translate(LangDefs.PENROSE_LINE_GEN,
                Utils.shortenNumber(this.view.generated())).withStyle(ChatFormatting.AQUA));
        lines.add(translate(LangDefs.PENROSE_LINE_USE,
                Utils.shortenNumber(this.view.consumed())).withStyle(ChatFormatting.RED));
        lines.add(translate(LangDefs.PENROSE_LINE_FE_IN_DISK,
                Utils.shortenNumber(this.view.storedInDisk())).withStyle(ChatFormatting.AQUA));
        return lines;
    }

    private List<Component> overviewTooltip() {
        List<Component> lines = new ArrayList<>();
        lines.add(translate(LangDefs.PENROSE_TITLE_PREVIEW).withStyle(ChatFormatting.GOLD));
        lines.add(translate(LangDefs.PENROSE_BH_POWER, Utils.shortenNumber(this.view.storedEnergy())));
        lines.add(translate(LangDefs.PENROSE_LINE_DISK_MASS,
                Utils.shortenNumber(this.view.diskMass())).withStyle(ChatFormatting.GRAY));
        lines.add(translate(LangDefs.PENROSE_LINE_HEAT,
                String.format("%.0f", this.view.heat())).withStyle(ChatFormatting.GRAY));
        lines.add(translate(LangDefs.PENROSE_LINE_GEN,
                Utils.shortenNumber(this.view.generated())).withStyle(ChatFormatting.AQUA));
        lines.add(translate(LangDefs.PENROSE_LINE_USE,
                Utils.shortenNumber(this.view.consumed())).withStyle(ChatFormatting.RED));
        return lines;
    }

    private List<Component> massTooltip() {
        List<Component> lines = new ArrayList<>();
        lines.add(translate(LangDefs.PENROSE_TITLE_MASS).withStyle(ChatFormatting.GOLD));
        lines.add(translate(LangDefs.PENROSE_MASS_CURRENT,
                Utils.shortenNumber(this.view.mass())).withStyle(ChatFormatting.GRAY));
        lines.add(translate(LangDefs.PENROSE_MASS_INITIAL,
                Utils.shortenNumber(this.view.initialMass())).withStyle(ChatFormatting.DARK_GRAY));
        lines.add(translate(LangDefs.PENROSE_MASS_MAX,
                Utils.shortenNumber(this.view.maxMass())).withStyle(ChatFormatting.DARK_GRAY));
        lines.add(translate(LangDefs.PENROSE_EFFICIENCY,
                String.format("%.1f%%", this.view.massEfficiency() * 100.0)).withStyle(ChatFormatting.GREEN));
        return lines;
    }

    private List<Component> heatTooltip() {
        List<Component> lines = new ArrayList<>();
        lines.add(translate(LangDefs.PENROSE_TITLE_HEAT).withStyle(ChatFormatting.GOLD));
        lines.add(translate(LangDefs.PENROSE_HEAT_CURRENT,
                String.format("%.0f", this.view.heat())).withStyle(ChatFormatting.GRAY));
        lines.add(translate(LangDefs.PENROSE_HEAT_MAX,
                String.format("%.0f", this.view.maxHeat())).withStyle(ChatFormatting.DARK_GRAY));
        lines.add(translate(LangDefs.PENROSE_EFFICIENCY,
                String.format("%.1f%%", this.view.heatEfficiency() * 100.0)).withStyle(ChatFormatting.GREEN));
        return lines;
    }

    private static MutableComponent translate(LangDefs entry, Object... args) {
        return Component.translatable(entry.getTranslationKey(), args);
    }

    private void drawPanel(GuiGraphics graphics) {
        int x = getX();
        int y = getY();

        graphics.fill(x + 2, y + 2, x + this.width + 2, y + this.height + 2, 0x55000000);
        graphics.fill(x, y, x + this.width, y + this.height, PANEL_BACKGROUND);
        graphics.fill(x, y, x + this.width, y + 1, PANEL_LIGHT);
        graphics.fill(x, y + this.height - 1, x + this.width, y + this.height, PANEL_DARK);
        graphics.fill(x, y, x + 1, y + this.height, PANEL_LIGHT);
        graphics.fill(x + this.width - 1, y, x + this.width, y + this.height, PANEL_DARK);
    }

    private void drawOverlayText(GuiGraphics graphics, int x, int y) {
        Font font = Minecraft.getInstance().font;

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(0.82f, 0.82f, 1.0f);

        int line = font.lineHeight + 3;
        int offset = 0;

        for (Component text : List.of(
                translate(LangDefs.PENROSE_OVERLAY_BH_MASS, Utils.shortenNumber(this.view.mass())),
                translate(LangDefs.PENROSE_OVERLAY_DISK_MASS, Utils.shortenNumber(this.view.diskMass())),
                translate(LangDefs.PENROSE_OVERLAY_GEN, Utils.shortenNumber(this.view.generated())),
                translate(LangDefs.PENROSE_OVERLAY_USE, Utils.shortenNumber(this.view.consumed())),
                translate(LangDefs.PENROSE_OVERLAY_FE_STORED, Utils.shortenNumber(this.view.storedEnergy())),
                translate(LangDefs.PENROSE_OVERLAY_FE_IN_DISK, Utils.shortenNumber(this.view.storedInDisk())))) {

            String value = text.getString();
            graphics.drawString(font, value, 1, offset + 1, 0xAA000000, false);
            graphics.drawString(font, value, 0, offset, 0xEDEDED, false);
            offset += line;
        }

        graphics.pose().popPose();
    }

    private void drawMassBar(GuiGraphics graphics, Layout layout, int mouseX, int mouseY) {
        double progress = massProgress();
        float hue = (float) (0.33 * (1.0 - progress));

        drawBar(graphics, layout.barX(), layout.massBarY(), layout.barW(), progress, hue, mouseX, mouseY,
                translate(LangDefs.PENROSE_MASS_BAR, String.format("%.2f%%", progress * 100.0)).getString());
    }

    private void drawHeatBar(GuiGraphics graphics, Layout layout, int mouseX, int mouseY) {
        double progress = this.view.maxHeat() <= 0.0
                ? 0.0
                : Mth.clamp(this.view.heat() / this.view.maxHeat(), 0.0, 1.0);
        float hue = (float) (0.58 * (1.0 - progress));

        drawBar(graphics, layout.barX(), layout.heatBarY(), layout.barW(), progress, hue, mouseX, mouseY,
                translate(LangDefs.PENROSE_HEAT_BAR,
                        String.format("%.0f", this.view.heat()),
                        String.format("%.0f", this.view.maxHeat())).getString());
    }

    private void drawBar(GuiGraphics graphics, int x, int y, int width, double progress, float hue,
            int mouseX, int mouseY, String label) {
        boolean hover = contains(mouseX, mouseY, x, y, width, BAR_HEIGHT);

        graphics.fill(x, y, x + width, y + BAR_HEIGHT, hover ? 0xFF1A1D27 : 0xFF141620);
        graphics.fill(x, y, x + width, y + 1, PANEL_LIGHT);
        graphics.fill(x, y + BAR_HEIGHT - 1, x + width, y + BAR_HEIGHT, PANEL_DARK);
        graphics.fill(x, y, x + 1, y + BAR_HEIGHT, PANEL_LIGHT);
        graphics.fill(x + width - 1, y, x + width, y + BAR_HEIGHT, PANEL_DARK);
        graphics.fill(x + 1, y + 1, x + width - 1, y + BAR_HEIGHT - 1, 0xFF2B2E38);

        int fill = (int) Math.floor((width - 2) * progress);
        if (fill > 0) {
            graphics.fillGradient(x + 1, y + 1, x + 1 + fill, y + BAR_HEIGHT - 1,
                    hsv(hue, 0.85f, 0.85f), hsv(hue, 1.0f, 1.0f));
            graphics.fillGradient(x + 1, y + 1, x + 1 + fill, y + 1 + BAR_HEIGHT / 2, 0x22FFFFFF, 0x00000000);
        }

        Font font = Minecraft.getInstance().font;
        int textX = x + (width - font.width(label)) / 2;
        int textY = y + (BAR_HEIGHT - font.lineHeight) / 2;
        graphics.drawString(font, label, textX + 1, textY + 1, 0xAA000000, false);
        graphics.drawString(font, label, textX, textY, 0xFFFFFFFF, false);
    }

    private void drawAccretionDisk(GuiGraphics graphics, Disk disk, float time) {
        float total = disk.outerRadius() - disk.innerRadius();
        if (diskFill() <= 0.0001f || total < 2.0f) {
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();

        float heat = this.view.maxHeat() <= 0.0
                ? 0.0f
                : (float) Mth.clamp(this.view.heat() / this.view.maxHeat(), 0.0, 1.0);
        float rotation = time * 0.26f;
        int baseAlpha = (int) Mth.clamp((this.view.active() ? 235 : 120) * (0.30f + 0.70f * diskFill()), 0f, 255f);

        int rings = Math.min(MAX_RINGS, Math.max(1, Mth.ceil(total / RING_WIDTH)));

        for (int ring = 0; ring < rings; ring++) {
            float from = Math.max(disk.innerRadius(), disk.innerRadius() + ring * RING_WIDTH - RING_OVERLAP * 0.5f);
            float to = Math.min(disk.outerRadius(), from + RING_WIDTH + RING_OVERLAP);
            if (from >= disk.outerRadius() || to - from <= 0.001f) {
                break;
            }

            float middle = Mth.clamp(((from + to) * 0.5f - disk.innerRadius()) / total, 0f, 1f);
            int alpha = (int) (baseAlpha * (1.0f - 0.55f * middle));

            buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
            for (int i = 0; i <= SEGMENTS; i++) {
                float angle = (float) (Math.PI * 2.0 * i / SEGMENTS);
                diskVertex(buffer, matrix, disk, angle, to, total, heat, rotation, (int) (alpha * 0.92f), 0.72f);
                diskVertex(buffer, matrix, disk, angle, from, total, heat, rotation, alpha, 1.0f);
            }
            BufferUploader.drawWithShader(buffer.end());
        }

        RenderSystem.disableBlend();
    }

    private void diskVertex(BufferBuilder buffer, Matrix4f matrix, Disk disk, float angle, float radius,
            float total, float heat, float rotation, int alpha, float intensityScale) {
        float radialPx = radius - disk.innerRadius();
        float radial = total <= 0.0001f ? 0f : Mth.clamp(radialPx / total, 0f, 1f);

        float radialHot = (float) Math.exp(-RADIAL_HEAT_K * radial * radial);
        float centerBoost = 0.70f - 0.25f * heat;
        float edgeCool = 0.10f + 0.28f * heat;
        float adjustedHeat = Mth.clamp(heat + centerBoost * radialHot - edgeCool * (1.0f - radialHot), 0f, 1f);

        float spiral = (angle * ARM_COUNT) - (radialPx * TWIST_PER_PX) + (rotation * 1.15f);
        float arm = (float) Math.pow(0.5f + 0.5f * Mth.sin(spiral), 3.0);
        float doppler = 0.55f + 0.45f * (float) Math.pow(Math.max(0.0f, Mth.cos(angle + rotation)), 2.2);

        float intensity = Mth.clamp(0.16f + 0.62f * doppler + 0.72f * arm, 0f, 1f)
                * (0.72f + 0.28f * (1.0f - radialHot));
        intensity = Mth.clamp(intensity * intensityScale, 0f, 1f);

        float warp = 1.0f + WARP_AMPLITUDE * Mth.sin(spiral) + (WARP_AMPLITUDE * 0.55f) * Mth.sin(spiral * 2.0f);
        int color = diskColor(adjustedHeat, radial, intensity, alpha);

        float x = disk.centerX() + Mth.cos(angle) * (radius * warp);
        float y = disk.centerY() + Mth.sin(angle) * (radius * warp) * DISK_Y_SCALE;

        buffer.vertex(matrix, x, y, 0)
                .color((color >>> 16) & 0xFF, (color >>> 8) & 0xFF, color & 0xFF, (color >>> 24) & 0xFF)
                .endVertex();
    }

    private static int diskColor(float heat, float radial, float intensity, int alpha) {
        int orange = 0xFFFF8C23;
        int red = 0xFFFF2D19;
        int purple = 0xFF9B37EB;
        int blue = 0xFF3CA0FF;
        int whiteHot = 0xFFFFF5EB;

        int base;
        if (heat < 0.35f) {
            base = lerpColor(orange, red, heat / 0.35f);
        } else if (heat < 0.75f) {
            base = lerpColor(red, purple, (heat - 0.35f) / 0.40f);
        } else {
            base = lerpColor(purple, blue, (heat - 0.75f) / 0.25f);
        }

        float shaded = (float) Math.pow(intensity, 0.90);
        float darken = 1.0f - 0.62f * (float) Math.pow(1.0f - shaded, 1.35);

        float core = (float) Math.exp(-6.0f * radial * radial);
        float hot = Mth.clamp((heat - 0.92f) / 0.08f, 0f, 1f);
        float coreMix = Mth.clamp(core + hot * 0.35f * core, 0f, 1f);

        base = lerpColor(base, whiteHot, coreMix);

        float brightness = Mth.clamp(
                (0.05f + 0.95f * Mth.lerp(shaded, 1.0f, coreMix)) * darken * (0.62f + 0.38f * (1.0f - radial)),
                0f, 1f);

        return argb(alpha,
                (int) (((base >>> 16) & 0xFF) * brightness),
                (int) (((base >>> 8) & 0xFF) * brightness),
                (int) ((base & 0xFF) * brightness));
    }

    private static void drawCircle(GuiGraphics graphics, float centerX, float centerY, float radius,
            int segments, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();

        int alpha = (color >>> 24) & 0xFF;
        int red = (color >>> 16) & 0xFF;
        int green = (color >>> 8) & 0xFF;
        int blue = color & 0xFF;

        buffer.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(matrix, centerX, centerY, 0).color(red, green, blue, alpha).endVertex();
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (Math.PI * 2.0 * i / segments);
            buffer.vertex(matrix, centerX + Mth.cos(angle) * radius, centerY + Mth.sin(angle) * radius, 0)
                    .color(red, green, blue, alpha)
                    .endVertex();
        }
        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
    }

    private float diskFill() {
        return (float) Mth.clamp(Math.max(0.0, this.view.diskMass()) / DISK_FULL_AT_MU, 0.0, 1.0);
    }

    private double massProgress() {
        long span = this.view.maxMass() - this.view.initialMass();
        if (!this.view.active() || span <= 0L) {
            return 0.0;
        }
        return Mth.clamp((double) (this.view.mass() - this.view.initialMass()) / span, 0.0, 1.0);
    }

    private static float animationTime(float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        long ticks = minecraft.level != null ? minecraft.level.getGameTime() : System.currentTimeMillis() / 50L;
        return (ticks % 24000L) + partialTick;
    }

    private Layout layout() {
        int barWidth = Math.max(20, this.width - PAD * 2);
        int heatBarY = getY() + this.height - PAD - BAR_HEIGHT;
        int massBarY = heatBarY - BAR_GAP - BAR_HEIGHT;
        int previewY = getY() + PAD;

        return new Layout(
                getX() + PAD, previewY, barWidth,
                Math.max(24, massBarY - PREVIEW_TO_BARS_GAP - previewY),
                getX() + PAD, massBarY, heatBarY, barWidth);
    }

    private Disk disk(Layout layout) {
        float centerX = layout.previewX() + layout.previewW() * BH_CENTER_X;
        float centerY = layout.previewY() + layout.previewH() * BH_CENTER_Y;

        float toLeft = centerX - layout.previewX();
        float toRight = (layout.previewX() + layout.previewW()) - centerX;
        float toTop = centerY - layout.previewY();
        float toBottom = (layout.previewY() + layout.previewH()) - centerY;

        float maxRadius = Math.min(Math.min(toLeft, toRight), Math.min(toTop, toBottom));
        if (maxRadius < 12f) {
            maxRadius = Math.min(layout.previewW(), layout.previewH()) * 0.48f;
        }

        float shadowRadius = maxRadius * BH_HOLE_R_FACTOR * 1.55f;
        float inner = shadowRadius * 1.02f;
        float outer = Math.min(maxRadius * 0.98f,
                inner + 1.2f + (maxRadius * 0.62f) * (float) Math.pow(diskFill(), 0.90));

        return new Disk(centerX, centerY, shadowRadius, inner, outer);
    }

    private static boolean contains(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    private static int lerpColor(int from, int to, float t) {
        t = Mth.clamp(t, 0f, 1f);
        return argb(
                Mth.lerpInt(t, (from >>> 24) & 0xFF, (to >>> 24) & 0xFF),
                Mth.lerpInt(t, (from >>> 16) & 0xFF, (to >>> 16) & 0xFF),
                Mth.lerpInt(t, (from >>> 8) & 0xFF, (to >>> 8) & 0xFF),
                Mth.lerpInt(t, from & 0xFF, to & 0xFF));
    }

    private static int hsv(float hue, float saturation, float value) {
        return 0xFF000000 | (Mth.hsvToRgb(((hue % 1f) + 1f) % 1f, saturation, value) & 0xFFFFFF);
    }

    private static int argb(int alpha, int red, int green, int blue) {
        return ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }

    private record Layout(int previewX, int previewY, int barW, int previewH,
            int barX, int massBarY, int heatBarY, int previewW) {
    }

    private record Disk(float centerX, float centerY, float shadowRadius, float innerRadius, float outerRadius) {
    }
}
