/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.structurelib;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

import com.gtnewhorizon.structurelib.StructureLibAPI;
import com.gtnewhorizon.structurelib.alignment.constructable.IMultiblockInfoContainer;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;

import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.struct.BlockArray;

/**
 * Exposes an Astral Sorcery multiblock (a {@link BlockArray} centred on a tile entity) to StructureLib's Multiblock
 * Hologram Projector: hint particles, creative auto-build and survival auto-build from the player's inventory.
 * AS structures don't rotate, so the facing StructureLib passes in is ignored.
 */
public class BlockArrayInfoContainer<T extends TileEntity> implements IMultiblockInfoContainer<T> {

    private final Function<T, BlockArray> structureProvider;
    private final String descriptionKey;

    public BlockArrayInfoContainer(Function<T, BlockArray> structureProvider, String descriptionKey) {
        this.structureProvider = structureProvider;
        this.descriptionKey = descriptionKey;
    }

    /** Pattern entries that need a block, bottom layer first so survival building works upwards. */
    private List<Map.Entry<BlockPos, BlockArray.BlockInformation>> entries(@Nullable BlockArray array) {
        List<Map.Entry<BlockPos, BlockArray.BlockInformation>> out = new ArrayList<>();
        if (array == null) return out;
        for (Map.Entry<BlockPos, BlockArray.BlockInformation> e : array.getPattern()
            .entrySet()) {
            BlockPos off = e.getKey();
            if (off.getX() == 0 && off.getY() == 0 && off.getZ() == 0) continue; // the tile's own block
            if (e.getValue().type == null || e.getValue().type == Blocks.air) continue;
            out.add(e);
        }
        out.sort(
            Comparator.comparingInt(
                (Map.Entry<BlockPos, BlockArray.BlockInformation> e) -> e.getKey()
                    .getY())
                .thenComparingInt(
                    e -> e.getKey()
                        .getX())
                .thenComparingInt(
                    e -> e.getKey()
                        .getZ()));
        return out;
    }

    private static boolean isSatisfied(World world, BlockPos at, BlockArray.BlockInformation info) {
        Block current = world.getBlock(at.getX(), at.getY(), at.getZ());
        return info.matcher.isStateValid(world, at, current);
    }

    @Override
    public void construct(ItemStack trigger, boolean hintsOnly, T tile, ExtendedFacing facing) {
        World world = tile.getWorldObj();
        BlockPos center = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        for (Map.Entry<BlockPos, BlockArray.BlockInformation> e : entries(structureProvider.apply(tile))) {
            BlockPos at = center.add(e.getKey());
            BlockArray.BlockInformation info = e.getValue();
            if (hintsOnly) {
                StructureLibAPI.hintParticle(world, at.getX(), at.getY(), at.getZ(), info.type, info.metadata);
            } else if (!world.isRemote && !isSatisfied(world, at, info)) {
                world.setBlock(at.getX(), at.getY(), at.getZ(), info.type, info.metadata, 3);
            }
        }
    }

    @Override
    public int survivalConstruct(ItemStack trigger, int elementBudget, ISurvivalBuildEnvironment env, T tile,
        ExtendedFacing facing) {
        World world = tile.getWorldObj();
        if (world.isRemote) return -1;
        BlockPos center = new BlockPos(tile.xCoord, tile.yCoord, tile.zCoord);
        EntityPlayer actor = env.getActor();
        boolean incomplete = false;
        int placed = 0;
        for (Map.Entry<BlockPos, BlockArray.BlockInformation> e : entries(structureProvider.apply(tile))) {
            BlockPos at = center.add(e.getKey());
            BlockArray.BlockInformation info = e.getValue();
            if (isSatisfied(world, at, info)) continue;
            incomplete = true;
            if (placed >= elementBudget) break;
            // liquids (e.g. liquid starlight pools) have to be poured by the player
            if (info.type instanceof IFluidBlock || info.type.getMaterial()
                .isLiquid()) continue;
            if (!StructureLibAPI.isBlockTriviallyReplaceable(world, at.getX(), at.getY(), at.getZ(), actor)) continue;
            Item item = Item.getItemFromBlock(info.type);
            if (item == null) continue;
            ItemStack needed = new ItemStack(item, 1, info.type.damageDropped(info.metadata));
            if (!env.getSource()
                .takeOne(needed, true)) continue;
            env.getSource()
                .takeOne(needed, false);
            world.setBlock(at.getX(), at.getY(), at.getZ(), info.type, info.metadata, 3);
            placed++;
        }
        return incomplete ? placed : -1;
    }

    @Override
    public String[] getDescription(ItemStack trigger) {
        return new String[] { StatCollector.translateToLocal(descriptionKey),
            StatCollector.translateToLocal("astralsorcery.structurelib.hint") };
    }
}
