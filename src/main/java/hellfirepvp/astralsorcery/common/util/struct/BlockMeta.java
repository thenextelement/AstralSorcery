/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.struct;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.util.BlockPos;

/** A block plus metadata: the 1.7 stand-in for the 1.10 IBlockStates used in structure/multiblock definitions. */
public final class BlockMeta {

    public final Block block;
    public final int meta;

    public BlockMeta(Block block, int meta) {
        this.block = block;
        this.meta = meta;
    }

    public void place(World world, BlockPos pos) {
        world.setBlock(pos.getX(), pos.getY(), pos.getZ(), block, meta, 3);
    }
}
