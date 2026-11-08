/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.client.util.ItemColorizationHelper;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.ICraftingProgress;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileAttunementRelay;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TraitRecipe
 * Created by HellFirePvP
 * Date: 06.03.2017 / 15:57
 */
public class TraitRecipe extends ConstellationRecipe implements ICraftingProgress {

    public static final BlockPos[] offsetRelays = new BlockPos[] { new BlockPos(0, 0, 3), new BlockPos(2, 0, 2),
        new BlockPos(3, 0, 0), new BlockPos(2, 0, -2), new BlockPos(0, 0, -3), new BlockPos(-2, 0, -2),
        new BlockPos(-3, 0, 0), new BlockPos(-2, 0, 2) };

    private List<ItemHandle> additionallyRequiredStacks = Lists.newLinkedList();
    private Map<TraitRecipeSlot, ItemHandle> matchTraitStacks = new HashMap<>();
    private IConstellation requiredConstellation = null;

    protected TraitRecipe(TileAltar.AltarLevel neededLevel, IAccessibleRecipe recipe) {
        super(neededLevel, recipe);
    }

    protected TraitRecipe(TileAltar.AltarLevel neededLevel, AbstractCacheableRecipe recipe) {
        super(neededLevel, recipe);
    }

    public TraitRecipe(AbstractCacheableRecipe recipe) {
        this(recipe.make());
    }

    public TraitRecipe(IAccessibleRecipe recipe) {
        super(TileAltar.AltarLevel.TRAIT_CRAFT, recipe);
        setPassiveStarlightRequirement(7500);
    }

