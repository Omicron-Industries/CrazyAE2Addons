---
navigation:
  parent: insaneae2addons_index.md
  title: Portable Penrose Sphere
  icon: insaneae2addons:portable_penrose_sphere_controller
categories:
  - Energy and Item Transfer
item_ids:
  - insaneae2addons:portable_penrose_sphere_controller
  - insaneae2addons:penrose_frame
  - insaneae2addons:penrose_glass
  - insaneae2addons:penrose_coil
  - insaneae2addons:penrose_port
  - insaneae2addons:penrose_laser
  - insaneae2addons:penrose_injection_port
  - insaneae2addons:penrose_heat_vent
  - insaneae2addons:penrose_hawking_vent
  - insaneae2addons:penrose_heat_emitter
  - insaneae2addons:penrose_mass_emitter
  - insaneae2addons:penrose_coolant_bucket
---

# Portable Penrose Sphere

The **Portable Penrose Sphere** is an endgame generator built around a contained black hole. You feed it Singularities,
they spiral through an accretion disc, and the disc pays out Forge Energy.

Running one is a balancing act between injection, heat, and mass. Getting it wrong ends in a meltdown that eats a large
part of your base.

---

## Structure

The core shell is Penrose Glass crossed by bands of Penrose Coil. The only swap it takes is a Penrose Frame in place of
glass, if you would rather not look at the black hole.

Everything you interact with goes on the outer skin, the Penrose Frame shell wrapped over the coils. Any Frame block
out there can be an Injection Port, a Heat Vent, a Hawking Vent, a Heat Emitter, or a Mass Emitter.

Four Penrose Ports sit at the equator, and four Penrose Lasers sit in the gap between core and coils. The laser spots
may be left empty and the sphere still forms, you simply cannot ignite it.

Penrose ports can be exchanged for any GregTechs dynamo hatch if the config is enabled.

Nobody builds this by hand. Place the controller, then let the [Multiblock Builder](multiblock_builder.md) or the
preview in the controller GUI do the work.

---

## Ignition

Igniting the black hole needs four charged lasers and a payload at the exact center of the sphere.

1. Put a Research Pedestal at the center of the core and place a storage cell on it holding at least 32512
   [Super Singularities](reinforced_matter_condenser.md). (full 4k item cell)
2. Charge all four Penrose Lasers. Each holds 2.1 billion FE and must be full.
3. Fire all four with redstone at the same time.

If everything lines up, the pedestal and its cell are consumed and the black hole appears.

A failed attempt is not free. A laser empties itself the moment it takes a redstone signal, charged or not, so a late
shot or a cell that is a few singularities short leaves you with nothing.

---

## The black hole is not part of the multiblock

Once lit, the black hole is a thing in the world of its own. It carries its own mass, its own accretion disc, and its
own heat, and it advances all three every tick, even if no controller is watching.

The controller only handles the surrounding machinery. It pulls singularities in through the injection ports, hands
coolant over from the Heat Vent, pays for evaporation from the Hawking Vent, collects the FE, and shows you the
numbers. The physics belongs to the black hole.

So taking the sphere apart does not switch it off. Break the structure, cut the ME network, or mine the controller, and
the disc keeps draining into the hole, mass keeps rising, and disc flow keeps making heat. What you lose is the
controls: no injection, no cooling, no evaporation. The numbers now move in one direction only, and the meltdown limits
are still there.

A hole whose disc has run dry just sits and waits. Heat stops rising because nothing flows, but nothing takes it away
either, and the hole keeps pulling and hurting and pulling anything that comes (32 blocks) near.

---

## Fuel and the accretion disc

Injection Ports pull ordinary AE2 Singularities out of the ME network while they have a redstone signal. Each port has a
configurable rate. 

Injected singularities do not become black hole mass right away. They enter the accretion disc, which keeps a rolling
window of about 120 seconds and drains with a mean delay of about 60 seconds. Output ramps up and down smoothly instead
of following your injection rate directly.

The practical sweet spot is around 16 singularities per tick. The hard cap exists so you can try more, not because more
is better: past a certain point the coolant demand grows faster than the power output.

---

## Heat

Disc flow generates heat, measured in MK. The black hole adds it every tick from its own disc flow and mass
multiplier, so heat rises with or without a controller. Heat is not only a hazard, it is also the efficiency curve.

Efficiency is 0 at 0 heat, peaks at 50000 MK, and falls off again above it. Running cold produces almost nothing, so a
working sphere sits near the peak on purpose.

At 100000 MK the sphere melts down.

---

## Mass

Every singularity that falls out of the disc adds to black hole mass. Mass gives an output multiplier: 1.0 at the edges
of the allowed window, up to 2.0 at the sweet spot in the middle.

The multiplier scales heating as well as power, so running at the sweet spot needs proportionally more cooling.

If mass reaches the top of the window, the sphere melts down.

---

## Heat Vents

A Heat Vent removes heat while it has a redstone signal, at the rate set in its GUI.

Cooling costs no energy, only coolant. Removing 1 MK takes 125 mB of Penrose Coolant, and a vent holds 64B, so pipe
it in. 

A sphere accepts only one Heat Vent. With a second one in the structure it refuses to form.

---

## Hawking Vents

A Hawking Vent removes black hole mass while it has a redstone signal.

Evaporation costs FE, and the cost grows exponentially with the rate you ask for. The controller pays from this tick's
generation first, then from its stored buffer, then from the vent's own FE buffer. Partial payment gives partial
evaporation.

Mass can never be pushed below the ignition mass.

While a vent is evaporating, the injection ports are off.

Like the Heat Vent, only one Hawking Vent is allowed per sphere.

---

## Output

The controller keeps generated energy in an internal buffer and exposes it as an FE capability. So does every Penrose
Frame in the structure, so you can tap the sphere anywhere on its skin.

Penrose Ports actively push energy into the blocks next to them.

With GregTech installed, its dynamo hatches count as ports and drain the buffer as EU. The exchange rate is GregTech's
own FE to EU ratio from its config, which is 4 FE per EU unless the pack changed it.

---

## Emitters

Heat Emitters and Mass Emitters output a redstone signal based on how far along its curve the sphere currently sits.

Each has a turn-on and a turn-off threshold, so the signal has hysteresis and does not flicker. Use them to drive vents
and injection ports without a computer.

---

## ComputerCraft

Every Penrose Frame is a ComputerCraft peripheral. It reports stored energy, disc energy, heat, mass, gross generation,
vent consumption, and the mass change over the last second.

---

## Meltdown

A meltdown is triggered by heat reaching the maximum or mass reaching the top of the window.

The black hole checks both limits itself, so a meltdown does not need a working sphere. A hole left with a loaded disc
and no cooling will reach one of the limits on its own and go off in the middle of whatever you built there instead.

By default it explodes and leaves behind a black hole field that eats blocks in a radius of 768. Both the explosion and
the field radius can be turned off in the config.

Do not run a sphere unattended without cooling, mass control, and something watching the numbers.
