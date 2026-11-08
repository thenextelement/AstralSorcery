/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.effect.time;

import java.awt.Color;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TimeStopEffectHelper
 * Created by HellFirePvP
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
// Shell class for rendering and syncing over network.
public class TimeStopEffectHelper {

    @Nonnull
    private BlockPos position;
    private float range;
    private boolean hasOwner;
    private int ownerEntityId;

    private TimeStopEffectHelper(@Nonnull BlockPos position, float range, boolean hasOwner, int ownerEntityId) {
        this.position = position;
        this.range = range;
        this.hasOwner = hasOwner;
        this.ownerEntityId = ownerEntityId;
    }

    static TimeStopEffectHelper fromZone(TimeStopZone zone) {
        return new TimeStopEffectHelper(zone.offset, zone.range, zone.hasOwner, zone.ownerId);
    }

    @Nonnull
    public BlockPos getPosition() {
        return position;
    }

    public float getRange() {
        return range;
    }

    @SideOnly(Side.CLIENT)
    private static void sparkle(Random rand, Color c, double x, double y, double z) {
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x, y, z);
        p.setColor(c)
            .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
        p.scale(rand.nextFloat() * 0.5F + 0.3F)
            .gravity(0.004);
        p.setMaxAge(40 + rand.nextInt(20));
        if (rand.nextFloat() < 0.9F) {
            p = EffectHelper.genericFlareParticle(x, y, z);
            p.setColor(Color.WHITE)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(rand.nextFloat() * 0.2F + 0.1F)
                .gravity(0.004);
            p.setMaxAge(30 + rand.nextInt(10));
        }
    }

    @SideOnly(Side.CLIENT)
    public void playClientTickEffect() {
        Color c = new Color(0xDDDDDD);
        Random rand = new Random();
        World w = Minecraft.getMinecraft().theWorld;
        double rSq = range * range;
        for (Object o : w.loadedEntityList) {
            if (!(o instanceof EntityLivingBase)) continue;
            EntityLivingBase e = (EntityLivingBase) o;
            if (e.isDead || (hasOwner && e.getEntityId() == ownerEntityId)) continue;
            if (e.getDistanceSq(position.getX(), position.getY(), position.getZ()) > rSq) continue;
            if (rand.nextInt(3) != 0) continue;
            sparkle(
                rand,
                c,
                e.posX - e.width + rand.nextFloat() * e.width * 2,
                e.boundingBox.minY + rand.nextFloat() * e.height,
                e.posZ - e.width + rand.nextFloat() * e.width * 2);
        }
        for (Object o : w.loadedTileEntityList) {
            TileEntity te = (TileEntity) o;
            if (!te.canUpdate() || rand.nextInt(3) != 0) continue;
            if (te.getDistanceFrom(position.getX(), position.getY(), position.getZ()) > rSq) continue;
            sparkle(rand, c, te.xCoord + rand.nextFloat(), te.yCoord + rand.nextFloat(), te.zCoord + rand.nextFloat());
        }
        for (int i = 0; i < 10; i++) {
            Vector3 pos = Vector3.random()
                .normalize()
                .multiply(rand.nextFloat() * range)
                .add(position.getX(), position.getY(), position.getZ());
            sparkle(rand, c, pos.getX(), pos.getY(), pos.getZ());
        }
        if (rand.nextInt(4) == 0) {
            Vector3 rand1 = Vector3.random()
                .normalize()
                .multiply(rand.nextFloat() * range)
                .add(position.getX(), position.getY(), position.getZ());
            Vector3 rand2 = Vector3.random()
                .normalize()
                .multiply(rand.nextFloat() * range)
                .add(position.getX(), position.getY(), position.getZ());
            AstralSorcery.proxy.fireLightning(w, rand1, rand2, c);
        }
    }

    @Nonnull
    public NBTTagCompound serializeNBT() {
        NBTTagCompound out = new NBTTagCompound();
        NBTUtils.writeBlockPosToNBT(this.position, out);
        out.setFloat("range", this.range);
        out.setBoolean("hasOwner", this.hasOwner);
        out.setInteger("ownerEntityId", this.ownerEntityId);
        return out;
    }

    @Nonnull
    public static TimeStopEffectHelper deserializeNBT(NBTTagCompound cmp) {
        return new TimeStopEffectHelper(
            NBTUtils.readBlockPosFromNBT(cmp),
            cmp.getFloat("range"),
            cmp.getBoolean("hasOwner"),
            cmp.getInteger("ownerEntityId"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeStopEffectHelper that = (TimeStopEffectHelper) o;
        return Float.compare(that.range, range) == 0 && hasOwner == that.hasOwner
            && ownerEntityId == that.ownerEntityId
            && position.equals(that.position);
    }

    @Override
    public int hashCode() {
        int result = position.hashCode();
        result = 31 * result + (range != +0.0f ? Float.floatToIntBits(range) : 0);
        result = 31 * result + (hasOwner ? 1 : 0);
        result = 31 * result + ownerEntityId;
        return result;
    }
}
