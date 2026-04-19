# Checkboxes

> Allows users to select one or more items from a list.

**Category:** Components

### checkbox

```xml
<f:checkbox title="${%Example}" field="hello" />
```

Example parameters:

- id (optional) - Sets an id for the input, useful for UI testing, one will be generated for you otherwise. Avoid using in repeatable elements as you will end up with duplicate IDs.
- default (optional) - Can be used to set the default to `true`, not generally recommended as this won't affect configuration as code users, only use if you want to set a new default when people configure the plugin.
- field - The field of the descriptor to bind to, must be of type `boolean`.
- title - The label for the checkbox.

There are more parameters available, but they are generally not needed and should be avoided.

