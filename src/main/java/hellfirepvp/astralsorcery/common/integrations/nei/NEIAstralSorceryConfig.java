/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.TemplateRecipeHandler;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.tile.TileAltar;

/** Discovered by NEI by class name (NEI*Config). Registers Astral Sorcery recipe handlers. */
public class NEIAstralSorceryConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        register(new AltarRecipeHandler(TileAltar.AltarLevel.DISCOVERY));
        register(new AltarRecipeHandler(TileAltar.AltarLevel.ATTUNEMENT));
        register(new AltarRecipeHandler(TileAltar.AltarLevel.CONSTELLATION_CRAFT));
        register(new SimpleRecipeHandlers.Infuser());
        register(new SimpleRecipeHandlers.Well());
        register(new SimpleRecipeHandlers.Transmutation());
    }

    private static void register(TemplateRecipeHandler handler) {
        API.registerRecipeHandler(handler);
        API.registerUsageHandler(handler);
    }

    @Override
    public String getName() {
        return AstralSorcery.NAME;
    }

    @Override
    public String getVersion() {
        return hellfirepvp.astralsorcery.Tags.VERSION;
    }
}
