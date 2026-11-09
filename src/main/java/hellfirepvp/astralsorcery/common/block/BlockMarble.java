/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMarble
 * Created by HellFirePvP
 * Date: 22.05.2016 / 16:13
 * (pillar model and sooty marble variants backported from 1.12.2-1.8.5)
 */
public class BlockMarble extends BlockMarbleBase {

    public BlockMarble() {
        super("BlockMarble", "marble", names());
    }

    private static String[] names() {
        MarbleBlockType[] values = MarbleBlockType.values();
        String[] out = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            out[i] = values[i].getName();
        }
        return out;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (MarbleBlockType t : MarbleBlockType.values()) {
            list.add(new ItemStack(item, 1, t.getMeta()));
        }
    }

    public static enum MarbleBlockType {

        RAW(0),
        BRICKS(1),
        PILLAR(2),
        ARCH(3),
        CHISELED(4),
        ENGRAVED(5),
        RUNED(6);

        private final int meta;

        private MarbleBlockType(int meta) {
            this.meta = meta;
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.blockMarble, 1, meta);
        }

        public Block asBlock() {
            return BlocksAS.blockMarble;
        }

        public int getMeta() {
            return meta;
        }

        public String getName() {
            return name().toLowerCase();
        }
    }
}
