/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape;

import java.awt.Color;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeArmorEffect
 * Created by HellFirePvP
 * Date: 09.10.2017 / 23:23
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
public abstract class CapeArmorEffect extends ConfigEntry {

    protected static final Random rand = new Random();

    // The mantle's persistent item NBT; changes are written straight into the worn stack.
    private final NBTTagCompound data;

    // MUST BE OVERWRITTEN, WITH NBTTAGCMP AS ONLY PARAM!
    protected CapeArmorEffect(NBTTagCompound cmp, String key) {
        super(Section.CAPE, key);
        this.data = cmp;
    }

    public abstract IConstellation getAssociatedConstellation();

    @SideOnly(Side.CLIENT)
    public abstract void playActiveParticleTick(EntityPlayer pl);

    @Override
    public String getConfigurationSection() {
        return super.getConfigurationSection() + "." + getKey();
    }

    public final NBTTagCompound getData() {
        return this.data;
    }

    @SideOnly(Side.CLIENT)
    protected void playConstellationCapeSparkles(EntityPlayer pl, float strength) {
        if (rand.nextFloat() < strength) {
            Color c = getAssociatedConstellation().getConstellationColor();
            if (c != null) {
                // 1.7 client player posY is at eye height
                double baseY = pl.boundingBox.minY;
                double x = pl.posX + rand.nextFloat() * pl.width - (pl.width / 2);
                double y = baseY + rand.nextFloat() * (pl.height / 2) + 0.2;
                double z = pl.posZ + rand.nextFloat() * pl.width - (pl.width / 2);

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
    }

}
