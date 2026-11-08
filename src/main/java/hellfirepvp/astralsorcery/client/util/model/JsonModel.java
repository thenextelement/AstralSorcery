/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.model;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;

/**
 * Renders 1.8+ style block model JSON files (the ones shipped in assets/astralsorcery/models/block) on 1.7.10.
 * <p>
 * 1.7.10 has no baked model pipeline, so the original Astral Sorcery block models would be invisible. This class
 * parses the "elements" format (from/to boxes, per-face uv, face and element rotation) and draws it with the
 * Tessellator, binding each texture file directly instead of going through the block atlas. Geometry is compiled
 * into one display list per texture and cached until the next resource reload.
 * <p>
 * Coordinates are in model space (0..16 = one block) and are drawn into the 0..1 unit cube at the current GL origin.
 */
@SideOnly(Side.CLIENT)
public class JsonModel {

    private final String name;
    private final Map<String, ResourceLocation> textures = new HashMap<>();
    private final List<Element> elements = new ArrayList<>();
    private final Map<ResourceLocation, AnimationInfo> animations = new HashMap<>();
    private Map<ResourceLocation, Integer> compiledLists = null;

    JsonModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /** Draws the model into the unit cube at the current origin. Caller controls blending, color and lighting. */
    public void render() {
        if (compiledLists == null) {
            compile();
        }
        for (Map.Entry<ResourceLocation, Integer> entry : compiledLists.entrySet()) {
            Minecraft.getMinecraft()
                .getTextureManager()
                .bindTexture(entry.getKey());
            AnimationInfo anim = animations.get(entry.getKey());
            if (anim != null) {
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                GL11.glTranslatef(0, anim.currentFrameOffset(), 0);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
            }
            GL11.glCallList(entry.getValue());
            if (anim != null) {
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
            }
        }
    }

    void invalidate() {
        if (compiledLists != null) {
            for (int list : compiledLists.values()) {
                GL11.glDeleteLists(list, 1);
            }
        }
        compiledLists = null;
    }

    private void compile() {
        Map<ResourceLocation, List<Face>> byTexture = new LinkedHashMap<>();
        for (Element e : elements) {
            for (Face f : e.faces) {
                ResourceLocation tex = resolveTexture(f.texture);
                if (tex == null) continue;
                byTexture.computeIfAbsent(tex, k -> new ArrayList<>())
                    .add(f);
            }
        }
        compiledLists = new LinkedHashMap<>();
        Tessellator tes = Tessellator.instance;
        for (Map.Entry<ResourceLocation, List<Face>> entry : byTexture.entrySet()) {
            AnimationInfo anim = animations.get(entry.getKey());
            float vScale = anim == null ? 1F : 1F / anim.frameCount;
            int list = GL11.glGenLists(1);
            GL11.glNewList(list, GL11.GL_COMPILE);
            tes.startDrawingQuads();
            for (Face f : entry.getValue()) {
                tes.setNormal(f.normal[0], f.normal[1], f.normal[2]);
                for (int i = 0; i < 4; i++) {
                    float[] v = f.vertices[i];
                    float[] uv = f.uvs[i];
                    tes.addVertexWithUV(v[0], v[1], v[2], uv[0], uv[1] * vScale);
                }
            }
            tes.draw();
            GL11.glEndList();
            compiledLists.put(entry.getKey(), list);
        }
    }

    private ResourceLocation resolveTexture(String ref) {
        String key = ref;
        int guard = 0;
        while (key != null && key.startsWith("#") && guard++ < 10) {
            ResourceLocation direct = textures.get(key.substring(1));
            if (direct != null) return direct;
            key = null;
        }
        if (key == null) return null;
        return toTextureLocation(key);
    }

    static ResourceLocation toTextureLocation(String modelTexture) {
        String domain = "minecraft";
        String path = modelTexture;
        int idx = modelTexture.indexOf(':');
        if (idx >= 0) {
            domain = modelTexture.substring(0, idx);
            path = modelTexture.substring(idx + 1);
        }
        return new ResourceLocation(domain, "textures/" + path + ".png");
    }

