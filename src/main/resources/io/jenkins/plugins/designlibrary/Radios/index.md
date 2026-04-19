# Radios

> Allows users to select a single option from a group.

**Category:** Components

## Simple radios

### index

```xml
<j:jelly xmlns:j="jelly:core" xmlns:f="/lib/form">
  <f:descriptorRadioList
    descriptors="${it.radios}"
    title="Test title"
    instance="${instance}"
    varName="realm"
  />
</j:jelly>
```

### Sample

```java
public class Sample {
    public ListUISample getRadios() {
        return new ArrayList(Jenkins.get().getExtensionList(UISample.class))
                .subList(0, 4);
    }
}
```

## Boolean radio

### booleanRadio

```xml
<f:booleanRadio field="myField" />
```

