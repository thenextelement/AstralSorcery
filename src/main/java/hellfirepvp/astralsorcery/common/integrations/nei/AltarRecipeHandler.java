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

import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.tile.TileAltar;

/** Starlight altar recipes, one handler per altar tier. Layout is a 5x5 grid: 3x3 core plus the outer relay slots. */
public abstract class AltarRecipeHandler extends ASRecipeHandlerBase {

    // NEI re-instantiates handlers via a public no-arg constructor, hence one subclass per tier.
    public static class Discovery extends AltarRecipeHandler {

        public Discovery() {
            super(TileAltar.AltarLevel.DISCOVERY);
        }
    }

    public static class Attunement extends AltarRecipeHandler {

        public Attunement() {
            super(TileAltar.AltarLevel.ATTUNEMENT);
        }
    }

    public static class Trait extends AltarRecipeHandler {

        public Trait() {
            super(TileAltar.AltarLevel.TRAIT_CRAFT);
        }
    }

    public static class Constellation extends AltarRecipeHandler {

        public Constellation() {
            super(TileAltar.AltarLevel.CONSTELLATION_CRAFT);
        }
    }

    private static final int GX = 10, GY = 2;
    private static final int OUT_X = 138, OUT_Y = GY + 2 * SLOT;

    private final TileAltar.AltarLevel level;

    protected AltarRecipeHandler(TileAltar.AltarLevel level) {
        this.level = level;
    }

    private static int[] cell(int cx, int cy) {
        return new int[] { GX + cx * SLOT, GY + cy * SLOT };
    }

    private static int[] attCell(AttunementRecipe.AttunementAltarSlot s) {
        switch (s) {
            case UPPER_LEFT:
                return cell(0, 0);
            case UPPER_RIGHT:
                return cell(4, 0);
            case LOWER_LEFT:
                return cell(0, 4);
            default:
                return cell(4, 4);
        }
    }

    private static int[] cstCell(ConstellationRecipe.ConstellationAtlarSlot s) {
        switch (s) {
            case UP_UP_LEFT:
                return cell(1, 0);
            case UP_UP_RIGHT:
                return cell(3, 0);
            case UP_LEFT_LEFT:
                return cell(0, 1);
            case UP_RIGHT_RIGHT:
                return cell(4, 1);
            case DOWN_LEFT_LEFT:
                return cell(0, 3);
            case DOWN_RIGHT_RIGHT:
                return cell(4, 3);
            case DOWN_DOWN_LEFT:
                return cell(1, 4);
            default:
                return cell(3, 4);
        }
    }

    private static int[] traitCell(TraitRecipe.TraitRecipeSlot s) {
        switch (s) {
            case UPPER_CENTER:
                return cell(2, 0);
            case LEFT_CENTER:
                return cell(0, 2);
            case RIGHT_CENTER:
                return cell(4, 2);
            default:
                return cell(2, 4);
        }
    }

    /** Items the relays around the iridescent altar have to supply, in a row under the grid. */
    private static int[] outerCell(int index) {
        return new int[] { GX + index * SLOT, GY + 5 * SLOT + 4 };
    }

    private static final int MAX_OUTER = 8;

    @Override
    protected List<ASCachedRecipe> buildAll() {
        List<ASCachedRecipe> out = new ArrayList<>();
        boolean gate = ItemHandle.ignoreGatingRequirement;
        ItemHandle.ignoreGatingRequirement = true;
        try {
            for (AbstractAltarRecipe r : AltarRecipeRegistry.getRecipesForLevel(level)) {
                ItemStack result = r.getOutputForRender();
                if (result == null) continue;
                ASCachedRecipe c = new ASCachedRecipe(result, OUT_X, OUT_Y);
                IAccessibleRecipe nat = r.getNativeRecipe();
                for (ShapedRecipeSlot srs : ShapedRecipeSlot.values()) {
                    int[] p = cell(1 + srs.columnMultiplier, 1 + srs.rowMultipler);
                    c.addInput(nat.getExpectedStackForRender(srs), p[0], p[1]);
                }
                if (r instanceof AttunementRecipe) {
                    for (AttunementRecipe.AttunementAltarSlot s : AttunementRecipe.AttunementAltarSlot.values()) {
                        int[] p = attCell(s);
                        c.addInput(((AttunementRecipe) r).getAttItems(s), p[0], p[1]);
                    }
                }
                if (r instanceof ConstellationRecipe) {
                    for (ConstellationRecipe.ConstellationAtlarSlot s : ConstellationRecipe.ConstellationAtlarSlot
                        .values()) {
                        int[] p = cstCell(s);
                        c.addInput(((ConstellationRecipe) r).getCstItems(s), p[0], p[1]);
                    }
                }
                if (r instanceof TraitRecipe) {
                    TraitRecipe tr = (TraitRecipe) r;
                    for (TraitRecipe.TraitRecipeSlot s : TraitRecipe.TraitRecipeSlot.values()) {
                        int[] p = traitCell(s);
                        c.addInput(tr.getInnerTraitItems(s), p[0], p[1]);
                    }
                    List<List<ItemStack>> outer = tr.getTraitItems();
                    for (int i = 0; i < outer.size() && i < MAX_OUTER; i++) {
                        int[] p = outerCell(i);
                        c.addInput(outer.get(i), p[0], p[1]);
                    }
                }
                out.add(c);
            }
        } finally {
            ItemHandle.ignoreGatingRequirement = gate;
        }
        return out;
    }

    @Override
    protected List<int[]> slotPositions() {
        List<int[]> l = new ArrayList<>();
        for (int x = 1; x <= 3; x++) for (int y = 1; y <= 3; y++) l.add(cell(x, y));
        if (level.ordinal() >= TileAltar.AltarLevel.ATTUNEMENT.ordinal()) {
            for (AttunementRecipe.AttunementAltarSlot s : AttunementRecipe.AttunementAltarSlot.values())
                l.add(attCell(s));
        }
        if (level.ordinal() >= TileAltar.AltarLevel.CONSTELLATION_CRAFT.ordinal()) {
            for (ConstellationRecipe.ConstellationAtlarSlot s : ConstellationRecipe.ConstellationAtlarSlot.values())
                l.add(cstCell(s));
        }
        if (level.ordinal() >= TileAltar.AltarLevel.TRAIT_CRAFT.ordinal()) {
            for (TraitRecipe.TraitRecipeSlot s : TraitRecipe.TraitRecipeSlot.values()) l.add(traitCell(s));
        }
        l.add(new int[] { OUT_X, OUT_Y });
        return l;
    }

    @Override
    public void drawExtras(int recipe) {
        codechicken.lib.gui.GuiDraw.drawStringC("→", 120, OUT_Y + 4, 0xFF404040, false);
    }

    @Override
    protected String titleKey() {
        return "astralsorcery.nei.altar." + level.name()
            .toLowerCase();
    }

    @Override
    public String getOverlayIdentifier() {
        return "astralsorcery.altar." + level.name()
            .toLowerCase();
    }
}
