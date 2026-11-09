/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectAevitas;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectArmara;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectBootes;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectDiscidia;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectEvorsio;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectFornax;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectHorologium;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectLucerna;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectMineralis;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectOctans;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectPelotrio;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectVicio;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.lib.Constellations;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectRegistry
 * Created by HellFirePvP
 * Date: 10.10.2017 / 00:46
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
public class CapeEffectRegistry {

    private static Map<IConstellation, CapeEffectFactory<? extends CapeArmorEffect>> armorEffectMap = new HashMap<>();

    @Nullable
    public static CapeEffectFactory<? extends CapeArmorEffect> getArmorEffect(IConstellation cst) {
        return armorEffectMap.get(cst);
    }

    public static void registerCapeArmorEffect(IConstellation cst, Class<? extends CapeArmorEffect> armorEffectClass) {
        if (getArmorEffect(cst) != null) return;
        armorEffectMap.put(cst, new CapeEffectFactory<>(armorEffectClass));
    }

    /** RegistryConstellations#registerCapeEffects in 1.8.5. */
    public static void init() {
        registerCapeArmorEffect(Constellations.discidia, CapeEffectDiscidia.class);
        registerCapeArmorEffect(Constellations.aevitas, CapeEffectAevitas.class);
        registerCapeArmorEffect(Constellations.evorsio, CapeEffectEvorsio.class);
        registerCapeArmorEffect(Constellations.armara, CapeEffectArmara.class);
        registerCapeArmorEffect(Constellations.vicio, CapeEffectVicio.class);

        registerCapeArmorEffect(Constellations.lucerna, CapeEffectLucerna.class);
        registerCapeArmorEffect(Constellations.fornax, CapeEffectFornax.class);
        registerCapeArmorEffect(Constellations.mineralis, CapeEffectMineralis.class);
        registerCapeArmorEffect(Constellations.pelotrio, CapeEffectPelotrio.class);
        registerCapeArmorEffect(Constellations.octans, CapeEffectOctans.class);
        registerCapeArmorEffect(Constellations.horologium, CapeEffectHorologium.class);
        registerCapeArmorEffect(Constellations.bootes, CapeEffectBootes.class);
    }

    public static void addDynamicConfigEntries() {
        Config.addDynamicEntry(new CapeEffectAevitas(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectDiscidia(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectEvorsio(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectArmara(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectVicio(new NBTTagCompound()));

        Config.addDynamicEntry(new CapeEffectLucerna(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectFornax(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectPelotrio(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectMineralis(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectOctans(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectHorologium(new NBTTagCompound()));
        Config.addDynamicEntry(new CapeEffectBootes(new NBTTagCompound()));
    }

}
