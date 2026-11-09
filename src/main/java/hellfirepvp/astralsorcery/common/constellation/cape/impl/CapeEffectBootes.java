/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.entities.EntityFlare;
import hellfirepvp.astralsorcery.common.lib.Constellations;

/** (backported from 1.12.2-1.8.5 to 1.7.10) */
public class CapeEffectBootes extends CapeArmorEffect {

    private static int maxFlareCount = 3;
    private static float summonFlareChance = 0.005F;

    public CapeEffectBootes(NBTTagCompound cmp) {
        super(cmp, "bootes");
    }

    public void onPlayerTick(EntityPlayer pl) {
        World w = pl.worldObj;
        int current = gatherEntities(w).size();
        EntityFlare flare = null;
        if (current < maxFlareCount) {
            if (rand.nextFloat() < summonFlareChance) {
                flare = new EntityFlare(w, pl.posX, pl.boundingBox.minY + pl.height / 2, pl.posZ);
                flare.setFollowingTarget(pl);
                w.spawnEntityInWorld(flare);
            }
        }
        updateEntityList(w, flare);

        for (Entity e : gatherEntities(w)) {
            if (e instanceof EntityFlare && ((EntityFlare) e).getFollowingEntity() != null
                && pl.getDistanceToEntity(e) >= 10) {
                e.setPositionAndRotation(pl.posX, pl.posY, pl.posZ, 0, 0);
            }
        }
    }

    public void onPlayerDamagedByEntity(EntityPlayer player, EntityLivingBase attacker) {
        for (Entity e : gatherEntities(player.worldObj)) {
            if (e instanceof EntityFlare) {
                ((EntityFlare) e).setAttackTarget(attacker);
            }
        }
    }

    private List<Integer> updateEntityList(World currentWorld, @Nullable Entity newEntityAdded) {
        List<Integer> out = new LinkedList<>();
        for (Integer id : gatherEntityIds()) {
            Entity e = currentWorld.getEntityByID(id);
            if (e != null && !e.isDead && (e instanceof EntityFlare)) {
                out.add(id);
            }
        }
        if (newEntityAdded != null && !newEntityAdded.isDead) {
            out.add(newEntityAdded.getEntityId());
        }
        int[] ids = new int[out.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = out.get(i);
        }
        getData().setIntArray("AS_Bootes_Entities", ids);
        return out;
    }

    private List<Entity> gatherEntities(World currentWorld) {
        List<Entity> out = new LinkedList<>();
        for (Integer id : gatherEntityIds()) {
            Entity e = currentWorld.getEntityByID(id);
            if (e != null && !e.isDead && (e instanceof EntityFlare)) {
                out.add(e);
            }
        }
        return out;
    }

    private List<Integer> gatherEntityIds() {
        List<Integer> entities = new LinkedList<>();
        for (int id : getData().getIntArray("AS_Bootes_Entities")) {
            entities.add(id);
        }
        return entities;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        maxFlareCount = cfg.getInt(
            getKey() + "MaxFlareCount",
            getConfigurationSection(),
            maxFlareCount,
            2,
            8,
            "Defines the maximum flare count the player can draw with him.");
        summonFlareChance = cfg.getFloat(
            getKey() + "TickSummonChance",
            getConfigurationSection(),
            summonFlareChance,
            0F,
            1F,
            "Defines the chance per tick that a new flare following the wearer is spawned.");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.bootes;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, 0.2F);
    }
}
