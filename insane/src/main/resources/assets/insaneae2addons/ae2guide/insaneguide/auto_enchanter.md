---
navigation:
  parent: insaneae2addons_index.md
  title: Auto Enchanter
  icon: insaneae2addons:auto_enchanter
categories:
  - Crafting and Patterns
item_ids:
  - insaneae2addons:auto_enchanter
  - insaneae2addons:xp_shard
---

# Auto Enchanter

The **Auto Enchanter** enchants items and books automatically, using experience stored in the ME network instead of
your own levels.

It is a real enchanting table under the hood, so the results follow the same rules a player would get.

---

## Placement

The block must sit exactly two blocks below an Enchanting Table, with the table directly above it.

Bookshelves around that table raise the enchantment power exactly as in vanilla.

---

## Slots

There are three slots: the item to enchant, lapis lazuli, and the output.

The input accepts anything enchantable plus plain books, which come out as enchanted books.

A new item is only enchanted when the output slot is empty. External automation inserts lapis and items on any side and
pulls finished items back out.

---

## Enchantment level

Three buttons pick which of the three enchanting table offers to take, exactly like the three rows of a vanilla table.

The chosen option is also the number of lapis consumed per enchant.

---

## Experience

The XP cost is derived from the level of the roll and multiplied by a config factor, 10 by default.

It is paid from the network in two forms:

* XP Shards, worth 10 experience each
* any fluid tagged forge:experience or forge:xpjuice, at 20 mB per experience point

Shards are spent first, fluid covers the rest. If the full cost cannot be paid, nothing is enchanted and nothing is
consumed.

---

## Auto supply

Two toggles let the machine refill itself from the network: one pulls lapis lazuli, the other pulls books into the
input slot.

Book supply only kicks in when the input slot is empty or already holds books, so it never fights with an item you
inserted yourself.

---

## Apotheosis

With Apotheosis installed the enchanter uses its enchanting logic instead of the vanilla one, reading Eterna, Quanta and
Arcana from the shelves around the table.
