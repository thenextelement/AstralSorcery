package hellfirepvp.astralsorcery.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hellfirepvp.astralsorcery.common.event.EntityKnockbackEvent;

/** Replaces the 1.10 ASM patch PatchKnockbackEvent. */
@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase {

    @Inject(method = "moveEntityWithHeading", at = @At("HEAD"), cancellable = true)
    private void astralsorcery$vicioGlide(float strafe, float forward, CallbackInfo ci) {
        if (hellfirepvp.astralsorcery.common.constellation.cape.impl.VicioGlideHandler
            .travel((EntityLivingBase) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "knockBack", at = @At("HEAD"), cancellable = true)
    private void astralsorcery$onKnockback(Entity attacker, float strength, double x, double z, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS
            .post(new EntityKnockbackEvent((EntityLivingBase) (Object) this, attacker, strength))) {
            ci.cancel(); // octans mantle in water
        }
    }
}
