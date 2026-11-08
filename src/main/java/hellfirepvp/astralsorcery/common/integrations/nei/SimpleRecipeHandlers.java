/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.nei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import hellfirepvp.astralsorcery.common.base.LightOreTransmutations;
import hellfirepvp.astralsorcery.common.base.WellLiquefaction;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;

/** One-input -> one-output handlers: starlight infuser, lightwell liquefaction, starlight transmutation. */
public class SimpleRecipeHandlers {

    private static final int IN_X = 40, OUT_X = 110, Y = 20;

    private static List<int[]> twoSlots() {
        List<int[]> l = new ArrayList<>();
        l.add(new int[] { IN_X, Y });
        l.add(new int[] { OUT_X, Y });
        return l;
    }

    public static class Infuser extends ASRecipeHandlerBase {

        @Override
        protected List<ASCachedRecipe> buildAll() {
            List<ASCachedRecipe> out = new ArrayList<>();
            List<AbstractInfusionRecipe> all = new ArrayList<>(InfusionRecipeRegistry.recipes);
            all.addAll(InfusionRecipeRegistry.mtRecipes); // MineTweaker additions are kept separately
            for (AbstractInfusionRecipe r : all) {
                ItemStack res = r.getOutputForRender();
                if (res == null) continue;
                ItemHandle in = r.getInput();
                List<ItemStack> inputs = new ArrayList<>();
                if (in.handleType == ItemHandle.Type.FLUID) {
                    FluidStack fs = in.getFluidTypeAndAmount();
                    ItemStack filled = fs == null ? null
                        : FluidContainerRegistry.fillFluidContainer(
                            new FluidStack(fs.getFluid(), FluidContainerRegistry.BUCKET_VOLUME),
                            FluidContainerRegistry.EMPTY_BUCKET);
                    if (filled != null) inputs.add(filled);
                } else {
                    inputs.addAll(in.getApplicableItems());
                }
                out.add(new ASCachedRecipe(res, OUT_X, Y).addInput(inputs, IN_X, Y));
            }
            return out;
        }

        @Override
        protected List<int[]> slotPositions() {
            return twoSlots();
        }

        @Override
        protected String titleKey() {
            return "astralsorcery.nei.infuser";
        }

        @Override
        public String getOverlayIdentifier() {
            return "astralsorcery.infuser";
        }
    }

    public static class Well extends ASRecipeHandlerBase {

        @Override
        protected List<ASCachedRecipe> buildAll() {
            List<ASCachedRecipe> out = new ArrayList<>();
            List<WellLiquefaction.LiquefactionEntry> all = new ArrayList<>(WellLiquefaction.getRegisteredLiquefactions());
            all.addAll(WellLiquefaction.mtLiquefactions.values());
            for (WellLiquefaction.LiquefactionEntry e : all) {
                ItemStack bucket = FluidContainerRegistry.fillFluidContainer(
                    new FluidStack(e.producing, FluidContainerRegistry.BUCKET_VOLUME),
                    FluidContainerRegistry.EMPTY_BUCKET);
                if (bucket == null || e.catalyst == null) continue;
                out.add(new ASCachedRecipe(bucket, OUT_X, Y).addInput(Collections.singletonList(e.catalyst.copy()), IN_X, Y));
            }
            return out;
        }

        @Override
        protected List<int[]> slotPositions() {
            return twoSlots();
        }

        @Override
        protected String titleKey() {
            return "astralsorcery.nei.well";
        }

        @Override
        public String getOverlayIdentifier() {
            return "astralsorcery.well";
        }
    }

    public static class Transmutation extends ASRecipeHandlerBase {

        @Override
        protected List<ASCachedRecipe> buildAll() {
            List<ASCachedRecipe> out = new ArrayList<>();
            List<LightOreTransmutations.Transmutation> all = new ArrayList<>(LightOreTransmutations.getRegisteredTransmutations());
            all.addAll(LightOreTransmutations.mtTransmutations);
            for (LightOreTransmutations.Transmutation t : all) {
                ItemStack in = t.getInputDisplayStack();
                ItemStack res = t.getOutputDisplayStack();
                if (in == null || res == null) continue;
                out.add(new ASCachedRecipe(res, OUT_X, Y).addInput(Collections.singletonList(in), IN_X, Y));
            }
            return out;
        }

        @Override
        protected List<int[]> slotPositions() {
            return twoSlots();
        }

        @Override
        protected String titleKey() {
            return "astralsorcery.nei.transmutation";
        }

        @Override
        public String getOverlayIdentifier() {
            return "astralsorcery.transmutation";
        }
    }
}
