/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.structurelib;

import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;

/**
 * Never placed in a world: BlockRenderer6343 builds one tile per registered StructureLib tile class (no-arg
 * constructor) to show NEI multiblock previews. All altar tiers share TileAltar, so each higher tier gets its own
 * class here to get its own preview.
 */
public class AltarPreviewTiles {

    public static class Attunement extends TileAltar {

        public Attunement() {
            super(AltarLevel.ATTUNEMENT);
        }
    }

    public static class Constellation extends TileAltar {

        public Constellation() {
            super(AltarLevel.CONSTELLATION_CRAFT);
        }
    }

    public static class Trait extends TileAltar {

        public Trait() {
            super(AltarLevel.TRAIT_CRAFT);
        }
    }

    /** A fresh collector tile has no type (so no structure); the preview is the celestial one. */
    public static class CelestialCollector extends TileCollectorCrystal {

        public CelestialCollector() {
            onPlace(null, null, false, BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL);
        }
    }
}
