package hellfirepvp.astralsorcery.common.util.struct;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OreDiscoverer
 * Created by HellFirePvP
 * Date: 12.03.2017 / 14:06
 */
public class OreDiscoverer {

    public static BlockArray startSearch(World world, Vector3 position, int xzLimit) {
        xzLimit = MathHelper.clamp_int(xzLimit, 0, 32);
        BlockPos originPos = position.toBlockPos();
        BlockArray out = new BlockArray();
        java.util.Set<Long> knownOres = new java.util.HashSet<>();
        java.util.Set<Long> knownNonOres = new java.util.HashSet<>();
        for (int xx = -xzLimit; xx <= xzLimit; xx++) {
            for (int zz = -xzLimit; zz <= xzLimit; zz++) {
                int wx = originPos.getX() + xx;
                int wz = originPos.getZ() + zz;
                net.minecraft.world.chunk.Chunk c = world.getChunkFromBlockCoords(wx, wz);
                int highest = (c.getTopFilledSegment() + 1) * 16;
                for (int y = 0; y < highest; y++) {
                    Block at = c.getBlock(wx & 15, y, wz & 15);
                    if (at == net.minecraft.init.Blocks.air) continue;
                    int meta = c.getBlockMetadata(wx & 15, y, wz & 15);
                    long key = ((long) Block.getIdFromBlock(at) << 4) | (meta & 15);
                    if (knownNonOres.contains(key)) continue;
                    if (!knownOres.contains(key)) {
                        if (!isOre(at, meta)) {
                            knownNonOres.add(key);
                            continue;
                        }
                        knownOres.add(key);
                    }
                    out.addBlock(new BlockPos(wx, y, wz), new BlockMeta(at, meta));
                }
            }
        }
        return out;
    }

    private static boolean isOre(Block state, int meta) {
        if (state instanceof BlockOre) { // WELL that's easy enough.
            return true;
        }
        if (net.minecraft.item.Item.getItemFromBlock(state) == null) return false;
        ItemStack blockStack = new ItemStack(state, 1, state.damageDropped(meta));
        return ItemUtils.hasOreNamePart(blockStack, "ore");
    }

}
