/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixins.early.minecraft;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import hellfirepvp.astralsorcery.common.world.SolarEclipseBrightness;

/** Solar eclipse: darken the rendered sun/sky brightness (World#getSunBrightness is client-only). */
@Mixin(World.class)
public abstract class MixinWorldClientSide {

    @Inject(method = "getSunBrightness", at = @At("RETURN"), cancellable = true)
    private void astralsorcery$eclipseSunBrightness(float partialTicks, CallbackInfoReturnable<Float> cir) {
        float f = SolarEclipseBrightness.getFactor((World) (Object) this);
        if (f < 1F) cir.setReturnValue(cir.getReturnValue() * f);
    }
}
