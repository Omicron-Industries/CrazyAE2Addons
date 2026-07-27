---
navigation:
  parent: insaneae2addons_index.md
  title: Auto Builder
  icon: insaneae2addons:auto_builder
categories:
  - Monitoring and Automation
item_ids:
  - insaneae2addons:auto_builder
  - insaneae2addons:builder_pattern
  - insaneae2addons:auto_builder_creative_supply
---

# Auto Builder

The **Auto Builder** is a programmable building block. It reads a **Builder Pattern**, then places and breaks blocks in
the world, pulling materials from the ME network and returning what it breaks.

Everything it does is written in a small language, described in [Auto Builder Language](auto_builder_language.md).

---

## Making a pattern

The Builder Pattern can copy an existing structure instead of being written by hand.

1. Right-click a block to set the first corner.
2. Right-click a second block to set the other corner. This second click also becomes the origin, and the direction you
   are facing becomes the pattern's forward.
3. Right-click air to scan the region and save the program onto the pattern.

Air is skipped, and blockstates are kept, so logs stay rotated and stairs stay turned.

Right-click again after both corners are set to start a new selection.

---

## Editing a pattern

Sneak and right-click the pattern to open the editor.

The editor has the program text, a rename field, and buttons to rotate the structure, flip it horizontally, and flip it
vertically. The confirm button reports a syntax error if the program does not compile.

The hammer button opens a generator that writes simple shapes for you: pick width, height, depth, the direction of each
axis, whether to place or break, and an optional condition block.

---

## Running

Put the pattern into the Auto Builder and give the block a redstone signal from the top.

The GUI has six arrow buttons that shift the build origin relative to the builder, and a preview toggle that renders the
whole program as ghost blocks so you can check the alignment first.

Everything is relative to the builder's facing, so the same pattern can be reused in any orientation.

When the program finishes, the builder emits a short redstone pulse.

---

## Materials

Before starting, the builder collects every block the program needs into an internal buffer.

If something is missing and a Crafting Card is installed, it requests a craft and waits. Without a card, the run either
stops or skips the missing placements, depending on the skip-missing toggle in the GUI.

Broken blocks and drained fluid sources go back into the network. Blocks are broken as if with Silk Touch, and nothing
is destroyed unless the drops actually fit into storage.

---

## Power

The whole energy cost is paid upfront when the program starts. If the network cannot pay it, the run does not begin and
the buffered materials are flushed back.

Cost scales with the distance of each placement or break from the builder, so a program that works far away is
expensive.

---

## Speed

The builder runs 1 instruction per tick with no upgrades and up to 128 with 6 Speed Cards.

Breaking a block adds a short delay of its own, so demolition is slower than placing.

---

## Creative supply

The **Auto Builder Creative Supply** is a block for creative and testing worlds. While one is on the same network, the
builder places blocks without taking anything out of storage.

---

## Upgrades

* Speed Card, up to 6
* Crafting Card, 1
