---
navigation:
  parent: insaneae2addons_index.md
  title: Energy Storage
  icon: insaneae2addons:energy_storage_256m
categories:
  - Energy and Item Transfer
item_ids:
  - insaneae2addons:energy_storage_1k
  - insaneae2addons:energy_storage_4k
  - insaneae2addons:energy_storage_16k
  - insaneae2addons:energy_storage_64k
  - insaneae2addons:energy_storage_256k
  - insaneae2addons:energy_storage_1m
  - insaneae2addons:energy_storage_4m
  - insaneae2addons:energy_storage_16m
  - insaneae2addons:energy_storage_64m
  - insaneae2addons:energy_storage_256m
---

# Energy Storage

**Energy Storage** blocks are AE2 energy cells at a much larger scale. They behave exactly like a Dense Energy Cell,
they just hold far more.

---

## Tiers

| Block | Capacity |
|-------|----------|
| 1k    | 8 MAE    |
| 4k    | 32 MAE   |
| 16k   | 128 MAE  |
| 64k   | 512 MAE  |
| 256k  | 2 GAE    |
| 1m    | 8 GAE    |
| 4m    | 32 GAE   |
| 16m   | 128 GAE  |
| 64m   | 512 GAE  |
| 256m  | 2 TAE    |

A config multiplier scales every tier at once, in case a pack wants them bigger or smaller.

---

## Crafting

They are not crafted at a bench. Each tier is transmuted in the [Entropy Cradle](entropy_cradle.md) from an AE2 energy
cells and a matching crafting storage.

The k tiers use a normal Energy Cell, the m tiers use a Dense Energy Cell and the k tier below them.
