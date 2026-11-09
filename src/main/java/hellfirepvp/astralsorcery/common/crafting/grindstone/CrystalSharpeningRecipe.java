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

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemRockCrystalBase;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalSharpeningRecipe
 * Created by HellFirePvP
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
public class CrystalSharpeningRecipe extends GrindstoneRecipe {

    public CrystalSharpeningRecipe(int chance) {
        super((ItemStack) null, null, chance);
    }

    @Override
    public boolean matches(ItemStack stackIn) {
        return stackIn != null && stackIn.getItem() instanceof ItemRockCrystalBase;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Nonnull
    @Override
    public GrindResult grind(ItemStack stackIn) {
        CrystalProperties prop = CrystalProperties.getCrystalProperties(stackIn);
        if (prop == null) return GrindResult.failBreakItem();
        CrystalProperties result = prop.grindCopy(rand);
        if (result == null) {
            return GrindResult.failBreakItem();
        }
        CrystalProperties.applyCrystalProperties(stackIn, result);
        if (result.getSize() <= 0) {
            return GrindResult.failBreakItem();
        }
        return GrindResult.success();
    }
}
