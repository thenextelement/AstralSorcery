package hellfirepvp.astralsorcery.mixins.late.etfuturum;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import hellfirepvp.astralsorcery.common.item.wearable.ItemCape;

/** The vicio mantle flies like an elytra but keeps its own model: no elytra wings on top of it. */
@Mixin(targets = "ganymedes01.etfuturum.client.renderer.entity.elytra.LayerBetterElytra", remap = false)
public abstract class MixinLayerBetterElytra {

    @ModifyVariable(method = "doRenderLayer", at = @At("STORE"), ordinal = 0)
    private static ItemStack astralsorcery$noWingsOnMantle(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemCape ? null : stack;
    }
}
