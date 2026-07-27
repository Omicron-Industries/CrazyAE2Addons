---
navigation:
  parent: insaneae2addons_index.md
  title: NBT View Cell
  icon: insaneae2addons:nbt_view_cell
categories:
  - Monitoring and Automation
item_ids:
  - insaneae2addons:nbt_view_cell
---

# NBT View Cell

The **NBT View Cell** is a View Cell that filters the ME Terminal by NBT data instead of by item selection.

Right-click the cell to open its editor, type an [NBT expression](nbt_matcher.md), and confirm it with the button on the
right. The filter is stored on the item, so it travels with the cell.

---

## In the terminal

Put the cell into any View Cell slot of a terminal. Only items matching the expression are listed.

The cell replaces the terminal filter instead of adding to it. When an NBT View Cell with a filter is present, normal
View Cells in the same terminal are ignored, and fluids and other non-item resources are hidden.

A cell with an empty filter does nothing and lets the other View Cells work as usual.
