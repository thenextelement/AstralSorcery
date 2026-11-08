/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;

/**
 * Solar eclipse darkening. 1.10 wrapped the world's WorldProvider (WorldProviderBrightnessInj); on 1.7 providers keep
 * state in fields, so the factor is applied through mixins on World instead (MixinWorld / MixinWorldClient).
 */
public class SolarEclipseBrightness {

    /** Multiplier for sun brightness: 1 normally, down to 0.15 at the peak of a solar eclipse. */
    public static float getFactor(World world) {
        if (world == null || world.provider == null) return 1F;
        WorldSkyHandler handle = ConstellationSkyHandler.getInstance()
            .getWorldHandler(world);
        if (handle == null || !handle.dayOfSolarEclipse || !handle.solarEclipse) return 1F;
        int eclTick = handle.solarEclipseTick;
        if (eclTick >= ConstellationSkyHandler.SOLAR_ECLIPSE_HALF_DUR) { // fading out
            eclTick -= ConstellationSkyHandler.SOLAR_ECLIPSE_HALF_DUR;
        } else {
            eclTick = ConstellationSkyHandler.SOLAR_ECLIPSE_HALF_DUR - eclTick;
        }
        float perc = ((float) eclTick) / ConstellationSkyHandler.SOLAR_ECLIPSE_HALF_DUR;
        return 0.15F + (0.85F * perc);
    }
}
