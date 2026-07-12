---
navigation:
  parent: crazyae2addons_index.md
  title: Display
  icon: crazyae2addons:display
categories:
  - Monitoring and Automation
item_ids:
  - crazyae2addons:display
---

# Display

Cable part that renders text, icons, and live ME data on its face.
Adjacent Displays can merge into one big screen.

Needs a channel for live data. Without it, text shows but all counts read 0.

Right-click opens the GUI. In Merge Mode, that edits the whole merged group.
Shift + right-click always opens just the one Display you clicked.

---

## Formatting

Headings: start a line with # through ###### — six levels, scaling from 1.6× down to 0.95×.

Bullet list: start a line with * or - followed by a space.

Indent a line with >> at the start (stack for deeper levels, e.g. >>>> for two levels).

**Bold**, *italic*, __underline__, ~~strikethrough~~ work with standard Markdown syntax.

---

## Colors and background

&cRRGGBB sets the text color from that point to the end of the line.

&cRRGGBB(text here) scopes the color — resets at the closing parenthesis. Can span multiple lines.

&bRRGGBB sets a fill color for the entire Display face. Only the last one in the text applies.

---

## Math expressions

&(expression) evaluates and inserts the result inline. Supports +, -, *, / and parentheses.
Evaluated after all token substitution, so you can do arithmetic on live counts.

Example: &(&s^minecraft:diamond * 100 / &s^minecraft:iron_ingot)

---

## Tables

Standard Markdown table syntax. Column alignment via the separator row:
|---| left, |:---:| center, |---:| right.

Cells can contain tokens, icons, and colors.

---

## Icons

&i^item:mod:name — item icon inline with text.

&i^fluid:mod:name — fluid icon.

&i^mod:name — tries item first, then fluid.

---

## Stock tokens

&s^mod:name shows how much of that item or fluid is stored in the ME network.

&s^mod:name%N divides the count by 10^N before displaying it.
For example, %3 divides by 1000, so 12345 becomes 12.

---

## Delta (rate) tokens

&d^mod:name@30s shows how much the stored amount changed per second, averaged over 30 seconds.
Positive = net inflow, negative = net outflow.

Default display unit is per second. Change it with a %Nu prefix before the @:

&d^minecraft:iron_ingot%1m@5m — per minute, averaged over 5 minutes

&d^minecraft:iron_ingot%1t@1m — per tick, averaged over 1 minute

Time units: t = ticks, s = seconds, m = minutes. Window minimum is 1 second, maximum 30 minutes.

---

## Tag expressions

Both stock and delta tokens accept a tag expression instead of a single item ID:

&s^tag{forge:ingots}

&s^tag{forge:ingots && !forge:ingots/iron}

&d^tag{forge:ingots}%1m@5m

See the [Tag Matcher](./tag_matcher.md) page for full syntax.

---

## Display Database variables

The **Display Database** block (a separate block, not a cable part) connects to your ME network
and holds named text variables you define, like factoryName = Reactor Hall.

Any Display on the same network can insert a variable with &varname:

# &factoryName

Variables are expanded before tokens, up to 8 levels deep, so a variable can itself contain
tokens or other variables. Useful for shared labels across many Displays.

---

## Mod compat types

With supported mods installed, use their resources in tokens and icons:

| Prefix | Mod |
|---|---|
| flux: | AppFlux |
| mana: | Applied Botania |
| source: | Ars Energistique |
| gas: | Mekanism |
| infusion: | Mekanism |
| pigment: | Mekanism |
| slurry: | Mekanism |

---

## Images

Open the Images tab in the GUI. Add images by picking a file, drag-and-dropping onto the GUI,
or pressing Ctrl+V — that also works with screenshots you copied to your clipboard.

Any common image format works (PNG, JPEG, etc.). Images larger than 512×512 are
automatically scaled down.

Per image: X and Y position it on the face (0–100% from the top-left corner),
Scale controls its size (1–100% of the face). Multiple images stack in list order;
use the ▲ and ▼ buttons to reorder them. Images render behind text.

---

## Merge Mode

Displays on the same face, same orientation merge into one screen when Merge Mode is on.

**Per-edge connect toggles** — each Display has four on/off switches for its edges.
Disabling one prevents merging across that edge, so you can split a wall of Displays
into separate logical groups without removing any.

**Center text** — horizontally centers each line across the merged screen.

**Add margin** — adds padding between the text and the Display edges.