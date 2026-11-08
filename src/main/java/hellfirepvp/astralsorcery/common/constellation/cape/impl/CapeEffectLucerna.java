/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import java.awt.Color;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/** (backported from 1.12.2-1.8.5 to 1.7.10) */
public class CapeEffectLucerna extends CapeArmorEffect {

    private static boolean findSpawners = true;
    private static float range = 36F;

    public CapeEffectLucerna(NBTTagCompound cmp) {
        super(cmp, "lucerna");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.lucerna;
    }

    /** Highlights living entities (and optionally spawners) around the wearer. */
    @SideOnly(Side.CLIENT)
    public void playClientHighlightTick(EntityPlayer player) {
        World w = player.worldObj;
        double rSq = range * range;
        for (Object o : w.loadedEntityList) {
            if (!(o instanceof EntityLivingBase)) continue;
            EntityLivingBase entity = (EntityLivingBase) o;
            if (entity.isDead || entity == player) continue;
            double dSq = entity.getDistanceSqToEntity(player);
            if (dSq > rSq || dSq < 36) continue;
            if (rand.nextFloat() > 0.4) continue;
            Vector3 at = new Vector3(
                entity.boundingBox.minX + entity.width * rand.nextFloat(),
                entity.boundingBox.minY + entity.height * rand.nextFloat(),
                entity.boundingBox.minZ + entity.width * rand.nextFloat());
            highlight(at, Constellations.lucerna.getConstellationColor());
        }
        if (!findSpawners) return;
        for (Object o : w.loadedTileEntityList) {
            if (!(o instanceof TileEntityMobSpawner)) continue;
            TileEntity te = (TileEntity) o;
            if (player.getDistanceSq(te.xCoord + 0.5, te.yCoord + 0.5, te.zCoord + 0.5) > rSq) continue;
            if (rand.nextFloat() > 0.4) continue;
            highlight(
                new Vector3(te.xCoord + rand.nextFloat(), te.yCoord + rand.nextFloat(), te.zCoord + rand.nextFloat()),
                new Color(0x9C0100));
        }
    }

    @SideOnly(Side.CLIENT)
    private static void highlight(Vector3 at, Color c) {
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
        p.setColor(c)
            .setAlphaMultiplier(1F)
            .scale(0.6F * rand.nextFloat() + 0.6F)
            .gravity(0.004)
            .setMaxAge(30 + rand.nextInt(20));
        if (rand.nextInt(3) == 0) {
            p.setColor(IConstellation.weak);
        }
        if (rand.nextFloat() < 0.8F) {
            p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
            p.setColor(Color.WHITE)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(rand.nextFloat() * 0.3F + 0.3F)
                .gravity(0.004);
            p.setMaxAge(20 + rand.nextInt(10));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, 0.14F);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        findSpawners = cfg.getBoolean(
            getKey() + "FindSpawners",
            getConfigurationSection(),
            findSpawners,
            "If this is set to true, particles spawned by the lucerna cape effect will also highlight spawners nearby.");
        range = cfg.getFloat(
            getKey() + "Range",
            getConfigurationSection(),
            range,
            12,
            512,
            "Sets the maximum range of where the lucerna cape effect will get entities (and potentially spawners given the config option is enabled) to highlight.");
    }
}
