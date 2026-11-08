/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMarbleSlab
 * Created by HellFirePvP
 * Date: 05.07.2017 / 18:47
 * (backported from 1.12.2-1.8.5 to 1.7.10; meta & 7 = type, meta & 8 = top half like vanilla 1.7 slabs)
 */
public class BlockMarbleSlab extends BlockSlab {

    public BlockMarbleSlab(boolean isDouble) {
        super(isDouble, Material.rock);
        setBlockName(isDouble ? "BlockMarbleDoubleSlab" : "BlockMarbleSlab");
        setHardness(1.0F);
        setHarvestLevel("pickaxe", 1);
        setResistance(3.0F);
        setStepSound(soundTypeStone);
        if (!isDouble) {
            setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        }
        useNeighborBrightness = true;
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(BlocksAS.blockMarbleSlab);
    }

    @Override
    public int damageDropped(int meta) {
        return EnumType.byMetadata(meta & 7)
            .ordinal();
    }

    @Override
    protected ItemStack createStackedBlock(int meta) {
        return new ItemStack(BlocksAS.blockMarbleSlab, 2, meta & 7);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z) {
        return Item.getItemFromBlock(BlocksAS.blockMarbleSlab);
    }

    @Override
    public String func_150002_b(int meta) {
        return super.getUnlocalizedName() + "." + EnumType.byMetadata(meta & 7)
            .getName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        if (field_150004_a) return;
        for (EnumType type : EnumType.values()) {
            list.add(new ItemStack(item, 1, type.ordinal()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return BlockMarble.MarbleBlockType.BRICKS.asBlock()
            .getIcon(side, BlockMarble.MarbleBlockType.BRICKS.getMeta());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {}

    public static enum EnumType {

        BRICKS;

        public String getName() {
            return name().toLowerCase();
        }

        public static EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= values().length) {
                meta = 0;
            }
            return values()[meta];
        }
    }
}
