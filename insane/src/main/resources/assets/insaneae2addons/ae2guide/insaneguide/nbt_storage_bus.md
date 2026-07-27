---
navigation:
  parent: insaneae2addons_index.md
  title: NBT Storage Bus
  icon: insaneae2addons:nbt_storage_bus
categories:
  - Energy and Item Transfer
item_ids:
  - insaneae2addons:nbt_storage_bus
---

# NBT Storage Bus

The **NBT Storage Bus** is a Storage Bus that decides what it stores from an [NBT expression](nbt_matcher.md) instead of
from config slots.

Attach it to any inventory like a normal Storage Bus. The expression replaces the whole partition list, so only items
matching it are stored, extracted, and reported to the network.

---

## Empty filter

An empty expression matches nothing, so a bus with no filter set exposes nothing at all.

This is different from a vanilla Storage Bus, where empty config slots mean "everything". Set an expression before
expecting the bus to do anything.

---

## Items only

Only item keys are matched. Fluids and other resource types are never exposed by this bus.

---

## GUI

The filter is written in a multiline field with syntax highlighting.

Three buttons sit next to it:

* Save the filter. If the expression does not compile, the tooltip shows the parser error.
* Load NBT from the item placed in the fake slot, which fills the field with that item's tag.
* Format the expression, which re-indents it for readability.

The priority button works exactly like on a normal Storage Bus.

---

## Upgrades

The bus has one upgrade slot and accepts a Void Card, which voids items that no longer fit into the attached inventory.
