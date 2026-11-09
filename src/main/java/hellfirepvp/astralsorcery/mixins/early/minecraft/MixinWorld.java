/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixins.early.minecraft;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import hellfirepvp.astralsorcery.common.world.SolarEclipseBrightness;

/** Solar eclipse: darken the world's skylight (server + client). Replaces 1.10's WorldProviderBrightnessInj. */
@Mixin(World.class)
public abstract class MixinWorld {

    @Redirect(
        method = "calculateSkylightSubtracted",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/WorldProvider;getSunBrightnessFactor(F)F",
            remap = false))
    private float astralsorcery$eclipseSkylight(WorldProvider provider, float partialTicks) {
        return provider.getSunBrightnessFactor(partialTicks) * SolarEclipseBrightness.getFactor((World) (Object) this);
    }
}
