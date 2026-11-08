/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.page;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalPageTraitRecipe
 * Created by HellFirePvP
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
public class JournalPageTraitRecipe implements IJournalPage {

    public TraitRecipe recipe;

    public JournalPageTraitRecipe(TraitRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public IGuiRenderablePage buildRenderPage() {
        return new Render(recipe, TileAltar.AltarLevel.TRAIT_CRAFT);
    }

    public static class Render extends JournalPageConstellationRecipe.Render {

        private static final BindableResource texGrid = AssetLibrary
            .loadTexture(AssetLoader.TextureLocation.GUI, "gridTr");

        private final TraitRecipe recipe;

        public Render(TraitRecipe recipe, TileAltar.AltarLevel altarLevel) {
            super(recipe, altarLevel);
            this.recipe = recipe;
            this.gridTexture = texGrid;
        }

        private void renderTraitInnerSlots(float offsetX, float offsetY, float zLevel) {
            RenderHelper.enableGUIStandardItemLighting();
            renderSlot(
                offsetX + 80,
                offsetY + 78,
                zLevel,
                recipe.getInnerTraitItems(TraitRecipe.TraitRecipeSlot.UPPER_CENTER));
            renderSlot(
                offsetX + 30,
                offsetY + 128,
                zLevel,
                recipe.getInnerTraitItems(TraitRecipe.TraitRecipeSlot.LEFT_CENTER));
            renderSlot(
                offsetX + 131,
                offsetY + 128,
                zLevel,
                recipe.getInnerTraitItems(TraitRecipe.TraitRecipeSlot.RIGHT_CENTER));
            renderSlot(
                offsetX + 80,
                offsetY + 178,
                zLevel,
                recipe.getInnerTraitItems(TraitRecipe.TraitRecipeSlot.LOWER_CENTER));
            RenderHelper.disableStandardItemLighting();
            TextureHelper.refreshTextureBindState();
        }

        private void renderTraitOuterSlots(float offsetX, float offsetY, float zLevel) {
            RenderHelper.enableGUIStandardItemLighting();
            float centerX = offsetX + 80;
            float centerY = offsetY + 128;
            float perc = (ClientScheduler.getClientTick() % 3000) / 3000F;
            List<ItemHandle> handles = recipe.getTraitItemHandles();
            int amt = handles.size();
            for (int i = 0; i < amt; i++) {
                double part = ((double) i) / ((double) amt) * 2.0 * Math.PI;
                part += (2.0 * Math.PI * perc) + Math.PI;
                renderSlot(
                    (float) (centerX + Math.sin(part) * 75.0),
                    (float) (centerY + Math.cos(part) * 75.0),
                    zLevel + 100,
                    handles.get(i)
                        .getApplicableItems());
            }
            RenderHelper.disableStandardItemLighting();
            TextureHelper.refreshTextureBindState();
        }

        private void renderSlot(float offsetX, float offsetY, float zLevel, List<ItemStack> stacks) {
            if (stacks == null || stacks.isEmpty()) return;
            long select = (ClientScheduler.getClientTick() / 20) % stacks.size();
            ItemStack draw = stacks.get((int) select);

            TextureHelper.refreshTextureBindState();
            GL11.glPushMatrix();
            GL11.glTranslated(offsetX, offsetY, zLevel + 60);
            GL11.glScaled(1.1, 1.1, 1.1);
            Rectangle r = drawItemStack(draw, 0, 0, 0);
            r = new Rectangle((int) offsetX, (int) offsetY, (int) (r.getWidth() * 1.1), (int) (r.getHeight() * 1.1));
            addRenderedStackRectangle(r, draw);
            GL11.glPopMatrix();
        }

        @Override
        public void addTooltip(List<String> out) {
            super.addTooltip(out);
            if (recipe.getRequiredConstellation() != null) {
                out.add(
                    StatCollector.translateToLocalFormatted(
                        "astralsorcery.journal.recipe.constellation",
                        StatCollector.translateToLocal(
                            recipe.getRequiredConstellation()
                                .getUnlocalizedName())));
            }
        }

        @Override
        public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {
            super.render(offsetX, offsetY, pTicks, zLevel, mouseX, mouseY);
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            renderTraitInnerSlots(offsetX, offsetY, zLevel);
            renderTraitOuterSlots(offsetX, offsetY, zLevel);
            IConstellation focus = recipe.getRequiredConstellation();
            if (focus != null) {
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                RenderConstellation.renderConstellationIntoGUI(
                    new Color(0xEEEEEE),
                    focus,
                    Math.round(offsetX + 30),
                    Math.round(offsetY + 78),
                    zLevel,
                    125,
                    125,
                    2F,
                    new RenderConstellation.BrightnessFunction() {

                        @Override
                        public float getBrightness() {
                            return 0.3F;
                        }
                    },
                    true,
                    false);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
            }
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopAttrib();
        }
    }
}
