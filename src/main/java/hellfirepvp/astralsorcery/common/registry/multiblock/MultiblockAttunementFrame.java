/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.multiblock;

import hellfirepvp.astralsorcery.common.util.struct.BlockMeta;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockAttunementFrame
 * Created by HellFirePvP
 * Date: 28.11.2016 / 10:36
 */
public class MultiblockAttunementFrame extends PatternBlockArray {

    public MultiblockAttunementFrame() {
        load();
    }

    private void load() {
        BlockMeta mar = new BlockMeta(BlocksAS.blockMarble, hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.ARCH.getMeta());
        BlockMeta mbl = new BlockMeta(BlocksAS.blockBlackMarble, hellfirepvp.astralsorcery.common.block.BlockBlackMarble.BlackMarbleBlockType.RAW.getMeta());

        addBlock(0, 0, 0, BlocksAS.attunementAltar);// .getDefaultState());
        // addBlock(0, 1, 0, BlocksAS.blockStructural.getDefaultState().withProperty(BlockStructural.BLOCK_TYPE, BlockStructural.BlockType.ATTUNEMENT_ALTAR_STRUCT));

        addBlockCube(mar, -7, -1, -8, 7, -1, -8);
        addBlockCube(mar, -7, -1, 8, 7, -1, 8);
        addBlockCube(mar, -8, -1, -7, -8, -1, 7);
        addBlockCube(mar, 8, -1, -7, 8, -1, 7);

        addBlockCube(mbl, -7, -1, -7, 7, -1, 7);

        pillarAt(-8, -0, -8);
        pillarAt(-8, -0, 8);
        pillarAt(8, -0, -8);
        pillarAt(8, -0, 8);

        addBlock(-9, -1, -9, mar);
        addBlock(-9, -1, -8, mar);
        addBlock(-9, -1, -7, mar);
        addBlock(-8, -1, -9, mar);
        addBlock(-7, -1, -9, mar);

        addBlock(-9, -1, 9, mar);
        addBlock(-9, -1, 8, mar);
        addBlock(-9, -1, 7, mar);
        addBlock(-8, -1, 9, mar);
        addBlock(-7, -1, 9, mar);

        addBlock(9, -1, -9, mar);
        addBlock(9, -1, -8, mar);
        addBlock(9, -1, -7, mar);
        addBlock(8, -1, -9, mar);
        addBlock(7, -1, -9, mar);

        addBlock(9, -1, 9, mar);
        addBlock(9, -1, 8, mar);
        addBlock(9, -1, 7, mar);
        addBlock(8, -1, 9, mar);
        addBlock(7, -1, 9, mar);
    }

    private void pillarAt(int x, int y, int z) {
        BlockMeta mru = new BlockMeta(BlocksAS.blockMarble, hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.RUNED.getMeta());
        BlockMeta mpl = new BlockMeta(BlocksAS.blockMarble, hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.PILLAR.getMeta());
        BlockMeta mch = new BlockMeta(BlocksAS.blockMarble, hellfirepvp.astralsorcery.common.block.BlockMarble.MarbleBlockType.CHISELED.getMeta());

        addBlock(x, y, z, mru);
        addBlock(x, y + 1, z, mpl);
        addBlock(x, y + 2, z, mpl);
        addBlock(x, y + 3, z, mpl);
        addBlock(x, y + 4, z, mch);
    }

    @Override
    public Map<BlockPos, Block> placeInWorld(World world, BlockPos center) {
        Map<BlockPos, Block> placed = super.placeInWorld(world, center);
        world.setBlockToAir(center.getX(), center.getY() + 1, center.getZ());
        BlockPos up = center.offset(ForgeDirection.UP, 1);
        world.setBlockToAir(up.getX(), up.getY(), up.getZ());
        placed.remove(center);
        placed.remove(center.offset(ForgeDirection.UP, 1));
        return placed;
    }
}
