# Tooltips

> Offers brief, contextual information when hovering over an element.

**Category:** Components

## Basic tooltip

### tooltip

```xml
<button tooltip="I am a tooltip" class="jenkins-button jenkins-button--primary">
  Hover over me
</button>
```

## HTML and interactive content

### html-tooltip

```xml
<j:set var="tooltip">
  <img src="${imagesURL}/svgs/logo.svg" />
</j:set>
<button data-html-tooltip="${tooltip}" data-tooltip-interactive="true" class="jenkins-button jenkins-button--primary">
  Hover over me
</button>
```

You can use tooltips with Jenkins Symbols too, using either the `tooltip` or `htmlTooltip` attribute. Setting both will prioritise the `htmlTooltip`.

### symbol-tooltip

```xml
<j:set var="tooltip">
  <img src="${imagesURL}/svgs/logo.svg" />
</j:set>
<l:icon src="symbol-jenkins plugin-ionicons-api" htmlTooltip="${tooltip}" class="jdl-symbols__symbol" />
```

Make sure that text is sanitized before displaying it inside of `data-html-tooltip` as this can lead to XSS attacks. Escape or avoid using it on user-controlled data like agent display names or descriptions. See Preventing Cross-Site Scripting in Jelly views.

