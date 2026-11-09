/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.nei;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLInterModComms;
import hellfirepvp.astralsorcery.AstralSorcery;

/**
 * GTNH NEI metadata for our handlers, sent by IMC (no NEI classes needed, so this is safe without NEI):
 * tab icon + "Astral Sorcery" as mod name (registerHandlerInfo) and the blocks the recipes are made in as
 * catalysts (registerCatalystInfo). Handler ids are the handler class names.
 */
public class NEIHandlerInfo {

    private static final String NEI = "NotEnoughItems";
    private static final String PKG = "hellfirepvp.astralsorcery.common.integrations.nei.";

    private static final String ALTAR = AstralSorcery.MODID + ":BlockAltar:";
    private static final String INFUSER = AstralSorcery.MODID + ":BlockStarlightInfuser";
    private static final String WELL = AstralSorcery.MODID + ":BlockWell";
    private static final String GRINDSTONE = AstralSorcery.MODID + ":BlockMachine:1";
    private static final String LENS = AstralSorcery.MODID + ":BlockLens";
    private static final String COLLECTOR = AstralSorcery.MODID + ":BlockCollectorCrystal";

    public static void sendIMC() {
        String[] altarHandlers = { "AltarRecipeHandler$Discovery", "AltarRecipeHandler$Attunement",
            "AltarRecipeHandler$Constellation", "AltarRecipeHandler$Trait" };
        for (int tier = 0; tier < altarHandlers.length; tier++) {
            String handler = PKG + altarHandlers[tier];
            handlerInfo(handler, ALTAR + tier, tier == 3 ? 122 : 96);
            // higher tier altars also craft every lower tier recipe
            for (int altar = tier; altar < altarHandlers.length; altar++) {
                catalyst(handler, ALTAR + altar, 10 - (altar - tier)); // own tier first
            }
        }
        simple("SimpleRecipeHandlers$Infuser", INFUSER);
        simple("SimpleRecipeHandlers$Well", WELL);
        simple("SimpleRecipeHandlers$Grindstone", GRINDSTONE);
        simple("SimpleRecipeHandlers$Transmutation", LENS);
        catalyst(PKG + "SimpleRecipeHandlers$Transmutation", COLLECTOR, 5);
    }

    private static void simple(String handlerClass, String medium) {
        handlerInfo(PKG + handlerClass, medium, 44);
        catalyst(PKG + handlerClass, medium, 10);
    }

    private static void handlerInfo(String handler, String iconItem, int height) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("handler", handler);
        nbt.setString("modName", AstralSorcery.NAME);
        nbt.setString("modId", AstralSorcery.MODID);
        nbt.setBoolean("modRequired", true);
        nbt.setString("itemName", iconItem);
        nbt.setInteger("handlerHeight", height);
        nbt.setInteger("handlerWidth", 166);
        nbt.setInteger("maxRecipesPerPage", 1);
        FMLInterModComms.sendMessage(NEI, "registerHandlerInfo", nbt);
    }

    private static void catalyst(String handler, String item, int priority) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("handlerID", handler);
        nbt.setString("itemName", item);
        nbt.setInteger("priority", priority);
        FMLInterModComms.sendMessage(NEI, "registerCatalystInfo", nbt);
    }
}
