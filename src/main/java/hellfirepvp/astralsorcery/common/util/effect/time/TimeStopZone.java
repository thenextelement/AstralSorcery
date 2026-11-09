/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.effect.time;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TimeStopZone
 * Created by HellFirePvP
 * (backported from 1.12.2-1.8.5 to 1.7.10: frozen tiles are taken out of World#loadedTileEntityList)
 */
public class TimeStopZone {

    final int ownerId;
    final boolean hasOwner;
    final float range;
    final BlockPos offset;
    private final World world;
    private int ticksToLive;
    private boolean active = true;

    private List<TileEntity> cachedTiles = new LinkedList<>();

    TimeStopZone(Entity owner, float range, BlockPos offset, World world, int tickLivespan) {
        this.hasOwner = owner != null;
        this.ownerId = this.hasOwner ? owner.getEntityId() : -1;
        this.range = range;
        this.offset = offset;
        this.world = world;
        this.ticksToLive = tickLivespan;
    }

    @SuppressWarnings("unchecked")
    void onServerTick() {
        if (!active) return;
        this.ticksToLive--;

        double rSq = range * range;
        List<TileEntity> loaded = (List<TileEntity>) world.loadedTileEntityList;
        for (TileEntity te : new java.util.ArrayList<>(loaded)) {
            if (te == null || te.isInvalid() || !te.canUpdate()) continue;
            double dx = te.xCoord - offset.getX(), dy = te.yCoord - offset.getY(), dz = te.zCoord - offset.getZ();
            if (dx * dx + dy * dy + dz * dz <= rSq) {
                loaded.remove(te);
                safeCacheTile(te);
            }
        }
    }

    private void safeCacheTile(TileEntity te) {
        for (TileEntity tile : cachedTiles) {
            if (tile == te) return;
        }
        cachedTiles.add(te);
    }

    @SuppressWarnings("unchecked")
    public void stopEffect() {
        List<TileEntity> loaded = (List<TileEntity>) world.loadedTileEntityList;
        for (TileEntity te : cachedTiles) {
            if (!te.isInvalid() && world.getTileEntity(te.xCoord, te.yCoord, te.zCoord) == te && !loaded.contains(te)) {
                loaded.add(te);
            }
        }
        this.cachedTiles.clear();
        this.active = false;
    }

    boolean shouldDespawn() {
        return ticksToLive <= 0 || !active;
    }

    boolean interceptEntityTick(EntityLivingBase e) {
        if (!active || e == null || (hasOwner && e.getEntityId() == ownerId)) return false;
        double dx = e.posX - offset.getX(), dy = e.boundingBox.minY - offset.getY(), dz = e.posZ - offset.getZ();
        return dx * dx + dy * dy + dz * dz <= range * range;
    }

    // Mainly because we still want to be able to do damage.
    void handleImportantEntityTicks(EntityLivingBase e) {
        if (e.hurtTime > 0) {
            e.hurtTime--;
        }
        if (e.hurtResistantTime > 0) {
            e.hurtResistantTime--;
        }
    }
}
