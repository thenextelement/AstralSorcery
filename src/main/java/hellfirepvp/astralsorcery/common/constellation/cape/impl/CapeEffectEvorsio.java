/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import java.awt.Color;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/** (backported from 1.12.2-1.8.5 to 1.7.10) */
public class CapeEffectEvorsio extends CapeArmorEffect {

    private static float percDamageAppliedNearby = 0.5F;
    private static float rangeDeathAOE = 4F;

    public CapeEffectEvorsio(NBTTagCompound cmp) {
        super(cmp, "evorsio");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.evorsio;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, 0.15F);
    }

    /** additionalData: block id | (meta << 12) of the broken block. */
    @SideOnly(Side.CLIENT)
    public static void playBlockBreakParticles(PktParticleEvent event) {
        Vector3 at = event.getVec();
        int data = (int) Math.round(event.getAdditionalData());
        if (data != 0 && !net.minecraft.client.Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode) {
            Block b = Block.getBlockById(data & 0xFFF);
            if (b != null && b != net.minecraft.init.Blocks.air) {
                RenderingUtils.playBlockBreakParticles(at.toBlockPos(), b);
            }
        }
        Color c = Constellations.evorsio.getConstellationColor();
        for (int i = 0; i < 4; i++) {
            double x = at.getX() + rand.nextFloat();
            double y = at.getY() + rand.nextFloat();
            double z = at.getZ() + rand.nextFloat();
            float scale = rand.nextFloat() * 0.2F + 0.3F;
            float mX = rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1);
            float mY = rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1);
            float mZ = rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1);
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x, y, z);
            p.setColor(c)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(scale);
            if (rand.nextInt(6) == 0) {
                p.setColor(IConstellation.weak);
            }
            p.setMaxAge(30 + rand.nextInt(10));
            p.motion(mX, mY, mZ);
            if (rand.nextFloat() < 0.4F) {
                p = EffectHelper.genericFlareParticle(x, y, z);
                p.setColor(Color.WHITE)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(scale * 0.35F);
                p.setMaxAge(20 + rand.nextInt(10));
                p.motion(mX, mY, mZ);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void playAreaDamageParticles(PktParticleEvent event) {
        Vector3 offset = event.getVec();
        Color c = Constellations.evorsio.getConstellationColor();
        spawnRing(offset, c, 45, 0.2F, 0.1F, 0.01F);
        spawnRing(offset, IConstellation.weak, 65, 0.3F, 0.5F, 0.015F);
    }

    @SideOnly(Side.CLIENT)
    private static void spawnRing(Vector3 offset, Color c, int count, float yScale, float offMul, float movMul) {
        for (int i = 0; i < count; i++) {
            Vector3 dir = Vector3.random();
            dir.setY(dir.getY() * yScale)
                .normalize()
                .multiply(rangeDeathAOE / 2F);
            Vector3 off = dir.clone()
                .multiply(offMul + rand.nextFloat() * 0.4F);
            Vector3 mov = dir.clone()
                .multiply(movMul);
            float scale = rand.nextFloat() * 0.2F + 0.2F;
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                offset.getX() + off.getX(),
                offset.getY() + off.getY(),
                offset.getZ() + off.getZ());
            p.setColor(c)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(scale);
            p.setMaxAge(30 + rand.nextInt(10));
            p.motion(mov.getX(), mov.getY(), mov.getZ());
            if (rand.nextFloat() < 0.4F) {
                p = EffectHelper.genericFlareParticle(
                    offset.getX() + off.getX(),
                    offset.getY() + off.getY(),
                    offset.getZ() + off.getZ());
                p.setColor(Color.WHITE)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(scale * 0.35F);
                p.setMaxAge(20 + rand.nextInt(10));
                p.motion(mov.getX(), mov.getY(), mov.getZ());
            }
        }
    }

    public void deathAreaDamage(DamageSource ds, EntityLivingBase entityLiving) {
        if (percDamageAppliedNearby <= 0) return;
        float damage = entityLiving.getMaxHealth() * percDamageAppliedNearby;
        float r = rangeDeathAOE;
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
            entityLiving.posX - r,
            entityLiving.posY - r,
            entityLiving.posZ - r,
            entityLiving.posX + r,
            entityLiving.posY + r,
            entityLiving.posZ + r);
        List<?> eList = entityLiving.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
        for (Object o : eList) {
            EntityLivingBase el = (EntityLivingBase) o;
            if (el.isDead || el.getHealth() <= 0
                || el == entityLiving
                || !el.isCreatureType(EnumCreatureType.monster, false)) continue;
            int preTime = el.hurtResistantTime;
            el.attackEntityFrom(ds, damage);
            el.hurtResistantTime = Math.max(preTime, el.hurtResistantTime);
        }
        PktParticleEvent ev = new PktParticleEvent(
            PktParticleEvent.ParticleEventType.CAPE_EVORSIO_AOE,
            new Vector3(entityLiving.posX, entityLiving.boundingBox.minY + entityLiving.height / 2, entityLiving.posZ));
        PacketChannel.CHANNEL
            .sendToAllAround(ev, PacketChannel.pointFromPos(entityLiving.worldObj, new BlockPos(entityLiving), 16));
    }

    /** side: 0-5 vanilla side index of the face the player hit. */
    public void breakBlocksPlane(EntityPlayerMP player, int sideBroken, World world, BlockPos at) {
        int dx = sideBroken == 4 || sideBroken == 5 ? 1 : 0;
        int dy = sideBroken == 0 || sideBroken == 1 ? 1 : 0;
        int dz = sideBroken == 2 || sideBroken == 3 ? 1 : 0;
        for (int xx = -2; xx <= 2; xx++) {
            if (dx != 0 && xx != 0) continue;
            for (int yy = -1; yy <= 3; yy++) {
                if (dy != 0 && yy != 0) continue;
                for (int zz = -2; zz <= 2; zz++) {
                    if (dz != 0 && zz != 0) continue;
                    BlockPos other = at.add(xx, yy, zz);
                    if (other.equals(at)) continue;
                    Block present = world.getBlock(other.getX(), other.getY(), other.getZ());
                    int meta = world.getBlockMetadata(other.getX(), other.getY(), other.getZ());
                    if (present.isAir(world, other.getX(), other.getY(), other.getZ())) continue;
                    if (world.getTileEntity(other.getX(), other.getY(), other.getZ()) != null) continue;
                    if (present.getBlockHardness(world, other.getX(), other.getY(), other.getZ()) == -1) continue;
                    if (MiscUtils.breakBlockWithPlayer(other, player)) {
                        PktParticleEvent ev = new PktParticleEvent(
                            PktParticleEvent.ParticleEventType.CAPE_EVORSIO_BREAK,
                            other.getX(),
                            other.getY(),
                            other.getZ());
                        ev.setAdditionalData(Block.getIdFromBlock(present) | (meta << 12));
                        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, other, 16));
                    }
                }
            }
        }
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        percDamageAppliedNearby = cfg.getFloat(
            getKey() + "PercentLifeDamage",
            getConfigurationSection(),
            percDamageAppliedNearby,
            0,
            10,
            "Defines the multiplier how much of the dead entity's max-life should be dealt as AOE damage to mobs nearby.");
        rangeDeathAOE = cfg.getFloat(
            getKey() + "DeathAOERange",
            getConfigurationSection(),
            rangeDeathAOE,
            0.5F,
            50,
            "Defines the Range of the death-AOE effect of when a mob gets killed by a player with this cape on.");
    }
}
