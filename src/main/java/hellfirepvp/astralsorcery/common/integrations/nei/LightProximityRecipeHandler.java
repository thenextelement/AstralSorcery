/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.nei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

import hellfirepvp.astralsorcery.common.crafting.ShapedLightProximityRecipe;

/**
 * Crafting table recipes that only work while a collector crystal shines starlight onto the table (luminous
 * crafting table, resonating wand). NEI's shaped handler does not know this IRecipe type.
 */
public class LightProximityRecipeHandler extends ASRecipeHandlerBase {

    private static final int GX = 25, GY = 6;
    private static final int OUT_X = 119, OUT_Y = 24;

    @Override
    protected List<ASCachedRecipe> buildAll() {
        List<ASCachedRecipe> out = new ArrayList<>();
        for (Object o : CraftingManager.getInstance()
            .getRecipeList()) {
            if (!(o instanceof ShapedLightProximityRecipe)) continue;
            ShapedLightProximityRecipe r = (ShapedLightProximityRecipe) o;
            ItemStack result = ((IRecipe) r).getRecipeOutput();
            if (result == null) continue;
            ASCachedRecipe c = new ASCachedRecipe(result, OUT_X, OUT_Y);
            Object[] in = r.getInput();
            for (int y = 0; y < r.getHeight(); y++) {
                for (int x = 0; x < r.getWidth(); x++) {
                    Object part = in[x + y * r.getWidth()];
                    if (part == null) continue;
                    c.addInput(part, GX + x * SLOT, GY + y * SLOT);
                }
            }
            out.add(c);
        }
        return out;
    }

    @Override
    protected List<int[]> slotPositions() {
        List<int[]> l = new ArrayList<>();
        for (int y = 0; y < 3; y++) for (int x = 0; x < 3; x++) l.add(new int[] { GX + x * SLOT, GY + y * SLOT });
        l.add(new int[] { OUT_X, OUT_Y });
        return l;
    }

    @Override
    public void drawExtras(int recipe) {
        codechicken.lib.gui.GuiDraw.drawStringC("→", 100, OUT_Y + 4, 0xFF404040, false);
        codechicken.lib.gui.GuiDraw.drawStringC(
            net.minecraft.util.StatCollector.translateToLocal("astralsorcery.nei.lightproximity.hint"),
            83,
            GY + 3 * SLOT + 2,
            0xFF404040,
            false);
    }

    @Override
    protected String titleKey() {
        return "astralsorcery.nei.lightproximity";
    }

    @Override
    public String getOverlayIdentifier() {
        return NEIHandlerInfo.LightProximityIds.OVERLAY;
    }
}
