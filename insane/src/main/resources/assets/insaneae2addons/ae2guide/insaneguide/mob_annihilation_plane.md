---
navigation:
  parent: insaneae2addons_index.md
  title: Mob Annihilation Plane
  icon: insaneae2addons:mob_annihilation_plane
categories:
  - Mob Storage
item_ids:
  - insaneae2addons:mob_annihilation_plane
---

# Mob Annihilation Plane

The **Mob Annihilation Plane** captures living mobs into the ME network as [mob keys](mob_storage_cells.md).

Point it at the block where mobs end up, give the network space for mob keys, and it does the rest. There is nothing to
configure.

---

## What it captures

Only the block directly in front of the plane is checked, and only real mobs are taken. Players, item entities, boats,
armor stands and similar entities are ignored.

If several mobs stand in that block, the one closest to the center goes first.

---

## Rate

The plane checks its target block every 5 ticks and captures one mob at a time. After a successful capture it speeds up,
so a full mob pen drains quickly.

It requires a channel. If the network has no space for the mob key, nothing is captured and the mob stays alive.
