---
navigation:
  parent: insaneae2addons_index.md
  title: Entity Ticker
  icon: insaneae2addons:entity_ticker
categories:
  - Monitoring and Automation
item_ids:
  - insaneae2addons:entity_ticker
---

# Entity Ticker

The **Entity Ticker** is a cable part that force-ticks the block entity in front of it, so the machine runs several times
per game tick.

It works on any block entity that has a ticker, so it speeds up furnaces, modded machines, and most other tickable blocks.

---

## Speed

Without upgrades the target is ticked one extra time per game tick, so it runs at double speed.

Each Speed Card doubles that again. The part accepts up to 8 Speed Cards, which is 511 extra ticks per game tick.

---

## Power

The part requires a channel and draws 1 AE/t idle.

While ticking, it pays 256 AE/t with no upgrades, and the cost is multiplied by 4 for every Speed Card.

If the network cannot pay the full cost for a tick, no extra ticks happen that tick.

---

## Blacklist

Block ids listed in the config are never ticked, even if the part faces them.

The list is empty by default.
