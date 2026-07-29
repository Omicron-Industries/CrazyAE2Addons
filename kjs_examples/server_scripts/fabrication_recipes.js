// Fabricator recipes (crazyae2addons:fabrication).
// Signature: fabrication(output, input, requiredKey?, fluidInput?, fluidOutput?)
//   output      = { item, count? }, or Item.empty for a fluid-only recipe
//   input       = array of ingredients, each { item, count? } or { tag, count? }
//                 ('2x minecraft:diamond' and '#forge:gems/diamond' work too)
//   requiredKey = research key string that must be on the inserted Data Drive
//                 (omit for an ungated recipe)
//   fluidInput  = { fluid, amount }, optional
//   fluidOutput = { fluid, amount }, optional

ServerEvents.recipes(event => {
    // Ungated recipe.
    event.recipes.crazyae2addons.fabrication(
        { item: 'insaneae2addons:data_drive', count: 1 },
        [
            { item: 'ae2:cell_component_256k', count: 1 },
            { item: 'ae2:blank_pattern', count: 2 }
        ]
    )

    // Research-gated recipe: needs 'insaneae2addons:auto_enchanter_research'
    // on the data drive.
    event.recipes.crazyae2addons.fabrication(
        { item: 'insaneae2addons:auto_enchanter', count: 1 },
        [
            { item: 'minecraft:enchanting_table', count: 2 },
            { item: 'ae2:import_bus', count: 1 },
            { item: 'ae2:export_bus', count: 1 }
        ],
        'insaneae2addons:auto_enchanter_research'
    )

    // Named-setter form is equivalent to the positional form above.
    event.recipes.crazyae2addons.fabrication({ item: 'insaneae2addons:entity_ticker' })
        .input([{ item: 'ae2:dense_energy_cell' }, { item: 'minecraft:nether_star' }])
        .requiredKey('insaneae2addons:entity_ticker_research')

    // Tag inputs work the same way as item inputs.
    event.recipes.crazyae2addons.fabrication(
        { item: 'crazyae2addons:wormhole', count: 1 },
        [
            { tag: 'forge:gems/diamond', count: 4 },
            { item: 'minecraft:nether_star', count: 1 }
        ]
    )

    // Fluid-only recipe: no item output, so pass Item.empty as the output.
    event.recipes.crazyae2addons.fabrication(Item.empty, [{ item: 'minecraft:lapis_lazuli', count: 1 }])
        .fluidInput({ fluid: 'minecraft:water', amount: 1000 })
        .fluidOutput({ fluid: 'insaneae2addons:research_fluid', amount: 1000 })

    // Remove a specific recipe by id (id = its json path, e.g.
    // data/crazyae2addons/recipes/fabrication/wormhole.json).
    event.remove({ id: 'crazyae2addons:fabrication/wormhole' })

    // Remove every fabrication recipe.
    // event.remove({ type: 'crazyae2addons:fabrication' })

    // Note: output/input filters (e.g. { output: 'crazyae2addons:wormhole' }) are
    // not reliable for this custom type - remove by id or type instead.
})
