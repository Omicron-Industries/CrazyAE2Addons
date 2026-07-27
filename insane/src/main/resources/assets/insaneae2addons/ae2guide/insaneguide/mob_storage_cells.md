---
navigation:
  parent: insaneae2addons_index.md
  title: Mob Storage
  icon: insaneae2addons:mob_storage_cell_64k
categories:
  - Mob Storage
item_ids:
  - insaneae2addons:mob_storage_cell_1k
  - insaneae2addons:mob_storage_cell_4k
  - insaneae2addons:mob_storage_cell_16k
  - insaneae2addons:mob_storage_cell_64k
  - insaneae2addons:mob_storage_cell_256k
  - insaneae2addons:mob_cell_housing
  - insaneae2addons:mob_key_selector
---

# Mob Storage

Mobs are a resource type of their own on the ME network, next to items and fluids. They are stored as **mob keys**.

A mob key is only an entity type and a count. Nothing else about a captured mob is kept, so a named, equipped, or
tamed mob comes back as a plain one.

---

## Mob Storage Cells

Mob Storage Cells come in 1k, 4k, 16k, 64k, and 256k. They are built from the normal AE2 cell components plus a
**Mob Cell Housing**.

They behave like ordinary AE2 cells, but accept nothing except mob keys, and each cell holds at most 5 different mob
types.

---

## Mob Key Selector

Config slots that expect a mob cannot take a spawn egg. They take a **Mob Key Selector** with a mob assigned to it.

Right-click the selector to open a searchable list of every entity type in the game and pick one. The choice is saved
on the item.

After that the selector works like a filter stamp: hold it and click into the config slot of a Mob Farm Controller,
Mob Export Bus, or Mob Formation Plane.

---

## Getting mobs in and out

Mobs enter the network through the [Mob Annihilation Plane](mob_annihilation_plane.md) or the
[Spawner Extractor](spawner_extractor.md).

They leave through the [Mob Export Bus](mob_export_bus.md) or the [Mob Formation Plane](mob_formation_plane.md), or get
turned into drops by the [Mob Farm](mob_farm.md).
