---
navigation:
  parent: insaneae2addons_index.md
  title: NBT Matcher
---

# NBT Matcher

The **NBT Matcher** is used by features that filter items by their NBT data.

Instead of picking one exact item stack, an NBT expression can match every item that carries a given tag structure.

It is used by the NBT View Cell, the NBT Storage Bus, and the NBT Export Bus.

---

## Criteria

A criterion is an SNBT snippet in curly braces, the same syntax the vanilla /give command uses.

An item matches a criterion when every key in the criterion is present on the item and the values match.

Keys that are on the item but not in the criterion are ignored, so criteria are partial matches, not exact ones.

Nested compounds are matched recursively.

---

## Lists

A list criterion matches when every entry of the criterion is found somewhere in the item's list.

An empty list criterion matches only an empty list on the item.

A compound criterion checked against a list matches when any entry of the list matches it.

---

## Operators

NBT expressions support boolean operators.

* ! means NOT
* & means AND
* | means OR
* ^ means XOR

The longer forms && and || are also accepted and are treated as & and |. There is no ^^ form and there are no word
forms like and, or, or nand.

---

## Operator priority

Operators are evaluated in this order:

* !
* &
* ^
* |

Parentheses can be used to group parts of an expression.

---

## Wildcards

A * used as a key or as a value means "anything".

{*: "diamond"} matches when any key on the item has the value diamond.

{display: *} matches when the item has a display tag at all. For a list value, * requires the list to be non-empty.

{*: *} matches any item that has NBT.

---

## Negated values

A string value starting with ! matches when the item's value is different.

{Damage: "!0"} matches every item whose Damage is not 0.

---

## Empty and invalid expressions

An empty expression matches nothing. Features usually treat it as "no filter set" instead of calling the matcher.

An invalid expression matches nothing. The GUI shows the parser error next to the confirm button, so a filter that
silently matches zero items is usually a syntax error.

---

## Examples

Match items with Sharpness:

{Enchantments: [{id: "minecraft:sharpness"}]}

Match a renamed item:

{display: {Name: '{"text":"My Sword"}'}}

Match enchanted items that are not damaged:

{Enchantments: *} & {Damage: 0}

Match items that have either a custom name or a custom model:

{display: *} | {CustomModelData: *}
