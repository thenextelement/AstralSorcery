/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.struct.BlockArray;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockArrayRender
 * Created by HellFirePvP
 * Date: 30.09.2016 / 11:37
 */
public class BlockArrayRenderHelper {

    private BlockArray blocks;
    private WorldBlockArrayRenderAccess renderAccess;
    private double rotX, rotY, rotZ;

    public BlockArrayRenderHelper(BlockArray blocks) {
        this.blocks = blocks;
        this.renderAccess = new WorldBlockArrayRenderAccess(blocks);
        resetRotation();
    }

    private void resetRotation() {
        this.rotX = -30;
        this.rotY = 45;
        this.rotZ = 0;
    }

    public void rotate(double x, double y, double z) {
        this.rotX += x;
        this.rotY += y;
        this.rotZ += z;
    }

    public void render3DGUI(double x, double y, float pTicks) {
        GuiScreen scr = Minecraft.getMinecraft().currentScreen;

        if (scr == null) return;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        Minecraft mc = Minecraft.getMinecraft();
        double sc = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight).getScaleFactor();
        GL11.glTranslated(x + 16D / sc, y + 16D / sc, 512);

        double mul = 10.5;

        double size = 2;
        double minSize = 0.5;

        Vec3 max = blocks.getMax();
        Vec3 min = blocks.getMin();

        double maxLength = 0;
        double pointDst = max.xCoord - min.xCoord;
        if (pointDst > maxLength) maxLength = pointDst;
        pointDst = max.yCoord - min.yCoord;
        if (pointDst > maxLength) maxLength = pointDst;
        pointDst = max.zCoord - min.zCoord;
        if (pointDst > maxLength) maxLength = pointDst;
        maxLength -= 5;

        if (maxLength > 0) {
            size = (size - minSize) * (1D - (maxLength / 20D));
        }

        double dr = -5.75 * size;
        GL11.glTranslated(dr, dr, dr);
        GL11.glRotated(rotX, 1, 0, 0);
        GL11.glRotated(rotY, 0, 1, 0);
        GL11.glRotated(rotZ, 0, 0, 1);
        GL11.glTranslated(-dr, -dr, -dr);

        GL11.glScaled(-size * mul, -size * mul, -size * mul);

        RenderBlocks brd = new RenderBlocks(renderAccess);
        TextureHelper.setActiveTextureToAtlasSprite();
        Tessellator tess = Tessellator.instance;
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glDisable(GL11.GL_CULL_FACE); // the mirrored scale flips winding
        int prevPass = getWorldRenderPass();
        for (int pass = 0; pass < 2; pass++) {
            setWorldRenderPass(pass);
            if (pass == 1) {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDepthMask(false);
            }
            tess.startDrawingQuads();
            for (Map.Entry<BlockPos, BakedBlockData> data : renderAccess.blockRenderData.entrySet()) {
                BlockPos at = data.getKey();
                Block block = data.getValue().type;
                if (block == null || block == Blocks.air || !block.canRenderInPass(pass)) continue;
                try {
                    brd.renderBlockByRenderType(block, at.getX(), at.getY(), at.getZ());
                } catch (Exception ignored) {
                    // a block that needs a real world; skip it in the preview
                }
            }
            tess.draw();
        }
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        setWorldRenderPass(prevPass);

        for (Map.Entry<BlockPos, BakedBlockData> data : renderAccess.blockRenderData.entrySet()) {
            BlockPos offset = data.getKey();
            BakedBlockData renderData = data.getValue();
            if (renderData.tileEntity != null && renderData.tesr != null) {
                renderData.tileEntity.setWorldObj(Minecraft.getMinecraft().theWorld);
                renderData.tesr
                    .renderTileEntityAt(renderData.tileEntity, offset.getX(), offset.getY(), offset.getZ(), pTicks);
            }
        }

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private static java.lang.reflect.Field passField;

    private static int getWorldRenderPass() {
        return net.minecraftforge.client.ForgeHooksClient.getWorldRenderPass();
    }

