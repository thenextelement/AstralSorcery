/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import hellfirepvp.astralsorcery.common.block.IMarblePillarBlock;
import hellfirepvp.astralsorcery.common.block.IMarblePillarBlock.Tex;

/**
 * Renders marble blocks; the pillar variant is drawn as the cuboids of the 1.12 pillar models (column + end cap).
 * RenderBlocks' default UV mapping (bounds * 16) matches the UVs of those models, so each element is a plain
 * renderStandardBlock call with per-face icons supplied through {@link #faceIcon(int)}.
 */
public class RenderBlockMarblePillar implements ISimpleBlockRenderingHandler {

    private static final ThreadLocal<IIcon[]> currentFaces = new ThreadLocal<>();

    private final int renderId;

    public RenderBlockMarblePillar(int renderId) {
        this.renderId = renderId;
    }

    /** Called by the blocks' world-aware getIcon while an element is being drawn; null otherwise. */
    public static IIcon faceIcon(int side) {
        IIcon[] faces = currentFaces.get();
        return faces == null ? null : faces[side];
    }

    /** One cuboid in 1/16 block units with icons for down, up and the four sides. */
    private static final class Element {

        final float x0, y0, z0, x1, y1, z1;
        final Tex down, up, sides;

        Element(float x0, float y0, float z0, float x1, float y1, float z1, Tex down, Tex up, Tex sides) {
            this.x0 = x0 / 16F;
            this.y0 = y0 / 16F;
            this.z0 = z0 / 16F;
            this.x1 = x1 / 16F;
            this.y1 = y1 / 16F;
            this.z1 = z1 / 16F;
            this.down = down;
            this.up = up;
            this.sides = sides;
        }

        IIcon[] icons(IMarblePillarBlock block) {
            IIcon s = block.getPillarIcon(sides);
            return new IIcon[] { block.getPillarIcon(down), block.getPillarIcon(up), s, s, s, s };
        }
    }

    private static final Element[] PILLAR = { new Element(2, 0, 2, 14, 16, 14, Tex.INNER, Tex.INNER, Tex.PILLAR) };
    private static final Element[] PILLAR_TOP = { new Element(0, 12, 0, 16, 16, 16, Tex.UPDOWN, Tex.UPDOWN, Tex.TOP),
        new Element(2, 0, 2, 14, 12, 14, Tex.INNER, Tex.INNER, Tex.TOP) };
    private static final Element[] PILLAR_BOTTOM = {
        new Element(0, 0, 0, 16, 4, 16, Tex.UPDOWN, Tex.ENGRAVED, Tex.BOTTOM),
        new Element(2, 4, 2, 14, 16, 14, Tex.INNER, Tex.INNER, Tex.BOTTOM) };

    private static Element[] elements(IMarblePillarBlock.Part part) {
        switch (part) {
            case PILLAR_TOP:
                return PILLAR_TOP;
            case PILLAR_BOTTOM:
                return PILLAR_BOTTOM;
            default:
                return PILLAR;
        }
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks rb) {
        Element[] els;
        if (IMarblePillarBlock.isPillar(block, metadata)) {
            els = PILLAR;
        } else {
            els = null;
        }
        Tessellator t = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        if (els == null) {
            rb.setRenderBounds(0, 0, 0, 1, 1, 1);
            drawInventoryCube(rb, block, t, new IIcon[] { block.getIcon(0, metadata), block.getIcon(1, metadata),
                block.getIcon(2, metadata), block.getIcon(3, metadata), block.getIcon(4, metadata),
                block.getIcon(5, metadata) });
        } else {
            for (Element e : els) {
                rb.setRenderBounds(e.x0, e.y0, e.z0, e.x1, e.y1, e.z1);
                drawInventoryCube(rb, block, t, e.icons((IMarblePillarBlock) block));
            }
        }
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    private static void drawInventoryCube(RenderBlocks rb, Block block, Tessellator t, IIcon[] icons) {
        t.startDrawingQuads();
        t.setNormal(0F, -1F, 0F);
        rb.renderFaceYNeg(block, 0, 0, 0, icons[0]);
        t.setNormal(0F, 1F, 0F);
        rb.renderFaceYPos(block, 0, 0, 0, icons[1]);
        t.setNormal(0F, 0F, -1F);
        rb.renderFaceZNeg(block, 0, 0, 0, icons[2]);
        t.setNormal(0F, 0F, 1F);
        rb.renderFaceZPos(block, 0, 0, 0, icons[3]);
        t.setNormal(-1F, 0F, 0F);
        rb.renderFaceXNeg(block, 0, 0, 0, icons[4]);
        t.setNormal(1F, 0F, 0F);
        rb.renderFaceXPos(block, 0, 0, 0, icons[5]);
        t.draw();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks rb) {
        if (!(block instanceof IMarblePillarBlock) || !IMarblePillarBlock.isPillarAt(world, x, y, z)) {
            rb.setRenderBounds(0, 0, 0, 1, 1, 1);
            return rb.renderStandardBlock(block, x, y, z);
        }
        IMarblePillarBlock pillar = (IMarblePillarBlock) block;
        boolean prevAllFaces = rb.renderAllFaces;
        rb.renderAllFaces = true; // inner cuboids; Block#shouldSideBeRendered only knows the full block bounds
        boolean any = false;
        try {
            for (Element e : elements(IMarblePillarBlock.getPart(world, x, y, z))) {
                currentFaces.set(e.icons(pillar));
                rb.setRenderBounds(e.x0, e.y0, e.z0, e.x1, e.y1, e.z1);
                any |= rb.renderStandardBlock(block, x, y, z);
            }
        } finally {
            currentFaces.remove();
            rb.renderAllFaces = prevAllFaces;
        }
        return any;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return renderId;
    }
}
