/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemSlab;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;

/** Item for the marble slabs; GameRegistry needs a (Block) constructor to find. */
public class ItemBlockMarbleSlab extends ItemSlab {

    public ItemBlockMarbleSlab(Block block) {
        super(block, BlocksAS.blockMarbleSlab, BlocksAS.blockMarbleDoubleSlab, block == BlocksAS.blockMarbleDoubleSlab);
    }
}
