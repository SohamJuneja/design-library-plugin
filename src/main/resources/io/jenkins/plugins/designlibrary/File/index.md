# File

> Enables file upload functionality.

**Category:** Components

### file

```xml
<f:file field="hello" />
```

Example parameters:

- field - The field of the descriptor to bind to, see examples in Jenkins core normally it will end up being a subclass of `FileItem`.
- clazz - Additional CSS class(es) to add.

## Additional parameters

You can also use any other attribute, it will be passed through to the input element.

### file-accept

```xml
<f:file field="hello" accept=".hpi,.jpi" clazz="extra-class" />
```

