import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'aoj_device_plugin_platform_interface.dart';

/// An implementation of [AojDevicePluginPlatform] that uses method channels.
class MethodChannelAojDevicePlugin extends AojDevicePluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('aoj_device_plugin');

  //final eventChannel = const EventChannel('aoj_device_plugin_stream');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<String?> connectToDevice(dynamic device) async {
    final result = await methodChannel.invokeMethod<String>('connectToDevice', <String, dynamic>{
      'arg1': device,
    });
    return result;
  }

  MethodChannelAojDevicePlugin() {
    setupListener();
  }
/*
  @override
  Stream<dynamic> aojDevicePluginStream() {
    return eventChannel.receiveBroadcastStream();
  }*/

  void setupListener() {
    methodChannel.setMethodCallHandler(_handleMethod);
  }

  Future<void> _handleMethod(MethodCall call) async {
    switch (call.method) {
      case 'onBluetoothDeviceFound':
        _handleDeviceFound(call.arguments);
        break;
    }
  }

  void _handleDeviceFound(dynamic deviceInfo) {
    print("Bluetooth Device Found: $deviceInfo");
  }
}
