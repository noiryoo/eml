package io.github.noiryoo.eml.tooltip;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.world.item.ItemStack;

/** Carries item icons through the vanilla text-to-tooltip conversion. */
public record ItemIconsLine(List<ItemStack> items, boolean hasMore, Component fallback) implements Component, FormattedCharSequence {
    public ItemIconsLine {
        items = List.copyOf(items);
    }

    public ItemIconsLine(List<ItemStack> items, Component fallback) {
        this(items, false, fallback);
    }

    @Override
    public ComponentContents getContents() {
        return fallback.getContents();
    }

    @Override
    public List<Component> getSiblings() {
        return fallback.getSiblings();
    }

    @Override
    public Style getStyle() {
        return fallback.getStyle();
    }

    @Override
    public FormattedCharSequence getVisualOrderText() {
        return this;
    }

    @Override
    public boolean accept(FormattedCharSink sink) {
        // Text-only consumers, including search and narration, receive useful item names.
        return fallback.getVisualOrderText().accept(sink);
    }
}
