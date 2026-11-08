/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import hellfirepvp.astralsorcery.common.entities.EntitySpectralTool;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderSpectralTool
 * Created by HellFirePvP
 * (backported from 1.12.2-1.8.5 to 1.7.10: translucent purple-tinted 2D item)
 */
public class RenderSpectralTool extends Render {

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ItemStack is = ((EntitySpectralTool) entity).getItem();
        if (is == null || is.getItem() == null) return;
        IIcon icon = is.getItem()
            .getIconIndex(is);
        if (icon == null) return;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glTranslated(x, y + 0.2, z);
        GL11.glRotatef(-entityYaw - 90, 0, 1, 0);
        if (is.getItem() instanceof ItemAxe) {
            GL11.glRotatef(180, 1, 0, 0);
            GL11.glRotatef(270, 0, 0, 1);
        }
        GL11.glTranslatef(-0.5F, -0.5F, 0.03125F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.001F);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        GL11.glColor4f(0x4A / 255F, 0x27 / 255F, 0xB0 / 255F, 0.7F);
        this.bindTexture(TextureMap.locationItemsTexture);
        ItemRenderer.renderItemIn2D(
            Tessellator.instance,
            icon.getMaxU(),
            icon.getMinV(),
            icon.getMinU(),
            icon.getMaxV(),
            icon.getIconWidth(),
            icon.getIconHeight(),
            0.0625F);
        GL11.glDepthMask(true);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.locationItemsTexture;
    }
}
