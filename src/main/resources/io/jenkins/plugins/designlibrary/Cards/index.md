# Cards

> Use cards to surface related information and controls to users.

**Category:** Components
**Since:** 2.479.1

### Dos and Don'ts

| Do | Don't |
|---|---|
| Keep your card glanceable, let the user expand your card for more information. | Don't add unnecessary visual elements that clutter the card. |
| Make your cards scalable to different screen sizes. | Don't have too many actions on one card. |
| Do use hierarchy and symbols to add visual context. |  |

## Creating a card

### card

```xml
<l:card title="Card title">
  Card content
</l:card>
```

Cards are designed to automatically expand and fill the available space within their container, making them flexible and easy to use in different layouts. Since Jenkins doesn't currently have a built-in grid system, you'll need to create your own if you want to arrange multiple cards in a grid-like structure. You can do this by utilizing Flexbox or Grid to manage the positioning and spacing of your cards within the parent container.

## Setting actions on a card

### actions

```xml
<j:set var="controls">
  <a class="jenkins-card__reveal" href="#" tooltip="Custom action">
    <l:icon src="symbol-sparkles-outline plugin-ionicons-api" />
  </a>
</j:set>
<l:card title="Card with actions" expandable="#" controls="${controls}">
  Card content
</l:card>
```

Actions are a great way to show key controls right on your card. These controls are tied to the card's purpose and are placed in the top-right corner, so they're easy to spot at a glance. The `expandable` property always shows up last, and when you click it, it takes you to your card's specific page for quick access to more details.

