---
navigation:
  parent: insaneae2addons_index.md
  title: Multiblock Builder
  icon: insaneae2addons:multiblock_builder
categories:
  - Monitoring and Automation
item_ids:
  - insaneae2addons:multiblock_builder
---

# Multiblock Builder

The **Multiblock Builder** builds the structure around a multiblock controller for you.

Place the controller where you want it, sneak and right-click it with the builder, and every missing block of the
pattern is placed in one go.

This is how the [Portable Penrose Sphere](penrose_sphere.md) is meant to be built. Doing that one by hand is not a
realistic plan.

---

## Where the blocks come from

The builder takes materials from your inventory first, then from an ME network.

Link it to a network by right-clicking a Wireless Access Point with it, the same way you link a wireless terminal. The
tooltip shows whether the tool is linked.

In creative mode it places everything for free.

---

## What it does not do

Blocks that are already correct are left alone, and blocks that cannot be replaced are skipped rather than destroyed.
Clear the area first if something is in the way.

When a symbol of the pattern allows several blocks, the builder uses the first one it can find materials for.

After building, it reports how many blocks it placed and how many it could not.

---

## Preview

Every multiblock controller also shows a preview of its structure in its own GUI, which is the easier way to check what
is missing before spending materials.
