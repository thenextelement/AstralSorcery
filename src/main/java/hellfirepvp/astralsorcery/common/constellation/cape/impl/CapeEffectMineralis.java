/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.block.EffectTranslucentFallingBlock;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/** (backported from 1.12.2-1.8.5 to 1.7.10) */
public class CapeEffectMineralis extends CapeArmorEffect {

    private static int highlightRange = 8;

    public CapeEffectMineralis(NBTTagCompound cmp) {
        super(cmp, "mineralis");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.mineralis;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, 0.15F);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        highlightRange = cfg.getInt(
            getKey() + "HighlightRange",
            getConfigurationSection(),
            highlightRange,
            4,
            32,
            "Sets the highlight radius in which the cape effect will search for the block you're holding.");
    }

    /** Ghost-highlights blocks of the held block type nearby (1.7: no offhand). */
    @SideOnly(Side.CLIENT)
    public void playClientHighlightTick(EntityPlayer pl) {
        if (rand.nextFloat() > 0.2F) return;
        ItemStack main = pl.getHeldItem();
        if (main == null || main.getItem() == null) return;
        Block b = Block.getBlockFromItem(main.getItem());
        if (b == null || b == net.minecraft.init.Blocks.air) return;
        int meta = main.getItem()
            .getMetadata(main.getItemDamage());
        List<BlockPos> blocks = MiscUtils.searchAreaFor(pl.worldObj, new BlockPos(pl), b, meta, highlightRange);
        if (blocks.isEmpty()) return;
        int index = blocks.size() > 10 ? rand.nextInt(blocks.size()) : rand.nextInt(10);
        if (index >= blocks.size()) {
            return;
        }
        BlockPos at = blocks.get(index);
        Block act = pl.worldObj.getBlock(at.getX(), at.getY(), at.getZ());
        EffectTranslucentFallingBlock bl = EffectHandler.getInstance()
            .translucentFallingBlock(new Vector3(at).add(0.5, 0.5, 0.5), act);
        bl.setDisableDepth(true)
            .setScaleFunction(new EntityComplexFX.ScaleFunction.Shrink<>());
        bl.setMotion(0, 0.02, 0)
            .setAlphaFunction(EntityComplexFX.AlphaFunction.PYRAMID);
        bl.tumble();
        bl.setMaxAge(40 + rand.nextInt(15));
    }
}
