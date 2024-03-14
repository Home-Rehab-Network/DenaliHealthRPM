package com.example.aoj_device_plugin;
import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.EventChannel.StreamHandler;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.aojmedical.plugin.ble.AHDevicePlugin;
import com.aojmedical.plugin.ble.OnSearchingListener;
import com.aojmedical.plugin.ble.data.BTDeviceInfo;
import com.aojmedical.plugin.ble.data.BTManagerStatus;
import com.aojmedical.plugin.ble.data.BTDeviceType;
import com.aojmedical.plugin.ble.data.BTScanFilter;
import com.aojmedical.plugin.ble.data.IDeviceData;
import com.aojmedical.plugin.ble.data.*;
import com.aojmedical.plugin.ble.OnSyncingListener;
import com.aojmedical.plugin.ble.OnSettingListener;
import com.aojmedical.plugin.ble.data.BTConnectState;
import com.aojmedical.plugin.ble.data.bpm.AHBpmConfig;
import com.aojmedical.plugin.ble.data.BTDeviceSyncSetting;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.util.Log;

/** AojDevicePlugin */
public class AojDevicePlugin implements FlutterPlugin, MethodCallHandler, StreamHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel methodChannel;
  private EventChannel eventChannel;
  private AojEventSink mEventSink;
  private static final int REQUEST_ENABLE_BT = 1;

  private Context ctx;

  List<BTDeviceInfo> discoveredDevices = new ArrayList<BTDeviceInfo>(); // mac

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {

    Context context = flutterPluginBinding.getApplicationContext();
    ctx = context;
    // 1. INITIALIZE
    AHDevicePlugin.getInstance().initPlugin(context);
    AHDevicePlugin.getInstance().registerReceiver(context);
    AHDevicePlugin.getInstance().openDebugMode("plugin.debug");
    Log.d("DEBUG", "AOJ SDK Version:" + AHDevicePlugin.getInstance().getVersion());

    // Method channel for Flutter
    methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "aoj_device_plugin");
    methodChannel.setMethodCallHandler(this);

    // Event channel for Flutter
    eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "aoj_device_plugin_stream");
    eventChannel.setStreamHandler(this);

    // 2. DEVICE SCAN
    BTManagerStatus sdkStatus = AHDevicePlugin.getInstance().getManagerStatus();
    if(sdkStatus == BTManagerStatus.Free){
      List<BTDeviceType> types=new ArrayList<BTDeviceType>();
      types.add(BTDeviceType.BloodPressureMeter);
      types.add(BTDeviceType.Oximeter);

      BTScanFilter filter = new BTScanFilter(types);
      AHDevicePlugin.getInstance().searchDevice(filter, new OnSearchingListener() {
        @Override
        public void onSearchResults(BTDeviceInfo device) {
          if(device.getMacAddress() == null || device.getDeviceName() == null) return;
            handleDiscoveredDevice(device);
        }
      });
    } else {
      Log.d("DEBUG", " BTManagerStatus.Free is not free");
    }
  }

  final OnSyncingListener onSyncingListener = new OnSyncingListener() {
    @Override
    public void onStateChanged(String broadcastId, BTConnectState state) {
      super.onStateChanged(broadcastId, state);
      Log.d("DEBUG", "CONNECT STATE CHANGED for device:  " + broadcastId + " onStateChanged cb: " + state.toString());

      /*if(state.toString() == "GattConnected") {
        Log.d("DEBUG", "REGISTER GattConnected.");
        AHDevicePlugin.getInstance().registerMessageService();
      }*/
    }
    @Override
    public void onDeviceDataUpdate(String mac, IDeviceData obj){
      Log.d("DEBUG", "Else " + obj.getMeasureTime()); // NEVER INVOKED!
      super.onDeviceDataUpdate(mac, obj);
    };
  };

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("connectToDevice")) {
      Map<?, ?> arg1 = call.argument("arg1");

      String deviceMac= (String) arg1.get("macAddress");
      String broadcastID= deviceMac.replace(":","");

      // 2. SET BLE DEVICE TO CONNECT TO
      BTDeviceInfo device = new BTDeviceInfo();
      device.setBroadcastID(deviceMac.replace(":",""));
      device.setMacAddress(deviceMac);
      device.setDeviceType(BTDeviceType.Oximeter.getValue());


      Log.d("DEBUG", "Adding device: " + deviceMac + " broadcastID: " + broadcastID);

      AHDevicePlugin.getInstance().stopSearch();
      AHDevicePlugin.getInstance().addDevice(device);

      BTManagerStatus sdkStatus = AHDevicePlugin.getInstance().getManagerStatus();

      if(sdkStatus == BTManagerStatus.Scanning){
        AHDevicePlugin.getInstance().stopSearch();
      }
      if(sdkStatus == BTManagerStatus.Syncing){
        return;
      }

      // 3. DEVICE CONNECT AND DATA SYNC
      if(sdkStatus == BTManagerStatus.Free){
        AHDevicePlugin.getInstance().startAutoConnect(onSyncingListener);
        Log.d("DEBUG", "CONNECT startAutoConnect: " + AHDevicePlugin.getInstance().checkConnectState(broadcastID));
      }

      result.success("Successfully connected to  " + arg1.toString());
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onListen(Object o, EventChannel.EventSink eventSink) {
    mEventSink = new AojEventSink(eventSink);
  }

  @Override
  public void onCancel(Object o) {
    mEventSink = null;
  }

  OnSettingListener onSettingListener = new OnSettingListener() {
    @Override
    public void onSuccess(String macAddress) {
      super.onSuccess(macAddress);
    }
    @Override
    public void onFailure(int errorCode) {
      super.onFailure(errorCode); }
  };

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    methodChannel.setMethodCallHandler(null);
    eventChannel.setStreamHandler(null);
    AHDevicePlugin.getInstance().unregisterReceiver();
    AHDevicePlugin.getInstance().clearScanCache();
    AHDevicePlugin.getInstance().stopAutoConnect();
  }

  // Check if the device is already discovered to avoid duplicates and send them to flutter side
  private void handleDiscoveredDevice(BTDeviceInfo device) {
    boolean deviceExists = false;
    for (BTDeviceInfo m : discoveredDevices) {
      if (m.getMacAddress().equals(device.getMacAddress())) {
        deviceExists = true;
        break;
      }
    }
    if (!deviceExists) {
      discoveredDevices.add(device);
      if(mEventSink == null) return;
      Log.d("DEBUG", "Sending discovered device " + device.getBroadcastID() + " to flutter side");
      mEventSink.success(serializeBTDeviceInfo(device));
    }
  }

  private Map<String, Object> serializeBTDeviceInfo(BTDeviceInfo object) {
    Map<String, Object> objectMap = new HashMap<>();
    objectMap.put("name", object.getDeviceName());
    objectMap.put("deviceId", object.getDeviceId());
    objectMap.put("macAddress", object.getMacAddress());
    objectMap.put("type", "discovered_device");
    return objectMap;
  }
}
