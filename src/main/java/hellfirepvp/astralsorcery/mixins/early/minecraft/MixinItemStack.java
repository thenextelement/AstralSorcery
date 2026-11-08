package hellfirepvp.astralsorcery.mixins.early.minecraft;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import hellfirepvp.astralsorcery.common.util.SwordSharpenHelper;

/** Replaces the 1.10 ASM patch PatchModifyAttributes: applies the grindstone sharpening bonus. */
@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Inject(method = "getAttributeModifiers", at = @At("RETURN"), cancellable = true)
    private void astralsorcery$applySharpen(CallbackInfoReturnable<Multimap<String, AttributeModifier>> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (!SwordSharpenHelper.isSwordSharpened(self)) return;
        // Returned map may be immutable or shared with the Item; copy before modifying.
        Multimap<String, AttributeModifier> copy = HashMultimap.create(cir.getReturnValue());
        SwordSharpenHelper.applySharpenModifier(self, 0, copy);
        cir.setReturnValue(copy);
    }
}
