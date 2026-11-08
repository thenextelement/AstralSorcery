/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.model;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Cache of {@link JsonModel}s by model name (e.g. "BlockWell" -> models/block/BlockWell.json). Cleared on resource
 * reload so resource packs and texture ids stay valid.
 */
@SideOnly(Side.CLIENT)
public class JsonModelLibrary implements IResourceManagerReloadListener {

    public static final JsonModelLibrary INSTANCE = new JsonModelLibrary();

    private final Map<String, JsonModel> models = new HashMap<>();

    private JsonModelLibrary() {}

    public static JsonModel get(String name) {
        return INSTANCE.models.computeIfAbsent(name, JsonModel::load);
    }

    /** Renders the given models (in order) into the unit cube at the current origin. */
    public static void render(String... names) {
        for (String n : names) {
            get(n).render();
        }
    }

    /**
     * Renders a "base" model opaque and a "glass" model translucent on top, mirroring the forge:multi-layer
     * blockstates (Solid + Translucent layer) used in 1.10.
     */
    public static void renderLayered(String solid, String translucent) {
        if (solid != null) {
            render(solid);
        }
        if (translucent != null) {
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDepthMask(false);
            render(translucent);
            GL11.glPopAttrib();
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager manager) {
        for (JsonModel m : models.values()) {
            m.invalidate();
        }
        models.clear();
    }
}
