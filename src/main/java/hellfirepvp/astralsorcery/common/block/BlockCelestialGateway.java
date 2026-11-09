package hellfirepvp.astralsorcery.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.GatewayCache;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.BlockBoxUtil;
import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCelestialGateway
 * Created by HellFirePvP
 * Date: 16.04.2017 / 20:19
 */
public class BlockCelestialGateway extends BlockContainer {

    private static final AxisAlignedBB box = AxisAlignedBB
        .getBoundingBox(1D / 16D, 0D / 16D, 1D / 16D, 15D / 16D, 1D / 16D, 10D / 15D);

    public BlockCelestialGateway() {
        super(Material.rock);
        setBlockName("BlockCelestialGateway");
        setStepSound(soundTypeStone);
        setHardness(4F);
        setResistance(40F);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return BlockBoxUtil.offset(box, x, y, z);
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
        BlockPos pos = new BlockPos(x, y, z);
        GatewayCache cache = WorldCacheManager.getOrLoadData(worldIn, WorldCacheManager.SaveKey.GATEWAY_DATA);
        cache.removePosition(worldIn, pos);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCelestialGateway();
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TileCelestialGateway();
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon("astralsorcery:particle/gateway");
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

    // @Override
    // public boolean canRenderInLayer(Block state, BlockRenderLayer layer) {
    // return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    // }

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

}
