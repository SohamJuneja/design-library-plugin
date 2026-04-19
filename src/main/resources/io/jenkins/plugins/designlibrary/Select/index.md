# Select

> Provides a dropdown for choosing one option from a predefined list.

**Category:** Components

## Simple select

If you want a simpler `select` to be rendered you can use the `f:select` Jelly tag and create a `doFillFieldNameItems` method in the `descriptor`.

### simple

```xml
<f:entry title="Fruits">
  <f:select field="fruit" />
</f:entry>
```

### Simple

```java
public ListBoxModel doFillFruitItems() {
    return new ListBoxModel(
        new ListBoxModel.Option("Apple"),
        new ListBoxModel.Option("Banana")
    );
}
```

## Enum select

With `f:enum` you can create a select that is based on an `enum` in Java.

### enum

```xml
<f:entry title="Unit" field="unit">
  <f:enum default="SECONDS">
    ${it.description}
  </f:enum>
</f:entry>
```

### Enum

```java
public class UnitHolder {

    private Unit unit;

    public Unit getUnit() {
        return unit;
    }

    @DataBoundSetter
    public void setUnit(Unit unit) {
        this.unit = unit
    }

    public enum Unit {
        MILLISECONDS("Milliseconds"),
        SECONDS("Seconds"),
        MINUTES("Minutes"),
        HOURS("Hours"),
        DAYS("Days");

        private final String description;

        Unit(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
```

## Advanced select

You can use `dropdownDescriptorSelector` to create a fully data-bound form and the option to include a config page for the `descriptor` by creating a file called `config.jelly`.

### advanced

```xml
<f:dropdownDescriptorSelector field="fruit" title="Fruits" descriptors="${it.fruitDescriptors}" />
```

### Advanced

```java
public DescriptorExtensionList<Fruit, Descriptor<Fruit>> getFruitDescriptors() {
    return Jenkins.get().getDescriptorList(Fruit.class);
}

public abstract static class Fruit implements ExtensionPoint, Describable<Fruit> {
    protected String name;

    protected Fruit(String name) {
        this.name = name;
    }

    public Descriptor<Fruit> getDescriptor() {
        return Jenkins.get().getDescriptor(getClass());
    }
}

public static class FruitDescriptor extends Descriptor<Fruit> {}

public static class Apple extends Fruit {
    private final int seeds;

    @DataBoundConstructor
    public Apple(int seeds) {
        super("Apple");
        this.seeds = seeds;
    }

    public int getSeeds() {
        return seeds;
    }

    @Extension
    public static final class DescriptorImpl extends FruitDescriptor {}
}

public static class Banana extends Fruit {
    private final boolean yellow;

    @DataBoundConstructor
    public Banana(boolean yellow) {
        super("Banana");
        this.yellow = yellow;
    }

    public boolean isYellow() {
        return yellow;
    }

    @Extension
    public static final class DescriptorImpl extends FruitDescriptor {}
}
```

## Dynamic select

Updates the contents of a "select" control dynamically based on selections of other controls.

### dynamic-sample

```xml
<j:jelly xmlns:j="jelly:core" xmlns:f="/lib/form">
  <f:entry title="State" field="state">
    <f:select/>
  </f:entry>
  <f:entry title="City" field="city">
    <f:select/>
  </f:entry>
</j:jelly>
```

### DynamicSample

```java
public static final class DescriptorImpl extends UISampleDescriptor {
    public ListBoxModel doFillStateItems() {
        ListBoxModel m = new ListBoxModel();
        for (String s : asList("A", "B", "C")) {
            m.add(String.format("State %s", s), s);
        }
        return m;
    }

    public ListBoxModel doFillCityItems(@QueryParameter String state) {
        ListBoxModel m = new ListBoxModel();
        for (String s : asList("X", "Y", "Z")) {
            m.add(String.format("City %s in %s", s, state), state + ':' + s);
        }
        return m;
    }
}
```

## Autocomplete

Allows users to quickly browse a select by typing to filter the available choices. Start typing a U.S. state for results to appear.

Using `combobox` forces the user to select an option from the available choices, it'll also display all choices on focus.

### combobox

```xml
<f:entry title="U.S. State" field="state">
  <f:combobox />
</f:entry>
```

### Combobox

```java
public class Sample {
    private static final String[] STATES = new String[]{
            "Alabama",
            "..."
    };

    public ComboBoxModel doFillStateItems() {
        return new ComboBoxModel(STATES);
    }
}
```

Using `textbox` on the other hand allows the user to enter free text if they so desire.

### textbox

```xml
<f:entry title="U.S. State" field="state">
  <f:textbox />
</f:entry>
```

### Textbox

```java
public class Sample {
  public AutoCompletionCandidates doAutoCompleteState(@QueryParameter String value) {
    AutoCompletionCandidates c = new AutoCompletionCandidates();
    for (String state : STATES) {
      if (state.toLowerCase().startsWith(value.toLowerCase())) {
        c.add(state);
      }
    }
    return c;
  }
}
```

