/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.util.AxisAlignedBB;

/**
 * 1.10 block boxes are relative to the block position; 1.7.10's getCollisionBoundingBoxFromPool /
 * getSelectedBoundingBoxFromPool must return world-absolute boxes (they also drive TESR frustum culling).
 */
public class BlockBoxUtil {

    public static AxisAlignedBB offset(AxisAlignedBB relative, int x, int y, int z) {
        return relative == null ? null : relative.getOffsetBoundingBox(x, y, z);
    }
}
