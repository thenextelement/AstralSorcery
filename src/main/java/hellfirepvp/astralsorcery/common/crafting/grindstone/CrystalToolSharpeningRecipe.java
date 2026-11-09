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

import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;
import hellfirepvp.astralsorcery.common.item.tool.ItemCrystalSword;
import hellfirepvp.astralsorcery.common.item.tool.ItemCrystalToolBase;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalToolSharpeningRecipe
 * Created by HellFirePvP
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
public class CrystalToolSharpeningRecipe extends GrindstoneRecipe {

    public CrystalToolSharpeningRecipe(int chance) {
        super((ItemStack) null, null, chance);
    }

    @Override
    public boolean matches(ItemStack stackIn) {
        return stackIn != null
            && (stackIn.getItem() instanceof ItemCrystalToolBase || stackIn.getItem() instanceof ItemCrystalSword);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Nonnull
    @Override
    public GrindResult grind(ItemStack stackIn) {
        boolean sword = stackIn.getItem() instanceof ItemCrystalSword;
        ToolCrystalProperties prop = sword ? ItemCrystalSword.getToolProperties(stackIn)
            : ItemCrystalToolBase.getToolProperties(stackIn);
        if (prop == null) return GrindResult.failBreakItem();
        ToolCrystalProperties result = prop.grindCopy(rand);
        if (result == null) {
            return GrindResult.failBreakItem();
        }
        if (sword) {
            ItemCrystalSword.setToolProperties(stackIn, result);
        } else {
            ItemCrystalToolBase.setToolProperties(stackIn, result);
        }
        if (result.getSize() <= 0) {
            return GrindResult.failBreakItem();
        }
        return GrindResult.success();
    }
}
