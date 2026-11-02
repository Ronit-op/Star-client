package dev.starclient.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ClickGUI extends Screen {

    private static final int BACKGROUND = 0xF0101218;
    private static final int SIDEBAR = 0xFF171A22;
    private static final int PANEL = 0xFF20242E;
    private static final int CARD = 0xFF292E3A;
    private static final int ACCENT = 0xFF8B72FF;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int MUTED = 0xFF9A9EAA;

    private static final String[] SECTIONS = {
        "Profiles", "UI", "Edit", "Mods"
    };

    private static final String[] MODULES = {
        "Sprint",
        "Fullbright",
        "Keystrokes",
        "Armor HUD",
        "Motion Blur",
        "Coordinates",
        "Potion HUD",
        "CPS Counter"
    };

    private int selectedSection = 3;
    private final boolean[] enabled = new boolean[MODULES.length];

    private double scale;
    private int panelX;
    private int panelY;

    public ClickGUI() {
        super(Component.literal("Star Client"));
    }

    @Override
    public void render(
        GuiGraphics graphics,
        int mouseX,
        int mouseY,
        float partialTick
    ) {
        graphics.fill(0, 0, this.width, this.height, 0x99000000);

        scale = Math.min(
            (this.width - 30.0) / 620.0,
            (this.height - 30.0) / 360.0
        );
        scale = Math.max(0.1, scale);

        int panelWidth = (int) (620 * scale);
        int panelHeight = (int) (360 * scale);

        panelX = (this.width - panelWidth) / 2;
        panelY = (this.height - panelHeight) / 2;

        // Main window and sidebar.
        fill(graphics, 0, 0, 620, 360, BACKGROUND);
        fill(graphics, 0, 0, 78, 360, SIDEBAR);

        // Logo.
        text(graphics, "S", 30, 22, ACCENT);

        // Sidebar navigation.
        for (int i = 0; i < SECTIONS.length; i++) {
            int itemY = 75 + i * 48;

            if (selectedSection == i) {
                fill(
                    graphics,
                    9, itemY - 8,
                    69, itemY + 19,
                    0xFF343044
                );
            }

            centeredText(
                graphics,
                SECTIONS[i],
                39,
                itemY,
                selectedSection == i ? ACCENT : MUTED
            );
        }

        // Header.
        text(graphics, "STAR CLIENT", 98, 19, WHITE);

        // Category tabs (visual placeholders for now).
        fill(graphics, 98, 48, 183, 75, PANEL);
        fill(graphics, 190, 48, 285, 75, PANEL);
        fill(graphics, 292, 48, 373, 75, PANEL);

        centeredText(graphics, "HUD", 140, 56, WHITE);
        centeredText(graphics, "Movement", 237, 56, WHITE);
        centeredText(graphics, "Visual", 332, 56, WHITE);

        // Search field (visual placeholder for now).
        fill(graphics, 390, 48, 600, 75, PANEL);
        text(graphics, "Search", 402, 56, MUTED);

        switch (selectedSection) {
            case 0 -> renderPlaceholder(
                graphics,
                "Profiles",
                "Save and load your configurations"
            );
            case 1 -> renderPlaceholder(
                graphics,
                "UI",
                "Customize colors, themes and interface settings"
            );
            case 2 -> renderPlaceholder(
                graphics,
                "Edit",
                "Customize and position your HUD"
            );
            case 3 -> renderModules(graphics);
            default -> {
            }
        }
    }

    private void renderModules(GuiGraphics graphics) {
        text(graphics, "Modules", 98, 94, WHITE);

        int cardWidth = 151;
        int cardHeight = 91;
        int startX = 98;
        int startY = 119;
        int gapX = 12;
        int gapY = 12;

        // Six modules are visible in this first layout milestone.
        for (int i = 0; i < 6; i++) {
            int column = i % 3;
            int row = i / 3;

            int x = startX + column * (cardWidth + gapX);
            int y = startY + row * (cardHeight + gapY);

            drawModuleCard(
                graphics,
                x,
                y,
                cardWidth,
                cardHeight,
                MODULES[i],
                enabled[i]
            );
        }

        // Placeholder scrollbar: real scrolling will be added later.
        if (MODULES.length > 6) {
            fill(graphics, 596, 119, 599, 325, 0xFF343044);
            fill(graphics, 596, 119, 599, 220, ACCENT);
        }
    }

    private void renderPlaceholder(
        GuiGraphics graphics,
        String heading,
        String description
    ) {
        text(graphics, heading, 98, 105, WHITE);
        text(graphics, description, 98, 133, MUTED);
    }

    private void drawModuleCard(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        int height,
        String name,
        boolean active
    ) {
        fill(graphics, x, y, x + width, y + height, CARD);

        text(graphics, name, x + 10, y + 12, WHITE);
        text(graphics, "Settings", x + 10, y + 62, MUTED);

        int toggleColor = active ? ACCENT : 0xFF454A58;

        fill(
            graphics,
            x + width - 43,
            y + 61,
            x + width - 11,
            y + 76,
            toggleColor
        );

        int knobX = active
            ? x + width - 23
            : x + width - 40;

        fill(
            graphics,
            knobX,
            y + 63,
            knobX + 11,
            y + 74,
            WHITE
        );
    }

    private void fill(
        GuiGraphics graphics,
        int x1,
        int y1,
        int x2,
        int y2,
        int color
    ) {
        graphics.fill(
            px(x1),
            py(y1),
            px(x2),
            py(y2),
            color
        );
    }

    private void text(
        GuiGraphics graphics,
        String value,
        int x,
        int y,
        int color
    ) {
        graphics.drawString(
            this.font,
            value,
            px(x),
            py(y),
            color,
            false
        );
    }

    private void centeredText(
        GuiGraphics graphics,
        String value,
        int centerX,
        int y,
        int color
    ) {
        graphics.drawCenteredString(
            this.font,
            value,
            px(centerX),
            py(y),
            color
        );
    }

    private int px(int value) {
        return panelX + (int) (value * scale);
    }

    private int py(int value) {
        return panelY + (int) (value * scale);
    }

    @Override
    public boolean mouseClicked(
        MouseButtonEvent event,
        boolean doubleClick
    ) {
        if (event.button() == 0) {
            double localX = (event.x() - panelX) / scale;
            double localY = (event.y() - panelY) / scale;

            // Sidebar navigation.
            for (int i = 0; i < SECTIONS.length; i++) {
                int itemY = 75 + i * 48;

                if (localX >= 9 && localX <= 69
                    && localY >= itemY - 8
                    && localY <= itemY + 19) {
                    selectedSection = i;
                    return true;
                }
            }

            // Prototype module toggles.
            if (selectedSection == 3) {
                int cardWidth = 151;
                int cardHeight = 91;
                int startX = 98;
                int startY = 119;
                int gapX = 12;
                int gapY = 12;

                for (int i = 0; i < 6; i++) {
                    int column = i % 3;
                    int row = i / 3;

                    int x = startX + column * (cardWidth + gapX);
                    int y = startY + row * (cardHeight + gapY);

                    if (localX >= x && localX <= x + cardWidth
                        && localY >= y + 55
                        && localY <= y + 82) {
                        enabled[i] = !enabled[i];
                        return true;
                    }
                }
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
