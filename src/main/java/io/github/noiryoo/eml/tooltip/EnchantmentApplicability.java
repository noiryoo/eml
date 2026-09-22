package io.github.noiryoo.eml.tooltip;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

public final class EnchantmentApplicability {
    private static final List<ItemGroup> GROUPS = List.of(
            new ItemGroup(ItemTags.DURABILITY_ENCHANTABLE, "durable_items", null),
            new ItemGroup(ItemTags.ARMOR_ENCHANTABLE, "armor", null),
            new ItemGroup(ItemTags.HEAD_ARMOR, "helmets", Items.IRON_HELMET),
            new ItemGroup(ItemTags.CHEST_ARMOR, "chestplates", Items.IRON_CHESTPLATE),
            new ItemGroup(ItemTags.LEG_ARMOR, "leggings", Items.IRON_LEGGINGS),
            new ItemGroup(ItemTags.FOOT_ARMOR, "boots", Items.IRON_BOOTS),
            new ItemGroup(ItemTags.SWORDS, "swords", Items.IRON_SWORD),
            new ItemGroup(ItemTags.AXES, "axes", Items.IRON_AXE),
            new ItemGroup(ItemTags.PICKAXES, "pickaxes", Items.IRON_PICKAXE),
            new ItemGroup(ItemTags.SHOVELS, "shovels", Items.IRON_SHOVEL),
            new ItemGroup(ItemTags.HOES, "hoes", Items.IRON_HOE),
            new ItemGroup(ItemTags.SPEARS, "spears", Items.IRON_SPEAR),
            new ItemGroup(ItemTags.BOW_ENCHANTABLE, "bows", Items.BOW),
            new ItemGroup(ItemTags.CROSSBOW_ENCHANTABLE, "crossbows", Items.CROSSBOW),
            new ItemGroup(ItemTags.TRIDENT_ENCHANTABLE, "tridents", Items.TRIDENT),
            new ItemGroup(ItemTags.MACE_ENCHANTABLE, "maces", Items.MACE),
            new ItemGroup(ItemTags.FISHING_ENCHANTABLE, "fishing_rods", Items.FISHING_ROD));

    public static final int MAX_COMPACT_ICONS = 10;
    private static final List<Item> SIGNATURE_ITEMS = List.of(
            Items.IRON_HELMET,
            Items.IRON_CHESTPLATE,
            Items.IRON_LEGGINGS,
            Items.IRON_BOOTS,
            Items.IRON_SWORD,
            Items.IRON_PICKAXE,
            Items.IRON_AXE,
            Items.IRON_SHOVEL,
            Items.IRON_HOE,
            Items.BOW);

    private EnchantmentApplicability() {}

    public static List<Item> iconItems(Enchantment enchantment) {
        var remaining = new LinkedHashSet<>(enchantment.getSupportedItems().stream().toList());
        var icons = new LinkedHashSet<Item>();

        for (var group : GROUPS) {
            // Broad categories have no single representative item: show their subgroups.
            if (group.representative() == null) {
                continue;
            }
            BuiltInRegistries.ITEM.get(group.tag()).ifPresent(items -> {
                var members = items.stream().toList();
                if (!members.isEmpty() && remaining.containsAll(members)) {
                    remaining.removeAll(members);
                    Item representative = members.stream().map(holder -> holder.value())
                            .filter(item -> item == group.representative()).findFirst()
                            .orElse(members.getFirst().value());
                    icons.add(representative);
                }
            });
        }

        remaining.forEach(item -> icons.add(item.value()));
        return List.copyOf(icons);
    }

    public static List<Item> iconItems(Enchantment enchantment, boolean compact) {
        List<Item> full = iconItems(enchantment);
        if (!compact || full.size() <= MAX_COMPACT_ICONS) {
            return full;
        }
        var compactSet = new LinkedHashSet<Item>();
        for (Item signature : SIGNATURE_ITEMS) {
            if (full.contains(signature)) {
                compactSet.add(signature);
                if (compactSet.size() == MAX_COMPACT_ICONS) {
                    break;
                }
            }
        }
        for (Item item : full) {
            if (compactSet.size() >= MAX_COMPACT_ICONS) {
                break;
            }
            compactSet.add(item);
        }
        return List.copyOf(compactSet);
    }

    public static boolean hasMoreIcons(Enchantment enchantment) {
        return iconItems(enchantment).size() > MAX_COMPACT_ICONS;
    }

    public static List<ItemStack> iconStacks(Enchantment enchantment, boolean compact) {
        return iconItems(enchantment, compact).stream().map(ItemStack::new).toList();
    }

    public static Component describe(Enchantment enchantment) {
        var remaining = new LinkedHashSet<>(enchantment.getSupportedItems().stream().toList());
        var names = new ArrayList<Component>();

        for (var group : GROUPS) {
            BuiltInRegistries.ITEM.get(group.tag()).ifPresent(items -> {
                var members = items.stream().toList();
                if (!members.isEmpty() && remaining.containsAll(members)) {
                    remaining.removeAll(members);
                    names.add(Component.translatable("eml.item_group." + group.name()));
                }
            });
        }

        remaining.forEach(item -> names.add(Component.translatable(item.value().getDescriptionId())));
        Component items = names.isEmpty()
                ? Component.translatable("eml.applicable_items.none")
                : ComponentUtils.formatList(names, Component.literal(", "));
        return Component.translatable("eml.applicable_items", items);
    }

    private record ItemGroup(TagKey<Item> tag, String name, Item representative) {}
}

