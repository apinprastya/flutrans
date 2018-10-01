# Midtrans Payment Gateway for Flutter

Flutter Midtrans Payment Plugin

## Install
Add to your pubspec.yaml
```
dependencies:
  flutrans: ^0.0.1
```

## Android setup
Add style to your android/app/src/main/res/values/styles.xml :
```
<style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
    <item name="windowActionBar">false</item>
    <item name="windowNoTitle">true</item>
</style>
```
And full styles.xml will be like below :
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="LaunchTheme" parent="@android:style/Theme.Black.NoTitleBar">
        <!-- Show a splash screen on the activity. Automatically removed when
             Flutter draws its first frame -->
        <item name="android:windowBackground">@drawable/launch_background</item>
    </style>
    <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
        <item name="windowActionBar">false</item>
        <item name="windowNoTitle">true</item>
    </style>
</resources>
```
And add the style to you Android Manifest in your application tag :
```
android:theme="@style/AppTheme"
```
## IOS
The plugin not working on IOS at the moment

## Example usage
```dart
import 'package:flutrans/flutrans.dart';
...
final flutrans = Flutrans();

//Init the client ID you URL base
flutrans.init("YOUR_CLIENT_ID", "YOUR_URL_BASE");

//Setup the callback when payment finished
flutrans.setFinishCallback((finished) {
    //finished is TransactionFinished
});

//Make payment
flutrans
.makePayment(
    MidtransTransaction(
        7500,
        MidtransCustomer(
            "Apin", "Prastya", "apin.klas@gmail.com", "08123456789"),
        [
        MidtransItem(
            "5c18ea1256f67560cb6a00cdde3c3c7a81026c29",
            7500,
            2,
            "USB FlashDisk",
        )
        ],
        skipCustomer: true,
        customField1: "ANYCUSTOMFIELD"),
)
.catchError((err) => print("ERROR $err"));
```
