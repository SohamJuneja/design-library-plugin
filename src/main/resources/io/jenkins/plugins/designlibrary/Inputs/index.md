# Inputs

> Captures user input through various text or data entry formats.

**Category:** Components

## Textbox

### example

```xml
<f:entry title="Example" field="example">
  <f:textbox placeholder="Enter an example"/>
</f:entry>
```

By adding `clazz="required"` you can mark a textbox as being mandatory.

## Search

### search

```xml
<l:search-bar />
```

## Number fields

### number

```xml
<f:entry title="Number" field="count">
  <f:number min="1" max="99" default="5"/>
</f:entry>
```

## Password fields

### password

```xml
<f:entry title="Password" field="password" description="The password to access the system.">
  <f:password />
</f:entry>
```

## Code editor

CodeMirror can be used to turn an ordinary text area into a syntax-highlighted content-assistive text area.

### codemirror

```xml
<f:textarea codemirror-mode="shell" codemirror-config='"lineNumbers": true' />
```

There is support for languages like `groovy`, `xml`, `yaml`, `css`, `javascript`, `shell`, `python`, `perl` and many more. Use the attribute `codemirror-config` to specify additional key/value pairs in the JSON format (except the start and end bracket) to be passed as CodeMirror option object.

