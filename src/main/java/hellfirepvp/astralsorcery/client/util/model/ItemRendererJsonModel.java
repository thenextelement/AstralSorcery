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

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Item renderer for blocks whose look comes from JSON block models (and optionally a TESR for the dynamic parts).
 * Replaces the 1.10 "inventory" blockstate variants.
 */
@SideOnly(Side.CLIENT)
public class ItemRendererJsonModel implements IItemRenderer {

    private final Map<Integer, Entry> byMeta = new HashMap<>();
    private Entry fallback = null;

    public ItemRendererJsonModel add(int meta, String solid, String translucent) {
        return add(meta, solid, translucent, null, null);
    }

    public ItemRendererJsonModel add(int meta, String solid, String translucent, TileEntitySpecialRenderer tesr,
        TileEntity dummy) {
        Entry e = new Entry(solid, translucent, tesr, dummy);
        byMeta.put(meta, e);
        if (fallback == null) fallback = e;
        return this;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return helper == ItemRendererHelper.ENTITY_ROTATION || helper == ItemRendererHelper.ENTITY_BOBBING
            || helper == ItemRendererHelper.INVENTORY_BLOCK;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        Entry e = byMeta.get(item.getItemDamage());
        if (e == null) e = fallback;
        if (e == null) return;
        ModelRenderHelper.beginItem(type);
        JsonModelLibrary.renderLayered(e.solid, e.translucent);
        if (e.tesr != null && e.dummy != null) {
            try {
                e.tesr.renderTileEntityAt(e.dummy, 0, 0, 0, 0F);
            } catch (Exception ignored) {
                // dummy tiles have no world; dynamic parts are optional for the item form
            }
        }
        ModelRenderHelper.endItem();
    }

    private static class Entry {

        private final String solid, translucent;
        private final TileEntitySpecialRenderer tesr;
        private final TileEntity dummy;

        private Entry(String solid, String translucent, TileEntitySpecialRenderer tesr, TileEntity dummy) {
            this.solid = solid;
            this.translucent = translucent;
            this.tesr = tesr;
            this.dummy = dummy;
        }
    }
}
