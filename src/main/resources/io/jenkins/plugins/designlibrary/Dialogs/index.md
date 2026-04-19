# Dialogs

> Displays overlay windows for additional information or user input without navigating away.

**Category:** Components
**Since:** 2.426.1

## Alerts

### alert

```javascript
function showAlert() {
  dialog.alert("Example alert", {
    message: "I'm an example of an error alert.",
    type: "destructive",
  });
}
```

Replaces the browser built-in alert(message). Contrary to the built-in the code will not wait until the user has closed the alert but as long as the alert is shown the user can't do anything else. Clicking outside the dialog will have no effect, the button must be used to close it. Alerts are usually used to show error messages.

## Prompts

### prompt

```javascript
function showPrompt() {
  dialog
    .prompt("Welcome to the Dialog demo", {
      message: "How should I call you?",
      minWidth: "450px",
      maxWidth: "600px",
    })
    .then(
      (name) => {
        dialog.alert("Hello " + name);
      },
      () => {
        dialog.alert("Hello Stranger");
      },
    );
}
```

Replaces the browser built-in prompt(message) to query a single value from a user. This returns a promise that allows the script to react when the user has entered the value or aborted. Clicking outside the dialog will have no effect, the buttons must be used to close it. Default `minWidth: 400px`

## Confirmations

### confirm

```javascript
function showConfirm() {
  dialog
    .confirm("Do it", {
      message: "Are you sure?",
      cancelText: dialog.translations.no,
    })
    .then(
      () => {
        dialog.alert("You've done it.");
      },
      () => {
        dialog.alert(null, { message: "OK, I'm not gonna do it." });
      },
    );
}
```

Replaces the browser built-in confirm(message) to make a user confirm an action. This returns a promise that allows the script to react when the user has confirmed or denied. Clicking outside the dialog will have no effect, the buttons must be used to close it. By default, the OK button gets the text `Yes`

## Modals

### modal

```javascript
function showModal() {
  const template = document.querySelector("#demo-template");
  const title = template.getAttribute("data-title");
  const content = template.content.firstElementChild.cloneNode(true);
  dialog.modal(content, {
    maxWidth: "550px",
    title: title,
  });
}
```

### modal

```xml
<template id="demo-template" data-title="${%Modal}">
  <div>
    <h2 class="jenkins-dialog__subtitle">${%Behaviour}</h2>
    ${%Click on the "X" in the top right or outside to close me}.
    <h2 class="jenkins-dialog__subtitle jenkins-!-margin-top-3">${%You can include icons}</h2>
    <l:icon src="symbol-chatbox-outline plugin-ionicons-api" class="icon-sm"/> ${%Dialogs are cool}.
  </div>
</template>
```

Presents a popup to the user with arbitrary information. A modal has no buttons at the bottom. Instead, a close button is added to the upper right corner (can also be hidden). The dialog is closed when clicking outside of it.

## Forms

### form

```javascript
function showForm() {
  const formTemplate = document.getElementById("demo-form");
  const form = formTemplate.firstElementChild.cloneNode(true);
  const toggle = document.getElementById("formsubmit");
  const title = formTemplate.dataset.title;
  dialog
    .form(form, {
      title: title,
      okText: "Order",
      submitButton: toggle.checked,
    })
    .then((formData) => {
      let title = "Order status";
      let message =
        "Thank you " +
        formData.get("name") +
        ".\n We received your order. Here are the details:" +
        "\n\nQuantity: " +
        formData.get("quantity") +
        "\nFlavor: " +
        formData.get("_.flavor");
      dialog.alert(title, { message: message });
    });
}
```

### form

```xml
<div id="demo-form" class="jenkins-hidden" data-title="${%Order your ice cream}">
  <f:form action="form" method="post">
    <f:entry title="${%Name}">
      <f:textbox name="name"/>
    </f:entry>
    <f:entry title="${%Quantity}">
      <f:number name="quantity" min="1" max="9" default="1"/>
    </f:entry>
    <f:entry title="${%Choose flavor}">
      <f:select field="flavor"/>
    </f:entry>
  </f:form>
</div>
```

Shows a form inside a dialog. For proper functionality, do not wrap the template inside a `template` block as scripts that are included by forms are not loaded immediately in that case. Do not include buttons to submit, apply or cancel the form. Those are added automatically. Adding buttons to validate things or testing connections is fine. You can either handle the form directly or submit the form. By default, the OK button gets the text `Submit` which is rather generic. Try to use something, that fits the context e.g. `Add`. Default `minWidth: 600px`

## Customizing the appearance and behaviour

All dialogs take a second optional parameter with options that allow to change certain aspects.

- `message`: Adds a message to the dialog
- `okText`: Adjust the text of the OK button, the default depends on the type of dialog
- `cancelText`: Adjust the text of the Cancel button, defaults to `Cancel`
- `type`: Change the color of the `OK` button. Allowed values: `destructive`
- `minWidth`: Set the minimum width of the dialog, the default depends on the type of dialog
- `maxWidth`: Set the maximum width of the dialog, defaults to `475px`
- `hideCloseButton`: Hides the close button in modal dialogs, defaults to `false`
- `allowEmpty`: When set to `false` the `OK` button will be disabled in the prompt dialog as long as the input field is empty or contains only whitespace, defaults to `false`
- `submitButton`: Change the behaviour of the submit button in a form dialog. When `true` the form will be submitted to the `action` attribute of the form. When `false`, a `FormData` object is passed to the `resolve` method of the `Promise`. Defaults to `true`

## OK Button texts

There are some predefined button texts available that come with translation. You can refer to them with e.g. `dialog.translations.apply`. You can choose from the following texts:

- `ok`: OK
- `cancel`: Cancel
- `yes`: Yes
- `no`: No
- `add`: Add
- `save`: Save
- `apply`: Apply
- `submit`: Submit

