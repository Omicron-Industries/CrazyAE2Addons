---
navigation:
  parent: insaneae2addons_index.md
  title: Entropy Cradle
  icon: insaneae2addons:entropy_cradle_controller
categories:
  - Crafting and Patterns
item_ids:
  - insaneae2addons:entropy_cradle_controller
  - insaneae2addons:entropy_cradle
  - insaneae2addons:entropy_cradle_capacitor
---

# Entropy Cradle

The **Entropy Cradle** is a huge energy accumulator that transmutes a block structure into a single powerful block when
it discharges.

It is how the Energy Storage blocks and the Penrose Frames are made.

---

## Structure

The multiblock is 11x11x8, built from Entropy Cradle blocks with an Entropy Cradle Capacitor in each corner column and
the controller in the bottom layer.

Inside is a 5x5x5 chamber where the transmutation happens.

---

## Charging

The cradle pulls AE from the network and stores it as FE, converting at 1 AE to 2 FE.

It holds 600 million FE and charges at up to 10 million AE per tick.

The six capacitor levels light up as the charge rises, and the capacitors emit a comparator signal once the cradle is
full.

---

## Transmutation

Build the input structure inside the chamber, then send a redstone pulse to the controller.

If the cradle is fully charged and the chamber matches a known recipe, the whole structure is consumed, the charge is
spent, and the result block appears in the middle of the chamber.

If the chamber does not match anything, or the cradle is not fully charged, nothing happens and the charge is lost.

---

## Recipes

Cradle recipes are shown in JEI and EMI with their full block layout.

Automating them is exactly what the [Auto Builder](auto_builder.md) is for: one pattern builds the input structure, a
redstone pulse fires the cradle, and another pattern picks the result up.
