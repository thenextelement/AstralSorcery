/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Marble blocks whose pillar variant connects vertically (1.12 pillar model: a thin column with a cap on the
 * top/bottom end). 1.7 has no block states, so the part is derived from the neighbours like 1.12's getActualState.
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
public interface IMarblePillarBlock {

    int PILLAR_META = 2;

    enum Part {
        PILLAR,
        PILLAR_TOP,
        PILLAR_BOTTOM
    }

    /** Texture slots of the 1.12 pillar models. */
    enum Tex {
        PILLAR,
        TOP,
        BOTTOM,
        INNER,
        UPDOWN,
        ENGRAVED
    }

    @SideOnly(Side.CLIENT)
    IIcon getPillarIcon(Tex tex);

    static boolean isPillar(Block block, int meta) {
        return block instanceof IMarblePillarBlock && meta == PILLAR_META;
    }

    static boolean isPillarAt(IBlockAccess world, int x, int y, int z) {
        return isPillar(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
    }

    static Part getPart(IBlockAccess world, int x, int y, int z) {
        Block self = world.getBlock(x, y, z);
        boolean up = world.getBlock(x, y + 1, z) == self && isPillarAt(world, x, y + 1, z);
        boolean down = world.getBlock(x, y - 1, z) == self && isPillarAt(world, x, y - 1, z);
        if (up && !down) return Part.PILLAR_BOTTOM;
        if (down && !up) return Part.PILLAR_TOP;
        return Part.PILLAR;
    }
}
