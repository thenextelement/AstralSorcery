/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.grindstone;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.util.ItemUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DustGrindstoneRecipe
 * Created by HellFirePvP
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
public class DustGrindstoneRecipe extends GrindstoneRecipe {

    private final float doubleChance;

    public DustGrindstoneRecipe(ItemHandle input, ItemStack output, int chance, float doubleChance) {
        super(input, output, chance);
        this.doubleChance = doubleChance;
    }

    public float getDoubleChance() {
        return MathHelper.clamp_float(doubleChance, 0, 1);
    }

    @Nonnull
    @Override
    public GrindResult grind(ItemStack stackIn) {
        if (rand.nextInt(chance) == 0) {
            ItemStack out = ItemUtils.copyStackWithSize(this.output, this.output.stackSize);
            if (rand.nextFloat() < doubleChance) {
                out.stackSize = out.stackSize * 2;
            }
            return GrindResult.itemChange(out);
        }
        return GrindResult.failNoOp();
    }
}
