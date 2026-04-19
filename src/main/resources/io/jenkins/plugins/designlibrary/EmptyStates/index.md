# Empty States

> For when there is no data, no configuration or no plugins installed that support the required feature an empty state can be provided instead.

**Category:** Components

### empty-states

```xml
<l:notice title="No plugins installed" icon="symbol-plugins" />
```

Supported Parameters:

- title - The message displayed in the notice box
- icon - The icon shown with the message

## Child content inside notices

You can also include child content within the notice as a call to action.

### empty-states-child-content

```xml
<l:notice title="No jobs created" icon="symbol-weather-icon-health-00to19">
    <button 
        type="button"
        class="jenkins-button jenkins-button--secondary">
        Create your first job
    </button>
</l:notice>
```

