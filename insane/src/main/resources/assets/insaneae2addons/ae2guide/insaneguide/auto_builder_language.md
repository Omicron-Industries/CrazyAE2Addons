---
navigation:
  parent: insaneae2addons_index.md
  title: Auto Builder Language
  icon: insaneae2addons:builder_pattern
categories:
  - Monitoring and Automation
item_ids:
  - insaneae2addons:builder_pattern
---

# Auto Builder Language

A Builder Pattern holds a small program: a cursor that moves around, places blocks, and breaks them.

The [Auto Builder](auto_builder.md) executes it relative to its own facing, so a program written facing north works the
same facing east.

---

## You do not have to type any of this

Sneak and right-click a Builder Pattern to open its editor. That is where programs live: a text area for the code, a
name field, buttons that rotate the structure or flip it on either axis, and a confirm button that refuses to save a
program with a syntax error and tells you what is wrong.

The hammer button in the editor opens a generator for simple shapes. Set width, height, and depth, pick the direction
of each axis, choose place or break, add a condition block if you want one, and it writes the code for you.

The pattern can also read a structure straight out of the world. Right-click a block with it to mark one corner,
right-click another block to mark the second corner, then right-click air to scan. The finished program lands on the
pattern, block map included, and you can open it in the editor afterwards to tweak it. The second corner is also the
origin, and the way you are facing at that moment becomes the program's forward.

Hand-writing is still the way to get loops, macros, waits, and conditions that no generator would guess.

---

## Program structure

A program has two or three sections separated by a pipe:

BLOCK_MAP | MACROS | CODE

The macro section can be left out by using two pipes:

BLOCK_MAP || CODE

---

## Block map

The map gives every block used by the program a number:

0(minecraft:stone),1(minecraft:dirt),2(minecraft:oak_planks)

Blockstates are allowed:

0(minecraft:oak_log[axis=y])

NBT is not. A map entry containing braces is rejected.

---

## Movement

| Code | Meaning       |
|------|---------------|
| F    | forward       |
| B    | backward      |
| L    | left          |
| R    | right         |
| U    | up            |
| D    | down          |
| H    | return home   |

Each move shifts the cursor by one block. Directions are relative to the builder, not to the world.

H puts the cursor back at the starting position.

---

## Placing

P(n) places the block with id n from the map.

Placing over an existing block breaks it first and returns the drops to the network.

A map entry pointing at minecraft:air does nothing when placed. Use X to break.

---

## Breaking

X breaks the block at the cursor and stores the drops in the network.

Fluid source blocks at the cursor are drained into the network as well.

---

## Conditions

Both placing and breaking can be made conditional on what is already at the cursor:

* X==(n) breaks only if the block matches map entry n
* X!=(n) breaks only if it does not match
* P(m)==(n) places m only if the block matches n
* P(m)!=(n) places m only if it does not match

Comparison looks at the block type only. Blockstate properties in the map entry are ignored by the check.

---

## Loops

A number followed by braces repeats their contents:

4{P(1)R}

Loops can be nested:

2{3{P(1)F}U}

---

## Waiting

Z(n) waits n ticks. 20 ticks is one second.

---

## Macros

Macros are named snippets defined in the middle section:

\[line\](3{P(0)F}) \[top\](U\[line\])

They are used by name in the code section, and may refer to other macros. Recursion is cut off after 50 expansions.

---

## Errors

The language is strict. A program is rejected if a P or a condition uses an id missing from the block map, if brackets
are unbalanced, if a macro is undefined, or if an unknown character appears anywhere.

The editor shows the error on the confirm button.

---

## Examples

A line of three stone blocks:

0(minecraft:stone) || P(0)F P(0)F P(0)F

The same with a loop:

0(minecraft:stone) || 3{P(0)F}

Break, wait a second, place planks:

0(minecraft:oak_planks) || X Z(20) P(0)

A staircase of five steps:

0(minecraft:oak_planks) || 5{P(0)U F}

Clear stone from a row of five, leaving everything else:

0(minecraft:stone) || 5{X==(0)F}

Fill only air with planks:

0(minecraft:oak_planks),1(minecraft:air) || 5{P(0)==(1)F}

Using macros:

0(minecraft:stone) | \[line\](3{P(0)F}) \[top\](U\[line\]) | \[line\]\[top\]\[line\]
