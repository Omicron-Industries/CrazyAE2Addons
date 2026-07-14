// Research Station recipes (insaneae2addons:research).
// Signature: research(duration, energyPerTick, consumables, unlock)
//   duration      = total computation to accumulate; each tick adds the summed
//                   computation of the working pedestals, finishing when the
//                   total reaches this. Real time = duration / computation-per-tick.
//   energyPerTick = AE drained from the network each tick by the research station
//   consumables   = array of { item, count?, computation? }
//                   count       = amount of items taken from a pedestal
//                   computation = minimum computation the pedestal must supply
//   unlock        = { key, label?, item? }
//                   key         = research key written to the Data Drive
//                   label       = display name in the research list
//                   item        = icon item id

ServerEvents.recipes(event => {
    event.recipes.insaneae2addons.research(
        72000,
        200,
        [
            { item: 'minecraft:diamond', count: 4, computation: 128 },
            { item: 'ae2:fluix_block', count: 1, computation: 96 }
        ],
        { key: 'kubejs:example_research', label: 'Example Research', item: 'minecraft:diamond' }
    )

    // Named-setter form.
    event.recipes.insaneae2addons.research()
        .duration(1206400)
        .energyPerTick(500)
        .consumables([{ item: 'minecraft:nether_star', count: 4, computation: 256 }])
        .unlock({ key: 'kubejs:star_research', label: 'Star Research' })

    // Remove a specific recipe by id (id = its json path, e.g.
    // data/insaneae2addons/recipes/research/builder_pattern.json).
    // event.remove({ id: 'insaneae2addons:research/builder_pattern' })

    // Remove every research recipe.
    // event.remove({ type: 'insaneae2addons:research' })

    // Note: output/input filters are not reliable for this custom type -
    // remove by id or type instead.
})
