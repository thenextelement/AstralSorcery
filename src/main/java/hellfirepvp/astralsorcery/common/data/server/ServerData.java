/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.ISaveHandler;

import cpw.mods.fml.common.FMLLog;
import hellfirepvp.astralsorcery.common.data.DataWorldSkyHandlers;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.config.Config;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ServerData
 * Created by HellFirePvP
 *
 * Decides which dimensions get a constellation sky handler (config 'skySupportedDimensions' plus dimensions
 * requested at runtime) and pushes that list to DataWorldSkyHandlers. This was dropped in the port, leaving every
 * dimension without constellations or starlight.
 */
public class ServerData {

    public static List<Integer> fileRequestedDimWhitelists = new ArrayList<>();

    @Nullable
    public static File getServerDataFile() {
        ISaveHandler handler;
        try {
            handler = MinecraftServer.getServer()
                .getEntityWorld()
                .getSaveHandler();
        } catch (Exception exc) {
            return null;
        }
        return getServerDataFile(handler);
    }

    @Nullable
    public static File getServerDataFile(ISaveHandler handler) {
        File worldDir;
        try {
            worldDir = handler.getWorldDirectory();
        } catch (Exception exc) {
            return null;
        }
        if (worldDir == null) return null;
        File f = new File(worldDir, "AstralSorcery_ServerData.dat");
        if (!f.exists()) {
            f.getParentFile()
                .mkdirs();
            try {
                CompressedStreamTools.write(new NBTTagCompound(), f);
            } catch (IOException ignored) {}
        }
        return f;
    }

    public static void reloadDataFromSaveHandler(ISaveHandler handler) {
        fileRequestedDimWhitelists = new ArrayList<>();
        loadDataFromFile(handler);
        updateData(handler);
    }

    public static void writeData() {
        updateData(null);
        fileRequestedDimWhitelists = new ArrayList<>();
    }

    public static void addDimensionToHandle(int dimId) {
        if (!fileRequestedDimWhitelists.contains(dimId)) {
            fileRequestedDimWhitelists.add(dimId);
            updateData(null);
        }
    }

    public static boolean isDimensionWhitelisted(int dimId) {
        return fileRequestedDimWhitelists.contains(dimId);
    }

    private static void updateData(@Nullable ISaveHandler handler) {
        File dataFile = handler == null ? getServerDataFile() : getServerDataFile(handler);
        try {
            if (dataFile == null) {
                FMLLog.bigWarning(
                    "[AstralSorcery] Couldn't find folder for AstralSorcery_ServerData.dat - Are you calling this too early or too late in the execution?");
                return;
            }
            List<Integer> collect = new ArrayList<>(fileRequestedDimWhitelists);
            for (Integer configDim : Config.constellationSkyDimWhitelist) {
                if (!collect.contains(configDim)) collect.add(configDim);
            }
            int[] ids = new int[collect.size()];
            for (int i = 0; i < ids.length; i++) ids[i] = collect.get(i);
            NBTTagCompound out = new NBTTagCompound();
            out.setIntArray("dimWhitelist", ids);
            CompressedStreamTools.write(out, dataFile);
        } catch (IOException e) {
            FMLLog.bigWarning(
                "[AstralSorcery] Couldn't write ServerData File (AstralSorcery_ServerData.dat) - Expect issues.");
        } finally {
            pushData();
        }
    }

    private static void pushData() {
        List<Integer> all = new ArrayList<>(fileRequestedDimWhitelists);
        for (Integer configDim : Config.constellationSkyDimWhitelist) {
            if (!all.contains(configDim)) all.add(configDim);
        }
        DataWorldSkyHandlers skyHandlers = SyncDataHolder.getDataServer(SyncDataHolder.DATA_SKY_HANDLERS);
        skyHandlers.update(all);
    }

    private static void loadDataFromFile(@Nullable ISaveHandler handler) {
        File dataFile = handler == null ? getServerDataFile() : getServerDataFile(handler);
        if (dataFile == null) return;
        try {
            NBTTagCompound cmp = CompressedStreamTools.read(dataFile);
            if (cmp != null) {
                for (int id : cmp.getIntArray("dimWhitelist")) {
                    if (!fileRequestedDimWhitelists.contains(id)) fileRequestedDimWhitelists.add(id);
                }
            }
        } catch (IOException e) {
            FMLLog.bigWarning(
                "[AstralSorcery] Couldn't read ServerData File (AstralSorcery_ServerData.dat) - Expect issues.");
        }
        for (Integer configDim : Config.constellationSkyDimWhitelist) {
            if (!fileRequestedDimWhitelists.contains(configDim)) fileRequestedDimWhitelists.add(configDim);
        }
    }
}
