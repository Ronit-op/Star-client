package dev.starclient.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * Star Client dashboard foundation.
 * Layout coordinates intentionally use the established 620x360 design space.
 */
public class ClickGUI extends Screen {

    private static final int W = 620;
    private static final int H = 360;

    private static final int BG = 0xF00D0B18;
    private static final int SIDEBAR = 0xFF151224;
    private static final int PANEL = 0xFF1B1730;
    private static final int CARD = 0xFF211B39;
    private static final int CARD_HOVER = 0xFF2B2050;
    private static final int BORDER = 0xFF493078;
    private static final int ACCENT = 0xFF9B45FF;
    private static final int ACCENT_BRIGHT = 0xFFC06BFF;
    private static final int WHITE = 0xFFF7F1FF;
    private static final int MUTED = 0xFFB5A9D0;
    private static final int OFF = 0xFF514A63;

    private static final String[] SECTIONS = {"Mods", "Edit", "UI", "Profiles"};
    private static final String[] MODULES = {
        "Sprint", "Fullbright", "Potion HUD",
        "Armor HUD", "Motion Blur", "Coordinates",
        "Keystrokes", "CPS Counter"
    };

    private final boolean[] enabled = new boolean[MODULES.length];
    private int selectedSection = 0;
    private int scrollOffset = 0;
    private double scale = 1.0;
    private int panelX;
    private int panelY;
    private int mouseLocalX;
    private int mouseLocalY;

    public ClickGUI() {
        super(Component.literal("Star Client"));
        enabled[0] = true;
        enabled[2] = true;
        enabled[3] = true;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0x99060410);

        // Keep the exact design-space proportions used by the existing prototype.
        scale = Math.min((this.width - 30.0) / W, (this.height - 30.0) / H);
        scale = Math.max(0.1, scale);
        int panelWidth = (int) (W * scale);
        int panelHeight = (int) (H * scale);
        panelX = (this.width - panelWidth) / 2;
        panelY = (this.height - panelHeight) / 2;
        mouseLocalX = (int) ((mouseX - panelX) / scale);
        mouseLocalY = (int) ((mouseY - panelY) / scale);

        rounded(graphics, 0, 0, W, H, 12, BG);
        rounded(graphics, 0, 0, 78, H, 12, SIDEBAR);
        fill(graphics, 72, 0, 78, H, SIDEBAR);
        fill(graphics, 78, 42, W, 43, 0xFF302544);
        fill(graphics, 78, 82, W, 83, 0xFF302544);

        drawLogo(graphics);
        drawHeader(graphics);
        drawNavigation(graphics);

        switch (selectedSection) {
            case 0 -> renderModules(graphics);
            case 1 -> renderEdit(graphics);
            case 2 -> renderUi(graphics);
            case 3 -> renderProfiles(graphics);
            default -> renderModules(graphics);
        }

