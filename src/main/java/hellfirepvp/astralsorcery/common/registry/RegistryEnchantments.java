package hellfirepvp.astralsorcery.common.registry;

import static hellfirepvp.astralsorcery.common.lib.EnchantmentsAS.enchantmentNightVision;
import static hellfirepvp.astralsorcery.common.lib.EnchantmentsAS.enchantmentScorchingHeat;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentNightVision;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentPlayerWornTick;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentScorchingHeat;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryEnchantments
 * Created by HellFirePvP
 * Date: 15.03.2017 / 19:41
 */
public class RegistryEnchantments {

    public static List<EnchantmentPlayerWornTick> wearableTickEnchantments = new LinkedList<>();

    /** 1.7 enchantments share a fixed id array and throw on duplicates: prefer the old id, else the first free slot. */
    public static int findFreeEnchantmentId(int preferred) {
        net.minecraft.enchantment.Enchantment[] list = net.minecraft.enchantment.Enchantment.enchantmentsList;
        if (preferred < list.length && list[preferred] == null) return preferred;
        for (int i = list.length - 1; i > 62; i--) {
            if (list[i] == null) return i;
        }
        throw new IllegalStateException("[AstralSorcery] No free enchantment id left!");
    }

    public static void init() {
        enchantmentNightVision = register(new EnchantmentNightVision());
        enchantmentScorchingHeat = register(new EnchantmentScorchingHeat());
    }

    private static <T extends Enchantment> T register(T e) {
        e.setName(AstralSorcery.MODID + ":" + e.getName());
        MinecraftForge.EVENT_BUS.register(e);
        if (e instanceof EnchantmentPlayerWornTick) {
            wearableTickEnchantments.add((EnchantmentPlayerWornTick) e);
        }
        return e;
    }

}
