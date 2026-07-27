---
navigation:
  parent: insaneae2addons_index.md
  title: Ampere Meter
  icon: insaneae2addons:ampere_meter
categories:
  - Monitoring and Automation
item_ids:
  - insaneae2addons:ampere_meter
---

# Ampere Meter

The **Ampere Meter** measures the energy that flows through it and reports the rate in its GUI and as a comparator signal.

It is an inline block, not a probe. Energy has to travel through it: the source pushes into one side, the meter forwards
everything into the block on the opposite side.

---

## Input and output sides

The meter uses the left and right side of its facing direction. One of them accepts energy, the other one feeds it forward.

The arrow button in the GUI swaps which side is the input and which one is the output.

Energy only moves from the input side to the output side. The meter never accepts energy on its output side, so it also
works as a diode.

---

## Displayed rate

The meter keeps the last five measured transfers and displays the largest one, not an average.

If nothing flows through for a while, the reading is cleared back to -. The idle timeout is configurable.

---

## Comparator output

Two text fields set the lower and upper threshold of the analog comparator signal.

Below the minimum the signal is 0, at or above the maximum it is 15, and in between it scales linearly.

---

## GregTech

With GregTech CEu installed the meter switches to amperes.

It then reports the amperage and the voltage tier of the measured current, for example 4A (LuV), instead of FE/t.
