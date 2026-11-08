/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileRitualLink;
import hellfirepvp.astralsorcery.common.util.BlockBoxUtil;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockRitualLink
 * Created by HellFirePvP
 * Date: 06.01.2017 / 02:40
 */
public class BlockRitualLink extends BlockContainer {

    private static final AxisAlignedBB box = AxisAlignedBB
        .getBoundingBox(6D / 16D, 2D / 16D, 6D / 16D, 10D / 16D, 14D / 16D, 10D / 16D);

    public BlockRitualLink() {
        super(Material.rock);
        setBlockName("BlockRitualLink");
        setHardness(3.0F);
        // setSoundType(SoundType.GLASS);
        setResistance(25.0F);
        setHarvestLevel("pickaxe", 2);
        setLightLevel(0.6F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return BlockBoxUtil.offset(box, x, y, z);
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TileRitualLink();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileRitualLink();
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon("astralsorcery:particle/ritual_link");
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isNormalCube() {
        return false;
    }

    @Override
    public boolean func_149730_j() {
        return false;
    }

    // glass parts render in the translucent pass, the rest in the solid pass
    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public boolean canRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }

    @Override
    public int getRenderType() {
        return hellfirepvp.astralsorcery.common.lib.RenderIDs.jsonModel;
    }

    // @Override
    // public boolean canRenderInLayer(BlockRenderLayer layer) {
    // return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    // }
}