    /** ISBRHs (our baked JSON models) read ForgeHooksClient.worldRenderPass; it has no public setter. */
    private static void setWorldRenderPass(int pass) {
        try {
            if (passField == null) {
                passField = net.minecraftforge.client.ForgeHooksClient.class.getDeclaredField("worldRenderPass");
                passField.setAccessible(true);
            }
            passField.setInt(null, pass);
        } catch (Exception ignored) {}
    }

    public static class BakedBlockData extends BlockArray.BlockInformation {

        private TileEntity tileEntity;
        private TileEntitySpecialRenderer tesr;

        protected BakedBlockData(Block type, int meta, TileEntity te) {
            super(type, meta);
            this.tileEntity = te;
            if (te != null) {
                tesr = TileEntityRendererDispatcher.instance.getSpecialRenderer(te);
            }
        }

    }

    public static class WorldBlockArrayRenderAccess implements IBlockAccess {

        private Map<BlockPos, BakedBlockData> blockRenderData = new HashMap<>();

        public WorldBlockArrayRenderAccess(BlockArray array) {
            for (Map.Entry<BlockPos, BlockArray.BlockInformation> entry : array.getPattern()
                .entrySet()) {
                BlockPos offset = entry.getKey();
                BlockArray.BlockInformation info = entry.getValue();
                if (info.type.hasTileEntity(info.metadata)) {
                    TileEntity te = info.type.createTileEntity(Minecraft.getMinecraft().theWorld, info.metadata);
                    BlockArray.TileEntityCallback callback = array.getTileCallbacks()
                        .get(offset);
                    if (te != null && callback != null) {
                        if (callback.isApplicable(te)) {
                            callback.onPlace(this, offset, te);
                        }
                    }
                    blockRenderData.put(offset, new BakedBlockData(info.type, info.metadata, te));
                } else {
                    blockRenderData.put(offset, new BakedBlockData(info.type, info.metadata, null));
                }
            }
        }

        @Override
        public Block getBlock(int x, int y, int z) {
            BlockPos pos = new BlockPos(x, y, z);
            return isInBounds(pos) ? blockRenderData.get(pos).type : Blocks.air;
        }

        @Nullable
        @Override
        public TileEntity getTileEntity(int x, int y, int z) {
            BlockPos pos = new BlockPos(x, y, z);
            return blockRenderData.containsKey(pos) ? blockRenderData.get(pos).tileEntity : null;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public int getLightBrightnessForSkyBlocks(int x, int y, int z, int lightValue) {
            return 0xF000F0; // full bright preview
        }

        @Override
        public int getBlockMetadata(int x, int y, int z) {
            BlockPos pos = new BlockPos(x, y, z);
            return isInBounds(pos) ? blockRenderData.get(pos).metadata : 0; // Blocks.AIR.getDefaultState();
        }

        @Override
        public boolean isAirBlock(int x, int y, int z) {
            BlockPos pos = new BlockPos(x, y, z);
            return !isInBounds(pos) || blockRenderData.get(pos).type == Blocks.air;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public BiomeGenBase getBiomeGenForCoords(int x, int z) {
            return BiomeGenBase.getBiome(0);
        }

        private boolean isInBounds(BlockPos pos) {
            return blockRenderData.containsKey(pos);
        }

        @Override
        public int getHeight() {
            return 0;
        }

        @Override
        public boolean extendedLevelsInChunkCache() {
            return false;
        }

        @Override
        public int isBlockProvidingPowerTo(int x, int y, int z, int directionIn) {
            return 0;
        }

        // @Override
        // @SideOnly(Side.CLIENT)
        // public WorldType getWorldType() {
        // return Minecraft.getMinecraft().theWorld.getWorldType();
        // }

        @Override
        public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
            BlockPos pos = new BlockPos(x, y, z);
            return isInBounds(pos) ? getBlock(pos.getX(), pos.getY(), pos.getZ())
                .isSideSolid(this, pos.getX(), pos.getY(), pos.getZ(), side) : _default;
        }

    }

}