    // ------------------------------------------------------------------------------------------------ parsing

    static JsonModel load(String name) {
        JsonModel model = new JsonModel(name);
        try {
            model.parseRecursive(name, AstralSorcery.MODID, 0);
        } catch (Exception exc) {
            AstralSorcery.log.warn("[JsonModel] Could not load model " + name + ": " + exc.getMessage());
        }
        model.detectAnimations();
        return model;
    }

    private void parseRecursive(String modelName, String defaultDomain, int depth) throws Exception {
        if (depth > 8) return;
        String domain = defaultDomain;
        String path = modelName;
        int idx = modelName.indexOf(':');
        if (idx >= 0) {
            domain = modelName.substring(0, idx);
            path = modelName.substring(idx + 1);
        }
        if (!domain.equals(AstralSorcery.MODID)) return; // vanilla parents (block/block, block/cube...) carry no elements
        ResourceLocation loc = new ResourceLocation(domain, "models/block/" + path + ".json");
        JsonObject root;
        IResource res = Minecraft.getMinecraft()
            .getResourceManager()
            .getResource(loc);
        try (InputStream in = res.getInputStream()) {
            root = new JsonParser().parse(new InputStreamReader(in, StandardCharsets.UTF_8))
                .getAsJsonObject();
        }
        if (root.has("textures")) {
            for (Map.Entry<String, JsonElement> e : root.getAsJsonObject("textures")
                .entrySet()) {
                // child definitions win over parent definitions
                if (!textures.containsKey(e.getKey())) {
                    String val = e.getValue()
                        .getAsString();
                    if (!val.startsWith("#")) {
                        textures.put(e.getKey(), toTextureLocation(val));
                    }
                }
            }
        }
        if (elements.isEmpty() && root.has("elements")) {
            for (JsonElement je : root.getAsJsonArray("elements")) {
                elements.add(parseElement(je.getAsJsonObject()));
            }
        }
        if (root.has("parent")) {
            // like vanilla, an un-prefixed parent ("block/block") refers to the minecraft domain
            parseRecursive(
                root.get("parent")
                    .getAsString(),
                "minecraft",
                depth + 1);
        }
    }

    private void detectAnimations() {
        for (ResourceLocation tex : textures.values()) {
            ResourceLocation meta = new ResourceLocation(tex.getResourceDomain(), tex.getResourcePath() + ".mcmeta");
            try {
                IResource res = Minecraft.getMinecraft()
                    .getResourceManager()
                    .getResource(meta);
                JsonObject root;
                try (InputStream in = res.getInputStream()) {
                    root = new JsonParser().parse(new InputStreamReader(in, StandardCharsets.UTF_8))
                        .getAsJsonObject();
                }
                if (!root.has("animation")) continue;
                IResource img = Minecraft.getMinecraft()
                    .getResourceManager()
                    .getResource(tex);
                java.awt.image.BufferedImage bi;
                try (InputStream in = img.getInputStream()) {
                    bi = javax.imageio.ImageIO.read(in);
                }
                int frames = Math.max(1, bi.getHeight() / bi.getWidth());
                if (frames <= 1) continue;
                JsonObject animObj = root.getAsJsonObject("animation");
                int frameTime = animObj.has("frametime") ? animObj.get("frametime")
                    .getAsInt() : 1;
                int[] order;
                if (animObj.has("frames")) {
                    JsonArray arr = animObj.getAsJsonArray("frames");
                    order = new int[arr.size()];
                    for (int i = 0; i < arr.size(); i++) {
                        JsonElement fe = arr.get(i);
                        order[i] = fe.isJsonObject() ? fe.getAsJsonObject()
                            .get("index")
                            .getAsInt() : fe.getAsInt();
                    }
                } else {
                    order = new int[frames];
                    for (int i = 0; i < frames; i++) order[i] = i;
                }
                animations.put(tex, new AnimationInfo(frames, frameTime, order));
            } catch (Exception ignored) {
                // no mcmeta -> not animated
            }
        }
    }

