/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.effect.time;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.data.DataTimeFreezeEffects;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TimeStopController
 * Created by HellFirePvP
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
public class TimeStopController implements ITickHandler {

    private Map<Integer, List<TimeStopZone>> activeTimeStopZones = new HashMap<>();

    public static TimeStopController INSTANCE = new TimeStopController();

    private TimeStopController() {}

    /**
     * Stops tile- and living entity ticks in a specific region of a world.
     *
     * @param ownerEntity the entity that caused this effect and is excluded from its freeze effects
     * @return null if the world's provider is null, otherwise a registered and running time stop zone
     */
    @Nullable
    public static TimeStopZone freezeWorldAt(@Nullable Entity ownerEntity, @Nonnull World world,
        @Nonnull BlockPos offset, float range, int maxAge) {
        if (world.provider == null || world.isRemote) return null;
        TimeStopZone stopZone = new TimeStopZone(ownerEntity, range, offset, world, maxAge);
        int dimId = world.provider.dimensionId;
        INSTANCE.activeTimeStopZones.computeIfAbsent(dimId, (id) -> new LinkedList<>())
            .add(stopZone);
        data().server_addNewEffect(dimId, TimeStopEffectHelper.fromZone(stopZone));
        return stopZone;
    }

    private static DataTimeFreezeEffects data() {
        return SyncDataHolder.getData(Side.SERVER, SyncDataHolder.DATA_TIME_FREEZE_EFFECTS);
    }

    @SubscribeEvent
    public void onWorldClear(WorldEvent.Unload event) {
        World w = event.world;
        if (w == null || w.provider == null || w.isRemote) return;
        int id = w.provider.dimensionId;
        List<TimeStopZone> freezeAreas = activeTimeStopZones.remove(id);
        if (freezeAreas != null) {
            for (TimeStopZone stop : freezeAreas) {
                stop.stopEffect();
            }
        }
        data().server_clearEffects(id);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingTick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase e = event.entityLiving;
        World w = e.worldObj;
        if (w == null || w.provider == null || w.isRemote) return;
        List<TimeStopZone> freezeAreas = activeTimeStopZones.get(w.provider.dimensionId);
        if (freezeAreas == null || freezeAreas.isEmpty()) return;
        for (TimeStopZone stop : freezeAreas) {
            if (stop.interceptEntityTick(e)) {
                stop.handleImportantEntityTicks(e);
                // keep frozen entities in place, 1.7 still applies velocity outside onLivingUpdate
                e.motionX = 0;
                e.motionY = 0;
                e.motionZ = 0;
                event.setCanceled(true);
                return;
            }
        }
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        for (Map.Entry<Integer, List<TimeStopZone>> zoneMap : activeTimeStopZones.entrySet()) {
            for (Iterator<TimeStopZone> iterator = zoneMap.getValue()
                .iterator(); iterator.hasNext();) {
                TimeStopZone zone = iterator.next();
                if (!zone.shouldDespawn()) {
                    zone.onServerTick();
                }
                if (zone.shouldDespawn()) {
                    zone.stopEffect();
                    data().server_removeEffect(zoneMap.getKey(), TimeStopEffectHelper.fromZone(zone));
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.SERVER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.START;
    }

    @Override
    public String getName() {
        return "TimeStop Controller";
    }
}
