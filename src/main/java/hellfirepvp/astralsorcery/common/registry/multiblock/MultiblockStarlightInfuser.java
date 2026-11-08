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
 * Class: MultiblockStarlightInfuser
 * Created by HellFirePvP
 * Date: 11.12.2016 / 16:35
 */
public class MultiblockStarlightInfuser extends PatternBlockArray {

    public MultiblockStarlightInfuser() {
        load();
    }

    private void load() {
        BlockMeta mar = new BlockMeta(BlocksAS.blockMarble, hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.ARCH.getMeta());
        BlockMeta mrw = new BlockMeta(BlocksAS.blockMarble, hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.RAW.getMeta());
        BlockMeta mpl = new BlockMeta(BlocksAS.blockMarble, hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.PILLAR.getMeta());
        BlockMeta mch = new BlockMeta(BlocksAS.blockMarble, hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.CHISELED.getMeta());
        BlockMeta mru = new BlockMeta(BlocksAS.blockMarble, hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.RUNED.getMeta());

        Block liquidStarlight = BlocksAS.blockLiquidStarlight;// .getDefaultState();

        addBlock(0, 0, 0, BlocksAS.starlightInfuser);// .getDefaultState());

        addBlockCube(mar, -2, -1, -3, 2, -1, 3);
        addBlockCube(mar, -3, -1, -2, 3, -1, 2);

        addBlockCube(mru, -2, -1, -2, 2, -1, 2);

        addBlock(0, -1, 0, Blocks.lapis_block);// .getDefaultState());
        addBlock(-2, -1, -1, liquidStarlight);
        addBlock(-2, -1, 0, liquidStarlight);
        addBlock(-2, -1, 1, liquidStarlight);
        addBlock(2, -1, -1, liquidStarlight);
        addBlock(2, -1, 0, liquidStarlight);
        addBlock(2, -1, 1, liquidStarlight);
        addBlock(-1, -1, -2, liquidStarlight);
        addBlock(0, -1, -2, liquidStarlight);
        addBlock(1, -1, -2, liquidStarlight);
        addBlock(-1, -1, 2, liquidStarlight);
        addBlock(0, -1, 2, liquidStarlight);
        addBlock(1, -1, 2, liquidStarlight);

        addBlock(-2, -2, -1, mrw);
        addBlock(-2, -2, 0, mrw);
        addBlock(-2, -2, 1, mrw);
        addBlock(2, -2, -1, mrw);
        addBlock(2, -2, 0, mrw);
        addBlock(2, -2, 1, mrw);
        addBlock(-1, -2, -2, mrw);
        addBlock(0, -2, -2, mrw);
        addBlock(1, -2, -2, mrw);
        addBlock(-1, -2, 2, mrw);
        addBlock(0, -2, 2, mrw);
        addBlock(1, -2, 2, mrw);

        addBlock(3, 3, 2, mar);
        addBlock(3, 3, -2, mar);
        addBlock(-3, 3, 2, mar);
        addBlock(-3, 3, -2, mar);
        addBlock(2, 3, 3, mar);
        addBlock(-2, 3, 3, mar);
        addBlock(2, 3, -3, mar);
        addBlock(-2, 3, -3, mar);

        addBlock(3, 4, 1, mar);
        addBlock(3, 4, 0, mar);
        addBlock(3, 4, -1, mar);
        addBlock(-3, 4, 1, mar);
        addBlock(-3, 4, 0, mar);
        addBlock(-3, 4, -1, mar);
        addBlock(1, 4, 3, mar);
        addBlock(0, 4, 3, mar);
        addBlock(-1, 4, 3, mar);
        addBlock(1, 4, -3, mar);
        addBlock(0, 4, -3, mar);
        addBlock(-1, 4, -3, mar);

        addBlock(-3, 0, 3, mpl);
        addBlock(-3, 1, 3, mpl);
        addBlock(-3, 2, 3, mch);
        addBlock(-3, 0, -3, mpl);
        addBlock(-3, 1, -3, mpl);
        addBlock(-3, 2, -3, mch);
        addBlock(3, 0, 3, mpl);
        addBlock(3, 1, 3, mpl);
        addBlock(3, 2, 3, mch);
        addBlock(3, 0, -3, mpl);
        addBlock(3, 1, -3, mpl);
        addBlock(3, 2, -3, mch);
    }

}
