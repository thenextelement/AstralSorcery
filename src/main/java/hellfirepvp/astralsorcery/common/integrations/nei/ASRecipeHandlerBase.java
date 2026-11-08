/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.nei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

/**
 * Shared base for Astral Sorcery's NEI handlers (replaces the 1.10 JEI categories). Recipes are read straight from
 * the live registries, so MineTweaker changes show up without extra bookkeeping.
 */
public abstract class ASRecipeHandlerBase extends TemplateRecipeHandler {

    protected static final int SLOT = 18;

    public class ASCachedRecipe extends CachedRecipe {

        private final List<PositionedStack> ingredients = new ArrayList<>();
        private final PositionedStack result;

        public ASCachedRecipe(ItemStack result, int rx, int ry) {
            this.result = result == null ? null : new PositionedStack(result, rx, ry, false);
        }

        public ASCachedRecipe addInput(Object stacks, int x, int y) {
            if (stacks instanceof List && ((List<?>) stacks).isEmpty()) return this;
            if (stacks == null) return this;
            ingredients.add(new PositionedStack(stacks, x, y, true));
            return this;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 20, ingredients);
        }

        @Override
        public PositionedStack getResult() {
            return result;
        }

        public boolean producesItem(ItemStack stack) {
            return result != null && NEIServerUtils.areStacksSameTypeCrafting(result.item, stack);
        }

        public boolean usesItem(ItemStack stack) {
            for (PositionedStack ps : ingredients) {
                if (ps.contains(stack)) return true;
            }
            return false;
        }
    }

    /** All recipes this handler displays. */
    protected abstract List<ASCachedRecipe> buildAll();

    /** Slot frames drawn by drawBackground, as {x, y} pairs. */
    protected abstract List<int[]> slotPositions();

    protected abstract String titleKey();

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal(titleKey());
    }

    @Override
    public String getGuiTexture() {
        return "textures/gui/container/crafting_table.png";
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return null;
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getOverlayIdentifier())) {
            arecipes.addAll(buildAll());
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (ASCachedRecipe r : buildAll()) {
            if (r.producesItem(result)) arecipes.add(r);
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (ASCachedRecipe r : buildAll()) {
            if (r.usesItem(ingredient)) arecipes.add(r);
        }
    }

    @Override
    public void drawBackground(int recipe) {
        for (int[] p : slotPositions()) {
            // simple slot frame: dark border, gray inside (matches vanilla slot look without a texture)
            GuiDraw.drawRect(p[0] - 1, p[1] - 1, SLOT, SLOT, 0xFF373737);
            GuiDraw.drawRect(p[0], p[1], SLOT - 1, SLOT - 1, 0xFFFFFFFF);
            GuiDraw.drawRect(p[0], p[1], SLOT - 2, SLOT - 2, 0xFF8B8B8B);
        }
    }

    @Override
    public void drawExtras(int recipe) {
        GuiDraw.drawStringC("→", 120, 41, 0xFF404040, false);
    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }
}
