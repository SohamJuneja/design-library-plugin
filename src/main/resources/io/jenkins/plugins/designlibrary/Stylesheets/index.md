# Stylesheets

> Provides reusable stylesheet principles and standards for maintaining consistency.

**Category:** Patterns

## Naming

Classnames should follow Block Element Modifier (BEM) methodology and be prefixed.

[Block Element Modifier (BEM) methodology](https://getbem.com)

**Block**: Standalone entity that is meaningful on its own. e.g. `header`, `container`, `menu`, `checkbox`, `input`

**Element**: A part of a block that has no standalone meaning and is semantically tied to its block. e.g. `__item`, `__checkbox-caption`, `__header`

**Modifier**: A flag on a block or element. Use them to change appearance or behavior. e.g. `--disabled`, `--selected`, `--large`

An example classname following this strategy could be .jenkins-cards__item--large or .jenkins-button--disabled.

Not every class falls neatly into BEM, for example a class that does not belong to a particular component - such as a spacing or color class. For those classes we use the `-!-` convention to indicate that the classes can be used on any component.

Examples include: `.jenkins-!-padding-6`, `.jenkins-!-margin-6` and `.jenkins-!-destructive-color`.

## Prefixes

Classes should be prefixed in a way that is unique to your service or plugin. We use prefixes due to the extensible nature of Jenkins, without prefixing we couldn't be certain a plugin class wouldn't interfere with a Jenkins class. We also use prefixing to prevent clashes with other libraries, such as Bootstrap.

Jenkins uses the prefixes `jenkins-` and `app-` to namespace CSS classes, these prefixes should not be used in your plugins. Due to legacy reasons, some classes in use in Jenkins aren't namespaced however they are okay to use in your plugin.

Classes prefixed with `jenkins-` indicate that they can be used in your plugin with confidence that they will only receive minimal changes.

Classes prefixed with `app-` indicate that they should not be used in your plugin as these classes are internal to Jenkins and may change at any time.

A simple way to come up with a prefix for your plugin is to abbreviate your plugin name, e.g. Jenkins Design Library becomes `jdl-`.

