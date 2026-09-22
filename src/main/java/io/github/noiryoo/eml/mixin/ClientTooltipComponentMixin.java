package io.github.noiryoo.eml.mixin;

import io.github.noiryoo.eml.tooltip.ItemIconsLine;
import io.github.noiryoo.eml.tooltip.ItemIconsTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientTooltipComponent.class)
interface ClientTooltipComponentMixin {
    @Inject(
            method = "create(Lnet/minecraft/util/FormattedCharSequence;)Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;",
            at = @At("HEAD"),
            cancellable = true)
    private static void eml$createItemIcons(
            FormattedCharSequence text, CallbackInfoReturnable<ClientTooltipComponent> callback) {
        if (text instanceof ItemIconsLine line) {
            callback.setReturnValue(new ItemIconsTooltip(line.items(), line.hasMore()));
        }
    }
}
