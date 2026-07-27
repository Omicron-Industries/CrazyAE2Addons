---
navigation:
  parent: insaneae2addons_index.md
  title: Spawner Extractor
  icon: insaneae2addons:spawner_extractor_controller
categories:
  - Mob Storage
item_ids:
  - insaneae2addons:spawner_extractor_controller
  - insaneae2addons:spawner_extractor_wall
---

# Spawner Extractor

The **Spawner Extractor** reads a real Monster Spawner and feeds its mob into the ME network as
[mob keys](mob_storage_cells.md), without ever spawning anything.

It turns a dungeon spawner into a permanent, lag-free mob source.

---

## Structure

The multiblock is a 7x7x7 shell of Spawner Extractor Walls with vibrant quartz glass windows, built around the spawner.

The spawner must sit in the exact center. The controller replaces one wall block in the second layer from the bottom.

Place the controller against the spawner first, then use the [Multiblock Builder](multiblock_builder.md) or the preview
in its GUI to build the shell.

---

## How it works

While the structure is formed the spawner inside is suppressed, so it never spawns a real mob again. Breaking the
structure restores it.

Every 20 ticks the controller reads what the spawner would have spawned and inserts one mob key of that type into the
network.

Changing the spawner changes what the extractor produces, so a spawner altered by another mod or a spawn egg keeps
working.

---

## Speed

Each Speed Card adds one more mob per cycle, up to 4 cards, so a fully upgraded extractor yields 5 mobs every 20 ticks.

---

## Peaceful

On Peaceful difficulty the extractor still works. A config option lets you turn it off.
