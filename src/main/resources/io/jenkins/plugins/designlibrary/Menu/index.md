# Menu

> Menus allow you to group similar controls under one roof. They're an effective way to de-clutter your page whilst offering users the actions they need.

**Category:** Components
**Since:** 2.452.1

### default

```xml
<l:overflowButton>
  <dd:item icon="symbol-star-outline plugin-ionicons-api"
           text="${%Action 1}" />
  <dd:item icon="symbol-star-outline plugin-ionicons-api"
           text="${%Action 2}" />
  <dd:separator />
  <dd:header text="${%Heading}" />
  <dd:item icon="symbol-star-outline plugin-ionicons-api"
           text="${%Action 3}" />
  <dd:item icon="symbol-star-outline plugin-ionicons-api"
           text="${%Action 4}" />
  <dd:separator />
  <dd:submenu icon="symbol-star-outline plugin-ionicons-api" text="${%Submenu}">
    <dd:item icon="symbol-star-outline plugin-ionicons-api"
             text="${%Subaction 1}" />
    <dd:item icon="symbol-star-outline plugin-ionicons-api"
             text="${%Subaction 2}" />
  </dd:submenu>
  <dd:separator />
  <dd:custom>
    <l:confirmationLink class="jenkins-dropdown__item jenkins-!-destructive-color"
                        href="doDelete"
                        message="Delete"
                        post="true">
      <div class="jenkins-dropdown__item__icon jenkins-!-destructive-color">
        <l:icon src="symbol-trash-outline plugin-ionicons-api" />
      </div>
      ${%Delete}
    </l:confirmationLink>
  </dd:custom>
</l:overflowButton>
```

### Dos and Don'ts

| Do | Don't |
|---|---|
| Use overflow buttons to avoid overcomplicating your page. | Avoid moving important, commonly used actions to menus. |
| Use overflow buttons to offer quick access to a subpage's actions. | Don't overcomplicate your menus, avoid using more than one level of submenus. |
| Use color to emphasise differences between actions. |  |

