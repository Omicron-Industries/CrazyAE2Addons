---
navigation:
  parent: insaneae2addons_index.md
  title: Player and Automation Cards
  icon: insaneae2addons:player_upgrade_card
categories:
  - Crafting and Patterns
item_ids:
  - insaneae2addons:player_upgrade_card
  - insaneae2addons:automation_upgrade_card
---

# Player and Automation Cards

These two upgrade cards control who is allowed to use the patterns of a Crazy Pattern Provider.

They do not change the patterns themselves. They only decide whether the provider counts as a valid pattern source for a
given crafting request.

---

## Cards

The **Player Upgrade Card** restricts the provider to crafting jobs started by a player, for example from a terminal.

The **Automation Upgrade Card** restricts it to jobs started by a machine, for example by a Level Emitter, an interface,
or another automation block on the network.

Install only one of them per provider. With both cards in the same provider the filter stops being meaningful.

---

## Where they fit

Both cards go into the upgrade slots of the Crazy Pattern Provider, block or part. One slot per card type.

No other block accepts them. A normal AE2 Pattern Provider is not affected.

---

## When the filter applies

The filter is checked twice: while the crafting job is being calculated, and again while the job is executed.

That means a job planned around a restricted provider will also be pushed to that provider, and a job that is not allowed
to use it will never plan around it in the first place.
