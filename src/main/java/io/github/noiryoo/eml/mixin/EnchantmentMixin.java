package io.github.noiryoo.eml.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
abstract class EnchantmentMixin {
    @Inject(method = "getFullname", at = @At("RETURN"), cancellable = true)
    private static void eml$appendMaximumLevel(
            Holder<Enchantment> enchantment,
            int level,
            CallbackInfoReturnable<Component> callback) {
        int maximumLevel = enchantment.value().getMaxLevel();
        MutableComponent name = callback.getReturnValue().copy();

        if (maximumLevel == 1) {
            if (level >= maximumLevel) {
                name.append(Component.literal(" ★").withStyle(ChatFormatting.GREEN));
            }
            callback.setReturnValue(name);
            return;
        }

        name.append("/").append(Component.translatableWithFallback(
                "enchantment.level." + maximumLevel, Integer.toString(maximumLevel)));
        if (level >= maximumLevel) {
            name.append(Component.literal(" ★").withStyle(ChatFormatting.GREEN));
        }
        callback.setReturnValue(name);
    }
}

