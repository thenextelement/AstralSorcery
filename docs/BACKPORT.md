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

- [ ] Grindstone recipe registry (crystal/tool/sword sharpening, dust recipes)
- [ ] Sooty marble variants, marble slabs, decorative blocks
- [ ] Evorsio + Pelotrio constellations, ritual effects
- [ ] Tier 4 "Iridescent" altar, trait upgrade/recipes, journal trait pages
- [ ] Mantles (capes) + constellation mantle effects (vicio flight adapted for 1.7, no elytra)
- [ ] Chalice, liquid interactions, liquid sparks, fountain
- [ ] Wand rework (augments), grapple wand, knowledge share, illumination/nocturnal powder usables
- [ ] Time-freeze effects (horologium)
- [ ] Destruction perks
- [ ] Treasure shrine structure
- [ ] Bores, ore generator, storage core (late 1.8 content, partially hidden upstream)
- [ ] Research entries, recipes, lang, models/textures for all of the above
- [ ] Bug fixes from the upstream changelog that apply to existing features

## Phase 2 — clean-room features (after 1.8.5)

Not started; see the note in the PR/README about clean-room process before beginning.
