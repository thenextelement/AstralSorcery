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

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockStarlightRelay
 * Created by HellFirePvP
 * Date: 30.03.2017 / 14:07
 */
public class MultiblockStarlightRelay extends PatternBlockArray {

    public MultiblockStarlightRelay() {
        load();
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;
        BlockMeta chiseled = new BlockMeta(marble, hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.CHISELED.getMeta());
        BlockMeta arch = new BlockMeta(marble, hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.ARCH.getMeta());
        BlockMeta sooty = new BlockMeta(BlocksAS.blockBlackMarble, hellfirepvp.astralsorcery.common.block.BlockBlackMarble.BlackMarbleBlockType.RAW.getMeta());

        addBlock(0, 0, 0, BlocksAS.attunementRelay);// .getDefaultState());

        addBlock(-1, -1, -1, chiseled);
        addBlock(1, -1, -1, chiseled);
        addBlock(1, -1, 1, chiseled);
        addBlock(-1, -1, 1, chiseled);

        addBlock(-1, -1, 0, arch);
        addBlock(1, -1, 0, arch);
        addBlock(0, -1, 1, arch);
        addBlock(0, -1, -1, arch);
        addBlock(0, -1, 0, sooty);
    }

}
