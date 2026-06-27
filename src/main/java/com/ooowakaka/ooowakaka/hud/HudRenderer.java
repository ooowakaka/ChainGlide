package com.ooowakaka.ooowakaka.hud;

import com.ooowakaka.ooowakaka.feature.Feature;
import com.ooowakaka.ooowakaka.feature.FeatureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.*;

public final class HudRenderer {

    public enum ColorTheme {
        PINK("粉色", 0.83f), BLUE("蓝色", 0.58f), GREEN("绿色", 0.33f),
        CYAN("青色", 0.50f), RED("红色", 0.00f), PURPLE("紫色", 0.75f),
        RAINBOW("彩虹", -1f);
        public final String name; public final float hue;
        ColorTheme(String name, float hue) { this.name = name; this.hue = hue; }
    }

    private static boolean visible = true;
    private static ColorTheme theme = ColorTheme.PINK;
    private static float colorSpeed = 1.0f;
    private static float colorTick;
    private static long lastFrameTime;

    private static final ArrayList<Feature> activeHacks = new ArrayList<>();
    private static final Set<Feature> fadingOutHacks = new HashSet<>();
    private static final Map<Feature, AnimTimer> timers = new HashMap<>();
    private static RenderEntry[] cachedEntries = new RenderEntry[0];
    private static boolean needsRebuild;

    private HudRenderer() {}

    public static void updateState(Feature hack) {
        needsRebuild = true;
        AnimTimer timer = new AnimTimer();
        timer.start();
        timers.put(hack, timer);
        if (hack.isEnabled()) {
            if (!activeHacks.contains(hack)) activeHacks.add(hack);
            fadingOutHacks.remove(hack);
        } else if (activeHacks.contains(hack)) {
            fadingOutHacks.add(hack);
        }
    }

    public static void onClientTick() {
        if (!visible) return;
        if (needsRebuild) { rebuild(); needsRebuild = false; }
        cleanupTimers();
    }

    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (!visible) return;
        RenderEntry[] entries = cachedEntries;
        if (entries.length == 0) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        GuiGraphics ctx = event.getGuiGraphics();
        Font font = mc.font;
        int screenW = mc.getWindow().getGuiScaledWidth();

        ctx.pose().pushPose();
        ctx.pose().scale(1.0f, 1.0f, 1);

        long now = System.currentTimeMillis();
        float dt = lastFrameTime == 0 ? 0.016f : Math.min((now - lastFrameTime) * 0.001f, 0.1f);
        lastFrameTime = now;
        colorTick += dt * colorSpeed;
        if (colorTick > 100f) colorTick -= 100f;

        float y = 2f;
        int stagger = 0;

        for (RenderEntry entry : entries) {
            Feature f = entry.hack;
            boolean fading = !f.isEnabled();
            AnimTimer t = timers.get(f);

            float anim;
            if (t != null && t.last > 0) {
                float raw = t.getValue();
                anim = fading ? Math.max(0f, 1f - raw) : Math.min(1f, raw);
            } else {
                anim = fading ? 0f : 1f;
            }
            if (anim <= 0.001f) continue;

            float xSlide = (1f - anim) * entry.totalWidth;
            float drawX = screenW - entry.totalWidth - 2f + xSlide;
            float drawY = y;
            int alpha = (int) (anim * 255);

            int rgb = color(stagger);
            int textColor = (alpha << 24) | (rgb & 0x00FFFFFF);
            int barColor = (alpha << 24) | (rgb & 0x00FFFFFF);

            ctx.fill((int) drawX - 3, (int) drawY - 1, (int) drawX - 2, (int) (drawY + font.lineHeight + 1), barColor);
            ctx.drawString(font, entry.name, (int) drawX, (int) drawY, textColor, false);

            y += font.lineHeight + 2f;
            stagger++;
        }
        ctx.pose().popPose();
    }

    private static void rebuild() {
        List<Feature> renderList = new ArrayList<>(activeHacks);
        for (Feature f : fadingOutHacks) if (!renderList.contains(f)) renderList.add(f);
        if (renderList.isEmpty()) { cachedEntries = new RenderEntry[0]; return; }
        Font font = Minecraft.getInstance().font;
        renderList.sort(Comparator.comparingInt(f -> -font.width(f.getName())));
        RenderEntry[] arr = new RenderEntry[renderList.size()];
        for (int i = 0; i < arr.length; i++) {
            Feature f = renderList.get(i);
            arr[i] = new RenderEntry(f, font.width(f.getName()));
        }
        cachedEntries = arr;
    }

    private static void cleanupTimers() {
        Iterator<Map.Entry<Feature, AnimTimer>> it = timers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Feature, AnimTimer> e = it.next();
            Feature f = e.getKey();
            AnimTimer t = e.getValue();
            if (t.cached == 1f || System.currentTimeMillis() - t.last > 500) {
                it.remove();
                if (!f.isEnabled()) { fadingOutHacks.remove(f); activeHacks.remove(f); needsRebuild = true; }
            }
        }
    }

    private static int color(int offset) {
        float t = colorTick + offset * 0.5f;
        if (theme.hue < 0) return hsb((t * 0.6f) % 1f, 1f, 1f, 255);
        return hsb(theme.hue, 0.6f, 0.85f + 0.15f * (float) Math.sin(t * 2.0), 255);
    }

    private static int hsb(float hue, float sat, float bri, int alpha) {
        float r, g, b; int hi = (int)(hue * 6) % 6; float f = hue * 6 - hi;
        float p = bri * (1 - sat), q = bri * (1 - f * sat), t = bri * (1 - (1 - f) * sat);
        switch (hi) { case 0: r = bri; g = t; b = p; break; case 1: r = q; g = bri; b = p; break;
            case 2: r = p; g = bri; b = t; break; case 3: r = p; g = q; b = bri; break;
            case 4: r = t; g = p; b = bri; break; default: r = bri; g = p; b = q; break; }
        return (clamp(alpha) << 24) | (clamp((int)(r*255)) << 16) | (clamp((int)(g*255)) << 8) | clamp((int)(b*255));
    }
    private static int clamp(int v) { return Math.max(0, Math.min(255, v)); }

    public static boolean isVisible() { return visible; }
    public static void setVisible(boolean v) { visible = v; }
    public static ColorTheme getTheme() { return theme; }
    public static void setTheme(ColorTheme t) { theme = t; }
    public static void setColorSpeed(float s) { colorSpeed = Math.max(0.1f, Math.min(50f, s)); }
    public static float getColorSpeed() { return colorSpeed; }

    private static class RenderEntry {
        final Feature hack; final String name; final float totalWidth;
        RenderEntry(Feature hack, float w) { this.hack = hack; this.name = hack.getName(); this.totalWidth = w; }
    }

    private static class AnimTimer {
        long last; float cached;
        void start() { cached = 0f; last = System.currentTimeMillis(); }
        float getValue() {
            if (cached == 1f) return cached;
            float t = (float)(System.currentTimeMillis() - last) / 250f;
            if (t > 1f) t = 1f; if (t == 1f) cached = t;
            return t;
        }
    }
}
