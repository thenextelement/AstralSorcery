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
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;

/** (backported from 1.12.2-1.8.5 to 1.7.10) */
public class CapeEffectVicio extends CapeArmorEffect {

    public CapeEffectVicio(NBTTagCompound cmp) {
        super(cmp, "vicio");
    }

    @Override
    public void loadFromConfig(Configuration cfg) {}

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.vicio;
    }

    /** 1.7 has no elytra: the mantle itself provides the glide, see VicioGlideHandler. */
    public static boolean isGliding(EntityPlayer pl) {
        return pl.getEntityData()
            .getBoolean("AS_VicioGlide")
            || hellfirepvp.astralsorcery.common.integrations.efr.ElytraIntegration.isElytraFlying(pl);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        if (isGliding(pl)) {
            playVicioElytraSparkles(pl, 1F);
            playVicioElytraSparkles(pl, 0.8F);
        } else {
            playConstellationCapeSparkles(pl, 0.15F);
        }
    }

    @SideOnly(Side.CLIENT)
    private void playVicioElytraSparkles(EntityPlayer pl, float strength) {
        if (rand.nextFloat() >= strength) return;
        Color c = getAssociatedConstellation().getConstellationColor();
        if (c == null) return;
        double x = pl.posX + rand.nextFloat() * pl.width - (pl.width / 2) - pl.motionX * 1.5F;
        double y = pl.boundingBox.minY + rand.nextFloat() * (pl.height / 2) + 0.2 - pl.motionY * 1.5F;
        double z = pl.posZ + rand.nextFloat() * pl.width - (pl.width / 2) - pl.motionZ * 1.5F;
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x, y, z);
        p.setColor(c)
            .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
        p.scale(rand.nextFloat() * 0.5F + 0.3F);
        if (rand.nextInt(3) == 0) {
            p.setColor(IConstellation.weak);
        }
        p.setMaxAge(30 + rand.nextInt(20));
        if (rand.nextFloat() < 0.8F) {
            p = EffectHelper.genericFlareParticle(x, y, z);
            p.setColor(Color.WHITE)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(rand.nextFloat() * 0.2F + 0.1F);
            p.setMaxAge(20 + rand.nextInt(10));
        }
    }
}
