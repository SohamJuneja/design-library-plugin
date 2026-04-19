# Toggle switch

> Enables users to switch between two states, such as on or off.

**Category:** Components

## Default

### default

```xml
<f:toggleSwitch name="enabled" title="Hello world" />
```

## Inverted

### inverted

```xml
<f:toggleSwitch name="enabled" title="Hello world" invertLabel="true" />
```

## Dynamic labels

Toggle switches labels can update in real time to reflect their status by setting the `checkedTitle` attribute. Try clicking on the toggle switch below to see how this works.

### dynamic

```xml
<f:toggleSwitch
  name="enable"
  title="${%Disabled}"
  checkedTitle="${%Enabled}"
  checked="true"
  tooltip="${%Enable or disable the current project}"
/>
```

