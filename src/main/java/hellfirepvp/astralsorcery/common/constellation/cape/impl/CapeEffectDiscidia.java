/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;

/** (backported from 1.12.2-1.8.5 to 1.7.10) */
public class CapeEffectDiscidia extends CapeArmorEffect {

    private static float multiplierGained = 1F;

    public CapeEffectDiscidia(NBTTagCompound cmp) {
        super(cmp, "discidia");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.discidia;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, getLastAttackDamage() <= 0 ? 0.1F : 0.3F);
    }

    public void writeLastAttackDamage(float dmgIn) {
        getData().setFloat("lastAttack", dmgIn);
    }

    public float getLastAttackDamage() {
        return (getData().hasKey("lastAttack") ? getData().getFloat("lastAttack") : 0F) * multiplierGained;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        multiplierGained = cfg.getFloat(
            getKey() + "Multiplier",
            getConfigurationSection(),
            1F,
            0F,
            100F,
            "Sets the multiplier for how much of the received damage is converted into additional damage");
    }
}
