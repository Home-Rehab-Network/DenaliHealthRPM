# aoj_device_plugin

A new Flutter plugin project.

## Getting Started

This project is a starting point for a Flutter
[plug-in package](https://flutter.dev/developing-packages/),
a specialized package that includes platform-specific implementation code for
Android and/or iOS.

For help getting started with Flutter development, view the
[online documentation](https://flutter.dev/docs), which offers tutorials,
samples, guidance on mobile development, and a full API reference.

## Plugin description

You can run 

```flutter run```

inside example folder - tested with flutter version 3.19.1 and Dart 3.3.0. 
onAttachedToEngine() method is similiar to onCreated in Android context. Discovered bluetooth devices are sent to flutter "example" app over mEventSink (handleDiscoveredDevice) 
and listed on the main.dart page. 

Once user clicks on eny of them, the "connectToDevice" method gets invoked over in MethodChannelAojDevicePlugin and in AojDevicePlugin.java, where mac is passed as an argument
