/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.model;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Bakes a block's static JSON model into the chunk mesh (solid parts in pass 0, glass parts in pass 1). Compared to
 * drawing them in a TESR every frame this gives stable, world-lit geometry that shader mods (Angelica) handle like any
 * other block. Dynamic parts stay in the TESRs.
 */
@SideOnly(Side.CLIENT)
public class JsonBlockModelRenderer implements ISimpleBlockRenderingHandler {

    private final int renderId;

    public JsonBlockModelRenderer(int renderId) {
        this.renderId = renderId;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        // items use ItemRendererJsonModel
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {
        JsonBlockModels.Spec spec = JsonBlockModels.get(block, world.getBlockMetadata(x, y, z));
        if (spec == null) return false;
        int pass = ForgeHooksClient.getWorldRenderPass();
        String name = pass == 1 ? spec.translucent : spec.solid;
        if (name == null) return false;
        JsonModel model = JsonModelLibrary.get(name);
        Tessellator tes = Tessellator.instance;
        boolean[] any = { false };
        model.visitFaces((tex, verts, uvs, normal) -> {
            IIcon icon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture
                : JsonBlockModels.getSprite(tex);
            if (icon == null) return;
            tes.setBrightness(faceBrightness(world, block, x, y, z, verts, normal));
            float shade = shade(normal);
            tes.setColorOpaque_F(shade, shade, shade);
            for (int i = 0; i < 4; i++) {
                float[] v = verts[i];
                float[] uv = uvs[i];
                tes.addVertexWithUV(
                    x + v[0],
                    y + v[1],
                    z + v[2],
                    icon.getInterpolatedU(uv[0] * 16D),
                    icon.getInterpolatedV(uv[1] * 16D));
            }
            any[0] = true;
        });
        return any[0];
    }

    /** Faces lying on the block boundary take the light of the neighbour they face, like vanilla blocks. */
    private static int faceBrightness(IBlockAccess world, Block block, int x, int y, int z, float[][] verts,
        float[] normal) {
        int ox = 0, oy = 0, oz = 0;
        if (Math.abs(normal[0]) > 0.99F && onPlane(verts, 0, normal[0] > 0 ? 1F : 0F)) ox = normal[0] > 0 ? 1 : -1;
        if (Math.abs(normal[1]) > 0.99F && onPlane(verts, 1, normal[1] > 0 ? 1F : 0F)) oy = normal[1] > 0 ? 1 : -1;
        if (Math.abs(normal[2]) > 0.99F && onPlane(verts, 2, normal[2] > 0 ? 1F : 0F)) oz = normal[2] > 0 ? 1 : -1;
        return block.getMixedBrightnessForBlock(world, x + ox, y + oy, z + oz);
    }

    private static boolean onPlane(float[][] verts, int axis, float value) {
        for (float[] v : verts) {
            if (Math.abs(v[axis] - value) > 1E-3F) return false;
        }
        return true;
    }

    /** Vanilla's directional face shading. */
    private static float shade(float[] n) {
        float ax = Math.abs(n[0]), ay = Math.abs(n[1]), az = Math.abs(n[2]);
        if (ay >= ax && ay >= az) return n[1] > 0 ? 1F : 0.5F;
        if (az >= ax) return 0.8F;
        return 0.6F;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return renderId;
    }
}
