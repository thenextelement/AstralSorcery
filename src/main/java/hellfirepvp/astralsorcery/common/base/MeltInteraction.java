package hellfirepvp.astralsorcery.common.base;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MeltInteraction
 * Created by HellFirePvP
 * Date: 22.04.2017 / 16:40
 */
public interface MeltInteraction {

    public boolean isMeltable(World world, BlockPos pos, Block state);

    default public int getMeltTickDuration() {
        return 100; // Def. Furance duration halved
    }

    // Result can be a blockstate and/or an itemstack.
    // The BlockState result, if present, takes precedence.
    @Nullable
    public Block getMeltResultState();

    @Nullable
    public ItemStack getMeltResultStack();

    /** Metadata of the placed melt result block (1.7 has no block states). */
    default public int getMeltResultMeta() {
        ItemStack out = getMeltResultStack();
        if (out != null && out.getItem() instanceof net.minecraft.item.ItemBlock) {
            return out.getItem()
                .getMetadata(out.getItemDamage());
        }
        return 0;
    }

    default public void placeResultAt(World world, BlockPos pos) {
        Block result = getMeltResultState();
        if (result != null) {
            world.setBlock(pos.getX(), pos.getY(), pos.getZ(), result, getMeltResultMeta(), 3);
        } else {
            world.setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
            ItemStack resultStack = getMeltResultStack();
            if (resultStack != null && resultStack.getItem() != null) {
                ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, resultStack);
            }
        }
    }

}
