# Buttons

> Triggers specific actions with a click or tap.

**Category:** Components

## Default

### default

```xml
<button class="jenkins-button">Default</button>
```

## Primary

Only use one primary button per page.

### primary

```xml
<button class="jenkins-button jenkins-button--primary">Primary</button>
```

## Tertiary

Tertiary buttons should be used for buttons which have minimal interaction or for those where a background creates too much visual clutter.

### tertiary

```xml
<button class="jenkins-button jenkins-button--tertiary">Tertiary</button>
```

## Symbol

Buttons can include symbols, and they'll be automatically sized to fit correctly.

### symbolButton

```xml
<button class="jenkins-button">
  <l:icon src="symbol-add" /> With symbol
</button>
```

## Modifiers

Buttons work with color modifier classes, allowing for easy customization.

### build

```xml
<button class="jenkins-button jenkins-button--primary jenkins-!-build-color">
  <l:icon src="symbol-play" />
  Build
</button>
```

### destructive

```xml
<button class="jenkins-button jenkins-!-destructive-color">
  <l:icon src="symbol-trash" />
  Delete
</button>
```

Destructive buttons should be used when something will be deleted or not easily undone. Generally these should have a confirmation dialog or page.

## Copy

A small button to copy text into the user's clipboard.

### copyButton

```xml
<l:copyButton text="A shoulder of lemon fields" />
```

## Help

The button should always be in the top right of a page, last in the row of components, or in an overflow menu.

### helpButton

```xml
<t:help href="https://www.jenkins.io" tooltip="${%Additional information on buttons}" />
```

