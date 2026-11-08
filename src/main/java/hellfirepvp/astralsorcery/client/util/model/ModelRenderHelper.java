/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.model;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** GL setup shared by the JSON-model TESRs and item renderers. */
@SideOnly(Side.CLIENT)
public class ModelRenderHelper {

    /** Standard state for drawing a static block model inside a TESR at (x, y, z). Must be paired with end(). */
    public static void beginWorld(double x, double y, double z) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_LIGHTING_BIT);
        GL11.glTranslated(x, y, z);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        // TESRs get the world lightmap from the dispatcher; keep the flat block look by disabling GL lighting.
        RenderHelper.disableStandardItemLighting();
    }

    public static void end() {
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    /**
     * Moves the GL origin so that a unit-cube model placed at (0,0,0)-(1,1,1) shows up correctly for the given
     * item render type. Must be paired with endItem().
     */
    public static void beginItem(IItemRenderer.ItemRenderType type) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_LIGHTING_BIT);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glEnable(GL11.GL_CULL_FACE);
        switch (type) {
            case ENTITY:
                GL11.glTranslatef(-0.5F, 0F, -0.5F);
                break;
            case INVENTORY:
                // RenderItem already applied the isometric block transform centered on the block.
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                break;
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
            default:
                break;
        }
        if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            RenderHelper.enableGUIStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
        }
    }

    public static void endItem() {
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