    private static float[] vec3(JsonObject obj, String key) {
        JsonArray arr = obj.getAsJsonArray(key);
        return new float[] { arr.get(0)
            .getAsFloat(),
            arr.get(1)
                .getAsFloat(),
            arr.get(2)
                .getAsFloat() };
    }

    private Element parseElement(JsonObject obj) {
        float[] from = vec3(obj, "from");
        float[] to = vec3(obj, "to");
        Element e = new Element();
        Rotation rot = null;
        if (obj.has("rotation")) {
            JsonObject r = obj.getAsJsonObject("rotation");
            rot = new Rotation();
            rot.origin = vec3(r, "origin");
            rot.axis = r.get("axis")
                .getAsString();
            rot.angle = r.get("angle")
                .getAsFloat();
            rot.rescale = r.has("rescale") && r.get("rescale")
                .getAsBoolean();
        }
        JsonObject faces = obj.getAsJsonObject("faces");
        for (Map.Entry<String, JsonElement> fe : faces.entrySet()) {
            Face f = buildFace(
                fe.getKey(),
                fe.getValue()
                    .getAsJsonObject(),
                from,
                to,
                rot);
            if (f != null) e.faces.add(f);
        }
        return e;
    }

    private static Face buildFace(String dir, JsonObject def, float[] from, float[] to, Rotation rot) {
        float x1 = from[0], y1 = from[1], z1 = from[2];
        float x2 = to[0], y2 = to[1], z2 = to[2];
        float[][] corners; // order: top-left, bottom-left, bottom-right, top-right as seen from outside
        float[] defUV;
        float[] normal;
        switch (dir) {
            case "north":
                corners = new float[][] { { x2, y2, z1 }, { x2, y1, z1 }, { x1, y1, z1 }, { x1, y2, z1 } };
                defUV = new float[] { 16 - x2, 16 - y2, 16 - x1, 16 - y1 };
                normal = new float[] { 0, 0, -1 };
                break;
            case "south":
                corners = new float[][] { { x1, y2, z2 }, { x1, y1, z2 }, { x2, y1, z2 }, { x2, y2, z2 } };
                defUV = new float[] { x1, 16 - y2, x2, 16 - y1 };
                normal = new float[] { 0, 0, 1 };
                break;
            case "west":
                corners = new float[][] { { x1, y2, z1 }, { x1, y1, z1 }, { x1, y1, z2 }, { x1, y2, z2 } };
                defUV = new float[] { z1, 16 - y2, z2, 16 - y1 };
                normal = new float[] { -1, 0, 0 };
                break;
            case "east":
                corners = new float[][] { { x2, y2, z2 }, { x2, y1, z2 }, { x2, y1, z1 }, { x2, y2, z1 } };
                defUV = new float[] { 16 - z2, 16 - y2, 16 - z1, 16 - y1 };
                normal = new float[] { 1, 0, 0 };
                break;
            case "up":
                corners = new float[][] { { x1, y2, z1 }, { x1, y2, z2 }, { x2, y2, z2 }, { x2, y2, z1 } };
                defUV = new float[] { x1, z1, x2, z2 };
                normal = new float[] { 0, 1, 0 };
                break;
            case "down":
                corners = new float[][] { { x1, y1, z2 }, { x1, y1, z1 }, { x2, y1, z1 }, { x2, y1, z2 } };
                defUV = new float[] { x1, 16 - z2, x2, 16 - z1 };
                normal = new float[] { 0, -1, 0 };
                break;
            default:
                return null;
        }
        float[] uv = defUV;
        if (def.has("uv")) {
            JsonArray a = def.getAsJsonArray("uv");
            uv = new float[] { a.get(0)
                .getAsFloat(),
                a.get(1)
                    .getAsFloat(),
                a.get(2)
                    .getAsFloat(),
                a.get(3)
                    .getAsFloat() };
        }
        float[][] uvCorners = new float[][] { { uv[0], uv[1] }, { uv[0], uv[3] }, { uv[2], uv[3] }, { uv[2], uv[1] } };
        int steps = def.has("rotation") ? ((def.get("rotation")
            .getAsInt() / 90) % 4 + 4) % 4 : 0;

        Face f = new Face();
        f.texture = def.has("texture") ? def.get("texture")
            .getAsString() : "#texture";
        for (int i = 0; i < 4; i++) {
            float[] c = corners[i].clone();
            if (rot != null) rot.apply(c);
            f.vertices[i] = new float[] { c[0] / 16F, c[1] / 16F, c[2] / 16F };
            float[] t = uvCorners[(i + steps) % 4];
            f.uvs[i] = new float[] { t[0] / 16F, t[1] / 16F };
        }
        if (rot != null) {
            float[] n = normal.clone();
            rot.applyDirection(n);
            normal = n;
        }
        f.normal = normal;
        return f;
    }

