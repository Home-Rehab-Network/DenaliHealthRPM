import 'package:flutter_test/flutter_test.dart';
import 'package:aoj_device_plugin/aoj_device_plugin.dart';
import 'package:aoj_device_plugin/aoj_device_plugin_platform_interface.dart';
import 'package:aoj_device_plugin/aoj_device_plugin_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockAojDevicePluginPlatform
    with MockPlatformInterfaceMixin
    implements AojDevicePluginPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final AojDevicePluginPlatform initialPlatform = AojDevicePluginPlatform.instance;

  test('$MethodChannelAojDevicePlugin is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelAojDevicePlugin>());
  });

  test('getPlatformVersion', () async {
    AojDevicePlugin aojDevicePlugin = AojDevicePlugin();
    MockAojDevicePluginPlatform fakePlatform = MockAojDevicePluginPlatform();
    AojDevicePluginPlatform.instance = fakePlatform;

    expect(await aojDevicePlugin.getPlatformVersion(), '42');
  });
}
