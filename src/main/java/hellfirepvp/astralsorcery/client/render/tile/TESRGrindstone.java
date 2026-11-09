/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.models.base.ASgrindingstone;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.tile.TileGrindstone;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRGrindstone
 * Created by HellFirePvP
 * Date: 10.11.2016 / 22:30
 */
public class TESRGrindstone extends TileEntitySpecialRenderer {

    private static final ASgrindingstone modelGrindstone = new ASgrindingstone();
    private static final BindableResource texGrindstone = AssetLibrary
        .loadTexture(AssetLoader.TextureLocation.MODELS, "base/grindingstone");

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        TileGrindstone te = (TileGrindstone) tile;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 1.65, z + 0.5);
        GL11.glRotated(180, 1, 0, 0);
        GL11.glScaled(0.067, 0.067, 0.067);

        GL11.glPushMatrix();
        GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(165.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        renderModel(te, 1);
        GL11.glPopMatrix();
        GL11.glPopAttrib();

        ItemStack grind = te.getGrindingItem();
        if (grind != null && grind.getItem() != null) {
            // 1.12.2-1.8.5 placement, rendered as a frozen item entity (1.7 has no TransformType.GROUND)
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5, y + 0.75, z + 0.6);
            GL11.glRotated(125, 1, 0, 0);
            GL11.glRotated(180, 0, 0, 1);
            net.minecraft.entity.item.EntityItem ei = new net.minecraft.entity.item.EntityItem(
                Minecraft.getMinecraft().theWorld,
                0,
                0,
                0,
                grind);
            ei.hoverStart = 0F;
            boolean frame = net.minecraft.client.renderer.entity.RenderItem.renderInFrame;
            net.minecraft.client.renderer.entity.RenderItem.renderInFrame = true;
            net.minecraft.client.renderer.entity.RenderManager.instance.renderEntityWithPosYaw(ei, 0, 0, 0, 0, 0);
            net.minecraft.client.renderer.entity.RenderItem.renderInFrame = frame;
            GL11.glPopMatrix();
            GL11.glPopAttrib();
            TextureHelper.refreshTextureBindState();
        }
    }

    private void renderModel(TileGrindstone te, float partialTicks) {
        texGrindstone.bind();
        double oldDeg = (((double) te.prevTickWheelAnimation) / (20D) * 360) % 360;
        double newDeg = (((double) te.tickWheelAnimation) / (20D) * 360) % 360;
        modelGrindstone.render(null, (float) RenderingUtils.interpolate(oldDeg, newDeg, partialTicks), 0, 0, 0, 0, 1);
    }
}
