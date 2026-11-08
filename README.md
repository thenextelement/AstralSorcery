# AstralSorcery (1.7.10 port)

Port of HellFirePvP's Astral Sorcery (1.10.2, version 1.4.x) to Minecraft 1.7.10 / Forge 10.13.4.1614,
built with the GTNH buildscript.

## Status

Goal of the current phase: every 1.4.x feature from the 1.10.2 source works on 1.7.10.
Later phases: features from the 1.12 and 1.16 versions.

Verified in-game (dedicated server + client):

- Block models (1.8-style JSON models are rendered by `client/util/model/JsonModel`), item renderers, sky,
  constellations in the night sky, telescope GUI
- Journal + research pages, altar GUI and altar crafting (wand-started), lightwell catalyst/liquid starlight,
  multiblock pasting with the correct marble variants
- NEI recipe handlers (altar tiers, starlight infuser, lightwell, transmutation)
- MineTweaker 3 scripts (`mods.astralsorcery.*`, see `AS-Example.zs`)

Ported but not yet exercised in-game: rituals, attunement, starlight network linking, perks, celestial gateway.

Untested: StructureLib (optional) support. The Multiblock Hologram Projector should show/build AS multiblocks when
used on the core block (altar, ritual pedestal, attunement altar, infuser, relay, celestial gateway, celestial
collector crystal).

Not in scope for 1.4.x: Evorsio/Pelotrio (1.12 constellations; stubs exist), 1.12 grindstone sharpening recipes.

## 1.7-specific architecture notes

- ASM patches from 1.10 are mixins (`mixins/early/minecraft`): block-change event, knockback event,
  sharpened-sword attributes, keyboard cancel, login payload, solar-eclipse brightness.
- Handlers are registered on both Forge and FML buses (`CommonProxy#registerEventHandler`): tick, login and
  crafting events live on the FML bus in 1.7.
- 1.10 block states became block + metadata (`util/struct/BlockMeta`, `BlockArray` overloads).
- Block bounding boxes must be world-absolute (`BlockBoxUtil.offset`).
- Tile inventories use `tile/base/ItemInventory` (ISidedInventory).

## Development

```
./gradlew runClient                    # dev client
./gradlew runServer                    # dev server; reads console commands from stdin
./gradlew runClient -PautoConnect      # client that joins a local runServer directly
```

To test MineTweaker scripts in dev, uncomment the `runtimeOnlyNonPublishable` MineTweaker line in
`dependencies.gradle` and put scripts into `run/scripts`.

Note: passive altar starlight collection needs the altar above y=40 (by design).
