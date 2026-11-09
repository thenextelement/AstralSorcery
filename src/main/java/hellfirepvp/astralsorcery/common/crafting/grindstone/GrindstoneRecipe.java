/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.grindstone;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.util.ItemUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GrindstoneRecipe
 * Created by HellFirePvP
 * Date: 19.11.2017 / 10:22
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
public class GrindstoneRecipe {

    protected static final Random rand = new Random();

    protected final ItemHandle input;
    @Nullable
    protected final ItemStack output;
    protected final int chance;

    public GrindstoneRecipe(@Nullable ItemStack input, @Nullable ItemStack output, int chance) {
        this.input = input == null ? null : new ItemHandle(input);
        this.output = output;
        this.chance = Math.max(1, chance);
    }

    public GrindstoneRecipe(ItemHandle input, @Nullable ItemStack output, int chance) {
        this.input = input;
        this.output = output;
        this.chance = Math.max(1, chance);
    }

    public boolean matches(@Nullable ItemStack stackIn) {
        return stackIn != null && this.input != null && this.input.matchCrafting(stackIn);
    }

    public boolean isValid() {
        return this.input != null && this.input.getApplicableItems()
            .size() > 0 && this.output != null && this.output.getItem() != null;
    }

    @Nonnull
    public GrindResult grind(ItemStack stackIn) {
        if (rand.nextInt(chance) == 0) {
            return GrindResult.itemChange(ItemUtils.copyStackWithSize(this.output, this.output.stackSize));
        }
        return GrindResult.failNoOp();
    }

    @Nullable
    public ItemStack getOutputForMatching() {
        return this.output;
    }

    @Nonnull
    public ItemHandle getOutputForRender() {
        return new ItemHandle(this.output);
    }

    @Nullable
    public ItemHandle getInputForRender() {
        return this.input;
    }

    public static class GrindResult {

        private final ResultType type;
        private final ItemStack stack;

        private GrindResult(ResultType type, @Nullable ItemStack stack) {
            this.type = type;
            this.stack = stack;
        }

        public ResultType getType() {
            return type;
        }

        @Nullable
        public ItemStack getStack() {
            return stack;
        }

        public static GrindResult success() {
            return new GrindResult(ResultType.SUCCESS, null);
        }

        public static GrindResult itemChange(@Nullable ItemStack newStack) {
            return new GrindResult(ResultType.ITEMCHANGE, newStack);
        }

        public static GrindResult failNoOp() {
            return new GrindResult(ResultType.FAIL_SILENT, null);
        }

        public static GrindResult failBreakItem() {
            return new GrindResult(ResultType.FAIL_BREAK_ITEM, null);
        }
    }

    public static enum ResultType {

        SUCCESS, // Successfully grinded something
        ITEMCHANGE, // Successfully grinded something, other item now on the grindstone
        FAIL_SILENT, // Did nothing, but nothing went wrong.
        FAIL_BREAK_ITEM // The item broke while grinding.
    }
}
