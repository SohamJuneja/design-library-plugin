# App bars

> Frames your page and contains your most important actions.

**Category:** Components

## Top app bar

Top app bar provides the page heading as well as important actions.

### topAppBar

```xml
<l:main-panel>
  <l:app-bar title="Page title" subtitle="3">
    <button class="jenkins-button jenkins-button--primary">
      <l:icon src="symbol-add" />
      Primary
    </button>
    <button class="jenkins-button">Secondary</button>
    <button class="jenkins-button">Secondary</button>
    <t:help href="https://www.jenkins.io" />
  </l:app-bar>
  ...
</l:main-panel>
```

### Dos and Don'ts

| Do | Don't |
|---|---|
| Use an app bar instead of a `h1` element as app bars have unique properties. | Avoid showing more than four actions at once, relegate less used actions to an overflow menu. |
| Place your most important actions in the app bar. | Avoid showing a count subtitle if it could relate to multiple things on your page. |
| Show a count subtitle if it explicitly relates to your page. |  |

## Bottom app bar

Bottom app bars are great for forms, they provide important actions for the current page, such as "Save" and "Apply". Bottom app bars stick to the bottom of the screen so that they're always present.

### bottomAppBar

```xml
<div>
  <f:bottomButtonBar>
    <button class="jenkins-button jenkins-button--primary">
      Save
    </button>
    <button class="jenkins-button">Apply</button>
  </f:bottomButtonBar>
</div>
```

