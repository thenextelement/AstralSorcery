/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.constellation.effect.GenListEntries;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectPelotrio
 * Created by HellFirePvP
 * Date: 28.07.2017 / 20:15
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
public class CEffectPelotrio extends CEffectPositionListGen<GenListEntries.PelotrioSpawnListEntry> {

    private static final int PROXIMITY_RANGE = 24;

    private static boolean enabled = true;
    private static float potencyMultiplier = 1F;
    private static int searchRange = 8;

    private static float selectChance = 0.1F;
    private static int proximityAmount = 40;

    public CEffectPelotrio(@Nullable ILocatable origin) {
        super(
            origin,
            Constellations.pelotrio,
            "pelotrio",
            searchRange,
            5,
            (w, pos) -> GenListEntries.PelotrioSpawnListEntry.createEntry(w, pos) != null,
            GenListEntries.PelotrioSpawnListEntry::new);
    }

    @Override
    public GenListEntries.PelotrioSpawnListEntry newElement(World world, BlockPos at) {
        GenListEntries.PelotrioSpawnListEntry e = GenListEntries.PelotrioSpawnListEntry.createEntry(world, at);
        return e != null ? e : new GenListEntries.PelotrioSpawnListEntry(at);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility,
        boolean extendedEffects) {
        for (int i = 0; i < 1 + rand.nextInt(3); i++) {
            Vector3 particlePos = new Vector3(
                pos.getX() - 3 + rand.nextFloat() * 7,
                pos.getY() + rand.nextFloat() * 2,
                pos.getZ() - 3 + rand.nextFloat() * 7);
            Vector3 dir = particlePos.clone()
                .subtract(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
                .normalize()
                .divide(-30);
            EntityFXFacingParticle p = EffectHelper
                .genericFlareParticle(particlePos.getX(), particlePos.getY(), particlePos.getZ());
            p.motion(dir.getX(), dir.getY(), dir.getZ())
                .setAlphaMultiplier(1F)
                .setMaxAge(rand.nextInt(40) + 20);
            p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                .scale(0.2F + rand.nextFloat() * 0.1F)
                .setColor(new Color(29, 123, 59));
        }
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect,
        @Nullable IMinorConstellation possibleTraitEffect) {
        if (!enabled) return false;
        percStrength *= potencyMultiplier;
        if (percStrength < 1) {
            if (world.rand.nextFloat() > percStrength) return false;
        }

        GenListEntries.PelotrioSpawnListEntry entry = getRandomElement(rand);
        if (entry != null) {
            entry.counter++;
            BlockPos at = entry.getPos();
            PktParticleEvent ev = new PktParticleEvent(
                PktParticleEvent.ParticleEventType.CE_SPAWN_PREPARE_EFFECTS,
                at.getX(),
                at.getY(),
                at.getZ());
            PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, at, 24));
            if (entry.counter >= 30) {
                entry.spawn(world);
                removeElement(entry);
            }
        }

        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
            pos.getX() - PROXIMITY_RANGE,
            pos.getY() - PROXIMITY_RANGE,
            pos.getZ() - PROXIMITY_RANGE,
            pos.getX() + PROXIMITY_RANGE,
            pos.getY() + PROXIMITY_RANGE,
            pos.getZ() + PROXIMITY_RANGE);
        List<?> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
        if (entities.size() > proximityAmount) {
            return false; // Flood & lag prevention.
        }

        boolean update = false;
        if (rand.nextFloat() < selectChance) {
            if (findNewPosition(world, pos)) {
                update = true;
            }
        }
        return update;
    }

    @Override
    public boolean playTraitEffect(World world, BlockPos pos, IMinorConstellation traitType, float traitStrength) {
        return false;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        searchRange = cfg.getInt(
            getKey() + "Range",
            getConfigurationSection(),
            searchRange,
            1,
            32,
            "Defines the radius (in blocks) in which the ritual will search for potential spawn points for entities.");
        enabled = cfg.getBoolean(
            getKey() + "Enabled",
            getConfigurationSection(),
            enabled,
            "Set to false to disable this ConstellationEffect.");
        potencyMultiplier = cfg.getFloat(
            getKey() + "PotencyMultiplier",
            getConfigurationSection(),
            potencyMultiplier,
            0.01F,
            100F,
            "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
        selectChance = cfg.getFloat(
            getKey() + "SpawnSearchChance",
            getConfigurationSection(),
            selectChance,
            0F,
            1F,
            "Defines the per-tick chance that a new position for a entity-spawn will be searched for.");
        proximityAmount = cfg.getInt(
            getKey() + "ProximityCheck",
            getConfigurationSection(),
            proximityAmount,
            1,
            256,
            "Defines the threshold at which the ritual will stop spawning mobs. If there are more or equal amount of mobs near this ritual, the ritual will not spawn more mobs. Mainly to reduce potential server lag.");
    }

    @SideOnly(Side.CLIENT)
    public static void playSpawnPrepareEffects(PktParticleEvent event) {
        Vector3 at = event.getVec()
            .add(0.5, 0, 0.5);
        Vector3 dir = new Vector3(
            rand.nextFloat() * (rand.nextBoolean() ? 1 : -1) * 0.05F,
            rand.nextFloat() * 0.05F,
            rand.nextFloat() * (rand.nextBoolean() ? 1 : -1) * 0.05F);
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
        p.motion(dir.getX(), dir.getY(), dir.getZ())
            .setAlphaMultiplier(1F)
            .setMaxAge(rand.nextInt(40) + 20);
        p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
            .scale(0.2F + rand.nextFloat() * 0.1F);
        p.setColor(rand.nextBoolean() ? new Color(29, 123, 59) : new Color(78, 1, 109));
    }

}
