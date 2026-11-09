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

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Which JSON models a block bakes into the world mesh (solid pass + optional translucent pass), and the block-atlas
 * sprites for their textures.
 */
@SideOnly(Side.CLIENT)
public class JsonBlockModels {

    public static final JsonBlockModels INSTANCE = new JsonBlockModels();

    public static class Spec {

        public final String solid, translucent;

        private Spec(String solid, String translucent) {
            this.solid = solid;
            this.translucent = translucent;
        }
    }

    private final Map<Block, Spec> specs = new HashMap<>();
    private final Map<Block, Map<Integer, Spec>> metaSpecs = new HashMap<>();
    private final Map<String, IIcon> sprites = new HashMap<>();

    private JsonBlockModels() {}

    public static void register(Block block, String solid, String translucent) {
        INSTANCE.specs.put(block, new Spec(solid, translucent));
    }

    /** Overrides the block's models for one metadata value. */
    public static void registerMeta(Block block, int meta, String solid, String translucent) {
        INSTANCE.metaSpecs.computeIfAbsent(block, b -> new HashMap<>())
            .put(meta, new Spec(solid, translucent));
    }

    public static Spec get(Block block) {
        return INSTANCE.specs.get(block);
    }

    public static Spec get(Block block, int meta) {
        Map<Integer, Spec> perMeta = INSTANCE.metaSpecs.get(block);
        if (perMeta != null) {
            Spec s = perMeta.get(meta);
            if (s != null) return s;
        }
        return INSTANCE.specs.get(block);
    }

    public static IIcon getSprite(ResourceLocation textureFile) {
        return INSTANCE.sprites.get(JsonModel.spriteNameFor(textureFile));
    }

    @SubscribeEvent
    public void onStitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() != 0) return; // blocks atlas only
        sprites.clear();
        java.util.List<Spec> all = new java.util.ArrayList<>(specs.values());
        for (Map<Integer, Spec> m : metaSpecs.values()) all.addAll(m.values());
        for (Spec spec : all) {
            for (String name : new String[] { spec.solid, spec.translucent }) {
                if (name == null) continue;
                for (ResourceLocation tex : JsonModel.load(name)
                    .getUsedTextures()) {
                    String sprite = JsonModel.spriteNameFor(tex);
                    if (sprites.containsKey(sprite)) continue;
                    JsonModelSprite s = new JsonModelSprite(sprite, tex);
                    event.map.setTextureEntry(sprite, s);
                    // setTextureEntry ignores duplicates; fetch whatever is registered under the name
                    IIcon registered = event.map.getTextureExtry(sprite);
                    sprites.put(sprite, registered != null ? registered : s);
                }
            }
        }
    }

}
