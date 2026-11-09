/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network;

import net.minecraft.item.ItemStack;

import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;

/** (backported from 1.12.2-1.8.5 CraftTweaker integration to MineTweaker 3) */
public class GrindstoneRecipeRemove implements SerializeableRecipe {

    private ItemStack out;

    GrindstoneRecipeRemove() {}

    public GrindstoneRecipeRemove(ItemStack out) {
        this.out = out;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.GRINDSTONE_REMOVE;
    }

    @Override
    public void read(ByteBuf buf) {
        this.out = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.out);
    }

    @Override
    public void applyServer() {
        CraftingAccessManager.removeGrindstoneRecipe(this.out);
    }

    @Override
    public void applyClient() {
        applyServer();
    }
}
