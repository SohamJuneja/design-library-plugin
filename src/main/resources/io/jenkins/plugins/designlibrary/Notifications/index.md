# Notifications

> Jenkins can display in-page notifications with a simple-to-use JavaScript API.

**Category:** Components

## Default

### showDefault

```javascript
notificationBar.show('Default')
```

## Success

### showSuccess

```javascript
notificationBar.show('Success', notificationBar.SUCCESS)
```

## Warning

### showWarning

```javascript
notificationBar.show('Warning', notificationBar.WARNING)
```

## Error

### showError

```javascript
notificationBar.show('Error', notificationBar.ERROR)
```

## Closing notifications

Notifications will hide after a few seconds, but you can programmatically hide them with:

### hideNotification

```javascript
notificationBar.hide()
```

