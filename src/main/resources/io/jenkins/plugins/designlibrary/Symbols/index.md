# Symbols

> Jenkins Symbols are an extensive and consistent collection of icons for use in Jenkins and plugins.

**Category:** Components

Symbols are intended to be used everywhere a traditional icon would be used, such as in the sidebar, in buttons and in tables. Symbols are scalable, support different weights and adapt to the user's theme.

### Dos and Don'ts

| Do | Don't |
|---|---|
| Symbols should be used to help a user recognise what a task does at a glance. | Don't use custom symbols that aren't consistent with Jenkins. |
| They should be recognisable and relevant, e.g. for locked resources. | Don't add symbols to headings, they just create visual clutter. |
| Use a tooltip on the symbol if there isn't any accompanying text. |  |

## Using Symbols

Using symbols in your view is simple. Use the existing `icon` component and set the `src` value to the symbol you want, prefixed with `"symbol-"`.

### symbol

```xml
<l:icon src="symbol-search" />
```

Symbols can also display tooltips:

### symbol-tooltip

```xml
<l:icon src="symbol-jenkins" tooltip="Howdy" />
```

It's also possible to customize symbols with custom classes:

### symbol-classes

```xml
<l:icon src="symbol-rss" class="spin" />
```

You can change the size of the symbol by using one of the sizing classes:

### symbol-size

```xml
<l:icon src="symbol-cube" class="icon-sm" />
<l:icon src="symbol-cube" class="icon-md" />
<l:icon src="symbol-cube" class="icon-lg" />
<l:icon src="symbol-cube" class="icon-xlg" />
```

You can use the build status symbols like so, append '-anime' to use the animated variant:

### symbol-status

```xml
<l:icon src="symbol-status-blue" />
<l:icon src="symbol-status-yellow" />
<l:icon src="symbol-status-red" />
<l:icon src="symbol-status-nobuilt" />
<l:icon src="symbol-status-aborted" />
<l:icon src="symbol-status-disabled" />
```

And you can use the weather health symbols like so:

### symbol-weather

```xml
<l:icon src="symbol-weather-icon-health-00to19" />
<l:icon src="symbol-weather-icon-health-20to39" />
<l:icon src="symbol-weather-icon-health-40to59" />
<l:icon src="symbol-weather-icon-health-60to79" />
<l:icon src="symbol-weather-icon-health-80plus" />
```

[View the complete list of Jenkins Symbols on GitHub](https://github.com/jenkinsci/jenkins/tree/master/war/src/main/resources/images/symbols)

## Add more symbols with the ionicons API plugin

To use more symbols, add the `ionicons-api-plugin` to your project.

[Click here for a sample how to add the API to your plugin](https://plugins.jenkins.io/ionicons-api/#dependencies)

Reference the symbol with:

### symbol-ionicons-reference

```xml
<l:icon src="symbol-symbolName-outline plugin-ionicons-api" />
```

[View the complete list of all symbols provided by the ionicons-api-plugin on ionic.io/ionicons](https://ionic.io/ionicons)

## Custom Symbols

Add your symbol to:

### snippet1

```javascript
{plugin-root}/src/main/resources/images/symbols
```

Reference the symbol with:

### symbol-plugin

```xml
<l:icon src="symbol-symbolName plugin-yourArtifactId" />
```

Make sure you replace the placeholder parameters:

- symbolName - the file name of the symbol without the file extension (e.g. cloud)
- yourArtifactId - the artifact ID of your plugin, or the one of the plugin you want to load the symbol from

ArtifactId can be found in your `pom.xml` for Maven plugins or as `shortName` in `build.gradle(.kts)` for Gradle plugins.

To make your symbol themeable, adjust the following properties:

- stroke="currentColor"
- fill="currentColor"

You don't need to add both attributes, pick the one that matches your use case.

