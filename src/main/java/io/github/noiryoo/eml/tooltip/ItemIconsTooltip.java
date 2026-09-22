package io.github.noiryoo.eml.tooltip;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class ItemIconsTooltip implements ClientTooltipComponent {
    private static final float SCALE = 0.75f;
    private static final int COLUMNS = 13;
    private static final int CELL_SIZE = 14;
    private static final int INDENT = 8;
    private static final int SHIFT_GAP = 3;
    private static final Component SHIFT_LABEL = Component.literal("[Shift]").withStyle(ChatFormatting.GRAY);
    private final List<ItemStack> items;
    private final boolean hasMore;

    public ItemIconsTooltip(List<ItemStack> items, boolean hasMore) {
        this.items = List.copyOf(items);
        this.hasMore = hasMore;
    }

    public ItemIconsTooltip(List<ItemStack> items) {
        this(items, false);
    }

    @Override
    public int getHeight(Font font) {
        int total = items.size() + (hasMore ? 1 : 0);
        return Math.max(1, Math.ceilDiv(total, COLUMNS)) * CELL_SIZE;
    }

    @Override
    public int getWidth(Font font) {
        if (items.isEmpty()) {
            return hasMore ? INDENT + font.width(SHIFT_LABEL) : 0;
        }
        int shiftWidth = hasMore ? SHIFT_GAP + font.width(SHIFT_LABEL) : 0;
        return INDENT + Math.min(items.size(), COLUMNS) * CELL_SIZE + shiftWidth - 2;
    }

    @Override
    public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
        if (hasMore) {
            int shiftCol = items.size() % COLUMNS;
            int shiftRow = items.size() / COLUMNS;
            int shiftX = x + INDENT + shiftCol * CELL_SIZE + SHIFT_GAP;
            int shiftY = y + shiftRow * CELL_SIZE + (CELL_SIZE - font.lineHeight) / 2 + 1;
            graphics.text(font, SHIFT_LABEL, shiftX, shiftY, 0xFFAAAAAA);
        }
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        for (int index = 0; index < items.size(); index++) {
            int itemX = x + INDENT + (index % COLUMNS) * CELL_SIZE;
            int itemY = y + (index / COLUMNS) * CELL_SIZE;
            graphics.pose().pushMatrix();
            graphics.pose().translate(itemX, itemY);
            graphics.pose().scale(SCALE, SCALE);
            graphics.item(items.get(index), 0, 0);
            graphics.pose().popMatrix();
        }
    }
}
