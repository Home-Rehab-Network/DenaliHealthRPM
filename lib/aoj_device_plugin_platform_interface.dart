import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'aoj_device_plugin_method_channel.dart';

abstract class AojDevicePluginPlatform extends PlatformInterface {
  /// Constructs a AojDevicePluginPlatform.
  AojDevicePluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static AojDevicePluginPlatform _instance = MethodChannelAojDevicePlugin();

  /// The default instance of [AojDevicePluginPlatform] to use.
  ///
  /// Defaults to [MethodChannelAojDevicePlugin].
  static AojDevicePluginPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [AojDevicePluginPlatform] when
  /// they register themselves.
  static set instance(AojDevicePluginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<String?> connectToDevice(dynamic device) {
    return AojDevicePluginPlatform.instance.connectToDevice(device);
  }

  Stream<dynamic> aojDevicePluginStream() {
    throw UnimplementedError('aojDevicePluginStream() has not been implemented.');
  }
}
