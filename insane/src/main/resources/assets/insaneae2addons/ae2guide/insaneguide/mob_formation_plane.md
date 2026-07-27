---
navigation:
  parent: insaneae2addons_index.md
  title: Mob Formation Plane
  icon: insaneae2addons:mob_formation_plane
categories:
  - Mob Storage
item_ids:
  - insaneae2addons:mob_formation_plane
---

# Mob Formation Plane

The **Mob Formation Plane** spawns mobs the moment they are inserted into the network, instead of letting them reach
storage.

It is the mob counterpart of the AE2 Formation Plane: an export-only storage that happens to be the world in front of it.

---

## Filter

The config slots decide which mobs the plane accepts. Fill them by clicking with a **Mob Key Selector** that has a mob
assigned.

With an Inverter Card installed the list becomes a blacklist.

Priority works like on a storage bus, so a plane with higher priority takes mobs before your Mob Storage Cells do.

---

## Spawn spot

Mobs appear in the block in front of the plane. That block and the block above it must both be air, otherwise the plane
refuses the insert and the mob goes to storage instead.

One insert operation spawns up to 64 mobs.

---

## Upgrades

* Capacity Card, up to 5, for more filter slots
* Inverter Card, to turn the filter into a blacklist
* Redstone Card, for redstone control
