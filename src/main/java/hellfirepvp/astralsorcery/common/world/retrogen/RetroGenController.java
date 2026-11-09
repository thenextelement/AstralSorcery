/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.retrogen;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.event.world.ChunkEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.world.AstralWorldGenerator;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RetroGenController
 * Created by HellFirePvP
 * Date: 12.01.2017 / 21:18
 */
public class RetroGenController {

    private static List<ChunkCoordIntPair> retroGenActive = new LinkedList<>();

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        net.minecraft.world.World world = event.world;
        ChunkCoordIntPair pos = event.getChunk()
            .getChunkCoordIntPair();
        if (world.isRemote || !event.getChunk().isTerrainPopulated || retroGenActive.contains(pos)) return;

        Integer chunkVersion = -1;
        net.minecraft.world.chunk.storage.IChunkLoader loader = ((net.minecraft.world.WorldServer) world).theChunkProviderServer.currentChunkLoader;
        if (loader instanceof net.minecraft.world.chunk.storage.AnvilChunkLoader
            && ((net.minecraft.world.chunk.storage.AnvilChunkLoader) loader)
                .chunkExists(world, pos.chunkXPos, pos.chunkZPos)) {
            chunkVersion = ChunkVersionController.instance.getGenerationVersion(pos);
            if (chunkVersion == null) {
                AstralSorcery.log
                    .info("[AstralSorcery] No ChunkVersion found for Chunk: " + pos + " - Skipping RetroGen...");
                return;
            }
        }
        if (chunkVersion >= AstralWorldGenerator.CURRENT_WORLD_GENERATOR_VERSION) return;
        AstralSorcery.log.info(
            "[AstralSorcery] Attempting AstralSorcery retrogen for chunk " + pos
                + " - Version "
                + chunkVersion
                + " -> "
                + AstralWorldGenerator.CURRENT_WORLD_GENERATOR_VERSION
                + " - Stack: "
                + retroGenActive.size());
        retroGenActive.add(pos);
        CommonProxy.worldGenerator.handleRetroGen(world, pos, chunkVersion);
        retroGenActive.remove(pos);
    }

}
