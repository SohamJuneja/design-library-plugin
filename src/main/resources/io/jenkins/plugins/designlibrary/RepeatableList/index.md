# Repeatable list

> Displays lists with varying content types within the same container.

**Category:** Components

### Config

```java
public static final class Config extends AbstractDescribableImpl<Config> {

  private final List<Entry> entries;

  @DataBoundConstructor public Config(List<Entry> entries) {
    this.entries = entries != null ? new ArrayList<Entry>(entries) : Collections.<Entry>emptyList();
  }

  public List<Entry> getEntries() {
    return Collections.unmodifiableList(entries);
  }

  @Extension public static class DescriptorImpl extends Descriptor<Config> {}
}
```

### config

```xml
<?jelly escape-by-default='true'?>
<j:jelly xmlns:j="jelly:core" xmlns:f="/lib/form">
  <f:section title="Entries">
    <f:entry field="entries">
      <f:repeatableHeteroProperty hasHeader="true" addCaption="${%Add entry}" />
    </f:entry>
  </f:section>
</j:jelly>
```

### Entry

```java
public static abstract class Entry extends AbstractDescribableImpl<Entry> {}
```

### ChoiceEntry

```java
public static final class ChoiceEntry extends Entry {

  private final String choice;

  @DataBoundConstructor public ChoiceEntry(String choice) {
    this.choice = choice;
  }

  public String getChoice() {
    return choice;
  }

  @Extension public static class DescriptorImpl extends Descriptor<Entry> {

    @Override public String getDisplayName() {
      return "Choice Entry";
    }

    public ListBoxModel doFillChoiceItems() {
      return new ListBoxModel().add("good").add("bad").add("ugly");
    }

  }
}
```

### SimpleEntry

```java
public static final class SimpleEntry extends Entry {

  private final String text;

  @DataBoundConstructor public SimpleEntry(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  @Extension public static class DescriptorImpl extends Descriptor<Entry> {
    @Override public String getDisplayName() {
      return "Simple Entry";
    }
  }

}
```

A flexible list of items, all derived from a shared base type. Users can add as many items as they need and configure each one independently. Each describable class needs to provide a `config.jelly`. The submission can be data-bound into `List<T>` where `T` is the common base type for the describable instances (`Entry` in the snippet above). See the documentation of repeatableHeteroProperty for a complete list of attributes and what they do. The example classes shown above are snippets from a parent class that implements the `Describable` interface. To use these classes, you must define them as inner classes within a parent class that also implements `Describable`.

