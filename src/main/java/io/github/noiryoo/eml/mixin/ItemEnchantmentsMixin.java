package io.github.noiryoo.eml.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.noiryoo.eml.tooltip.EnchantmentTooltip;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEnchantments.class)
abstract class ItemEnchantmentsMixin {
    @Inject(
            method = "addToTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
                    shift = At.Shift.AFTER),
            require = 2)
    private void eml$appendDetails(
            CallbackInfo callback,
            @Local(argsOnly = true) Consumer<Component> tooltip,
            @Local Holder<Enchantment> enchantment) {
        int count = ((ItemEnchantments) (Object) this).size();
        boolean detailed = EnchantmentTooltip.isDetailedView();
        if (count > 1 && !detailed) {
            return;
        }
        EnchantmentTooltip.appendDetails(enchantment, tooltip, detailed);
    }

    @Inject(method = "addToTooltip", at = @At("RETURN"))
    private void eml$appendShiftHint(
            CallbackInfo callback,
            @Local(argsOnly = true) Consumer<Component> tooltip) {
        if (EnchantmentTooltip.isDetailedView()) {
            return;
        }
        ItemEnchantments enchantments = (ItemEnchantments) (Object) this;
        if (enchantments.size() > 1) {
            EnchantmentTooltip.appendHoldShiftHint(tooltip);
        }
    }
}

