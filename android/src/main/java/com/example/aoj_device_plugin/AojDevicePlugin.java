package com.example.aoj_device_plugin;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import java.util.HashMap;

import com.aojmedical.plugin.ble.AHDevicePlugin;
import com.aojmedical.plugin.ble.OnSearchingListener;
import com.aojmedical.plugin.ble.data.BTDeviceInfo;

/** AojDevicePlugin */
public class AojDevicePlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "aoj_device_plugin");
    channel.setMethodCallHandler(this);

    AHDevicePlugin.getInstance().initPlugin(flutterPluginBinding.getApplicationContext());

    OnSearchingListener listener = new OnSearchingListener() {
      @Override
      public void onSearchResults(BTDeviceInfo device) {
        HashMap<String, Object> deviceMap = new HashMap<>();
        deviceMap.put("name", device.getDeviceName());

        // Send data to Flutter
        channel.invokeMethod("onBluetoothDeviceFound", deviceMap);
      }
    };
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
