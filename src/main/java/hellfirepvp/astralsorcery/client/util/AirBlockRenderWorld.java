/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AirBlockRenderWorld
 * Created by HellFirePvP
 * Date: 07.02.2017 / 01:32
 */
public class AirBlockRenderWorld implements IBlockAccess {

    private final BiomeGenBase globalBiome;
    private final WorldType globalType;

    public AirBlockRenderWorld(BiomeGenBase globalBiome, WorldType globalType) {
        this.globalBiome = globalBiome;
        this.globalType = globalType;
    }

    private final java.util.Map<Long, Integer> metaOverrides = new java.util.HashMap<>();

    /** Lets RenderBlocks see the metadata of a faked block at the given position. */
    public void setMetadata(int x, int y, int z, int meta) {
        metaOverrides.put(key(x, y, z), meta);
    }

    private static long key(int x, int y, int z) {
        return ((long) x & 0x3FFFFFFL) << 38 | ((long) y & 0xFFFL) << 26 | ((long) z & 0x3FFFFFFL);
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return net.minecraft.init.Blocks.air;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        return null;
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int lightValue) {
        return 0;
    }

    // @Override
    // public int getCombinedLight(BlockPos pos, int lightValue) {
    // return 0;
    // }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
        Integer m = metaOverrides.get(key(x, y, z));
        return m == null ? 0 : m;
    }
    // public Block getBlockState(BlockPos pos) {
    // return Blocks.AIR.getDefaultState();
    // }

    @Override
    public boolean isAirBlock(int x, int y, int z) {
        return true;
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(int x, int z) {
        return globalBiome;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public boolean extendedLevelsInChunkCache() {
        return false;
    }

    @Override
    public int isBlockProvidingPowerTo(int x, int y, int z, int directionIn) {
        return 0;
    }

    // @Override
    // public WorldType getWorldType() {
    // return globalType;
    // }

    @Override
    public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
        return _default;
    }

}
