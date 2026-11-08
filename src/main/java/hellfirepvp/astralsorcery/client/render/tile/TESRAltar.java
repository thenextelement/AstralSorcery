/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.models.base.ASaltarT2;
import hellfirepvp.astralsorcery.client.models.base.ASaltarT3;
import hellfirepvp.astralsorcery.client.util.ItemColorizationHelper;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRAltar
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:21
 */
public class TESRAltar extends TileEntitySpecialRenderer {

    private static final Random rand = new Random();
    private static final ASaltarT2 modelAltar2 = new ASaltarT2();
    private static final BindableResource texAltar2 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MODELS, "base/altarT2");

    private static final ASaltarT3 modelAltar3 = new ASaltarT3();
    private static final BindableResource texAltar3 = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MODELS, "base/altarT3");

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {

        TileAltar te = (TileAltar) tile;
        long sBase = 7553015156732193565L;
        sBase ^= (long) te.xCoord;
        sBase ^= (long) te.yCoord;
        sBase ^= (long) te.zCoord;
        double jBase = ClientScheduler.getClientTick() + partialTicks;
        jBase /= 20D;

        switch (te.getAltarLevel()) {
            case ATTUNEMENT:
                renderT2Additions(te, x, y, z, jBase);
                break;
            case CONSTELLATION_CRAFT:
                renderT3Additions(te, x, y, z, jBase);
                break;
            case TRAIT_CRAFT:
                if (te.getMultiblockState()) {
                    IConstellation c = te.getFocusedConstellation();
                    if (c != null) {
                        GL11.glPushMatrix();
                        float alphaDaytime = ConstellationSkyHandler.getInstance()
                            .getCurrentDaytimeDistribution(te.getWorldObj());
                        alphaDaytime *= 0.8F;

                        int max = 5000;
                        int t = (int) (ClientScheduler.getClientTick() % max);
                        float halfAge = max / 2F;
                        float tr = 1F - (Math.abs(halfAge - t) / halfAge);
                        tr *= 2;

                        RenderingUtils.removeStandartTranslationFromTESRMatrix(partialTicks);

                        float br = 0.9F * alphaDaytime;

                        RenderConstellation.renderConstellationIntoWorldFlat(
                            c,
                            c.getConstellationColor(),
                            new Vector3(te).add(0.5, 0.03, 0.5),
                            5 + tr,
                            2,
                            0.1F + br);
                        GL11.glPopMatrix();
                    }
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    GL11.glPushMatrix();
                    GL11.glTranslated(x + 0.5, y + 4, z + 0.5);
                    ActiveCraftingTask act = te.getActiveCraftingTask();
                    if (act != null && act.getRecipeToCraft() instanceof TraitRecipe) {
                        Collection<ItemHandle> requiredHandles = ((TraitRecipe) act.getRecipeToCraft())
                            .getTraitItemHandles();
                        if (!requiredHandles.isEmpty()) {
                            int amt = 60 / requiredHandles.size();
                            for (ItemHandle outer : requiredHandles) {
                                List<ItemStack> stacksApplicable = outer.getApplicableItemsForRender();
                                int mod = (int) (ClientScheduler.getClientTick() % (stacksApplicable.size() * 60));
                                ItemStack element = stacksApplicable.get(
                                    MathHelper.floor_double(
                                        MathHelper.clamp_double(
                                            stacksApplicable.size() * (mod / (stacksApplicable.size() * 60)),
                                            0,
                                            stacksApplicable.size() - 1)));
                                Color col = ItemColorizationHelper.getDominantColorFromItemStack(element);
                                if (col == null) {
                                    col = BlockCollectorCrystal.CollectorCrystalType.CELESTIAL_CRYSTAL.displayColor;
                                }
                                RenderingUtils.renderLightRayEffects(
                                    0,
                                    0.5,
                                    0,
                                    col,
                                    0x12315L | outer.hashCode(),
                                    ClientScheduler.getClientTick(),
                                    20,
                                    2F,
                                    amt,
                                    amt / 2);
                            }
                        }
                        RenderingUtils.renderLightRayEffects(
                            0,
                            0.5,
                            0,
                            Color.WHITE,
                            0,
                            ClientScheduler.getClientTick(),
                            15,
                            2F,
                            40,
                            25);
                    } else {
                        RenderingUtils.renderLightRayEffects(
                            0,
                            0.5,
                            0,
                            Color.WHITE,
                            0x12315661L,
                            ClientScheduler.getClientTick(),
                            20,
                            2F,
                            50,
                            25);
                        RenderingUtils.renderLightRayEffects(
                            0,
                            0.5,
                            0,
                            Color.BLUE,
                            0,
                            ClientScheduler.getClientTick(),
                            10,
                            1F,
                            40,
                            25);
                    }
                    GL11.glTranslated(0, 0.15, 0);
                    GL11.glScaled(0.7, 0.7, 0.7);
                    TESRCollectorCrystal.renderCrystal(true, true);
                    GL11.glPopMatrix();
                    TextureHelper.refreshTextureBindState();
                    GL11.glPopAttrib();
                }
                break;
        }

        ActiveCraftingTask task = te.getActiveCraftingTask();
        if (task != null) {
            task.getRecipeToCraft()
                .onCraftTESRRender(te, x, y, z, partialTicks);
        }
    }

    private void renderT3Additions(TileAltar te, double x, double y, double z, double jump) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 1.5, z + 0.5);
        GL11.glRotated(180, 1, 0, 0);
        GL11.glScaled(0.0625, 0.0625, 0.0625);
        RenderHelper.disableStandardItemLighting();

        GL11.glPushMatrix();
        // GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();

        texAltar3.bind();
        // pulled towards the camera so it doesn't z-fight with the baked altar body (1.7 chunk vs TESR precision)
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(-1F, -2F);
        modelAltar3.render(null, (float) jump, 0, 0, 0, 0, 1F);
        GL11.glPolygonOffset(0F, 0F);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void renderT2Additions(TileAltar te, double x, double y, double z, double jump) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 1.5, z + 0.5);
        GL11.glRotated(180, 1, 0, 0);
        GL11.glScaled(0.0625, 0.0625, 0.0625);
        RenderHelper.disableStandardItemLighting();

        GL11.glPushMatrix();
        // GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();

        texAltar2.bind();
        // pulled towards the camera so it doesn't z-fight with the baked altar body (1.7 chunk vs TESR precision)
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(-1F, -2F);
        modelAltar2.render(null, (float) jump, 0, 0, 0, 0, 1F);
        GL11.glPolygonOffset(0F, 0F);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

}
