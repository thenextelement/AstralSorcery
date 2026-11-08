package hellfirepvp.astralsorcery.mixins.late.etfuturum;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import hellfirepvp.astralsorcery.common.integrations.efr.ElytraIntegration;

/** EFR looks the worn elytra up here (takeoff, flight tick, render): a vicio mantle counts as one. */
// string target: ItemArmorElytra implements a Baubles Expanded interface we do not compile against
@Mixin(targets = "ganymedes01.etfuturum.items.equipment.ItemArmorElytra", remap = false)
public abstract class MixinItemArmorElytra {

    @Inject(method = "getElytra", at = @At("RETURN"), cancellable = true)
    private static void astralsorcery$vicioMantle(EntityLivingBase entity, CallbackInfoReturnable<ItemStack> cir) {
        if (cir.getReturnValue() == null && entity instanceof EntityPlayer) {
            ItemStack mantle = ElytraIntegration.getVicioMantle((EntityPlayer) entity);
            if (mantle != null) {
                cir.setReturnValue(mantle);
            }
        }
    }
}
