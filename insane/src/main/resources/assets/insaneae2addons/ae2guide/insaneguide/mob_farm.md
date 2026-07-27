---
navigation:
  parent: insaneae2addons_index.md
  title: Mob Farm
  icon: insaneae2addons:mob_farm_controller
categories:
  - Mob Storage
item_ids:
  - insaneae2addons:mob_farm_controller
  - insaneae2addons:mob_farm_wall
  - insaneae2addons:mob_farm_input
  - insaneae2addons:mob_farm_damage
  - insaneae2addons:mob_farm_collector
  - insaneae2addons:looting_upgrade_card
  - insaneae2addons:experience_upgrade_card
  - insaneae2addons:xp_shard
---

# Mob Farm

The **Mob Farm** takes [mob keys](mob_storage_cells.md) out of network storage, simulates killing them, and puts the
drops and XP Shards back in.

No entity is ever spawned, so a farm running at full speed costs nothing in entity ticks.

---

## Structure

The controller sits in the wall of the second layer from the bottom. Place it, then use the
[Multiblock Builder](multiblock_builder.md) or the preview in its GUI to get the rest right.

---

## Setup

The controller has three mob slots. Fill them with a **Mob Key Selector** that has a mob assigned. The farm cycles
through the three slots.

The tool slot holds the weapon used for the simulated kill. Its enchantments count, so Looting on the weapon adds to the
Looting Cards. 

---

## Speed

One kill cycle runs every 20 ticks and kills 16 mobs by default.

Each Speed Card adds 12 more kills per cycle, up to 4 cards, so a fully upgraded farm processes 64 mobs per second.

---

## Drops

Drops come from the mob's real loot table, rolled once plus once per level of Looting, and from its custom death loot,
which is where special drops like wither skulls come from.

Items that carry NBT or do not stack are thrown away. A mob farm never produces enchanted or named gear.

Equipment is out too. The mob the farm simulates is stripped of its armor and held items before anything is rolled, so
nothing a mob would have been carrying, from a zombie's shovel to a drowned's nautilus shell, ever comes out of it.

---

## Experience

Every kill also produces XP Shards. One shard is worth 10 experience points.

Each Experience Card multiplies the experience of a kill, up to 4 cards. A kill always yields at least one shard.

---

## Upgrades

* Speed Card, up to 4
* Looting Upgrade Card, up to 4
* Experience Upgrade Card, up to 4
