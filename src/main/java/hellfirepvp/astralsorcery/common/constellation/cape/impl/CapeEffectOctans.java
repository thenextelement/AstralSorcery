/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;

/** (backported from 1.12.2-1.8.5 to 1.7.10) */
public class CapeEffectOctans extends CapeArmorEffect {

    private static float healPerTick = 0.01F;

    public CapeEffectOctans(NBTTagCompound cmp) {
        super(cmp, "octans");
    }

    public void onWaterHealTick(EntityPlayer pl) {
        if (healPerTick <= 0) return;
        pl.heal(healPerTick);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        healPerTick = cfg.getFloat(
            getKey() + "HealPerTick",
            getConfigurationSection(),
            healPerTick,
            0,
            5,
            "Defines the amount of health that is healed while the wearer is in water. Can be set to 0 to disable this.");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.octans;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, pl.isInsideOfMaterial(Material.water) ? 0.3F : 0.15F);
    }
}
