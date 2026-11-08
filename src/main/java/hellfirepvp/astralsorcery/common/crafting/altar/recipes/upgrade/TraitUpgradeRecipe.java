/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes.upgrade;

import java.awt.Color;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.block.BlockBlackMarble;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.crafting.IAltarUpgradeRecipe;
import hellfirepvp.astralsorcery.common.crafting.INighttimeRecipe;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapeMap;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TraitUpgradeRecipe
 * Created by HellFirePvP
 * Date: 18.10.2017 / 01:23
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
public class TraitUpgradeRecipe extends ConstellationRecipe implements IAltarUpgradeRecipe, INighttimeRecipe {

    private static Vector3[] offsetPillars = new Vector3[] { new Vector3(4, 4, 4), new Vector3(-4, 4, 4),
        new Vector3(4, 4, -4), new Vector3(-4, 4, -4) };

    public TraitUpgradeRecipe() {
        super(
            new ShapedRecipe(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_4.ordinal()))
                .addPart(ItemHandle.getCrystalVariant(false, true), ShapedRecipeSlot.CENTER)
                .addPart(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(), ShapedRecipeSlot.UPPER_CENTER)
                .addPart(
                    BlockMarble.MarbleBlockType.RUNED.asStack(),
                    ShapedRecipeSlot.UPPER_LEFT,
                    ShapedRecipeSlot.UPPER_RIGHT,
                    ShapedRecipeSlot.LOWER_LEFT,
                    ShapedRecipeSlot.LOWER_RIGHT));
        setAttItem(BlockMarble.MarbleBlockType.RUNED.asStack(), AttunementRecipe.AttunementAltarSlot.values());
        setCstItem(
            ItemCraftingComponent.MetaType.RESO_GEM.asStack(),
            ConstellationAtlarSlot.UP_RIGHT_RIGHT,
            ConstellationAtlarSlot.UP_LEFT_LEFT,
            ConstellationAtlarSlot.DOWN_RIGHT_RIGHT,
            ConstellationAtlarSlot.DOWN_LEFT_LEFT);
        setCstItem(
            BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),
            ConstellationAtlarSlot.UP_UP_LEFT,
            ConstellationAtlarSlot.UP_UP_RIGHT,
            ConstellationAtlarSlot.DOWN_DOWN_LEFT,
            ConstellationAtlarSlot.DOWN_DOWN_RIGHT);
    }

    @Override
    public TileAltar.AltarLevel getLevelUpgradingTo() {
        return TileAltar.AltarLevel.TRAIT_CRAFT;
    }

    @Nonnull
    @Override
    public ItemStack getOutputForRender() {
        return new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_4.ordinal());
    }

    @Override
    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler,
        boolean ignoreStarlightRequirement) {
        return altar.getAltarLevel()
            .ordinal() < getLevelUpgradingTo().ordinal()
            && super.matches(altar, invHandler, ignoreStarlightRequirement);
    }

    @Nullable
    @Override
    public ItemStack getOutput(ShapeMap centralGridMap, TileAltar tileAltar) {
        return null;
    }

    @Override
    public void onCraftServerFinish(TileAltar altar, Random rand) {
        super.onCraftServerFinish(altar, rand);
        altar.tryForceLevelUp(getLevelUpgradingTo(), true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);
        if (state != ActiveCraftingTask.CraftingState.ACTIVE) return;

        Vector3 altarVec = new Vector3(altar);
        Vector3 thisAltar = altarVec.clone()
            .add(0.5, 0.5, 0.5);
        for (int i = 0; i < 4; i++) {
            Vector3 dir = offsetPillars[rand.nextInt(offsetPillars.length)].clone();
            dir.multiply(rand.nextFloat());
            dir.setY(dir.getY() * rand.nextFloat());
            dir.add(thisAltar.clone());

            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(dir.getX(), dir.getY(), dir.getZ());
            particle.setColor(MiscUtils.calcRandomConstellationColor(rand.nextFloat()))
                .scale(0.4F + (0.2F * rand.nextFloat()))
                .gravity(0.004);
            particle = EffectHelper.genericFlareParticle(dir.getX(), dir.getY(), dir.getZ());
            particle.setColor(Color.WHITE)
                .scale(0.1F + (0.2F * rand.nextFloat()))
                .gravity(0.004);
        }

        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
            altar.xCoord - 3 + rand.nextFloat() * 7,
            altar.yCoord + 0.02,
            altar.zCoord - 3 + rand.nextFloat() * 7);
        p.gravity(0.004)
            .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
            .scale(rand.nextFloat() * 0.2F + 0.15F);
        p.setColor(Color.WHITE);

        for (int i = 0; i < 10; i++) {
            Vector3 dir = offsetPillars[rand.nextInt(offsetPillars.length)].clone();
            dir.setY(rand.nextFloat() * dir.getY());
            dir.add(-0.3 + 1.6 * rand.nextFloat(), 0, -0.3 + 1.6 * rand.nextFloat());
            Vector3 r = altarVec.clone()
                .add(dir);
            p = EffectHelper.genericFlareParticle(r.getX(), r.getY(), r.getZ());
            p.gravity(0.01 + 0.02 * rand.nextFloat())
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                .scale(rand.nextFloat() * 0.5F + 0.3F);
            p.setColor(Color.WHITE);
        }

        if (rand.nextInt(10) == 0) {
            Vector3 from = new Vector3(
                altar.xCoord - 3 + rand.nextFloat() * 7,
                altar.yCoord + 0.02,
                altar.zCoord - 3 + rand.nextFloat() * 7);
            MiscUtils.applyRandomOffset(from, rand, 0.4F);
            EffectLightbeam lightbeam = EffectHandler.getInstance()
                .lightbeam(
                    from.clone()
                        .addY(4 + rand.nextInt(2)),
                    from,
                    1);
            lightbeam.setMaxAge(64);
            lightbeam.setColorOverlay(new Color(0x5369FF));
        }
        if (tick % 48 == 0 && rand.nextInt(2) == 0) {
            EffectHandler.getInstance()
                .textureSpritePlane(SpriteLibrary.spriteCraftBurst, Vector3.RotAxis.Y_AXIS.clone())
                .setPosition(new Vector3(altar).add(0.5, 0.05, 0.5))
                .setScale(7 + rand.nextInt(2))
                .setNoRotation(rand.nextInt(360));
        }
    }

}