    // 1.12.2-1.8.5: the four inner slots of the iridescent altar grid
    public TraitRecipe setInnerTraitItem(Item i, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new ItemStack(i), slots);
    }

    public TraitRecipe setInnerTraitItem(Block b, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new ItemStack(b), slots);
    }

    public TraitRecipe setInnerTraitItem(ItemStack stack, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new ItemHandle(stack), slots);
    }

    public TraitRecipe setInnerTraitItem(String oreDict, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new ItemHandle(oreDict), slots);
    }

    public TraitRecipe setInnerTraitItem(FluidStack fluid, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new ItemHandle(fluid), slots);
    }

    public TraitRecipe setInnerTraitItem(Fluid fluid, int mbAmount, TraitRecipeSlot... slots) {
        return setInnerTraitItem(new FluidStack(fluid, mbAmount), slots);
    }

    public TraitRecipe setInnerTraitItem(Fluid fluid, TraitRecipeSlot... slots) {
        return setInnerTraitItem(fluid, 1000, slots);
    }

    public TraitRecipe setInnerTraitItem(ItemHandle handle, TraitRecipeSlot... slots) {
        for (TraitRecipeSlot slot : slots) {
            matchTraitStacks.put(slot, handle);
        }
        return this;
    }

    @Nonnull
    public List<ItemStack> getInnerTraitItems(TraitRecipeSlot slot) {
        ItemHandle handle = matchTraitStacks.get(slot);
        if (handle != null) {
            return handle.getApplicableItems();
        }
        return Lists.newArrayList();
    }

    @Nullable
    public ItemHandle getInnerTraitItemHandle(TraitRecipeSlot slot) {
        return matchTraitStacks.get(slot);
    }

    // 1.8.5 names for the outer (relay) items
    public TraitRecipe addOuterTraitItem(Item i) {
        return addTraitItem(i);
    }

    public TraitRecipe addOuterTraitItem(Block b) {
        return addTraitItem(b);
    }

    public TraitRecipe addOuterTraitItem(ItemStack stack) {
        return addTraitItem(stack);
    }

    public TraitRecipe addOuterTraitItem(String oreDict) {
        return addTraitItem(oreDict);
    }

    public TraitRecipe addOuterTraitItem(FluidStack fluid) {
        return addTraitItem(fluid);
    }

    public TraitRecipe addOuterTraitItem(Fluid fluid, int mbAmount) {
        return addTraitItem(fluid, mbAmount);
    }

    public TraitRecipe addOuterTraitItem(Fluid fluid) {
        return addTraitItem(fluid);
    }

    public TraitRecipe addOuterTraitItem(ItemHandle handle) {
        return addTraitItem(handle);
    }

    public TraitRecipe addTraitItem(Item i) {
        return addTraitItem(new ItemStack(i));
    }

    public TraitRecipe addTraitItem(Block b) {
        return addTraitItem(new ItemStack(b));
    }

    public TraitRecipe addTraitItem(ItemStack stack) {
        return addTraitItem(new ItemHandle(stack));
    }

    public TraitRecipe addTraitItem(String oreDict) {
        return addTraitItem(new ItemHandle(oreDict));
    }

    public TraitRecipe addTraitItem(FluidStack fluid) {
        return addTraitItem(new ItemHandle(fluid));
    }

    public TraitRecipe addTraitItem(Fluid fluid, int mbAmount) {
        return addTraitItem(new FluidStack(fluid, mbAmount));
    }

    public TraitRecipe addTraitItem(Fluid fluid) {
        return addTraitItem(fluid, 1000);
    }

    public TraitRecipe addTraitItem(ItemHandle handle) {
        additionallyRequiredStacks.add(handle);
        return this;
    }

    @Nonnull
    public List<List<ItemStack>> getTraitItems() {
        List<List<ItemStack>> out = Lists.newArrayList();
        for (ItemHandle handle : additionallyRequiredStacks) {
            out.add(handle.getApplicableItems());
        }
        return out;
    }

    @Nonnull
    public List<ItemHandle> getTraitItemHandles() {
        return Lists.newArrayList(additionallyRequiredStacks);
    }

    public void setRequiredConstellation(IConstellation requiredConstellation) {
        this.requiredConstellation = requiredConstellation;
    }

    @Nullable
    public IConstellation getRequiredConstellation() {
        return requiredConstellation;
    }

    @Override
    public int craftingTickTime() {
        return 1400;
    }

    @Override
    public boolean tryProcess(TileAltar altar, ActiveCraftingTask runningTask, NBTTagCompound craftingData,
        int activeCraftingTick) {
        if (!fulfillesStarlightRequirement(altar)) {
            return false; // Duh.
        }

        List<CraftingFocusStack> stacks = collectCurrentStacks(craftingData);
        if (!matchFocusStacks(altar, stacks)) {
            return false;
        }

        int required = additionallyRequiredStacks.size();
        if (required <= 0) return true;
        int part = craftingTickTime() / 2;
        int offset = craftingTickTime() / 10;
        int cttPart = part / required;
        for (int i = 0; i < required; i++) {
            int timing = (i * cttPart) + offset;
            if (activeCraftingTick >= timing) {
                CraftingFocusStack found = null;
                for (CraftingFocusStack stack : stacks) {
                    if (stack.stackIndex == i) {
                        found = stack;
                        break;
                    }
                }
                if (found == null) {
                    BlockPos next = findUnusedRelay(altar, stacks);
                    if (next != null) {
                        CraftingFocusStack stack = new CraftingFocusStack(i, next);
                        stacks.add(stack);
                        storeCurrentStacks(craftingData, stacks);
                    }
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler,
        boolean ignoreStarlightRequirement) {
        IConstellation req = getRequiredConstellation();
        if (req != null) {
            IConstellation focus = altar.getFocusedConstellation();
            if (focus == null) return false;
            if (!req.equals(focus)) return false;
        }
        for (TraitRecipeSlot slot : TraitRecipeSlot.values()) {
            ItemStack altarItem = invHandler.getStackInSlot(slot.getSlotId());
            ItemHandle expected = matchTraitStacks.get(slot);
            if (expected != null) {
                if (!expected.matchCrafting(altarItem)) {
                    return false;
                }
            } else if (altarItem != null && altarItem.getItem() != null) {
                return false;
            }
        }
        return super.matches(altar, invHandler, ignoreStarlightRequirement);
    }

    public void consumeOuterInputs(TileAltar altar, ActiveCraftingTask craftingTask) {
        List<CraftingFocusStack> stacks = collectCurrentStacks(craftingTask.getCraftingData());
        BlockPos pos = new BlockPos(altar.xCoord, altar.yCoord, altar.zCoord);
        for (CraftingFocusStack stack : stacks) {
            if (stack.stackIndex < 0 || stack.stackIndex >= additionallyRequiredStacks.size()) continue; // Duh

            ItemHandle required = additionallyRequiredStacks.get(stack.stackIndex);
            TileAttunementRelay tar = MiscUtils
                .getTileAt(altar.getWorldObj(), pos.add(stack.offset), TileAttunementRelay.class, true);
            if (tar == null) continue;
            // The relay item was matched in the same tick the recipe finished, so it is the expected one.
            ItemStack found = tar.getInventoryHandler()
                .getStackInSlot(0);
            if (found == null) continue;
            if (required.getFluidTypeAndAmount() != null) {
                ItemStack drained = ItemUtils.drainFluidFromItem(found, required.getFluidTypeAndAmount(), true);
                if (drained != null) {
                    tar.getInventoryHandler()
                        .setInventorySlotContents(0, drained);
                }
            } else if (found.getItem()
                .hasContainerItem(found)) {
                    tar.getInventoryHandler()
                        .setInventorySlotContents(
                            0,
                            found.getItem()
                                .getContainerItem(found));
                } else {
                    ItemUtils.decrStackInInventory(tar.getInventoryHandler(), 0);
                }
            tar.markForUpdate();
        }
    }

    @Nonnull
    @Override
    public ResearchProgression getRequiredProgression() {
        return ResearchProgression.RADIANCE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);
        playActiveCraftEffects(altar, state, rand);

        ActiveCraftingTask act = altar.getActiveCraftingTask();
        if (act != null) {
            List<CraftingFocusStack> stacks = collectCurrentStacks(act.getCraftingData());
            for (CraftingFocusStack stack : stacks) {
                if (stack.stackIndex < 0 || stack.stackIndex >= additionallyRequiredStacks.size()) continue; // Duh

                ItemHandle required = additionallyRequiredStacks.get(stack.stackIndex);
                BlockPos pos = new BlockPos(altar.xCoord, altar.yCoord, altar.zCoord);
                TileAttunementRelay tar = MiscUtils
                    .getTileAt(altar.getWorldObj(), pos.add(stack.offset), TileAttunementRelay.class, true);
                if (tar != null) { // If it's null then the server messed up or we're desynced..
                    ItemStack found = tar.getInventoryHandler()
                        .getStackInSlot(0);
                    if (found != null && found.getItem() != null && required.matchCrafting(found)) {
                        Color c = ItemColorizationHelper.getDominantColorFromItemStack(found);
                        if (c == null) {
                            c = BlockCollectorCrystal.CollectorCrystalType.CELESTIAL_CRYSTAL.displayColor;
                        }
                        if (ClientScheduler.getClientTick() % 35 == 0) {
                            EffectLightbeam beam = EffectHandler.getInstance()
                                .lightbeam(
                                    new Vector3(tar).add(0.5, 0.1, 0.5),
                                    new Vector3(altar).add(0.5, 4.5, 0.5),
                                    0.8);
                            beam.setColorOverlay(c);
                        }
                        if (rand.nextBoolean()) {
                            Vector3 at = new Vector3(tar);
                            at.add(rand.nextFloat() * 0.8 + 0.1, 0, rand.nextFloat() * 0.8 + 0.1);
                            EntityFXFacingParticle p = EffectHelper
                                .genericFlareParticle(at.getX(), at.getY(), at.getZ());
                            p.setAlphaMultiplier(0.7F);
                            p.setMaxAge((int) (30 + rand.nextFloat() * 50));
                            p.gravity(0.01)
                                .scale(0.3F + rand.nextFloat() * 0.1F);
                            p.setColor(c);
                            if (rand.nextInt(3) == 0) {
                                p.gravity(0.004)
                                    .scale(0.1F + rand.nextFloat() * 0.1F);
                                p.setColor(Color.WHITE);
                            }
                        }
                    } else {
                        List<ItemStack> stacksApplicable = required.getApplicableItemsForRender();
                        if (stacksApplicable.size() > 0) {
                            int mod = (int) (ClientScheduler.getClientTick() % (stacksApplicable.size() * 60));
                            ItemStack element = stacksApplicable.get(
                                MathHelper.floor_double(
                                    MathHelper.clamp_double(
                                        stacksApplicable.size() * (mod / (stacksApplicable.size() * 60)),
                                        0,
                                        stacksApplicable.size() - 1)));
                            Color c = ItemColorizationHelper.getDominantColorFromItemStack(element);
                            if (c == null) {
                                c = BlockCollectorCrystal.CollectorCrystalType.CELESTIAL_CRYSTAL.displayColor;
                            }
                            if (ClientScheduler.getClientTick() % 35 == 0) {
                                EffectLightbeam beam = EffectHandler.getInstance()
                                    .lightbeam(
                                        new Vector3(tar).add(0.5, 0.1, 0.5),
                                        new Vector3(altar).add(0.5, 4.5, 0.5),
                                        0.8);
                                beam.setColorOverlay(c);
                            }
                            if (rand.nextBoolean()) {
                                Vector3 at = new Vector3(tar);
                                at.add(rand.nextFloat() * 0.8 + 0.1, 0, rand.nextFloat() * 0.8 + 0.1);
                                EntityFXFacingParticle p = EffectHelper
                                    .genericFlareParticle(at.getX(), at.getY(), at.getZ());
                                p.setAlphaMultiplier(0.7F);
                                p.setMaxAge((int) (30 + rand.nextFloat() * 50));
                                p.gravity(0.01)
                                    .scale(0.3F + rand.nextFloat() * 0.1F);
                                p.setColor(c);
                                if (rand.nextInt(3) == 0) {
                                    p.gravity(0.004)
                                        .scale(0.1F + rand.nextFloat() * 0.1F);
                                    p.setColor(Color.WHITE);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playActiveCraftEffects(TileAltar altar, ActiveCraftingTask.CraftingState state, Random rand) {
        if (state != ActiveCraftingTask.CraftingState.ACTIVE) return;
        Vector3 thisAltar = new Vector3(altar).add(0.5, 0.5, 0.5);
        EntityFXFacingParticle p;
        if (rand.nextInt(4) == 0) {
            p = EffectHelper.genericFlareParticle(
                altar.xCoord - 3 + rand.nextFloat() * 7,
                altar.yCoord + 0.02,
                altar.zCoord - 3 + rand.nextFloat() * 7);
            p.gravity(0.004)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                .scale(rand.nextFloat() * 0.2F + 0.15F);
            p.setColor(Color.WHITE);
        }
        Vector3 r = Vector3.random()
            .setY(0)
            .normalize()
            .multiply(1.3 + rand.nextFloat() * 0.5)
            .add(
                thisAltar.clone()
                    .addY(2 + rand.nextFloat() * 0.4));
        p = EffectHelper.genericFlareParticle(r.getX(), r.getY(), r.getZ());
        p.gravity(0.004)
            .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
            .scale(rand.nextFloat() * 0.2F + 0.1F);
        p.setColor(Color.WHITE);
        for (int i = 0; i < 2; i++) {
            r = Vector3.random()
                .setY(0)
                .normalize()
                .multiply(2 + rand.nextFloat() * 0.5)
                .add(
                    thisAltar.clone()
                        .addY(1.1 + rand.nextFloat() * 0.4));
            p = EffectHelper.genericFlareParticle(r.getX(), r.getY(), r.getZ());
            p.gravity(0.004)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT)
                .scale(rand.nextFloat() * 0.2F + 0.1F);
            p.setColor(Color.WHITE);
        }
        if (rand.nextInt(20) == 0) {
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
            lightbeam.setColorOverlay(Color.WHITE);
        }
    }

    /** Relays still waiting for their item show a slowly spinning ghost of the expected item. */
    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftTESRRender(TileAltar altar, double x, double y, double z, float partialTicks) {
        super.onCraftTESRRender(altar, x, y, z, partialTicks);
        ActiveCraftingTask act = altar.getActiveCraftingTask();
        if (act == null) return;
        BlockPos pos = new BlockPos(altar.xCoord, altar.yCoord, altar.zCoord);
        for (CraftingFocusStack stack : collectCurrentStacks(act.getCraftingData())) {
            if (stack.stackIndex < 0 || stack.stackIndex >= additionallyRequiredStacks.size()) continue;
            ItemHandle required = additionallyRequiredStacks.get(stack.stackIndex);
            TileAttunementRelay tar = MiscUtils
                .getTileAt(altar.getWorldObj(), pos.add(stack.offset), TileAttunementRelay.class, true);
            ItemStack found = tar == null ? null
                : tar.getInventoryHandler()
                    .getStackInSlot(0);
            if (found != null && found.getItem() != null && required.matchCrafting(found)) continue;
            List<ItemStack> applicable = required.getApplicableItemsForRender();
            if (applicable.isEmpty()) continue;
            int idx = (int) ((ClientScheduler.getClientTick() / 60) % applicable.size());
            hellfirepvp.astralsorcery.client.util.RenderingUtils.renderTranslucentItemGhost(
                applicable.get(idx),
                x + stack.offset.getX(),
                y + stack.offset.getY(),
                z + stack.offset.getZ(),
                partialTicks);
        }
    }

    @Nullable
    protected BlockPos findUnusedRelay(TileAltar center, List<CraftingFocusStack> found) {
        List<BlockPos> eligableRelayOffsets = Lists.newLinkedList();
        for (int xx = -3; xx <= 3; xx++) {
            for (int zz = -3; zz <= 3; zz++) {
                if (xx == 0 && zz == 0) continue; // Not that it matters tho

                BlockPos offset = new BlockPos(xx, 0, zz);
                BlockPos pos = new BlockPos(center.xCoord, center.yCoord, center.zCoord);
                TileAttunementRelay tar = MiscUtils
                    .getTileAt(center.getWorldObj(), pos.add(offset), TileAttunementRelay.class, true);
                if (tar != null) {
                    eligableRelayOffsets.add(offset);
                }
            }
        }
        for (CraftingFocusStack stack : found) {
            eligableRelayOffsets.remove(stack.offset);
        }
        if (eligableRelayOffsets.size() <= 0) {
            return null;
        }
        return eligableRelayOffsets.get(center.getWorldObj().rand.nextInt(eligableRelayOffsets.size()));
    }

    protected boolean matchFocusStacks(TileAltar altar, List<CraftingFocusStack> stacks) {
        for (CraftingFocusStack stack : stacks) {
            int index = stack.stackIndex;
            if (index < 0 || index >= additionallyRequiredStacks.size()) continue;
            ItemHandle required = additionallyRequiredStacks.get(index);
            BlockPos pos = new BlockPos(altar.xCoord, altar.yCoord, altar.zCoord);
            TileAttunementRelay relay = MiscUtils
                .getTileAt(altar.getWorldObj(), pos.add(stack.offset), TileAttunementRelay.class, true);
            if (relay == null) {
                return false;
            }
            ItemStack in = relay.getInventoryHandler()
                .getStackInSlot(0);
            if (in == null || in.getItem() == null || !required.matchCrafting(in)) {
                return false;
            }
        }
        return true;
    }

    protected void storeCurrentStacks(NBTTagCompound craftingStorage, List<CraftingFocusStack> stacks) {
        NBTTagList list = new NBTTagList();
        for (CraftingFocusStack stack : stacks) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("focusIndex", stack.stackIndex);
            NBTUtils.writeBlockPosToNBT(stack.offset, tag);
            list.appendTag(tag);
        }
        craftingStorage.setTag("offsetFocusList", list);
    }

    protected List<CraftingFocusStack> collectCurrentStacks(NBTTagCompound craftingStorage) {
        List<CraftingFocusStack> stacks = Lists.newLinkedList();
        NBTTagList list = craftingStorage.getTagList("offsetFocusList", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound cmp = list.getCompoundTagAt(i);
            int index = cmp.getInteger("focusIndex");
            BlockPos pos = NBTUtils.readBlockPosFromNBT(cmp);
            stacks.add(new CraftingFocusStack(index, pos));
        }
        return stacks;
    }

    protected static class CraftingFocusStack {

        protected final Integer stackIndex; // Index of required stack
        protected final BlockPos offset;

        protected CraftingFocusStack(Integer stackIndex, BlockPos offset) {
            this.stackIndex = stackIndex;
            this.offset = offset;
        }

    }

    public static enum TraitRecipeSlot {

        UPPER_CENTER(21),
        LEFT_CENTER(22),
        RIGHT_CENTER(23),
        LOWER_CENTER(24);

        private final int slotId;

        TraitRecipeSlot(int slotId) {
            this.slotId = slotId;
        }

        public int getSlotId() {
            return slotId;
        }

    }

}
