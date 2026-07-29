# KubeJS examples

Example scripts for adding and removing CrazyAE2Addons / InsaneAE2Addons recipes
from KubeJS.

Copy the files from `server_scripts/` into your instance's
`kubejs/server_scripts/` folder, then run `/reload` (or `/kubejs reload`) in game.

Requires KubeJS installed. The mods register these typed recipe builders:

| Builder                                         | Recipe type                  | Mod    |
|-------------------------------------------------|------------------------------|--------|
| `event.recipes.crazyae2addons.fabrication(...)` | `crazyae2addons:fabrication` | core   |
| `event.recipes.insaneae2addons.research(...)`   | `insaneae2addons:research`   | insane |
| `event.recipes.insaneae2addons.cradle(...)`     | `insaneae2addons:cradle`     | insane |

Removal does not need the builder. Remove everything of a type with
`event.remove({ type: 'insaneae2addons:research' })`, or one recipe by id with
`event.remove({ id: 'insaneae2addons:research/builder_pattern' })`.

Notes:
- Fabrication inputs take `{ item, count }` or `{ tag, count }`, and the recipe can
  also carry `fluid_input` / `fluid_output`. For a fluid-only recipe pass `Item.empty`
  as the output.
- Positional args follow the order shown below; the named setters
  (`.duration(...)`, `.energyPerTick(...)`, ...) also work.
- Remove recipes by `type` or `id`. Output/input filters are not reliable for
  these custom recipe types.
