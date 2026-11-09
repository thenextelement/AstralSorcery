/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.structurelib;

import com.gtnewhorizon.structurelib.alignment.constructable.IMultiblockInfoContainer;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.tile.TileAttunementRelay;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.struct.BlockArray;
import hellfirepvp.astralsorcery.common.util.struct.BlockMeta;

/**
 * StructureLib (GTNH) integration: right-click an AS multiblock's core block with the Multiblock Hologram Projector to
 * see / build the structure. Only loaded when StructureLib is present.
 */
public class ModIntegrationStructureLib {

    private static BlockArray altarT3Combined;

    public static void register() {
        BlockArrayInfoContainer<TileAltar> altar = new BlockArrayInfoContainer<TileAltar>(
            ModIntegrationStructureLib::altarStructure,
            "astralsorcery.structurelib.altar").setController(
                te -> new BlockMeta(
                    BlocksAS.blockAltar,
                    te.getAltarLevel()
                        .ordinal()));
        IMultiblockInfoContainer.registerTileClass(TileAltar.class, altar);
        // NEI previews per tier (BlockRenderer6343 keys previews by tile class)
        IMultiblockInfoContainer.registerTileClass(AltarPreviewTiles.Attunement.class, altar);
        IMultiblockInfoContainer.registerTileClass(AltarPreviewTiles.Constellation.class, altar);
        IMultiblockInfoContainer.registerTileClass(AltarPreviewTiles.Trait.class, altar);

        IMultiblockInfoContainer.registerTileClass(
            TileRitualPedestal.class,
            new BlockArrayInfoContainer<TileRitualPedestal>(
                te -> MultiBlockArrays.patternRitualPedestal,
                "astralsorcery.structurelib.ritual_pedestal")
                    .setController(te -> new BlockMeta(BlocksAS.ritualPedestal, 0)));
        IMultiblockInfoContainer.registerTileClass(
            TileAttunementAltar.class,
            new BlockArrayInfoContainer<TileAttunementAltar>(
                te -> MultiBlockArrays.patternAttunementFrame,
                "astralsorcery.structurelib.attunement_altar")
                    .setController(te -> new BlockMeta(BlocksAS.attunementAltar, 0)));
        IMultiblockInfoContainer.registerTileClass(
            TileStarlightInfuser.class,
            new BlockArrayInfoContainer<TileStarlightInfuser>(
                te -> MultiBlockArrays.patternStarlightInfuser,
                "astralsorcery.structurelib.infuser").setController(te -> new BlockMeta(BlocksAS.starlightInfuser, 0)));
        IMultiblockInfoContainer.registerTileClass(
            TileAttunementRelay.class,
            new BlockArrayInfoContainer<TileAttunementRelay>(
                te -> MultiBlockArrays.patternCollectorRelay,
                "astralsorcery.structurelib.relay").setController(te -> new BlockMeta(BlocksAS.attunementRelay, 0)));
        IMultiblockInfoContainer.registerTileClass(
            TileCelestialGateway.class,
            new BlockArrayInfoContainer<TileCelestialGateway>(
                te -> MultiBlockArrays.patternCelestialGateway,
                "astralsorcery.structurelib.gateway").setController(te -> new BlockMeta(BlocksAS.celestialGateway, 0)));
        BlockArrayInfoContainer<TileCollectorCrystal> collector = new BlockArrayInfoContainer<TileCollectorCrystal>(
            te -> te.getType() == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL
                ? MultiBlockArrays.patternCollectorEnhancement
                : null,
            "astralsorcery.structurelib.collector_enhancement")
                .setController(te -> new BlockMeta(BlocksAS.celestialCollectorCrystal, 0));
        IMultiblockInfoContainer.registerTileClass(TileCollectorCrystal.class, collector);
        IMultiblockInfoContainer.registerTileClass(AltarPreviewTiles.CelestialCollector.class, collector);
        AstralSorcery.log.info("[AstralSorcery] StructureLib found: registered multiblock hologram support.");
    }

    /**
     * Each tier shows its own structure (discovery: the one its upgrade needs); tier 3 needs both tier 2 and tier 3
     * structures.
     */
    private static BlockArray altarStructure(TileAltar altar) {
        switch (altar.getAltarLevel()) {
            case DISCOVERY:
                return MultiBlockArrays.patternAltarAttunement; // what the upgrade needs
            case ATTUNEMENT:
                return MultiBlockArrays.patternAltarAttunement;
            case CONSTELLATION_CRAFT:
                return altarT3();
            case TRAIT_CRAFT:
                return MultiBlockArrays.patternAltarTrait;
            default:
                return null;
        }
    }

    private static BlockArray altarT3() {
        if (altarT3Combined == null) {
            BlockArray combined = new BlockArray();
            combined.addAll(MultiBlockArrays.patternAltarAttunement);
            combined.addAll(MultiBlockArrays.patternAltarConstellation);
            altarT3Combined = combined;
        }
        return altarT3Combined;
    }
}
