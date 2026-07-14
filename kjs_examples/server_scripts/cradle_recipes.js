// Entropy Cradle recipes (insaneae2addons:cradle).
// Signature: cradle(resultBlock, pattern, description?)
//   resultBlock = block id produced in the cradle chamber
//   pattern     = { symbols, layers }
//                 symbols = map of symbol -> array of accepted block ids
//                 layers  = array (bottom to top) of rows; each cell is a
//                           symbol or "." for empty. Cells in a row are space
//                           separated. The chamber is 5x5x5.
//   description = translation key or literal text shown in JEI/EMI (optional)

ServerEvents.recipes(event => {
    event.recipes.insaneae2addons.cradle(
        'insaneae2addons:energy_storage_1k',
        {
            symbols: {
                A: ['ae2:energy_cell'],
                B: ['ae2:fluix_block'],
                D: ['minecraft:iron_block'],
                E: ['ae2:1k_crafting_storage']
            },
            layers: [
                ['A A B A A', 'A A D A A', 'B D D D B', 'A A D A A', 'A A B A A'],
                ['A A D A A', 'A E E E A', 'D E E E D', 'A E E E A', 'A A D A A'],
                ['B D D D B', 'D E E E D', 'D E E E D', 'D E E E D', 'B D D D B'],
                ['A A D A A', 'A E E E A', 'D E E E D', 'A E E E A', 'A A D A A'],
                ['A A B A A', 'A A D A A', 'B D D D B', 'A A D A A', 'A A B A A']
            ]
        },
        'ec.1k'
    )

    // Remove a specific recipe by id (id = its json path, e.g.
    // data/insaneae2addons/recipes/cradle/energy1.json).
    // event.remove({ id: 'insaneae2addons:cradle/energy1' })

    // Remove every cradle recipe.
    // event.remove({ type: 'insaneae2addons:cradle' })

    // Note: output/input filters are not reliable for this custom type -
    // remove by id or type instead.
})
