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
 * Class: BlockBlackMarble
 * Created by HellFirePvP
 * Date: 21.10.2016 / 23:02
 * (pillar model and sooty marble variants backported from 1.12.2-1.8.5)
 */
public class BlockBlackMarble extends BlockMarbleBase {

    public BlockBlackMarble() {
        super("BlockBlackMarble", "black_marble", names());
    }

    private static String[] names() {
        BlackMarbleBlockType[] values = BlackMarbleBlockType.values();
        String[] out = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            out[i] = values[i].getName();
        }
        return out;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (BlackMarbleBlockType t : BlackMarbleBlockType.values()) {
            list.add(new ItemStack(item, 1, t.getMeta()));
        }
    }

    public static enum BlackMarbleBlockType {

        RAW(0),
        BRICKS(1),
        PILLAR(2),
        ARCH(3),
        CHISELED(4),
        ENGRAVED(5),
        RUNED(6);

        private final int meta;

        private BlackMarbleBlockType(int meta) {
            this.meta = meta;
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.blockBlackMarble, 1, meta);
        }

        public Block asBlock() {
            return BlocksAS.blockBlackMarble;
        }

        public int getMeta() {
            return meta;
        }

        public String getName() {
            return name().toLowerCase();
        }
    }
}
