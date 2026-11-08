/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import java.awt.Color;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/** (backported from 1.12.2-1.8.5 to 1.7.10) */
public class CapeEffectAevitas extends CapeArmorEffect {

    private static final int ticksPerRound = 30;

    private static float range = 10F;
    private static float potency = 1F;
    private static float turnChance = 0.2F;

    public CapeEffectAevitas(NBTTagCompound cmp) {
        super(cmp, "aevitas");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.aevitas;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, 0.1F);
        Color c = getAssociatedConstellation().getConstellationColor();
        if (c == null) return;
        Vector3 centerOffset = new Vector3(pl.posX, pl.boundingBox.minY, pl.posZ);
        float tick = (float) (ClientScheduler.getClientTick() % ticksPerRound);
        Vector3 axis = Vector3.RotAxis.Y_AXIS;
        Vector3 circleVec = axis.clone()
            .perpendicular()
            .normalize()
            .multiply(range * 0.9 * rand.nextFloat());
        double deg = 360D * (tick / (float) (ticksPerRound));
        for (int i = 0; i < 2; i++) {
            if (rand.nextFloat() >= 0.2) continue;
            Vector3 at = circleVec.clone()
                .rotate(Math.toRadians(deg + 180 * i), axis.clone())
                .add(centerOffset);
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
            p.setColor(c)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(rand.nextFloat() * 0.5F + 0.3F);
            if (rand.nextInt(3) == 0) {
                p.setColor(IConstellation.weak);
            }
            p.setMaxAge(30 + rand.nextInt(20));
            if (rand.nextFloat() < 0.8F) {
                p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
                p.setColor(Color.WHITE)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(rand.nextFloat() * 0.2F + 0.1F);
                p.setMaxAge(20 + rand.nextInt(10));
            }
        }
    }

    public float getTurnChance() {
        return turnChance;
    }

    public float getRange() {
        return range;
    }

    public float getPotency() {
        return potency;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        range = cfg.getFloat(
            getKey() + "Range",
            getConfigurationSection(),
            range,
            1,
            32,
            "Defines the radius (in blocks) for the aoe effect.");
        potency = cfg.getFloat(
            getKey() + "Potency",
            getConfigurationSection(),
            potency,
            0,
            1,
            "Defines the multiplier if the aoe will happen at all");
        turnChance = cfg.getFloat(
            getKey() + "PlantTransformChance",
            getConfigurationSection(),
            turnChance,
            0,
            1,
            "Defines the chance that the aoe will search for a plant to turn into another plant.");
    }
}