        // Subtle outer frame.
        outline(graphics, 0, 0, W, H, 12, BORDER);
    }

    private void drawLogo(GuiGraphics g) {
        rounded(g, 12, 9, 54, 54, 12, 0xFF1D1634);
        centered(g, "✦", 39, 18, 0xFFC45BFF);
    }

    private void drawHeader(GuiGraphics g) {
        text(g, "STAR CLIENT", 96, 13, WHITE);
        text(g, "—", 570, 13, MUTED);
        text(g, "□", 590, 13, MUTED);
        text(g, "×", 606, 13, MUTED);

        int[] tabX = {92, 190, 288, 386};
        for (int i = 0; i < SECTIONS.length; i++) {
            boolean active = selectedSection == i;
            int x = tabX[i];
            rounded(g, x, 49, 94, 25, 7, active ? 0xFF34204F : PANEL);
            if (active) {
                fill(g, x + 5, 72, x + 89, 74, ACCENT);
            }
            centered(g, tabLabel(i), x + 47, 56, active ? WHITE : MUTED);
        }

        rounded(g, 490, 49, 118, 25, 13, PANEL);
        outline(g, 490, 49, 118, 25, 13, BORDER);
        text(g, "⌕  Search", 502, 56, MUTED);
    }

    private String tabLabel(int index) {
        return switch (index) {
            case 0 -> "MODS";
            case 1 -> "EDIT";
            case 2 -> "UI";
            case 3 -> "PROFILES";
            default -> "";
        };
    }

    private void drawNavigation(GuiGraphics g) {
        String[] icons = {"▤", "✥", "●", "▱"};
        for (int i = 0; i < SECTIONS.length; i++) {
            int y = 76 + i * 66;
            boolean active = selectedSection == i;
            rounded(g, 7, y, 64, 57, 9, active ? 0xFF30204B : 0xFF1B172B);
            if (active) {
                outline(g, 7, y, 64, 57, 9, ACCENT);
                fill(g, 7, y + 8, 9, y + 49, ACCENT_BRIGHT);
            }
            centered(g, icons[i], 39, y + 8, active ? ACCENT_BRIGHT : MUTED);
            centered(g, SECTIONS[i].toUpperCase(), 39, y + 34, active ? WHITE : MUTED);
        }
    }

    private void renderModules(GuiGraphics g) {
        text(g, "YOUR MODULES", 96, 91, WHITE);
        text(g, "Enable modules and open their settings", 96, 106, MUTED);

        int cardW = 158;
        int cardH = 91;
        int startX = 96;
        int startY = 128;
        int gapX = 12;
        int gapY = 12;
        int visibleRows = 2;

        for (int visible = 0; visible < 6; visible++) {
            int index = visible + scrollOffset;
            if (index >= MODULES.length) break;
            int col = visible % 3;
            int row = visible / 3;
            int x = startX + col * (cardW + gapX);
            int y = startY + row * (cardH + gapY);
            drawModuleCard(g, x, y, cardW, cardH, index);
        }

        if (MODULES.length > 6) {
            rounded(g, 606, 128, 3, 194, 2, 0xFF35294B);
            rounded(g, 606, 128 + scrollOffset * 10, 3, 54, 2, ACCENT);
        }
    }

    private void drawModuleCard(GuiGraphics g, int x, int y, int w, int h, int index) {
        boolean hovered = mouseLocalX >= x && mouseLocalX <= x + w
            && mouseLocalY >= y && mouseLocalY <= y + h;
        rounded(g, x, y, w, h, 9, hovered ? CARD_HOVER : CARD);
        outline(g, x, y, w, h, 9, hovered ? ACCENT : 0xFF33264F);
        centered(g, moduleIcon(index), x + 25, y + 18, ACCENT_BRIGHT);
        text(g, MODULES[index], x + 48, y + 15, WHITE);
        text(g, "⚙", x + 12, y + 62, MUTED);
        drawToggle(g, x + w - 43, y + 62, enabled[index]);
    }

    private String moduleIcon(int index) {
        return switch (index) {
            case 0 -> "➤";
            case 1 -> "☼";
            case 2 -> "♜";
            case 3 -> "⬟";
            case 4 -> "◈";
            case 5 -> "⌖";
            case 6 -> "⌨";
            default -> "◎";
        };
    }

    private void drawToggle(GuiGraphics g, int x, int y, boolean on) {
        rounded(g, x, y, 32, 15, 8, on ? ACCENT : OFF);
        rounded(g, x + (on ? 18 : 2), y + 2, 11, 11, 6, WHITE);
    }

    private void renderEdit(GuiGraphics g) {
        text(g, "HUD EDITOR", 96, 101, WHITE);
        text(g, "Move and arrange your HUD elements in the Minecraft world.", 96, 124, MUTED);
        rounded(g, 96, 151, 500, 145, 10, PANEL);
        outline(g, 96, 151, 500, 145, 10, BORDER);
        centered(g, "HUD POSITIONING EDITOR", 346, 202, ACCENT_BRIGHT);
        centered(g, "The floating CLIENT return button will appear in Edit mode.", 346, 222, MUTED);
    }

    private void renderUi(GuiGraphics g) {
        text(g, "APPEARANCE", 96, 94, WHITE);
        rounded(g, 96, 119, 270, 205, 10, PANEL);
        outline(g, 96, 119, 270, 205, 10, BORDER);
        text(g, "Accent Color", 112, 137, WHITE);
        text(g, "Choose your favorite color", 112, 155, MUTED);
        int[] colors = {0xFF9B45FF, 0xFFE319A5, 0xFF168DE8, 0xFF08C3D8, 0xFF36CF55, 0xFFFFC928, 0xFFFF8B24, 0xFFEF465B};
        for (int i = 0; i < colors.length; i++) {
            rounded(g, 112 + i * 29, 183, 20, 20, 10, colors[i]);
        }
        text(g, "Interface Settings", 112, 225, WHITE);
        text(g, "Blur Background          ON", 112, 251, MUTED);
        text(g, "Rounded Corners          ON", 112, 270, MUTED);
        text(g, "Show Module Icons        ON", 112, 289, MUTED);

        rounded(g, 380, 119, 216, 88, 10, PANEL);
        outline(g, 380, 119, 216, 88, 10, BORDER);
        text(g, "Animations", 397, 139, WHITE);
        text(g, "Smooth interface motion", 397, 158, MUTED);
        drawToggle(g, 550, 151, true);

        rounded(g, 380, 220, 216, 104, 10, PANEL);
        outline(g, 380, 220, 216, 104, 10, BORDER);
        text(g, "Theme", 397, 240, WHITE);
        text(g, "Dark", 397, 262, MUTED);
        text(g, "Interface Scale", 397, 285, WHITE);
        text(g, "100%", 550, 285, MUTED);
    }

    private void renderProfiles(GuiGraphics g) {
        text(g, "PROFILES", 96, 101, WHITE);
        text(g, "Save and load your preferred client configurations.", 96, 124, MUTED);
        String[] names = {"PvP", "Survival", "Performance"};
        for (int i = 0; i < names.length; i++) {
            int y = 151 + i * 54;
            rounded(g, 96, y, 500, 43, 9, CARD);
            outline(g, 96, y, 500, 43, 9, BORDER);
            text(g, names[i], 113, y + 14, WHITE);
            text(g, "LOAD", 548, y + 14, ACCENT_BRIGHT);
        }
    }

    private void rounded(GuiGraphics g, int x, int y, int w, int h, int r, int color) {
        if (w <= 0 || h <= 0) return;
        int radius = Math.max(0, Math.min(r, Math.min(w / 2, h / 2)));
        g.fill(px(x + radius), py(y), px(x + w - radius), py(y + h), color);
        for (int i = 0; i < radius; i++) {
            int inset = radius - (int) Math.floor(Math.sqrt(radius * radius - (radius - i) * (radius - i)));
            g.fill(px(x + inset), py(y + i), px(x + w - inset), py(y + i + 1), color);
            g.fill(px(x + inset), py(y + h - i - 1), px(x + w - inset), py(y + h - i), color);
        }
    }

    private void outline(GuiGraphics g, int x, int y, int w, int h, int r, int color) {
        rounded(g, x, y, w, 1, 0, color);
        rounded(g, x, y + h - 1, w, 1, 0, color);
        rounded(g, x, y, 1, h, 0, color);
        rounded(g, x + w - 1, y, 1, h, 0, color);
    }

    private void fill(GuiGraphics g, int x1, int y1, int x2, int y2, int color) {
        g.fill(px(x1), py(y1), px(x2), py(y2), color);
    }

    private void text(GuiGraphics g, String value, int x, int y, int color) {
        g.drawString(this.font, value, px(x), py(y), color, false);
    }

    private void centered(GuiGraphics g, String value, int x, int y, int color) {
        g.drawCenteredString(this.font, value, px(x), py(y), color);
    }

    private int px(int x) { return panelX + (int) (x * scale); }
    private int py(int y) { return panelY + (int) (y * scale); }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            double x = (event.x() - panelX) / scale;
            double y = (event.y() - panelY) / scale;

            int[] tabX = {92, 190, 288, 386};
            for (int i = 0; i < tabX.length; i++) {
                if (x >= tabX[i] && x <= tabX[i] + 94 && y >= 49 && y <= 74) {
                    selectedSection = i;
                    return true;
                }
            }

            for (int i = 0; i < SECTIONS.length; i++) {
                int itemY = 76 + i * 66;
                if (x >= 7 && x <= 71 && y >= itemY && y <= itemY + 57) {
                    selectedSection = i;
                    return true;
                }
            }

            if (selectedSection == 0) {
                int cardW = 158;
                int cardH = 91;
                int startX = 96;
                int startY = 128;
                int gapX = 12;
                int gapY = 12;
                for (int visible = 0; visible < 6; visible++) {
                    int index = visible + scrollOffset;
                    if (index >= MODULES.length) break;
                    int col = visible % 3;
                    int row = visible / 3;
                    int cardX = startX + col * (cardW + gapX);
                    int cardY = startY + row * (cardH + gapY);
                    if (x >= cardX && x <= cardX + cardW && y >= cardY + 56 && y <= cardY + 84) {
                        enabled[index] = !enabled[index];
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
