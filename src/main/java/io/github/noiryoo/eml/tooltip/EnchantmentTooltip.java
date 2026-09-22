package io.github.noiryoo.eml.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class EnchantmentTooltip {
    private EnchantmentTooltip() {}

    public static boolean isDetailedView() {
        return RenderSystem.isOnRenderThread() && Minecraft.getInstance().hasShiftDown();
    }

    public static boolean hasHiddenDetails(ItemEnchantments enchantments) {
        for (Holder<Enchantment> enchantment : enchantments.keySet()) {
            if (hasHiddenDetails(enchantment)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasHiddenDetails(Holder<Enchantment> enchantment) {
        if (EnchantmentApplicability.hasMoreIcons(enchantment.value())) {
            return true;
        }
        return enchantment.unwrapKey().map(key -> {
            var id = key.identifier();
            String extraKey = "enchantment." + id.getNamespace() + "." + id.getPath().replace('/', '.') + ".desc.extra";
            return Language.getInstance().has(extraKey);
        }).orElse(false);
    }

    public static void appendDetails(
            Holder<Enchantment> enchantment,
            Consumer<Component> tooltip,
            boolean detailed) {
        enchantment.unwrapKey().ifPresent(key -> {
            var id = key.identifier();
            String prefix = "enchantment." + id.getNamespace() + "." + id.getPath().replace('/', '.');
            String descKey = prefix + ".desc";
            if (Language.getInstance().has(descKey)) {
                appendWrapped(Component.translatable(descKey), ChatFormatting.GRAY, tooltip);
            }
            if (detailed) {
                String extraKey = prefix + ".desc.extra";
                if (Language.getInstance().has(extraKey)) {
                    appendWrapped(Component.translatable(extraKey), ChatFormatting.GRAY, tooltip);
                }
            }
        });

        boolean hasMore = !detailed && EnchantmentApplicability.hasMoreIcons(enchantment.value());
        var items = EnchantmentApplicability.iconStacks(enchantment.value(), !detailed);
        if (!items.isEmpty()) {
            tooltip.accept(new ItemIconsLine(items, hasMore, EnchantmentApplicability.describe(enchantment.value())));
        } else if (detailed) {
            appendWrapped(EnchantmentApplicability.describe(enchantment.value()), ChatFormatting.GRAY, tooltip);
        }
    }

    public static void appendHoldShiftHint(Consumer<Component> tooltip) {
        tooltip.accept(Component.translatable("eml.tooltip.hold_shift").withStyle(ChatFormatting.DARK_GRAY));
    }

    private static void appendWrapped(Component text, ChatFormatting color, Consumer<Component> tooltip) {
        if (!RenderSystem.isOnRenderThread()) {
            tooltip.accept(text.copy().withStyle(color));
            return;
        }
        Minecraft.getInstance().font.getSplitter().splitLines(text, 210, Style.EMPTY)
                .forEach(line -> tooltip.accept(Component.literal("  " + line.getString()).withStyle(color)));
    }
}
