/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationCrafttweaker;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.BaseTweaker;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.GrindstoneRecipeAdd;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.GrindstoneRecipeRemove;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/** (backported from 1.12.2-1.8.5 CraftTweaker integration to MineTweaker 3) */
@ZenClass("mods.astralsorcery.Grindstone")
public class GrindstoneRecipe extends BaseTweaker {

    protected static final String name = "AstralSorcery Grindstone";

    @ZenMethod
    public static void addRecipe(IIngredient input, IItemStack output) {
        ItemHandle in = convertToHandle(input);
        if (in == null || in.handleType == ItemHandle.Type.FLUID) {
            MineTweakerAPI.logError("[" + name + "] Skipping recipe-add due to invalid input itemstack.");
            return;
        }
        ItemStack out = convertToItemStack(output);
        if (out == null) {
            MineTweakerAPI.logError("[" + name + "] Skipping recipe-add due to invalid output itemstack.");
            return;
        }
        ModIntegrationCrafttweaker.recipeModifications.add(new GrindstoneRecipeAdd(in, out));
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        ItemStack out = convertToItemStack(output);
        if (out == null) {
            MineTweakerAPI.logError("[" + name + "] Skipping recipe-remove due to invalid output itemstack.");
            return;
        }
        ModIntegrationCrafttweaker.recipeModifications.add(new GrindstoneRecipeRemove(out));
    }
}
