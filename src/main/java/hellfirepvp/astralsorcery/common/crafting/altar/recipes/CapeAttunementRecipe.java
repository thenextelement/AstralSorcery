/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.wearable.ItemCape;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.ItemUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeAttunementRecipe
 * Created by HellFirePvP
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
public class CapeAttunementRecipe extends TraitRecipe {

    private final IConstellation constellation;

    public CapeAttunementRecipe(IConstellation constellation, AbstractCacheableRecipe recipe) {
        super(recipe);
        setPassiveStarlightRequirement(3000);
        this.constellation = constellation;
        setRequiredConstellation(constellation);
    }

    private ItemStack attuned() {
        ItemStack cape = new ItemStack(ItemsAS.armorImbuedCape);
        ItemCape.setAttunedConstellation(cape, constellation);
        return cape;
    }

    @Nonnull
    @Override
    public ItemStack getOutputForRender() {
        return attuned();
    }

    @Nonnull
    @Override
    public ItemStack getOutputForMatching() {
        return attuned();
    }

    @Nullable
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar altar) {
        ItemStack cape = attuned();
        ItemStack center = altar.getInventoryHandler()
            .getStackInSlot(ShapedRecipeSlot.CENTER.getSlotID());
        if (center != null && center.getItem() instanceof ItemCape) {
            // keep enchantments/durability of the mantle that was put in
            cape = ItemUtils.copyStackWithSize(center, center.stackSize);
        }
        ItemCape.setAttunedConstellation(cape, constellation);
        return cape;
    }
}
