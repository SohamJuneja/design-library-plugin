# Validation

> Ensures user inputs meet specified criteria or rules before submission.

**Category:** Patterns

### sample

```xml
<f:entry title="${%State name}" field="name">
  <f:textbox />
</f:entry>
```

### Sample

```java
@Extension
public static class DescriptorImpl extends Descriptor<State> {
    public FormValidation doCheckName(
            @QueryParameter String value,
            @RelativePath("capital") @QueryParameter String name
    ) {
        /*
        @RelativePath("capital") @QueryParameter
         ... is short for
        @RelativePath("capital") @QueryParameter("name")
         ... and thus can be thought of "capital/name"

        so this matches the current city name entered as the capital of this state
        */
        if (name == null) {
            return FormValidation.ok();
        }

        return FormValidation.ok("Are you sure " + name + " is a capital of " + value + "?");
    }
}
```

Form field validation can access values of the nearby input controls, which is useful for performing complex context-sensitive form validation. The same technique can be also used for auto-completion, populating combobox/listbox, and so on. The example above is a bit contrived, but all the input elements are named "name" (for city name and state name), and we use @RelativePath so that the validation of the state name refers to the capital name, and the validation of the city name refers to the state name.

To implement this you need to provide a `doCheckXXX` method, where XXX is the name of your field. This should return a `FormValidation` object.

There is also the possibility to use client side CSS validation inside Jelly, with the help of `clazz=`. Existing validations are `required`, `number` and `positive-number`. They can also be combined, e.g. `clazz="required number"` to validate a mandatory integer field.

### sample-clazz

```xml
<f:entry title="${%Non-negative integer}" field="name">
  <f:number min="0" clazz="number"/>
</f:entry>
```

