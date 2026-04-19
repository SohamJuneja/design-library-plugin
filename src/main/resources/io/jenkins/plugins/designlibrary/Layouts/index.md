# Layouts

> Predefined layout structures and patterns to organize page content.

**Category:** Patterns

## Two column

Two column layouts are for 'hub' style pages, pages that have child pages for the user to navigate to.

Examples of such pages include 'Dashboard', 'Design Library' and 'Plugin Manager'.

### Dos and Don'ts

| Do | Don't |
|---|---|
| Include a title on your sidebar that best encapsulates its child pages. | Don't include hierarchical navigation in your sidebar, such as "Back to Dashboard" or "Up". |
| Ensure all child pages share the same sidebar for consistency. | Don't include actions or external pages in your sidebar, these belong in the top app bar. |
| Buttons should only relate to/manipulate their parent column. |  |
| One (and only one) sidebar item should be in its selected state at a time. |  |

## Usage

The 'two-column' layout is default, so you don't need to do anything to use it.

### twoColumn

```xml
<l:layout title="Heading">
  <l:side-panel>
    <l:app-bar title="Heading" />

    <l:tasks>
      <l:task title="Root" href="..." icon="symbol-jenkins" />
      <l:task title="Second page" href="..." icon="symbol-edit" />
      <l:task title="Third page" href="..." icon="symbol-details" badge="${it.badge}" />
    </l:tasks>
  </l:side-panel>

  <l:main-panel>
    <l:app-bar title="Subheading">
      <button class="jenkins-button">
        <l:icon src="symbol-add" />
      </button>
    </l:app-bar>

    <p class="jenkins-leading-paragraph">This is some content</p>
  </l:main-panel>
</l:layout>
```

The example above is all you need to set up a two-column page, it's complete with headings, sidebar items and content.

## Badges

You can include badges (instances of `jenkins.management.badge`) in your sidebar to highlight important information. You should only use a badge if the information is useful to the user, for example:

- Updates count on the 'Plugins' page
- Passing/failed tests count
- Unread notifications

## One column

One column layouts are for pages that don't need navigation; they're ideal for standalone pages that don't have child navigation.

### Dos and Don'ts

| Do | Don't |
|---|---|
| Use the "one-column" layout if your page doesn't need a sidebar. | Avoid sprawling content, use tabs if your page is lengthy and can be separated into clearly labelled sections. |

## Usage

Set the type attribute to 'one-column' to use this layout:

### oneColumn

```xml
<l:layout title="Heading" type="one-column">
  <l:main-panel>
    <l:app-bar title="Heading">
      <button class="jenkins-button">
        <l:icon src="symbol-add" />
      </button>
    </l:app-bar>

    <p class="jenkins-leading-paragraph">This is some content</p>
  </l:main-panel>
</l:layout>
```

## Fullscreen

Fullscreen layouts hide the Jenkins user interface, providing a blank canvas for your page. Such layouts aren't intended for the average page and should only be used if your page needs it.

An example of such a page is Build Monitor View

[Build Monitor View](https://plugins.jenkins.io/build-monitor-plugin/)

## Usage

Set the type attribute to 'full-screen' to use this layout:

### fullscreen

```xml
<l:layout title="Heading" type="full-screen">
  <l:main-panel>
    <l:app-bar title="Heading">
      <button class="jenkins-button">
        <l:icon src="symbol-add" />
      </button>
    </l:app-bar>

    <p class="jenkins-leading-paragraph">This is some content</p>
  </l:main-panel>
</l:layout>
```

