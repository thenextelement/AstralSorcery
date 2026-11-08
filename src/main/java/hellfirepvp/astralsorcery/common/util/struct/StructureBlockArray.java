/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.struct;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.base.Mods;
import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiBlockArray
 * Created by HellFirePvP
 * Date: 30.07.2016 / 16:07
 */
public class StructureBlockArray extends BlockArray {

    /** Offsets of the shrine lights (sea lanterns in 1.10+); stored as glowstone in the pattern. */
    private final Set<BlockPos> lightOffsets = new HashSet<>();

    /** A sea lantern if Et Futurum Requiem provides one, glowstone otherwise. */
    public void addLightBlock(int x, int y, int z) {
        addBlock(x, y, z, Blocks.glowstone);
        lightOffsets.add(new BlockPos(x, y, z));
    }

    public static Block getShrineLightBlock() {
        if (Mods.ETFUTURUM.isPresent()) {
            Block lantern = GameRegistry.findBlock("etfuturum", "sea_lantern"); // null if disabled in EFR's config
            if (lantern != null) return lantern;
        }
        return Blocks.glowstone;
    }

    @Override
    public Map<BlockPos, Block> placeInWorld(World world, BlockPos center) {
        return placeStructure(world, center);
    }

    private Map<BlockPos, Block> placeStructure(World world, BlockPos center) {
        Map<BlockPos, Block> result = super.placeInWorld(world, center);
        Block light = getShrineLightBlock();
        if (light != Blocks.glowstone) {
            for (BlockPos offset : lightOffsets) {
                BlockPos at = center.add(offset);
                world.setBlock(at.getX(), at.getY(), at.getZ(), light, 0, 2);
                result.put(at, light);
            }
        }
        // Water placed during world generation stays a still source: its block update is dropped while the
        // neighbouring chunks are not there yet. Use flowing water and nudge it again once the area exists.
        Set<BlockPos> liquids = new HashSet<>();
        for (Map.Entry<BlockPos, Block> e : result.entrySet()) {
            if (e.getValue() == Blocks.water) {
                BlockPos at = e.getKey();
                world.setBlock(at.getX(), at.getY(), at.getZ(), Blocks.flowing_water, 0, 2);
                e.setValue(Blocks.flowing_water);
                liquids.add(at);
            }
        }
        if (!liquids.isEmpty() && !world.isRemote) {
            AstralSorcery.proxy.scheduleDelayed(() -> {
                for (BlockPos at : liquids) {
                    Block b = world.getBlock(at.getX(), at.getY(), at.getZ());
                    if (b instanceof BlockLiquid) {
                        world.scheduleBlockUpdate(at.getX(), at.getY(), at.getZ(), b, b.tickRate(world));
                    }
                }
            }, 20);
        }
        return result;
    }

    public Map<BlockPos, Block> placeInWorld(World world, BlockPos center, PastPlaceProcessor processor) {
        Map<BlockPos, Block> result = placeStructure(world, center);
        if (processor != null) {
            for (Map.Entry<BlockPos, Block> entry : result.entrySet())
                processor.process(world, entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static interface PastPlaceProcessor {

        public void process(World world, BlockPos pos, Block currentState);

    }

}
