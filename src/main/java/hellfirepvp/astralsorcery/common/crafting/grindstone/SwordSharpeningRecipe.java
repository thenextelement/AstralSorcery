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

import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.SwordSharpenHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SwordSharpeningRecipe
 * Created by HellFirePvP
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
public class SwordSharpeningRecipe extends GrindstoneRecipe {

    public SwordSharpeningRecipe() {
        super((ItemStack) null, null, 40);
    }

    @Override
    public boolean matches(ItemStack stackIn) {
        return stackIn != null && SwordSharpenHelper.canBeSharpened(stackIn)
            && !SwordSharpenHelper.isSwordSharpened(stackIn);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Nonnull
    @Override
    public GrindResult grind(ItemStack stackIn) {
        if (SwordSharpenHelper.canBeSharpened(stackIn) && rand.nextInt(chance) == 0) {
            ItemStack copy = ItemUtils.copyStackWithSize(stackIn, stackIn.stackSize);
            SwordSharpenHelper.setSwordSharpened(copy);
            return GrindResult.itemChange(copy);
        }
        return GrindResult.failNoOp();
    }
}
