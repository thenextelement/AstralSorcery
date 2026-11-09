/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.grindstone;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.util.ItemUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GrindstoneRecipeRegistry
 * Created by HellFirePvP
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
public class GrindstoneRecipeRegistry {

    private static final Random rand = new Random();

    public static List<GrindstoneRecipe> recipes = new LinkedList<>();
    public static List<GrindstoneRecipe> mtRecipes = new LinkedList<>();

    private static List<GrindstoneRecipe> localFallback = new LinkedList<>();

    public static GrindstoneRecipe registerGrindstoneRecipe(ItemStack in, ItemStack out, int chance) {
        return registerGrindstoneRecipe(new GrindstoneRecipe(in, out, chance));
    }

    // Used for both MineTweaker as well as internal registrations
    public static GrindstoneRecipe registerGrindstoneRecipe(GrindstoneRecipe recipe) {
        recipes.add(recipe);
        return recipe;
    }

    /** Recipes with a fixed input/output (for recipe viewers); the sharpening recipes are special-cased. */
    public static Collection<GrindstoneRecipe> getValidRecipes() {
        List<GrindstoneRecipe> all = new LinkedList<>(recipes);
        all.addAll(mtRecipes);
        return all.stream()
            .filter(GrindstoneRecipe::isValid)
            .filter(
                r -> !(r instanceof CrystalToolSharpeningRecipe || r instanceof CrystalSharpeningRecipe
                    || r instanceof SwordSharpeningRecipe))
            .collect(Collectors.toList());
    }

    @Nullable
    public static GrindstoneRecipe tryRemoveGrindstoneRecipe(ItemStack matchOut) {
        for (GrindstoneRecipe gr : recipes) {
            if (gr.isValid() && ItemUtils.matchStackLoosely(gr.getOutputForMatching(), matchOut)) {
                recipes.remove(gr);
                return gr;
            }
        }
        return null;
    }

    public static void cacheLocalFallback() {
        if (localFallback.isEmpty()) {
            localFallback.addAll(recipes);
        }
    }

    public static void loadFromFallback() {
        recipes.clear();
        recipes.addAll(localFallback);
    }

    @Nullable
    public static GrindstoneRecipe findMatchingRecipe(@Nullable ItemStack stackIn) {
        if (stackIn == null) return null;
        List<GrindstoneRecipe> matching = new LinkedList<>();
        for (GrindstoneRecipe gr : recipes) {
            if (gr.isValid() && gr.matches(stackIn)) matching.add(gr);
        }
        for (GrindstoneRecipe gr : mtRecipes) {
            if (gr.isValid() && gr.matches(stackIn)) matching.add(gr);
        }
        if (matching.isEmpty()) return null;
        return matching.get(rand.nextInt(matching.size()));
    }
}
