# Links

> Creates navigational connections to internal or external destinations.

**Category:** Patterns

Jenkins consists of a large and complex graph of domain objects (`ModelObject`), where each node in the graph is a web page and edges are hyperlinks. To help users navigate quickly on this graph, Jenkins provides a mechanism to attach context menus to model objects, which can be used to hop multiple edges without going through individual hyperlinks.

## Defining context menu

To define a context menu on `ModelObject`, implement `ModelObjectWithContextMenu`.

### Links

```java
package io.jenkins.plugins.designlibrary;

import hudson.Extension;
import jenkins.model.ModelObjectWithChildren;
import jenkins.model.ModelObjectWithContextMenu;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;

/**
 * @author Kohsuke Kawaguchi
 */
@Extension
public class Links extends UISample implements ModelObjectWithContextMenu, ModelObjectWithChildren {
  @Override
  public String getIconFileName() {
    return "symbol-at-outline plugin-ionicons-api";
  }

  /**
   * This method is called via AJAX to obtain the context menu for this model object.
   */
  public ContextMenu doContextMenu(StaplerRequest2 request, StaplerResponse2 response) throws Exception {
    if (false) {
      /*
        this implementation is sufficient for most ModelObjects. It uses sidepanel.jelly to
        generate the context menu
      */
      return new ContextMenu().from(this,request,response);
    } else {
      /*
       otherwise you can also programatically create them.
       see the javadoc for various convenience methods to add items
       */
      return new ContextMenu()
              .add(new MenuItem().withUrl("https://www.jenkins.io/")
                      .withDisplayName("Jenkins project")
                      .withIconClass("symbol-jenkins plugin-ionicons-api"))
              .add(new MenuItem().withUrl("https://plugins.jenkins.io/")
                      .withDisplayName("Plugin Documentation")
                      .withIconClass("symbol-extension-puzzle-outline plugin-ionicons-api"));
    }
  }

  public ContextMenu doChildrenContextMenu(StaplerRequest2 request, StaplerResponse2 response) throws Exception {
    /* You implement this method in much the same way you do doContextMenu */
    return new ContextMenu()
            .add("https://yahoo.com/","Yahoo")
            .add("https://google.com/","Google")
            .add("https://microsoft.com/","Microsoft");
  }

  @Extension
  public static final class DescriptorImpl extends UISampleDescriptor {
  }
}
```

## Breadcrumb integration

Implementing `ModelObjectWithContextMenu` is sufficient for the core to show the context menu for your model object in the breadcrumb. Hover your mouse over the breadcrumb of this page to see context menu associated with this sample. In addition, implementing `ModelObjectWithChildren` enables you to show children of your model object in the breadcrumb when you click the ‘>’ icon that separates breadcrumb items.

## Model hyperlink

By adding CSS class "model-link" to the <a> tags pointing to model objects with context menu, you can enable the context menu support to that hyperlink. For example:

Unless the hyperlink appears inline, it is often better to pre-allocate a space for the context menu anchor that appears when the mouse hovers over. To do this, also add the "inside" CSS element. For example:

