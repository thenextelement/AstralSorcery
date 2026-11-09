/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.model;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.AstralSorcery;

/**
 * Block-atlas sprite loaded from an arbitrary texture file (e.g. textures/models/lightwell/lightwell.png), so JSON
 * block models can be baked into chunk meshes. Animations (.mcmeta) are kept.
 */
@SideOnly(Side.CLIENT)
public class JsonModelSprite extends TextureAtlasSprite {

    private final ResourceLocation file;

    public JsonModelSprite(String spriteName, ResourceLocation file) {
        super(spriteName);
        this.file = file;
    }

    @Override
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true;
    }

    /** TextureMap stitches the sprite when this returns false. */
    @Override
    public boolean load(IResourceManager manager, ResourceLocation location) {
        try {
            IResource res = manager.getResource(file);
            BufferedImage[] images = new BufferedImage[1 + Minecraft.getMinecraft().gameSettings.mipmapLevels];
            try (InputStream in = res.getInputStream()) {
                images[0] = ImageIO.read(in);
            }
            AnimationMetadataSection anim = (AnimationMetadataSection) res.getMetadata("animation");
            loadSprite(images, anim, Minecraft.getMinecraft().gameSettings.anisotropicFiltering > 1.0F);
            return false;
        } catch (Exception exc) {
            AstralSorcery.log.warn("[JsonModel] Could not load model texture " + file + ": " + exc.getMessage());
            return true;
        }
    }
}
