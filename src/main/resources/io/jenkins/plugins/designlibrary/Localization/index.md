# Localization

> Ensures texts can be displayed in the user's language.

**Category:** Patterns

## Localize text in Jelly

Use ${%Text} to create texts that can be localized. Create a properties files for content longer than a few words.

### localize

```xml
<?jelly escape-by-default='true'?>
<j:jelly xmlns:j="jelly:core">
  ${%longtext}
  ${%hello('World')}
</j:jelly>
```

### localize

```properties
longtext=A longer text that should not appear inline in the Jelly file.
hello=Hello {0}
```

### localize_de

```properties
longtext=Ein längerer Text, der nicht inline in der Jelly-Datei erscheinen soll.
hello=Hallo {0}
```

## Localize text in Java

Create a file Messages.properties in the package folder. After compilation, the class Messages has corresponding methods for the texts.

### Localize

```java
public class Localize {
    public String getMessage() {
        return Messages.Localize_message();
    }

    public String getGreet() {
        return Messages.Localize_greet("World");
    }
}
```

### Messages

```properties
Localize.message=How to localize text in Java
Localize.greet=Hello {0}
```

### Messages_de

```properties
Localize.message=So lokalisieren Sie Texte in Java
Localize.greet=Hallo {0}
```

## Localize text in JavaScript via Jelly

Attach the localizations in attributes of template HTML elements via Jelly which can then be retrieved via the document.

### localize-js

```xml
<?jelly escape-by-default='true'?>
<j:jelly xmlns:j="jelly:core">
  <template
    id="plugin-i18n"
    data-longtext="${%longtext}"
    data-hello="${%hello}"
  />
</j:jelly>
```

### localize-jelly

```javascript
const i18n = document.getElementById("plugin-i18n");

const longText = i18n.dataset.longtext;
const hello = i18n.dataset.hello;

console.log(longText);
console.log(hello.replace("{0}", "World!"));
```

## Localize text in JavaScript via HTTP

Load them over HTTP via `$JENKINS_URL/i18n/resourceBundle?baseName=package.path.to.property.file` which retrieves the property file in the users' locale. This returns a JSON object with the localized texts.

### localize-http

```javascript
const response = await fetch(
  `${JENKINS_URL}/i18n/resourceBundle?baseName=io.jenkins.plugins.designlibrary.localize`,
);
const json = await response.json();
const i18n = json.data;

const longText = i18n.longtext;
const hello = i18n.hello;

console.log(longText);
console.log(hello.replace("{0}", "World!"));
```

A detailed explanation on how to localize text in Java and Jelly files can be found in the developer guide Internationalization and Localization.

