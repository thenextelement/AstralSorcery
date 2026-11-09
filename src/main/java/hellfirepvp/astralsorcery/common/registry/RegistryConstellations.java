/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import static hellfirepvp.astralsorcery.common.constellation.starmap.ConstellationMapEffectRegistry.registerMapEffect;
import static hellfirepvp.astralsorcery.common.lib.Constellations.*;

import java.awt.Color;
import java.util.Arrays;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import hellfirepvp.astralsorcery.common.constellation.ConstellationBase;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.constellation.starmap.ConstellationMapEffectRegistry;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.event.APIRegistryEvent;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.EnchantmentsAS;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryConstellations
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:40
 */
public class RegistryConstellations {

    public static void init() {
        buildConstellations();

        registerConstellations();

        registerEffects();

        MinecraftForge.EVENT_BUS.post(new APIRegistryEvent.ConstellationRegister());
    }

    /** Star map effects, values from 1.12.2-1.8.5 (enchantments missing in 1.7 are left out). */
    private static void registerEffects() {
        registerMapEffect(
            discidia,
            Arrays.asList(
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.sharpness, 3, 7),
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.power, 3, 7)),
            Arrays.asList(new ConstellationMapEffectRegistry.PotionMapEffect(Potion.damageBoost, 0, 3)));
        registerMapEffect(
            armara,
            Arrays.asList(new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.protection, 3, 5)),
            Arrays.asList(new ConstellationMapEffectRegistry.PotionMapEffect(Potion.resistance)));
        registerMapEffect(
            vicio,
            Arrays.asList(new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.featherFalling, 3, 5)),
            Arrays.asList(new ConstellationMapEffectRegistry.PotionMapEffect(Potion.moveSpeed, 1, 3)));
        registerMapEffect(
            aevitas,
            Arrays.asList(new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.unbreaking, 1, 3)),
            Arrays.asList(new ConstellationMapEffectRegistry.PotionMapEffect(Potion.regeneration, 0, 3)));
        registerMapEffect(
            evorsio,
            Arrays.asList(new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.efficiency, 3, 5)),
            Arrays.asList(new ConstellationMapEffectRegistry.PotionMapEffect(Potion.digSpeed, 1, 3)));

        registerMapEffect(
            lucerna,
            Arrays.asList(
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(EnchantmentsAS.enchantmentNightVision, 1, 1)),
            Arrays.asList(new ConstellationMapEffectRegistry.PotionMapEffect(Potion.nightVision)));
        registerMapEffect(
            mineralis,
            Arrays.asList(new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.fortune, 1, 3)),
            Arrays.asList(new ConstellationMapEffectRegistry.PotionMapEffect(Potion.digSpeed, 0, 3)));
        registerMapEffect(
            horologium,
            Arrays.asList(
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.fortune, 4, 6),
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.looting, 3, 5)),
            Arrays.asList(
                new ConstellationMapEffectRegistry.PotionMapEffect(Potion.digSpeed, 5, 8),
                new ConstellationMapEffectRegistry.PotionMapEffect(Potion.moveSpeed, 1, 4)));
        registerMapEffect(
            octans,
            Arrays.asList(new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.respiration, 2, 4)),
            Arrays.asList(new ConstellationMapEffectRegistry.PotionMapEffect(Potion.waterBreathing, 2, 4)));
        registerMapEffect(
            bootes,
            Arrays.asList(new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.silkTouch, 1, 1)),
            Arrays.asList(new ConstellationMapEffectRegistry.PotionMapEffect(Potion.field_76443_y, 2, 5)));
        registerMapEffect(
            fornax,
            Arrays.asList(
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.fireAspect, 1, 3),
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.flame, 1, 2),
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(EnchantmentsAS.enchantmentScorchingHeat, 1, 1)),
            Arrays.asList(new ConstellationMapEffectRegistry.PotionMapEffect(Potion.fireResistance, 0, 0)));
        registerMapEffect(
            pelotrio,
            Arrays.asList(
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.infinity, 1, 1),
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.field_151369_A, 4, 6)),
            Arrays.asList(
                new ConstellationMapEffectRegistry.PotionMapEffect(Potion.regeneration, 2, 4),
                new ConstellationMapEffectRegistry.PotionMapEffect(Potion.field_76444_x, 1, 4)));

        registerMapEffect(
            gelu,
            Arrays.asList(
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.featherFalling),
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.unbreaking, 1, 4)),
            Arrays.asList(
                new ConstellationMapEffectRegistry.PotionMapEffect(Potion.resistance, 1, 2),
                new ConstellationMapEffectRegistry.PotionMapEffect(Potion.fireResistance, 0, 0),
                new ConstellationMapEffectRegistry.PotionMapEffect(Potion.moveSlowdown, 0, 0)));
        registerMapEffect(
            ulteria,
            Arrays.asList(
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.unbreaking, 2, 4)
                    .setIgnoreCompatibility(),
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.fireProtection, 4, 6)
                    .setIgnoreCompatibility(),
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.blastProtection, 4, 6)
                    .setIgnoreCompatibility(),
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.projectileProtection, 4, 6)
                    .setIgnoreCompatibility()),
            Arrays.asList(
                new ConstellationMapEffectRegistry.PotionMapEffect(Potion.field_76444_x, 0, 3),
                new ConstellationMapEffectRegistry.PotionMapEffect(Potion.regeneration, 1, 2),
                new ConstellationMapEffectRegistry.PotionMapEffect(Potion.weakness, 1, 2)));
        registerMapEffect(
            alcara,
            Arrays.asList(
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.field_151369_A, 2, 5)
                    .setIgnoreCompatibility(),
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.field_151370_z, 3, 6)
                    .setIgnoreCompatibility(),
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.silkTouch, 1, 1)),
            Arrays.asList(
                new ConstellationMapEffectRegistry.PotionMapEffect(Potion.invisibility, 0, 1),
                new ConstellationMapEffectRegistry.PotionMapEffect(Potion.hunger, 0, 1)));
        registerMapEffect(
            vorux,
            Arrays.asList(
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.smite, 4, 6)
                    .setIgnoreCompatibility(),
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.baneOfArthropods, 4, 6)
                    .setIgnoreCompatibility(),
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.sharpness, 3, 5)
                    .setIgnoreCompatibility(),
                new ConstellationMapEffectRegistry.EnchantmentMapEffect(Enchantment.power, 3, 5)
                    .setIgnoreCompatibility()),
            Arrays.asList(
                new ConstellationMapEffectRegistry.PotionMapEffect(Potion.damageBoost, 2, 3),
                new ConstellationMapEffectRegistry.PotionMapEffect(Potion.resistance, 0, 1),
                new ConstellationMapEffectRegistry.PotionMapEffect(Potion.digSlowdown, 0, 1)));
    }

    /** 1.12.2-1.8.5 signature items (mantle attunement, wand augments); needs items and blocks registered. */
    public static void initConstellationSignatures() {
        ItemHandle starDust = new ItemHandle(ItemCraftingComponent.MetaType.STARDUST.asStack());

        discidia.addSignatureItem(new ItemHandle(new ItemStack(Items.flint)));
        discidia.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_IRON_INGOT));
        discidia.addSignatureItem(new ItemHandle(new ItemStack(Items.arrow)));
        discidia.addSignatureItem(new ItemHandle("dustRedstone"));

        armara.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_IRON_INGOT));
        armara.addSignatureItem(new ItemHandle(new ItemStack(Items.leather)));
        armara.addSignatureItem(new ItemHandle(new ItemStack(Items.clay_ball)));
        armara.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_GLOWSTONE_DUST));

        vicio.addSignatureItem(new ItemHandle(new ItemStack(Items.feather)));
        vicio.addSignatureItem(new ItemHandle(new ItemStack(Items.sugar)));
        vicio.addSignatureItem(new ItemHandle(new ItemStack(Items.string)));
        vicio.addSignatureItem(new ItemHandle(new ItemStack(Items.fish)));

        aevitas.addSignatureItem(new ItemHandle(OreDictAlias.BLOCK_SAPLING));
        aevitas.addSignatureItem(starDust);
        aevitas.addSignatureItem(new ItemHandle(new ItemStack(Items.wheat_seeds)));
        aevitas.addSignatureItem(new ItemHandle(new ItemStack(Items.reeds)));

        evorsio.addSignatureItem(new ItemHandle("cobblestone"));
        evorsio.addSignatureItem(new ItemHandle(new ItemStack(Items.flint)));
        evorsio.addSignatureItem(new ItemHandle(new ItemStack(Items.gunpowder)));
        evorsio.addSignatureItem(new ItemHandle(new ItemStack(Blocks.tnt)));

        lucerna.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_GLOWSTONE_DUST));
        lucerna.addSignatureItem(new ItemHandle(new ItemStack(Blocks.torch)));
        lucerna.addSignatureItem(new ItemHandle("dustRedstone"));
        lucerna.addSignatureItem(new ItemHandle(new ItemStack(Items.coal)));

        mineralis.addSignatureItem(new ItemHandle("oreIron"));
        mineralis.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_GOLD_INGOT));
        mineralis.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_IRON_INGOT));
        mineralis.addSignatureItem(new ItemHandle("dustRedstone"));

        horologium.addSignatureItem(starDust);
        horologium.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_DIAMOND));
        horologium.addSignatureItem(new ItemHandle(new ItemStack(Items.ender_pearl)));
        horologium.addSignatureItem(new ItemHandle(new ItemStack(Items.clock)));

        octans.addSignatureItem(new ItemHandle(new ItemStack(Items.fish)));
        octans.addSignatureItem(new ItemHandle(new ItemStack(Items.fishing_rod)));
        octans.addSignatureItem(new ItemHandle("dyeBlue"));
        octans.addSignatureItem(new ItemHandle(new ItemStack(Items.clay_ball)));

        bootes.addSignatureItem(new ItemHandle(new ItemStack(Items.wheat)));
        bootes.addSignatureItem(new ItemHandle(new ItemStack(Items.bone)));
        bootes.addSignatureItem(new ItemHandle(new ItemStack(Items.apple)));
        bootes.addSignatureItem(new ItemHandle(new ItemStack(Items.leather)));

        fornax.addSignatureItem(new ItemHandle(new ItemStack(Items.coal)));
        fornax.addSignatureItem(new ItemHandle("dustRedstone"));
        fornax.addSignatureItem(new ItemHandle(OreDictAlias.ITEM_IRON_INGOT));
        fornax.addSignatureItem(new ItemHandle(new ItemStack(Items.gunpowder)));

        pelotrio.addSignatureItem(new ItemHandle(new ItemStack(Items.blaze_powder)));
        pelotrio.addSignatureItem(new ItemHandle(new ItemStack(Items.apple)));
        pelotrio.addSignatureItem(new ItemHandle(new ItemStack(Items.egg)));
        pelotrio.addSignatureItem(new ItemHandle(new ItemStack(Items.rotten_flesh)));
    }

    private static void registerConstellations() {
        // Major
        ConstellationRegistry.registerConstellation(discidia);
        ConstellationRegistry.registerConstellation(armara);
        ConstellationRegistry.registerConstellation(vicio);
        ConstellationRegistry.registerConstellation(aevitas);
        ConstellationRegistry.registerConstellation(evorsio);

        // Weak
        ConstellationRegistry.registerConstellation(lucerna);
        ConstellationRegistry.registerConstellation(mineralis);
        ConstellationRegistry.registerConstellation(horologium);
        ConstellationRegistry.registerConstellation(octans);
        ConstellationRegistry.registerConstellation(bootes);
        ConstellationRegistry.registerConstellation(fornax);
        ConstellationRegistry.registerConstellation(pelotrio);

        // Minor
        ConstellationRegistry.registerConstellation(gelu);
        ConstellationRegistry.registerConstellation(ulteria);
        ConstellationRegistry.registerConstellation(alcara);
        ConstellationRegistry.registerConstellation(vorux);
    }

    private static void buildConstellations() {
        StarLocation sl1, sl2, sl3, sl4, sl5, sl6, sl7, sl8, sl9;

        discidia = new ConstellationBase.Major("discidia", new Color(0xE01903));
        sl1 = discidia.addStar(7, 2);
        sl2 = discidia.addStar(3, 6);
        sl3 = discidia.addStar(5, 12);
        sl4 = discidia.addStar(20, 11);
        sl5 = discidia.addStar(15, 17);
        sl6 = discidia.addStar(26, 21);
        sl7 = discidia.addStar(23, 27);
        sl8 = discidia.addStar(15, 25);

        discidia.addConnection(sl1, sl2);
        discidia.addConnection(sl2, sl3);
        discidia.addConnection(sl2, sl4);
        discidia.addConnection(sl4, sl5);
        discidia.addConnection(sl5, sl7);
        discidia.addConnection(sl6, sl7);
        discidia.addConnection(sl7, sl8);

        armara = new ConstellationBase.Major("armara", new Color(0xB7BBB8));
        sl1 = armara.addStar(8, 4);
        sl2 = armara.addStar(9, 15);
        sl3 = armara.addStar(11, 26);
        sl4 = armara.addStar(19, 25);
        sl5 = armara.addStar(23, 14);
        sl6 = armara.addStar(23, 4);
        sl7 = armara.addStar(15, 7);

        armara.addConnection(sl1, sl2);
        armara.addConnection(sl2, sl3);
        armara.addConnection(sl3, sl4);
        armara.addConnection(sl4, sl5);
        armara.addConnection(sl5, sl6);
        armara.addConnection(sl6, sl7);
        armara.addConnection(sl7, sl1);
        armara.addConnection(sl2, sl5);
        armara.addConnection(sl2, sl7);
        armara.addConnection(sl5, sl7);

        vicio = new ConstellationBase.Major("vicio", new Color(0x00BDAD));
        sl1 = vicio.addStar(3, 8);
        sl2 = vicio.addStar(13, 9);
        sl3 = vicio.addStar(6, 23);
        sl4 = vicio.addStar(14, 16);
        sl5 = vicio.addStar(23, 24);
        sl6 = vicio.addStar(22, 16);
        sl7 = vicio.addStar(24, 4);

        vicio.addConnection(sl1, sl2);
        vicio.addConnection(sl2, sl7);
        vicio.addConnection(sl3, sl4);
        vicio.addConnection(sl4, sl7);
        vicio.addConnection(sl5, sl6);
        vicio.addConnection(sl6, sl7);

        aevitas = new ConstellationBase.Major("aevitas", new Color(0x29F615));
        sl1 = aevitas.addStar(15, 14);
        sl2 = aevitas.addStar(7, 12);
        sl3 = aevitas.addStar(3, 6);
        sl4 = aevitas.addStar(21, 8);
        sl5 = aevitas.addStar(25, 2);
        sl6 = aevitas.addStar(13, 21);
        sl7 = aevitas.addStar(9, 26);
        sl8 = aevitas.addStar(17, 28);
        sl9 = aevitas.addStar(27, 17);

        aevitas.addConnection(sl1, sl2);
        aevitas.addConnection(sl2, sl3);
        aevitas.addConnection(sl1, sl4);
        aevitas.addConnection(sl4, sl5);
        aevitas.addConnection(sl1, sl6);
        aevitas.addConnection(sl6, sl7);
        aevitas.addConnection(sl6, sl8);
        aevitas.addConnection(sl4, sl9);

        // 1.12.2-1.8.5
        evorsio = new ConstellationBase.Major("evorsio", new Color(0xA00100));
        sl1 = evorsio.addStar(13, 16);
        sl2 = evorsio.addStar(18, 6);
        sl3 = evorsio.addStar(26, 4);
        sl4 = evorsio.addStar(24, 13);
        sl5 = evorsio.addStar(2, 18);
        sl6 = evorsio.addStar(4, 27);
        sl7 = evorsio.addStar(11, 24);

        evorsio.addConnection(sl1, sl2);
        evorsio.addConnection(sl1, sl3);
        evorsio.addConnection(sl1, sl4);
        evorsio.addConnection(sl1, sl5);
        evorsio.addConnection(sl1, sl6);
        evorsio.addConnection(sl1, sl7);

        lucerna = new ConstellationBase.Weak("lucerna", new Color(0xFFE709));
        sl1 = lucerna.addStar(15, 13);
        sl2 = lucerna.addStar(3, 5);
        sl3 = lucerna.addStar(25, 3);
        sl4 = lucerna.addStar(28, 16);
        sl5 = lucerna.addStar(22, 27);
        sl6 = lucerna.addStar(6, 26);

        lucerna.addConnection(sl1, sl2);
        lucerna.addConnection(sl1, sl3);
        lucerna.addConnection(sl1, sl4);
        lucerna.addConnection(sl1, sl5);
        lucerna.addConnection(sl1, sl6);

        mineralis = new ConstellationBase.Weak("mineralis", new Color(0xCB7D0A));
        sl1 = mineralis.addStar(16, 2);
        sl2 = mineralis.addStar(8, 8);
        sl3 = mineralis.addStar(9, 22);
        sl4 = mineralis.addStar(15, 29);
        sl5 = mineralis.addStar(23, 21);
        sl6 = mineralis.addStar(24, 9);

        mineralis.addConnection(sl1, sl2);
        mineralis.addConnection(sl2, sl3);
        mineralis.addConnection(sl3, sl4);
        mineralis.addConnection(sl4, sl5);
        mineralis.addConnection(sl5, sl6);
        mineralis.addConnection(sl6, sl1);
        mineralis.addConnection(sl1, sl4);

        horologium = new ConstellationBase.WeakSpecial("horologium", new Color(0x7D16B4)) {

            @Override
            public boolean doesShowUp(WorldSkyHandler handle, World world, long day) {
                return isDayOfSolarEclipse(world.getSeed(), day);
            }

            @Override
            public float getDistribution(WorldSkyHandler handle, World world, long day, boolean showsUp) {
                return showsUp ? 1F : 0.6F;
            }
        };
        sl1 = horologium.addStar(7, 6);
        sl2 = horologium.addStar(22, 5);
        sl3 = horologium.addStar(5, 27);
        sl4 = horologium.addStar(23, 25);

        horologium.addConnection(sl1, sl2);
        horologium.addConnection(sl2, sl3);
        horologium.addConnection(sl3, sl4);
        horologium.addConnection(sl4, sl1);

        octans = new ConstellationBase.Weak("octans", new Color(0x706EFF));
        sl1 = octans.addStar(3, 6);
        sl2 = octans.addStar(11, 11);
        sl3 = octans.addStar(18, 4);
        sl4 = octans.addStar(18, 29);

        octans.addConnection(sl1, sl2);
        octans.addConnection(sl2, sl3);
        octans.addConnection(sl3, sl4);
        octans.addConnection(sl2, sl4);

        bootes = new ConstellationBase.Weak("bootes", new Color(0xD41CD6));
        sl1 = bootes.addStar(9, 22);
        sl2 = bootes.addStar(3, 14);
        sl3 = bootes.addStar(22, 27);
        sl4 = bootes.addStar(16, 5);
        sl5 = bootes.addStar(26, 3);
        sl6 = bootes.addStar(24, 11);

        bootes.addConnection(sl1, sl2);
        bootes.addConnection(sl1, sl3);
        bootes.addConnection(sl1, sl4);
        bootes.addConnection(sl1, sl6);
        bootes.addConnection(sl4, sl5);
        bootes.addConnection(sl5, sl6);

        fornax = new ConstellationBase.Weak("fornax", new Color(0xFF4E1B));
        sl1 = fornax.addStar(4, 20);
        sl2 = fornax.addStar(14, 23);
        sl3 = fornax.addStar(28, 16);
        sl4 = fornax.addStar(12, 13);
        sl5 = fornax.addStar(16, 11);

        fornax.addConnection(sl1, sl2);
        fornax.addConnection(sl2, sl3);
        fornax.addConnection(sl2, sl4);
        fornax.addConnection(sl2, sl5);

        // 1.12.2-1.8.5
        pelotrio = new ConstellationBase.WeakSpecial("pelotrio", new Color(0xEC006B)) {

            @Override
            public boolean doesShowUp(WorldSkyHandler handle, World world, long day) {
                return handle.getCurrentMoonPhase() == MoonPhase.NEW || handle.getCurrentMoonPhase() == MoonPhase.FULL;
            }

            @Override
            public float getDistribution(WorldSkyHandler handle, World world, long day, boolean showingUp) {
                if (showingUp) return 1F;
                MoonPhase current = handle.getCurrentMoonPhase();
                if (current == MoonPhase.WANING1_2 || current == MoonPhase.WAXING1_2) {
                    return 0.5F;
                }
                return 0.75F;
            }
        };
        sl1 = pelotrio.addStar(4, 7);
        sl2 = pelotrio.addStar(12, 2);
        sl3 = pelotrio.addStar(20, 3);
        sl4 = pelotrio.addStar(15, 13);
        sl5 = pelotrio.addStar(10, 23);
        sl6 = pelotrio.addStar(26, 11);

        pelotrio.addConnection(sl1, sl2);
        pelotrio.addConnection(sl2, sl4);
        pelotrio.addConnection(sl3, sl4);
        pelotrio.addConnection(sl4, sl5);
        pelotrio.addConnection(sl4, sl6);
        pelotrio.addConnection(sl6, sl3);

        gelu = new ConstellationBase.Minor("gelu", new Color(0x758BA8));
        sl1 = gelu.addStar(8, 7);
        sl2 = gelu.addStar(28, 8);
        sl3 = gelu.addStar(23, 21);
        sl4 = gelu.addStar(3, 22);
        sl5 = gelu.addStar(17, 17);
        sl6 = gelu.addStar(16, 13);

        gelu.addConnection(sl1, sl2);
        gelu.addConnection(sl3, sl4);
        gelu.addConnection(sl2, sl5);
        gelu.addConnection(sl4, sl6);

        ulteria = new ConstellationBase.Minor("ulteria", new Color(0x347463));
        sl1 = ulteria.addStar(14, 9);
        sl2 = ulteria.addStar(17, 16);
        sl3 = ulteria.addStar(25, 19);
        sl4 = ulteria.addStar(7, 21);
        sl5 = ulteria.addStar(22, 25);

        ulteria.addConnection(sl1, sl2);
        ulteria.addConnection(sl2, sl3);
        ulteria.addConnection(sl4, sl5);

        alcara = new ConstellationBase.Minor("alcara", new Color(0x802952));
        sl1 = alcara.addStar(6, 27);
        sl2 = alcara.addStar(14, 20);
        sl3 = alcara.addStar(17, 24);
        sl4 = alcara.addStar(10, 18);
        sl5 = alcara.addStar(7, 5);
        sl6 = alcara.addStar(17, 9);

        alcara.addConnection(sl1, sl2);
        alcara.addConnection(sl2, sl3);
        alcara.addConnection(sl1, sl4);
        alcara.addConnection(sl4, sl5);
        alcara.addConnection(sl4, sl6);

        // 1.12.2-1.8.5
        vorux = new ConstellationBase.Minor("vorux", new Color(0xA8881E));
        sl1 = vorux.addStar(3, 21);
        sl2 = vorux.addStar(7, 7);
        sl3 = vorux.addStar(14, 15);
        sl4 = vorux.addStar(18, 5);
        sl5 = vorux.addStar(25, 16);
        sl6 = vorux.addStar(16, 26);
        sl7 = vorux.addStar(27, 2);

        vorux.addConnection(sl1, sl2);
        vorux.addConnection(sl2, sl3);
        vorux.addConnection(sl3, sl4);
        vorux.addConnection(sl4, sl5);
        vorux.addConnection(sl5, sl6);
        vorux.addConnection(sl4, sl7);
    }

}
