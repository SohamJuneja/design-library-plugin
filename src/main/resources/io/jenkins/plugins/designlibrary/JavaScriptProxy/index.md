# JavaScript Proxy

> Export arbitrary server-side Java object to JavaScript and invoke their methods from JavaScript.

**Category:** Patterns

In Jenkins, you can export arbitrary server-side Java object to JavaScript via a proxy, then invoke their methods from JavaScript. In this sample, we call a method on the server to increment a counter. This object is a singleton, so you'll see the same counter value across all the browsers.

To expose a method of a Java class to JavaScript proxy, annotate the method with `@JavaScriptMethod`. For security reasons, only public methods on public classes annotated with this annotation are invokable from browsers:

### Foo

```java
import org.kohsuke.stapler.bind.JavaScriptMethod;

public class Foo {
    private int i;

    @JavaScriptMethod
    public int increment(int n) {
        return i += n;
    }
}
```

Then from Jelly scripts, use `st:bind` tag to export a Java object into a proxy. The "value" attribute evaluates to a server-side Java object to be exported, and the tag produces a JavaScript expression that creates a proxy. In the example below, we are pretending that the JEXL expression evaluates to some instance of Foo.

### sample

```xml
<?jelly escape-by-default='true'?>
<j:jelly xmlns:j="jelly:core" xmlns:st="jelly:stapler">
  ...

  <st:bind var="a" value="${it}"/>

  <div id="msg" />
  <st:adjunct includes="io.jenkins.plugins.<your-plugin>.<your-path>"/>
  ...
</j:jelly>
```

Below, you can find the sample for the adjunct to include. More information about adjuncts can be found on the security page.

### sample-adjunct

```javascript
a.increment(1, function (t) {
  document.getElementById("msg").innerHTML = t.responseObject();
});
```

## Invoking method

As you can see above, one can invoke methods on the proxy created by the `st:bind` tag. The JavaScript method takes the arguments that the Java method takes, then it can optionally take a function as an additional parameter, which is used as a callback method when the return value is available. The callback method receives an Ajax.Response object.

If the Java method returns an object value (such as `int, String, Collection, Object[], JSONObject`, etc.), you can use the `responseObject()` method to evaluate the response into a JavaScript object and use it. If the Java method renders more complex HTTP response (for example by writing directly to `StaplerResponse2` or returning an `HttpResponse`), JavaScript can use other `Ajax.Response` methods to access the full HTTP response.

The method call uses `XmlHttpRequest` underneath, and it gets eventually routed to the corresponding method call on the exact instance that was exported.

## Parameters of the server Java method.

The Java method can define arbitrary number of parameters for JavaScript. Each parameter is converted from JSON to Java via `StaplerRequest2.bindJSON`, so aside from primitive Java data types and typeless `JSONObject` / `JSONArray`, you can use Stapler databinding to accept typed structured data.

After defining the parameters from JavaScript, you can additionally define parameters that are injectable by Stapler, such as `StaplerRequest2` or `StaplerResponse2`.

## Exporting null

If the value attribute of a `st:bind` tag evaluates to `null`, then the corresponding JavaScript proxy will be `null`.

