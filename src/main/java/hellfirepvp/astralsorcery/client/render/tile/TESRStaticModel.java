/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.util.model.JsonModelLibrary;
import hellfirepvp.astralsorcery.client.util.model.ModelRenderHelper;

/** TESR that only draws a block's JSON model(s) - used for blocks that had no TESR in 1.10. */
@SideOnly(Side.CLIENT)
public class TESRStaticModel extends TileEntitySpecialRenderer {

    private final String solid, translucent;

    public TESRStaticModel(String solid, String translucent) {
        this.solid = solid;
        this.translucent = translucent;
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        ModelRenderHelper.beginWorld(x, y, z);
        JsonModelLibrary.renderLayered(solid, translucent);
        ModelRenderHelper.end();
    }
}
