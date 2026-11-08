/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.efr;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.item.wearable.ItemCape;
import hellfirepvp.astralsorcery.common.lib.Constellations;

/**
 * Optional Et Futurum Requiem support: a worn vicio mantle counts as an elytra, so EFR handles takeoff, flight
 * physics, the gliding pose, sounds and syncing. Everything touching EFR classes lives in {@link Access} and is only
 * reached when EFR is loaded.
 */
public class ElytraIntegration {

    private static Boolean loaded = null;

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = Mods.ETFUTURUM.isPresent();
        }
        return loaded;
    }

    /** The mantle the player wears if it is attuned to vicio. */
    @Nullable
    public static ItemStack getVicioMantle(@Nullable EntityPlayer player) {
        if (player == null || ItemCape.getCapeEffect(player, Constellations.vicio) == null) return null;
        return ItemCape.getWornCape(player);
    }

    public static boolean isElytraFlying(EntityPlayer player) {
        return isLoaded() && Access.isElytraFlying(player);
    }

    private static class Access {

        static boolean isElytraFlying(EntityPlayer player) {
            return player instanceof ganymedes01.etfuturum.api.elytra.IElytraPlayer
                && ((ganymedes01.etfuturum.api.elytra.IElytraPlayer) player).etfu$isElytraFlying();
        }
    }
}
