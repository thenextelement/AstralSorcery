/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/
package hellfirepvp.astralsorcery.common.integrations.structurelib;

import hellfirepvp.astralsorcery.common.tile.TileAltar;

/**
 * NEI multiblock preview tile, never placed in a world. BlockRenderer6343 builds one tile per registered StructureLib
 * tile class; it must be top-level since StructureLib registers classes by canonical name.
 */
public class PreviewTileAltarAttunement extends TileAltar {

    public PreviewTileAltarAttunement() {
        super(AltarLevel.ATTUNEMENT);
    }
}
