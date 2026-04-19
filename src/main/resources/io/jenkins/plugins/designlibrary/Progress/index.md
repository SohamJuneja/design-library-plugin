# Progress

> Indicates the completion status of a task or operation.

**Category:** Components

## Spinner

Use the spinner component to display progress for short operations, such as loading a page or card.

### spinner

```xml
<l:spinner />
```

### spinnerLabel

```xml
<l:spinner text="I am a label" />
```

## Progress animation

Use the progress animation component for indeterminate operations, such as displaying logs.

### progressAnimation

```xml
<l:progressAnimation />
```

## Progress bar

Use the progress bar component to display progress for long operations, such as building a job.

### progressBar

```xml
<t:progressBar pos="50" />
```

### progressBarIndeterminate

```xml
<t:progressBar pos="-1" />
```

## Progressive rendering

Shows how to do a complex operation on the server while displaying its progress, before displaying to the user.

### progressiveRendering

```xml
<form method="POST" action=".">
  <f:entry title="Enter a big number">
    <f:number name="number" value="${number}" />
  </f:entry>
  <button class="jenkins-button">
    Find factors
  </button>
</form>
<j:if test="${number != null}">
<st:adjunct includes="io.jenkins.plugins.<your-plugin>.<your-path>"/>
<p>Factors of ${number}:</p>
<l:progressiveRendering handler="${it.factor(number)}" callback="display" tooltip="Factoring…"/>
<ul id="factors"/>
</j:if>
```

### display

```javascript
function display(r) {
  for (var i = 0; r.newfactors.length > i; i++) {
    var li = document.createElement("li");
    li.appendChild(document.createTextNode(r.newfactors[i]));
    $(factors).appendChild(li);
  }
}
```

