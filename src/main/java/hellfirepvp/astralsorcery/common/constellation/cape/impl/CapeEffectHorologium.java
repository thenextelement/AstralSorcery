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
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.effect.time.TimeStopController;

/** (backported from 1.12.2-1.8.5 to 1.7.10) */
public class CapeEffectHorologium extends CapeArmorEffect {

    private static float effectRange = 20F;
    private static int duration = 180;
    private static int cooldown = 1000;
    private static float chanceProc = 0.4F;

    public CapeEffectHorologium(NBTTagCompound cmp) {
        super(cmp, "horologium");
    }

    // 1.7 has no item cooldown tracker: the cooldown end (world time) is kept on the mantle.
    private boolean hasCooldown(EntityPlayer player) {
        return player.worldObj.getTotalWorldTime() < getData().getLong("AS_Horologium_Cooldown");
    }

    public void onHurt(EntityPlayer player) {
        if (hasCooldown(player)) return;
        if (rand.nextFloat() < chanceProc) {
            TimeStopController.freezeWorldAt(player, player.worldObj, new BlockPos(player), effectRange, duration);
            getData().setLong("AS_Horologium_Cooldown", player.worldObj.getTotalWorldTime() + cooldown);
        }
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.horologium;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, hasCooldown(pl) ? 0.2F : 0.35F);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        effectRange = cfg.getFloat(
            getKey() + "FreezeRange",
            getConfigurationSection(),
            effectRange,
            4F,
            64F,
            "Defines the range of the time-freeze effect");
        duration = cfg.getInt(
            getKey() + "Duration",
            getConfigurationSection(),
            duration,
            40,
            50_000,
            "Defines the duration of the time-freeze bubble");
        cooldown = cfg.getInt(
            getKey() + "Cooldown",
            getConfigurationSection(),
            cooldown,
            40,
            70_000,
            "Defines the cooldown for the time-freeze effect after it triggered (should be longer than duration!)");
        chanceProc = cfg.getFloat(
            getKey() + "TriggerChance",
            getConfigurationSection(),
            chanceProc,
            0F,
            1F,
            "Defines the chance for the time-freeze effect to trigger when being hit");
    }
}
