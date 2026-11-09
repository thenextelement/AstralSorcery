/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.multiblock;

import net.minecraft.block.Block;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.struct.BlockMeta;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockAltarAttunement
 * Created by HellFirePvP
 * Date: 17.10.2016 / 11:46
 */
public class MultiblockAltarAttunement extends PatternBlockArray {

    public MultiblockAltarAttunement() {
        load();
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;
        Block darkMarble = BlocksAS.blockBlackMarble;
        BlockMeta mch = new BlockMeta(
            marble,
            hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.CHISELED.getMeta());
        BlockMeta mbr = new BlockMeta(
            marble,
            hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.BRICKS.getMeta());
        // Block mrw = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW);
        BlockMeta mar = new BlockMeta(
            marble,
            hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.ARCH.getMeta());
        BlockMeta mpl = new BlockMeta(
            marble,
            hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.PILLAR.getMeta());
        BlockMeta mbl = new BlockMeta(
            darkMarble,
            hellfirepvp.astralsorcery.common.block.BlockBlackMarble.BlackMarbleBlockType.RAW.getMeta());

        // addBlockCube(Blocks.AIR.getDefaultState(), -3, 0, -3, 3, 2, 3);
        addBlockCube(mbl, -3, -1, -3, 3, -1, 3);

        addBlock(
            0,
            0,
            0,
            new BlockMeta(
                BlocksAS.blockAltar,
                hellfirepvp.astralsorcery.common.block.network.BlockAltar.AltarType.ALTAR_2.ordinal()));

        for (int i = -3; i <= 3; i++) {
            addBlock(4, -1, i, mar);
            addBlock(-4, -1, i, mar);
            addBlock(i, -1, 4, mar);
            addBlock(i, -1, -4, mar);
            addBlock(3, -1, i, mbr);
            addBlock(-3, -1, i, mbr);
            addBlock(i, -1, 3, mbr);
            addBlock(i, -1, -3, mbr);
        }
        addBlock(3, -1, 3, mch);
        addBlock(3, -1, -3, mch);
        addBlock(-3, -1, 3, mch);
        addBlock(-3, -1, -3, mch);

        addBlock(2, -1, 0, mbr);
        addBlock(-2, -1, 0, mbr);
        addBlock(0, -1, 2, mbr);
        addBlock(0, -1, -2, mbr);

        for (int i = 0; i < 2; i++) {
            addBlock(3, i, 3, mpl);
            addBlock(3, i, -3, mpl);
            addBlock(-3, i, 3, mpl);
            addBlock(-3, i, -3, mpl);
        }
        addBlock(3, 2, 3, mch);
        addBlock(3, 2, -3, mch);
        addBlock(-3, 2, 3, mch);
        addBlock(-3, 2, -3, mch);
    }

}