    // ------------------------------------------------------------------------------------------------ data

    private static class Element {

        private final List<Face> faces = new ArrayList<>(6);
    }

    private static class Face {

        private String texture;
        private final float[][] vertices = new float[4][];
        private final float[][] uvs = new float[4][];
        private float[] normal;
    }

    private static class Rotation {

        private float[] origin;
        private String axis;
        private float angle;
        private boolean rescale;

        private void apply(float[] p) {
            float x = p[0] - origin[0], y = p[1] - origin[1], z = p[2] - origin[2];
            applyRaw(p, x, y, z);
            if (rescale) {
                float scale = (float) (1D / Math.cos(Math.toRadians(Math.abs(angle))));
                switch (axis) {
                    case "x":
                        p[1] = (p[1] - origin[1]) * scale + origin[1];
                        p[2] = (p[2] - origin[2]) * scale + origin[2];
                        break;
                    case "y":
                        p[0] = (p[0] - origin[0]) * scale + origin[0];
                        p[2] = (p[2] - origin[2]) * scale + origin[2];
                        break;
                    case "z":
                        p[0] = (p[0] - origin[0]) * scale + origin[0];
                        p[1] = (p[1] - origin[1]) * scale + origin[1];
                        break;
                }
            }
        }

        private void applyRaw(float[] p, float x, float y, float z) {
            double rad = Math.toRadians(angle);
            float cos = (float) Math.cos(rad), sin = (float) Math.sin(rad);
            switch (axis) {
                case "x":
                    p[0] = x + origin[0];
                    p[1] = y * cos - z * sin + origin[1];
                    p[2] = y * sin + z * cos + origin[2];
                    break;
                case "y":
                    p[0] = x * cos + z * sin + origin[0];
                    p[1] = y + origin[1];
                    p[2] = -x * sin + z * cos + origin[2];
                    break;
                case "z":
                    p[0] = x * cos - y * sin + origin[0];
                    p[1] = x * sin + y * cos + origin[1];
                    p[2] = z + origin[2];
                    break;
                default:
                    p[0] = x + origin[0];
                    p[1] = y + origin[1];
                    p[2] = z + origin[2];
            }
        }

        private void applyDirection(float[] n) {
            float[] saved = origin;
            origin = new float[] { 0, 0, 0 };
            applyRaw(n, n[0], n[1], n[2]);
            origin = saved;
        }
    }

    private static class AnimationInfo {

        private final int frameCount;
        private final int frameTime;
        private final int[] order;

        private AnimationInfo(int frameCount, int frameTime, int[] order) {
            this.frameCount = frameCount;
            this.frameTime = Math.max(1, frameTime);
            this.order = order;
        }

        private float currentFrameOffset() {
            long ticks = Minecraft.getMinecraft().theWorld != null ? Minecraft.getMinecraft().theWorld.getTotalWorldTime()
                : Minecraft.getSystemTime() / 50L;
            int idx = (int) ((ticks / frameTime) % order.length);
            return order[idx] / (float) frameCount;
        }
    }
}
