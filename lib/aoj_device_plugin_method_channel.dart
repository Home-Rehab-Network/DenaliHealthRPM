import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'aoj_device_plugin_platform_interface.dart';

/// An implementation of [AojDevicePluginPlatform] that uses method channels.
class MethodChannelAojDevicePlugin extends AojDevicePluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('aoj_device_plugin');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  MethodChannelAojDevicePlugin() {
    setupListener();
  }

  void setupListener() {
    methodChannel.setMethodCallHandler(_handleMethod);
  }

  Future<void> _handleMethod(MethodCall call) async {
    switch (call.method) {
      case 'onBluetoothDeviceFound':
        _handleDeviceFound(call.arguments);
        break;
    // Handle other method calls if necessary
    }
  }

  void _handleDeviceFound(dynamic deviceInfo) {
    print("Bluetooth Device Found: $deviceInfo");
  }
}
