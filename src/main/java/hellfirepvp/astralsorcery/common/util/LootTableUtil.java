/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.util.ResourceLocation;

import hellfirepvp.astralsorcery.AstralSorcery;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LootTableUtil
 * Created by HellFirePvP
 * Date: 01.08.2016 / 19:29
 */
public class LootTableUtil {

    public static final ResourceLocation LOOT_TABLE_SHRINE = new ResourceLocation(
        AstralSorcery.MODID.toLowerCase(),
        "chest_shrine");

    /** 1.7 has no loot tables: the shrine chest uses a ChestGenHooks category mirroring loot_tables/chest_shrine.json. */
    public static final String CHEST_SHRINE = AstralSorcery.MODID + ":chest_shrine";

    public static void initLootTable() {
        net.minecraftforge.common.ChestGenHooks shrine = net.minecraftforge.common.ChestGenHooks.getInfo(CHEST_SHRINE);
        shrine.setMin(5);
        shrine.setMax(8); // 4-6 rolls + 1-2 bonus rolls in 1.10
        shrine.addItem(content(new net.minecraft.item.ItemStack(ItemsAS.constellationPaper, 1, 0), 1, 1, 18));
        shrine.addItem(content(new net.minecraft.item.ItemStack(ItemsAS.craftingComponent, 1, 0), 1, 2, 12));
        shrine.addItem(content(new net.minecraft.item.ItemStack(net.minecraft.init.Items.bone), 1, 3, 10));
        shrine.addItem(content(new net.minecraft.item.ItemStack(net.minecraft.init.Items.gold_ingot), 1, 2, 7));
        shrine.addItem(content(new net.minecraft.item.ItemStack(net.minecraft.init.Items.iron_ingot), 1, 4, 15));
        shrine.addItem(content(new net.minecraft.item.ItemStack(net.minecraft.init.Items.diamond), 1, 2, 3));
        shrine.addItem(content(new net.minecraft.item.ItemStack(net.minecraft.init.Items.glowstone_dust), 1, 3, 7));
        shrine.addItem(content(new net.minecraft.item.ItemStack(net.minecraft.init.Items.emerald), 1, 1, 1));
        shrine.addItem(content(new net.minecraft.item.ItemStack(net.minecraft.init.Items.ender_pearl), 1, 1, 3));

        // 1.10 added constellation papers to these vanilla loot tables
        String[] paperChests = { net.minecraftforge.common.ChestGenHooks.STRONGHOLD_LIBRARY,
            net.minecraftforge.common.ChestGenHooks.MINESHAFT_CORRIDOR,
            net.minecraftforge.common.ChestGenHooks.PYRAMID_JUNGLE_CHEST,
            net.minecraftforge.common.ChestGenHooks.PYRAMID_DESERT_CHEST };
        for (String chest : paperChests) {
            net.minecraftforge.common.ChestGenHooks.addItem(
                chest,
                content(new net.minecraft.item.ItemStack(ItemsAS.constellationPaper, 1, 0), 1, 1, Config.constellationPaperRarity));
        }
    }

    private static net.minecraft.util.WeightedRandomChestContent content(net.minecraft.item.ItemStack stack, int min,
        int max, int weight) {
        return new net.minecraft.util.WeightedRandomChestContent(stack, min, max, weight);
    }

    public static void fillShrineChest(net.minecraft.inventory.IInventory chest, java.util.Random rand) {
        net.minecraftforge.common.ChestGenHooks info = net.minecraftforge.common.ChestGenHooks.getInfo(CHEST_SHRINE);
        net.minecraft.util.WeightedRandomChestContent
            .generateChestContents(rand, info.getItems(rand), chest, info.getCount(rand));
    }

}
