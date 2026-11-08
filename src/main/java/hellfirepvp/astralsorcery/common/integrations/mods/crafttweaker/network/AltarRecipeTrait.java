/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;

/**
 * Iridescent altar recipe added through MineTweaker: 25 altar slots, any further inputs are relay items, optional
 * required focus constellation. (backported from 1.12.2-1.8.5 CraftTweaker integration)
 */
public class AltarRecipeTrait extends BaseAltarRecipe {

    @Nullable
    private IConstellation focusRequiredConstellation;

    AltarRecipeTrait() {
        super(null, null, 0, 0);
        this.focusRequiredConstellation = null;
    }

    public AltarRecipeTrait(ItemHandle[] inputs, ItemStack output, int starlightRequired, int craftingTickTime,
        @Nullable IConstellation focus) {
        super(inputs, output, starlightRequired, craftingTickTime);
        this.focusRequiredConstellation = focus;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.ALTAR_T4_ADD;
    }

    @Override
    public void read(ByteBuf buf) {
        super.read(buf);
        if (buf.readBoolean()) {
            this.focusRequiredConstellation = ConstellationRegistry
                .getConstellationByName(ByteBufUtils.readString(buf));
        } else {
            this.focusRequiredConstellation = null;
        }
    }

    @Override
    public void write(ByteBuf buf) {
        super.write(buf);
        buf.writeBoolean(this.focusRequiredConstellation != null);
        if (this.focusRequiredConstellation != null) {
            ByteBufUtils.writeString(buf, this.focusRequiredConstellation.getUnlocalizedName());
        }
    }

    private void apply() {
        AbstractAltarRecipe aar = buildRecipeUnsafe(
            TileAltar.AltarLevel.TRAIT_CRAFT,
            this.starlightRequired,
            this.craftingTickTime,
            this.output,
            this.inputs);
        if (aar instanceof TraitRecipe && focusRequiredConstellation != null) {
            ((TraitRecipe) aar).setRequiredConstellation(focusRequiredConstellation);
        }
        CraftingAccessManager.registerMTAltarRecipe(aar);
    }

    @Override
    public void applyServer() {
        apply();
    }

    @Override
    public void applyClient() {
        apply();
    }
}
