/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;

import hellfirepvp.astralsorcery.common.potion.PotionBleed;
import hellfirepvp.astralsorcery.common.potion.PotionCheatDeath;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryPotions
 * Created by HellFirePvP
 * Date: 13.11.2016 / 01:32
 */
public class RegistryPotions {

    public static PotionCheatDeath potionCheatDeath;
    public static PotionBleed potionBleed;

    public static void init() {
        potionCheatDeath = registerPotion(new PotionCheatDeath());
        potionBleed = registerPotion(new PotionBleed());
    }

    /**
     * 1.7 potions live in a fixed id array shared by all mods: use the preferred id if free, otherwise the first free
     * slot (AS used hard-coded ids 24/25, which clash in larger packs).
     */
    public static int findFreePotionId(int preferred) {
        Potion[] types = Potion.potionTypes;
        if (preferred < types.length && types[preferred] == null) return preferred;
        for (int i = 24; i < types.length; i++) {
            if (types[i] == null) return i;
        }
        throw new IllegalStateException(
            "[AstralSorcery] No free potion id left (array size " + types.length + "); install a potion id extender.");
    }

    private static <T extends Potion> T registerPotion(T potion) {
        potion.setPotionName(
            potion.getClass()
                .getSimpleName());
        MinecraftForge.EVENT_BUS.register(potion);
        return potion;
    }

}
