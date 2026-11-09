/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.render.block.RenderBlockMarblePillar;
import hellfirepvp.astralsorcery.common.lib.RenderIDs;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;

/**
 * Shared behaviour of marble and sooty marble: full opaque cubes, except the pillar variant which is a thin column
 * (1.12 pillar model). 1.7's opacity is per block, so isOpaqueCube is false and the full-cube behaviour is restored
 * per position (light opacity, solid sides, face culling).
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
public abstract class BlockMarbleBase extends Block implements BlockCustomName, IMarblePillarBlock {

    private final String texturePrefix;
    private final String[] variantNames;

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    @SideOnly(Side.CLIENT)
    private IIcon[] pillarIcons;

    protected BlockMarbleBase(String name, String texturePrefix, String[] variantNames) {
        super(Material.rock);
        this.texturePrefix = texturePrefix;
        this.variantNames = variantNames;
        setBlockName(name);
        setHardness(1.0F);
        setHarvestLevel("pickaxe", 1);
        setResistance(3.0F);
        setStepSound(soundTypeStone);
        setLightOpacity(255);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    private int clampMeta(int meta) {
        return meta < 0 || meta >= variantNames.length ? 0 : meta;
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        return variantNames[clampMeta(meta)];
    }

    @Override
    public int damageDropped(int meta) {
        return clampMeta(meta);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return RenderIDs.marblePillar;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return !IMarblePillarBlock.isPillarAt(world, x, y, z);
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        if (IMarblePillarBlock.isPillarAt(world, x, y, z)) {
            return side == ForgeDirection.UP;
        }
        return true;
    }

    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        return IMarblePillarBlock.isPillarAt(world, x, y, z) ? 0 : 255;
    }

    /** x, y, z is the neighbour the side faces. */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        Block other = world.getBlock(x, y, z);
        if (other instanceof BlockMarbleBase && !IMarblePillarBlock.isPillarAt(world, x, y, z)) {
            return false;
        }
        return super.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        IIcon face = RenderBlockMarblePillar.faceIcon(side);
        if (face != null) return face;
        return getIcon(side, world.getBlockMetadata(x, y, z));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        meta = clampMeta(meta);
        if (meta == PILLAR_META) {
            return getPillarIcon(side < 2 ? Tex.INNER : Tex.PILLAR);
        }
        return icons[meta];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getPillarIcon(Tex tex) {
        return pillarIcons[tex.ordinal()];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        icons = new IIcon[variantNames.length];
        for (int i = 0; i < variantNames.length; i++) {
            icons[i] = reg.registerIcon("astralsorcery:" + texturePrefix + "_" + variantNames[i]);
        }
        pillarIcons = new IIcon[Tex.values().length];
        String p = "astralsorcery:" + texturePrefix + "_pillar";
        pillarIcons[Tex.PILLAR.ordinal()] = icons[PILLAR_META];
        pillarIcons[Tex.TOP.ordinal()] = reg.registerIcon(p + "_top");
        pillarIcons[Tex.BOTTOM.ordinal()] = reg.registerIcon(p + "_bottom");
        pillarIcons[Tex.INNER.ordinal()] = reg.registerIcon(p + "_inner");
        pillarIcons[Tex.UPDOWN.ordinal()] = reg.registerIcon(p + "_updown");
        pillarIcons[Tex.ENGRAVED.ordinal()] = reg.registerIcon("astralsorcery:" + texturePrefix + "_engraved");
    }
}
