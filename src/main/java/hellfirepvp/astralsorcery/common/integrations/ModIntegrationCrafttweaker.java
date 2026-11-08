/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations;

import hellfirepvp.astralsorcery.AstralSorcery;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.SerializeableRecipe;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks.AltarRecipe;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks.InfusionRecipe;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks.LightTransmutations;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks.RitualMineralis;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks.WellRecipe;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncMinetweakerChanges;
import minetweaker.MineTweakerAPI;
import minetweaker.MineTweakerImplementationAPI;
import minetweaker.util.IEventHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationCrafttweaker
 * Created by HellFirePvP
 * Date: 27.02.2017 / 00:45
 */
public class ModIntegrationCrafttweaker {

    public static ModIntegrationCrafttweaker instance = new ModIntegrationCrafttweaker();
    public static List<SerializeableRecipe> recipeModifications = new LinkedList<>();

    private ModIntegrationCrafttweaker() {}

    public void load() {
        MineTweakerAPI.registerClass(InfusionRecipe.class);
        MineTweakerAPI.registerClass(RitualMineralis.class);
        MineTweakerAPI.registerClass(LightTransmutations.class);
        MineTweakerAPI.registerClass(AltarRecipe.class);
        MineTweakerAPI.registerClass(WellRecipe.class);

        MineTweakerImplementationAPI.onReloadEvent(AstralRecipeReloadHandlerPre.instance);
        // MineTweaker 3 (1.7) has no post-reload hook; the pre-reload handler schedules the apply step instead.
    }

    /**
     * MineTweaker 3 does not fire a ReloadEvent for the very first script load (server start), so the collected
     * modifications are applied once the server has started.
     */
    public static void applyInitialModifications() {
        AstralRecipeReloadHandlerPost.instance.handle(null);
    }

    private static class AstralRecipeReloadHandlerPre
        implements IEventHandler<MineTweakerImplementationAPI.ReloadEvent> {

        static AstralRecipeReloadHandlerPre instance = new AstralRecipeReloadHandlerPre();

        private AstralRecipeReloadHandlerPre() {}

        @Override
        public void handle(MineTweakerImplementationAPI.ReloadEvent reloadEvent) {
            recipeModifications.clear();
            // Scripts run synchronously right after this event; apply the collected changes on the next tick.
            AstralSorcery.proxy.scheduleDelayed(() -> AstralRecipeReloadHandlerPost.instance.handle(reloadEvent), 1);
        }

    }

    private static class AstralRecipeReloadHandlerPost
        implements IEventHandler<MineTweakerImplementationAPI.ReloadEvent> {

        static AstralRecipeReloadHandlerPost instance = new AstralRecipeReloadHandlerPost();

        private AstralRecipeReloadHandlerPost() {}

        @Override
        public void handle(MineTweakerImplementationAPI.ReloadEvent reloadEvent) {
            CraftingAccessManager.clearModifications();

            for (SerializeableRecipe modification : recipeModifications) {
                modification.applyServer();
            }

            CraftingAccessManager.compile();
            AstralSorcery.log.info("[AstralSorcery] Applied " + recipeModifications.size() + " MineTweaker modification(s)");

            if (FMLCommonHandler.instance()
                .getEffectiveSide() == Side.SERVER && Loader.instance()
                    .hasReachedState(LoaderState.SERVER_ABOUT_TO_START)) {
                PacketChannel.CHANNEL.sendToAll(compileRecipeChangePacket());
            }
        }
    }

    public static IMessage compileRecipeChangePacket() {
        PktSyncMinetweakerChanges.Compound compound = new PktSyncMinetweakerChanges.Compound();
        for (SerializeableRecipe modification : recipeModifications) {
            PktSyncMinetweakerChanges change = new PktSyncMinetweakerChanges(modification);
            compound.parts.add(change);
        }
        return compound;
    }

}
