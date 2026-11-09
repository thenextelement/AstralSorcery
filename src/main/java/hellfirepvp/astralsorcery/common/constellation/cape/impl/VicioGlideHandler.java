/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.item.wearable.ItemCape;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktVicioGlide;
import hellfirepvp.astralsorcery.mixins.early.minecraft.AccessorNetHandlerPlayServer;

/**
 * Vicio mantle flight for 1.7.10. 1.12 lets the vicio mantle act as an elytra; 1.7 has no elytra, so the mantle
 * gets its own glide state: hold jump while falling fast to start, it ends on landing, in liquids or when flying.
 * Movement while gliding follows vanilla elytra flight physics (applied from MixinEntityLivingBase).
 */
public class VicioGlideHandler {

    public static final String TAG = "AS_VicioGlide";

    public static boolean canGlide(EntityPlayer pl) {
        return ItemCape.getCapeEffect(pl, Constellations.vicio) != null && !pl.onGround
            && !pl.capabilities.isFlying
            && !pl.isInWater()
            && !pl.handleLavaMovement()
            && pl.ridingEntity == null;
    }

    public static void setGliding(EntityPlayer pl, boolean gliding) {
        pl.getEntityData()
            .setBoolean(TAG, gliding);
        if (gliding) {
            pl.fallDistance = 0;
        }
    }

    /** Called from the player tick on both sides. With Et Futurum Requiem the mantle uses EFR's elytra instead. */
    public static void tick(EntityPlayer pl) {
        if (hellfirepvp.astralsorcery.common.integrations.efr.ElytraIntegration.isLoaded()) {
            if (pl.getEntityData()
                .getBoolean(TAG)) {
                setGliding(pl, false);
            }
            return;
        }
        boolean gliding = pl.getEntityData()
            .getBoolean(TAG);
        if (gliding && !canGlide(pl)) {
            setGliding(pl, false);
            gliding = false;
        }
        if (!pl.worldObj.isRemote) {
            if (gliding) {
                pl.fallDistance = 0;
                if (pl instanceof EntityPlayerMP && ((EntityPlayerMP) pl).playerNetServerHandler != null) {
                    // not "flying" in the anti-cheat sense
                    ((AccessorNetHandlerPlayServer) ((EntityPlayerMP) pl).playerNetServerHandler)
                        .astralsorcery$setFloatingTickCount(0);
                }
            }
        } else {
            clientTick(pl);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void clientTick(EntityPlayer pl) {
        if (!(pl instanceof net.minecraft.client.entity.EntityPlayerSP)) return;
        net.minecraft.client.entity.EntityPlayerSP spl = (net.minecraft.client.entity.EntityPlayerSP) pl;
        boolean gliding = pl.getEntityData()
            .getBoolean(TAG);
        if (!gliding) {
            if (spl.movementInput.jump && spl.motionY < -0.5 && canGlide(pl)) {
                setGliding(pl, true);
                PacketChannel.CHANNEL.sendToServer(new PktVicioGlide(true));
            }
        } else if (!canGlide(pl) || spl.movementInput.sneak) {
            setGliding(pl, false);
            PacketChannel.CHANNEL.sendToServer(new PktVicioGlide(false));
        }
    }

    /**
     * Vanilla elytra travel (1.9+ EntityLivingBase#travel, elytra branch). Returns true if the movement was handled.
     */
    public static boolean travel(EntityLivingBase e) {
        // only the fallback glide; with EFR its own elytra mixin moves the player
        if (!(e instanceof EntityPlayer) || !e.getEntityData()
            .getBoolean(TAG)) return false;
        Vec3 look = e.getLookVec();
        float pitch = e.rotationPitch * 0.017453292F;
        double horiz = Math.sqrt(look.xCoord * look.xCoord + look.zCoord * look.zCoord);
        double speedH = Math.sqrt(e.motionX * e.motionX + e.motionZ * e.motionZ);
        double lookLen = look.lengthVector();
        float lift = MathHelper.cos(pitch);
        lift = (float) ((double) lift * (double) lift * Math.min(1.0D, lookLen / 0.4D));
        e.motionY += -0.08D + (double) lift * 0.06D;

        if (e.motionY < 0.0D && horiz > 0.0D) {
            double d2 = e.motionY * -0.1D * (double) lift;
            e.motionY += d2;
            e.motionX += look.xCoord * d2 / horiz;
            e.motionZ += look.zCoord * d2 / horiz;
        }
        if (pitch < 0.0F && horiz > 0.0D) {
            double d9 = speedH * (double) (-MathHelper.sin(pitch)) * 0.04D;
            e.motionY += d9 * 3.2D;
            e.motionX -= look.xCoord * d9 / horiz;
            e.motionZ -= look.zCoord * d9 / horiz;
        }
        if (horiz > 0.0D) {
            e.motionX += (look.xCoord / horiz * speedH - e.motionX) * 0.1D;
            e.motionZ += (look.zCoord / horiz * speedH - e.motionZ) * 0.1D;
        }
        // the mantle is slightly better than an elytra (1.12: motion *= 1.005 after the elytra update)
        e.motionX *= 0.99D * 1.005D;
        e.motionY *= 0.98D * 1.005D;
        e.motionZ *= 0.99D * 1.005D;
        e.moveEntity(e.motionX, e.motionY, e.motionZ);
        return true;
    }
}
