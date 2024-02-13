
import 'aoj_device_plugin_platform_interface.dart';

class AojDevicePlugin {
  Future<String?> getPlatformVersion() {
    return AojDevicePluginPlatform.instance.getPlatformVersion();
  }
}
