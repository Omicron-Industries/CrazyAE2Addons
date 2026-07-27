---
navigation:
  parent: insaneae2addons_index.md
  title: Mob Export Bus
  icon: insaneae2addons:mob_export_bus
categories:
  - Mob Storage
item_ids:
  - insaneae2addons:mob_export_bus
---

# Mob Export Bus

The **Mob Export Bus** pulls [mob keys](mob_storage_cells.md) out of network storage and spawns them into the world.

It works like a normal Export Bus, except its config slots hold mobs. Fill them by clicking with a
**Mob Key Selector** that has a mob assigned.

---

## Spawn spot

Mobs appear in the block in front of the bus. That block and the block above it must both be air, otherwise the bus does
nothing.

---

## Rate

However fast the bus runs, it never spawns more than one mob per tick. It works out its budget from how many ticks
passed since its last batch, so a bus that wakes up after five idle ticks may spawn five mobs at once, and that is the
ceiling.

Without Speed Cards the bus handles a single mob per operation, which is well under that ceiling. Cards let it reach
one mob per tick, and nothing takes it higher.

The scheduling mode setting decides in which order the config slots are served, exactly like on an AE2 Export Bus.

---

## Upgrades

* Capacity Card, up to 5, for more config slots
* Speed Card, up to 4, to reach the one mob per tick ceiling
* Redstone Card, for redstone control
