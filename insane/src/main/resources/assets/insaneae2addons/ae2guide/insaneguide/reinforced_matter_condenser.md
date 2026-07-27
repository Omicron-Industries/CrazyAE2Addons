---
navigation:
  parent: insaneae2addons_index.md
  title: Reinforced Matter Condenser
  icon: insaneae2addons:reinforced_matter_condenser
categories:
  - Crafting and Patterns
item_ids:
  - insaneae2addons:reinforced_matter_condenser
  - insaneae2addons:super_singularity
  - insaneae2addons:super_singularity_block
---

# Reinforced Matter Condenser

The **Reinforced Matter Condenser** compresses ordinary AE2 Singularities into **Super Singularities**, the payload used
to ignite a [Portable Penrose Sphere](penrose_sphere.md).

---

## Setup

The condenser needs a compression matrix before it accepts anything: a full stack of 64 **256k Cell Components** in its
component slot.

With the matrix in place, insert Singularities. Every 8192 of them produce one Super Singularity in the output slot.

The output slot holds up to 64 Super Singularities, and the condenser stops accepting input when the output is full.

---

## Automation

Both the input and the output are exposed to item handlers on every side, so a normal export bus in and import bus out
is enough. The component slot is not exposed and has to be filled by hand.

---

## Super Singularity Block

Nine Super Singularities plus netherite and a nether star compact into a **Super Singularity Block**, which is a
component of the Penrose Frame recipe.

The block is not decoration. On a network it draws 8192 AE/t and injures anything living within two blocks of it.
