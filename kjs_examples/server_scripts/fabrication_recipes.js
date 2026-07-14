// Fabricator recipes (crazyae2addons:fabrication).
// Signature: fabrication(output, input, requiredKey?)
//   output      = { item, count? }
//   input       = array of { item, count? }
//   requiredKey = research key string that must be on the inserted Data Drive
//                 (omit for an ungated recipe)

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

    // Remove a specific recipe by id (id = its json path, e.g.
    // data/crazyae2addons/recipes/fabrication/wormhole.json).
    event.remove({ id: 'crazyae2addons:fabrication/wormhole' })

    // Remove every fabrication recipe.
    // event.remove({ type: 'crazyae2addons:fabrication' })

    // Note: output/input filters (e.g. { output: 'crazyae2addons:wormhole' }) are
    // not reliable for this custom type - remove by id or type instead.
})
