
import 'aoj_device_plugin_platform_interface.dart';
import 'package:flutter/services.dart';

class AojDevicePlugin {
  static const EventChannel _eventChannel = EventChannel('aoj_device_plugin_stream');

  Future<String?> connectToDevice(dynamic device) {
    return AojDevicePluginPlatform.instance.connectToDevice(device);
  }

  /*Stream<dynamic?> getAOJDevicePluginStream() {
    return AojDevicePluginPlatform.instance.aojDevicePluginStream();
  }*/
  Stream<dynamic> get discovered_devices_stream {
    return _eventChannel.receiveBroadcastStream().where((event) {
      return event['type'] == 'discovered_device';
    });
  }

}
