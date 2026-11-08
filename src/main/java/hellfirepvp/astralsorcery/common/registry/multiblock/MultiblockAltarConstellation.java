/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.multiblock;

import hellfirepvp.astralsorcery.common.util.struct.BlockMeta;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockAltarConstellation
 * Created by HellFirePvP
 * Date: 22.10.2016 / 12:48
 */
public class MultiblockAltarConstellation extends PatternBlockArray {

    public MultiblockAltarConstellation() {
        load();
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;
        BlockMeta mch = new BlockMeta(marble, hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.CHISELED.getMeta());
        BlockMeta mbr = new BlockMeta(marble, hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.BRICKS.getMeta());
        BlockMeta mrw = new BlockMeta(marble, hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.RAW.getMeta());
        BlockMeta mru = new BlockMeta(marble, hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.RUNED.getMeta());
        BlockMeta mpl = new BlockMeta(marble, hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.PILLAR.getMeta());
        BlockMeta bml = new BlockMeta(BlocksAS.blockBlackMarble, hellfirepvp.astralsorcery.common.block.BlockBlackMarble.BlackMarbleBlockType.RAW.getMeta());
        Block air = Blocks.air;// .getDefaultState();

        // addBlockCube(air, -4, 0, -4, 4, 3, 4);
        addBlockCube(mbr, -4, -1, -4, 4, -1, 4);
        addBlockCube(bml, -3, -1, -3, 3, -1, 3);

        addBlock(0, 0, 0, new BlockMeta(BlocksAS.blockAltar, hellfirepvp.astralsorcery.common.block.network.BlockAltar.AltarType.ALTAR_3.ordinal()));

        addBlock(-4, -1, -4, mrw);
        addBlock(-4, -1, -3, mrw);
        addBlock(-3, -1, -4, mrw);
        addBlock(4, -1, -4, mrw);
        addBlock(4, -1, -3, mrw);
        addBlock(3, -1, -4, mrw);
        addBlock(-4, -1, 4, mrw);
        addBlock(-4, -1, 3, mrw);
        addBlock(-3, -1, 4, mrw);
        addBlock(4, -1, 4, mrw);
        addBlock(4, -1, 3, mrw);
        addBlock(3, -1, 4, mrw);

        addBlock(-5, -1, -5, mbr);
        addBlock(-5, -1, -4, mbr);
        addBlock(-5, -1, -3, mbr);
        addBlock(-4, -1, -5, mbr);
        addBlock(-3, -1, -5, mbr);
        addBlock(5, -1, -5, mbr);
        addBlock(5, -1, -4, mbr);
        addBlock(5, -1, -3, mbr);
        addBlock(4, -1, -5, mbr);
        addBlock(3, -1, -5, mbr);
        addBlock(-5, -1, 5, mbr);
        addBlock(-5, -1, 4, mbr);
        addBlock(-5, -1, 3, mbr);
        addBlock(-4, -1, 5, mbr);
        addBlock(-3, -1, 5, mbr);
        addBlock(5, -1, 5, mbr);
        addBlock(5, -1, 4, mbr);
        addBlock(5, -1, 3, mbr);
        addBlock(4, -1, 5, mbr);
        addBlock(3, -1, 5, mbr);

        /*
         * addBlock(-4, -1, -2, mbr);
         * addBlock(-2, -1, -4, mbr);
         * addBlock(-4, -1, 2, mbr);
         * addBlock(-2, -1, 4, mbr);
         * addBlock( 4, -1, -2, mbr);
         * addBlock( 2, -1, -4, mbr);
         * addBlock( 4, -1, 2, mbr);
         * addBlock( 2, -1, 4, mbr);
         */

        addBlock(-4, 0, -4, mru);
        addBlock(-4, 0, 4, mru);
        addBlock(4, 0, -4, mru);
        addBlock(4, 0, 4, mru);
        addBlock(-4, 1, -4, mpl);
        addBlock(-4, 1, 4, mpl);
        addBlock(4, 1, -4, mpl);
        addBlock(4, 1, 4, mpl);
        addBlock(-4, 2, -4, mpl);
        addBlock(-4, 2, 4, mpl);
        addBlock(4, 2, -4, mpl);
        addBlock(4, 2, 4, mpl);
        addBlock(-4, 3, -4, mch);
        addBlock(-4, 3, 4, mch);
        addBlock(4, 3, -4, mch);
        addBlock(4, 3, 4, mch);
    }

}
