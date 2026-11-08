# 1.12+ feature backport (branch `112FOSSFeatureTest`)

## Licensing boundary

| Upstream range | License | How it's brought in |
|---|---|---|
| up to `2006ca5ce` (1.12.2-1.8.5, 2018-01-24) | GPLv3 | ported directly from the upstream source |
| `2ed2518` "Update LICENSE" and everything after (1.12 1.9+, 1.14/1.15/1.16) | All Rights Reserved | **clean-room only**: no upstream source after `2ed2518` may be read or copied; features are re-implemented from public behaviour descriptions |

Rule for contributors/agents: never `git show`, `git diff`, check out or otherwise read upstream files at or after
`2ed2518`. The `upstream` remote is only used for history up to `2006ca5ce`.

## Phase 1 — GPL features (1.4.x → 1.8.5)

Source: `git diff 3488cc84 2006ca5ce`. Each item is translated to the 1.7.10 port (block+meta instead of block
states, JSON models via `JsonModel`, both event buses, mixins instead of ASM, etc.).

- [x] Grindstone recipe registry (crystal/tool/sword sharpening, dust recipes, MineTweaker `mods.astralsorcery.Grindstone`, NEI)
- [x] Sooty marble variants, marble slabs, 1.12 pillar model (festive/holiday textures skipped)
- [x] Evorsio + Pelotrio (+ minor Vorux) constellations, ritual effects, 1.8.5 star map effect table (1.7 lacks mending/sweeping/luck; those entries are left out)
- [x] Tier 4 "Iridescent" altar: T3 -> T4 upgrade recipe, 1.8.5 TraitRecipe (inner slots, relay items, focus constellation), T4 body model, NEI + MineTweaker (`addTraitAltarRecipe`), research. Altar XP leveling kept (upgrades already go through recipes); journal trait-recipe page still to do with the first trait recipes
- [x] Mantles (capes) + all 12 constellation mantle effects, trait/attunement recipes, journal trait-recipe page. Vicio: own glide mode (hold jump while falling fast, sneak to stop) with vanilla elytra physics via mixin; octans knockback immunity via the knockback event (1.7 armor has no attributes); horologium cooldown kept in item NBT
- [ ] Chalice, liquid interactions, liquid sparks, fountain
- [ ] Wand rework (augments), grapple wand, knowledge share, illumination/nocturnal powder usables
- [x] Time-freeze effects (TimeStopController, synced client effects); frozen tiles are taken out of `World#loadedTileEntityList`
- [ ] Destruction perks
- [ ] Treasure shrine structure
- [ ] Bores, ore generator, storage core (late 1.8 content, partially hidden upstream)
- [ ] Research entries, recipes, lang, models/textures for all of the above
- [ ] Bug fixes from the upstream changelog that apply to existing features

## Phase 2 — clean-room features (after 1.8.5)

Not started; see the note in the PR/README about clean-room process before beginning.
