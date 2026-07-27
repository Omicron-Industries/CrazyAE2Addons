---
navigation:
  parent: insaneae2addons_index.md
  title: NBT Export Bus
  icon: insaneae2addons:nbt_export_bus
categories:
  - Energy and Item Transfer
item_ids:
  - insaneae2addons:nbt_export_bus
---

# NBT Export Bus

The **NBT Export Bus** exports every item in network storage that matches an [NBT expression](nbt_matcher.md), instead of
a fixed list of items.

This is how you push out "all damaged tools" or "all enchanted books" without listing every single stack.

---

## How it picks items

Each work cycle the bus walks the network inventory and exports every item key that matches the expression, until it
runs out of operations for that tick.

By default one operation moves 4 items. The factor is configurable.

An empty or invalid expression matches nothing, so the bus does nothing until a valid filter is saved.

---

## GUI

The filter is written in a multiline field with syntax highlighting.

Three buttons sit next to it:

* Save the filter. If the expression does not compile, the tooltip shows the parser error.
* Load NBT from the item placed in the fake slot, which fills the field with that item's tag.
* Format the expression, which re-indents it for readability.
