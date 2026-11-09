/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipe;
import hellfirepvp.astralsorcery.common.crafting.grindstone.GrindstoneRecipeRegistry;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.IVariantTileProvider;
import hellfirepvp.astralsorcery.common.tile.TileGrindstone;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import hellfirepvp.astralsorcery.common.util.BlockBoxUtil;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStoneMachine
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:11
 */
public class BlockMachine extends BlockContainer implements BlockCustomName {

    private static final Random rand = new Random();

    // public static PropertyEnum<MachineType> MACHINE_TYPE = PropertyEnum.create("machine", MachineType.class);

    public BlockMachine() {
        super(Material.rock); // Material.BARRIER -> (new
                              // Material(MapColor.airColor).setRequiresTool().setImmovableMobility());
        setBlockName("BlockMachine");
        setHardness(3.0F);
        setStepSound(soundTypeStone);
        setResistance(25.0F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        BlockPos pos = new BlockPos(x, y, z);
        MachineType type = MachineType.values()[meta];
        switch (type) {
            case TELESCOPE:
                RenderingUtils.playBlockBreakParticles(pos.up(), Blocks.planks);
            case GRINDSTONE:
                RenderingUtils.playBlockBreakParticles(pos, Blocks.planks);
                break;
        }
        return false;
    }

    @Override
    public String getHarvestTool(int meta) {
        MachineType t = MachineType.values()[meta];
        switch (t) {
            case TELESCOPE:
                return "axe";
            case GRINDSTONE:
                return "pickaxe";
        }
        return super.getHarvestTool(meta);
    }

    // @Override
    // public SoundType getSoundType(Block state, World world, BlockPos pos, @Nullable Entity entity) {
    // MachineType t = state.getValue(MACHINE_TYPE);
    // switch (t) {
    // case TELESCOPE:
    // return SoundType.WOOD;
    // case GRINDSTONE:
    // return SoundType.STONE;
    // }
    // return super.getSoundType(state, world, pos, entity);
    // }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        int meta = worldIn.getBlockMetadata(x, y, z);
        MachineType type = MachineType.values()[meta];
        switch (type) {
            case TELESCOPE:
                return BlockBoxUtil.offset(AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 2, 1), x, y, z);
        }
        return super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        int meta = worldIn.getBlockMetadata(x, y, z);
        MachineType type = MachineType.values()[meta];
        switch (type) {
            case TELESCOPE:
                return BlockBoxUtil.offset(AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 2, 1), x, y, z);
        }
        return super.getSelectedBoundingBoxFromPool(worldIn, x, y, z);
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (MachineType type : MachineType.values()) {
            list.add(type.asStack());
        }
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        MachineType type = MachineType.values()[meta];
        if (type == null) return null;
        return type.provideTileEntity(world, type.getMeta());
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        MachineType type = MachineType.values()[meta];
        if (type == null || type != MachineType.GRINDSTONE) return;
        BlockPos pos = new BlockPos(x, y, z);
        TileGrindstone tgr = MiscUtils.getTileAt(worldIn, pos, TileGrindstone.class, true);
        if (tgr == null || tgr.getGrindingItem() == null) return;
        ItemUtils
            .dropItemNaturally(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, tgr.getGrindingItem());
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        BlockPos pos = new BlockPos(x, y, z);
        int meta = worldIn.getBlockMetadata(x, y, z);
        ItemStack stack = player.getHeldItem();
        MachineType type = MachineType.values()[meta];
        if (type == null) return true;
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        switch (type) {
            case TELESCOPE:
                if (player.worldObj.isRemote) {
                    AstralSorcery.proxy
                        .openGui(CommonProxy.EnumGuiId.TELESCOPE, player, player.worldObj, posX, posY, posZ);
                }
                break;
            case GRINDSTONE:
                TileGrindstone tgr = MiscUtils.getTileAt(worldIn, pos, TileGrindstone.class, true);
                if (tgr != null) {
                    if (!worldIn.isRemote) {
                        ItemStack grind = tgr.getGrindingItem();
                        if (grind != null) {
                            if (player.isSneaking()) {
                                if (!player.inventory.addItemStackToInventory(grind)) {
                                    ItemUtils.dropItem(worldIn, posX + 0.5F, posY + 1F, posZ + 0.5F, grind);
                                }
                                tgr.setGrindingItem(null);
                            } else {
                                GrindstoneRecipe recipe = GrindstoneRecipeRegistry.findMatchingRecipe(grind);
                                if (recipe != null) {
                                    GrindstoneRecipe.GrindResult result = recipe.grind(grind);
                                    switch (result.getType()) {
                                        case SUCCESS:
                                            tgr.setGrindingItem(grind); // Update
                                            break;
                                        case ITEMCHANGE:
                                            tgr.setGrindingItem(result.getStack());
                                            break;
                                        case FAIL_BREAK_ITEM:
                                            tgr.setGrindingItem(null);
                                            worldIn.playSoundEffect(
                                                posX + 0.5,
                                                posY + 0.5,
                                                posZ + 0.5,
                                                "random.break",
                                                0.5F,
                                                worldIn.rand.nextFloat() * 0.2F + 0.8F);
                                            break;
                                        default:
                                            break;
                                    }
                                    tgr.playWheelEffect();
                                }
                            }
                        } else if (stack != null) {
                            GrindstoneRecipe recipe = GrindstoneRecipeRegistry.findMatchingRecipe(stack);
                            if (recipe != null) {
                                ItemStack toSet = stack.copy();
                                toSet.stackSize = 1;
                                tgr.setGrindingItem(toSet);
                                worldIn.playSoundEffect(
                                    posX + 0.5,
                                    posY + 0.5,
                                    posZ + 0.5,
                                    "random.pop",
                                    0.5F,
                                    worldIn.rand.nextFloat() * 0.2F + 0.8F);
                                if (!player.capabilities.isCreativeMode) {
                                    stack.stackSize--;
                                    if (stack.stackSize <= 0) {
                                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                                    }
                                }
                            } else if (player.isSneaking()) {
                                return false;
                            }
                        }
                    } else {
                        ItemStack grind = tgr.getGrindingItem();
                        if (grind != null && !player.isSneaking()
                            && GrindstoneRecipeRegistry.findMatchingRecipe(grind) != null) {
                            for (int j = 0; j < 8; j++) {
                                worldIn.spawnParticle(
                                    "crit",
                                    posX + 0.5,
                                    posY + 0.8,
                                    posZ + 0.4,
                                    (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3,
                                    (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3,
                                    (rand.nextBoolean() ? 1 : -1) * rand.nextFloat() * 0.3);
                            }
                        }
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        BlockPos pos = new BlockPos(x, y, z).up();
        MachineType type = MachineType.values()[stack.getItemDamage()];
        switch (type) {
            case TELESCOPE:
                worldIn.setBlock(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    BlocksAS.blockStructural,
                    BlockStructural.BlockType.TELESCOPE_STRUCT.getMeta(),
                    3);
                break;
        }
        super.onBlockPlacedBy(worldIn, x, y, z, placer, stack);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        BlockPos pos = new BlockPos(x, y, z);
        int meta = worldIn.getBlockMetadata(x, y, z);
        switch (meta) {
            case 0:// TELESCOPE:
                if (worldIn.isAirBlock(
                    pos.up()
                        .getX(),
                    pos.up()
                        .getY(),
                    pos.up()
                        .getZ())) {
                    worldIn.setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
                }
                break;
        }
        super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        if (!(world instanceof World)) {
            super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
            return;
        }
        BlockPos pos = new BlockPos(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        switch (meta) {
            case 0:// TELESCOPE:
                if (world.isAirBlock(
                    pos.up()
                        .getX(),
                    pos.up()
                        .getY(),
                    pos.up()
                        .getZ())) {
                    ((World) world).setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
                }
                break;
        }
    }

    // @Override
    // public Block getStateFromMeta(int meta) {
    // return meta < MachineType.values().length ? getDefaultState().withProperty(MACHINE_TYPE,
    // MachineType.values()[meta]) : getDefaultState();
    // }
    //
    // @Override
    // public int getMeta(Block state) {
    // MachineType type = state.getValue(MACHINE_TYPE);
    // return type == null ? 0 : type.ordinal();
    // }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    public boolean isBlockSolid(IBlockAccess worldIn, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    // @Override
    // @SideOnly(Side.CLIENT)
    // public boolean isTranslucent(Block state) {
    // return true;
    // }
    //
    // @Override
    // protected BlockStateContainer createBlockState() {
    // return new BlockStateContainer(this, MACHINE_TYPE);
    // }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isNormalCube() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    private net.minecraft.util.IIcon[] machineIcons;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(net.minecraft.client.renderer.texture.IIconRegister reg) {
        machineIcons = new net.minecraft.util.IIcon[] { reg.registerIcon("astralsorcery:particle/telescope"),
            reg.registerIcon("astralsorcery:particle/grindstone") };
        this.blockIcon = machineIcons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public net.minecraft.util.IIcon getIcon(int side, int meta) {
        return machineIcons[meta >= 0 && meta < machineIcons.length ? meta : 0];
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        MachineType type = MachineType.values()[meta];
        return type.getName();
    }

    // @Override
    // public List<Block> getValidStates() {
    // List<Block> ret = new LinkedList<>();
    // for (MachineType type : MachineType.values()) {
    // ret.add(getDefaultState().withProperty(MACHINE_TYPE, type));
    // }
    // return ret;
    // }
    //
    // @Override
    // public String getMetaName(int meta) {
    // MachineType type = MachineType.values()[meta];
    // return type.getName();
    // }
    //
    // @Override
    // public int getMeta() {
    // return 0;
    // }

    public static enum MachineType implements IVariantTileProvider {

        TELESCOPE((world, pos) -> new TileTelescope()),
        GRINDSTONE((world, pos) -> new TileGrindstone());

        private final IVariantTileProvider provider;

        private MachineType(IVariantTileProvider provider) {
            this.provider = provider;
        }

        @Override
        public TileEntity provideTileEntity(World world, int metadata) {
            return provider.provideTileEntity(world, metadata);
        }

        public int getMeta() {
            return ordinal();
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.blockMachine, 1, getMeta());
        }

        public String getName() {
            return name().toLowerCase();
        }

        @Override
        public String toString() {
            return getName();
        }
    }

}
