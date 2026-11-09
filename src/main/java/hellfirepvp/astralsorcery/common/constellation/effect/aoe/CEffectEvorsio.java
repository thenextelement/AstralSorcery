/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.constellation.effect.GenListEntries;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.TileRitualLink;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.BlockDropCaptureAssist;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectEvorsio
 * Created by HellFirePvP
 * Date: 26.07.2017 / 16:15
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
public class CEffectEvorsio extends CEffectPositionListGen<GenListEntries.SimpleBlockPosEntry> {

    public static final Color COLOR_EVORSIO = new Color(0xA00100);
    public static final Color COLOR_ARMARA = new Color(0xB7BBB8);

    /** Ritual pedestal + link pattern, stacked 5 times upwards; never broken by the ritual. */
    private static Set<BlockPos> protectedOffsets = null;

    public static boolean enabled = true;
    public static float potencyMultiplier = 1F;
    public static int searchRange = 13;

    public CEffectEvorsio(@Nullable ILocatable origin) {
        super(
            origin,
            Constellations.evorsio,
            "evorsio",
            searchRange,
            2,
            (w, pos) -> isAllowedToBreak(origin, w, pos),
            GenListEntries.SimpleBlockPosEntry::new);
    }

    private static boolean isAllowedToBreak(@Nullable ILocatable origin, World world, BlockPos pos) {
        if (!MiscUtils.isChunkLoaded(world, new ChunkCoordIntPair(pos.chunkX(), pos.chunkZ()))) return false;
        if (world.isAirBlock(pos.getX(), pos.getY(), pos.getZ())) return false;
        Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
        float hardness = block.getBlockHardness(world, pos.getX(), pos.getY(), pos.getZ());
        if (hardness < 0 || hardness > 75) {
            return false;
        }
        if (origin == null) return false;
        BlockPos o = origin.getPos();
        if (!MiscUtils.isChunkLoaded(world, new ChunkCoordIntPair(o.chunkX(), o.chunkZ()))) return false;
        TileRitualPedestal pedestal = MiscUtils.getTileAt(world, o, TileRitualPedestal.class, true);
        if (pedestal != null) {
            Set<BlockPos> prot = getProtectedOffsets();
            return prot == null
                || !prot.contains(new BlockPos(pos.getX() - o.getX(), pos.getY() - o.getY(), pos.getZ() - o.getZ()));
        }
        return MiscUtils.getTileAt(world, o, TileRitualLink.class, true) == null;
    }

    @Nullable
    private static Set<BlockPos> getProtectedOffsets() {
        if (protectedOffsets == null && MultiBlockArrays.patternRitualPedestalWithLink != null) {
            Set<BlockPos> out = new HashSet<>();
            for (BlockPos p : MultiBlockArrays.patternRitualPedestalWithLink.getPattern()
                .keySet()) {
                for (int i = 0; i < 5; i++) {
                    out.add(p.add(0, i, 0));
                }
            }
            protectedOffsets = out;
        }
        return protectedOffsets;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility,
        boolean extendedEffects) {
        if (rand.nextInt(4) == 0) {
            Vector3 at = new Vector3(
                pos.getX() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5,
                pos.getY() + rand.nextFloat() * 2 + 0.5,
                pos.getZ() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5);
            for (int i = 0; i < 15; i++) {
                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
                p.gravity(0.004);
                p.scale(0.25F)
                    .setMaxAge(35 + rand.nextInt(10));
                Vector3 mot = new Vector3();
                MiscUtils.applyRandomOffset(mot, rand, 0.01F * rand.nextFloat() + 0.01F);
                p.motion(mot.getX(), mot.getY(), mot.getZ());
                p.setColor(rand.nextInt(3) == 2 ? COLOR_ARMARA : COLOR_EVORSIO);
            }
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

        if (world instanceof WorldServer && findNewPosition(world, pos)) {
            GenListEntries.SimpleBlockPosEntry be = getRandomElement(world.rand);
            if (be != null) {
                removeElement(be);
                BlockPos at = be.getPos();
                BlockDropCaptureAssist.startCapturing(true);
                List<ItemStack> captured;
                try {
                    MiscUtils.breakBlockWithoutPlayer((WorldServer) world, at);
                } finally {
                    captured = BlockDropCaptureAssist.getCapturedStacksAndStop();
                }
                for (ItemStack stack : captured) {
                    ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, stack);
                }
                PktParticleEvent ev = new PktParticleEvent(
                    PktParticleEvent.ParticleEventType.CE_BREAK_BLOCK,
                    at.getX(),
                    at.getY(),
                    at.getZ());
                PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, at, 16));
            }
        }
        return false;
    }

    @Override
    public boolean playTraitEffect(World world, BlockPos pos, IMinorConstellation traitType, float traitStrength) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public static void playBreakEffects(PktParticleEvent event) {
        Vector3 at = event.getVec()
            .add(0.5, 0.5, 0.5);
        for (int i = 0; i < 15; i++) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
            p.gravity(0.004);
            p.scale(0.25F)
                .setMaxAge(35 + rand.nextInt(10));
            Vector3 mot = new Vector3();
            MiscUtils.applyRandomOffset(mot, rand, 0.01F * rand.nextFloat() + 0.01F);
            p.motion(mot.getX(), mot.getY(), mot.getZ());
            switch (rand.nextInt(3)) {
                case 0:
                    p.setColor(Color.WHITE);
                    break;
                case 1:
                    p.setColor(COLOR_EVORSIO);
                    break;
                default:
                    p.setColor(COLOR_ARMARA);
                    break;
            }
        }
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        searchRange = cfg.getInt(
            getKey() + "Range",
            getConfigurationSection(),
            searchRange,
            1,
            32,
            "Defines the radius (in blocks) in which the ritual will search for blocks to break.");
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
    }

}
