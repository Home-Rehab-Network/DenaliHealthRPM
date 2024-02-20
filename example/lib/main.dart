import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:aoj_device_plugin/aoj_device_plugin.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _aojDevicePlugin = AojDevicePlugin();
  List<dynamic> _devices = [];

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion =
          await _aojDevicePlugin.getPlatformVersion() ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    _listenToDevices();
    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  void _listenToDevices() {
    _aojDevicePlugin.discovered_devices_stream.listen((event) {
      setState(() {
        _devices.add(event);
      });
    });
  }

  void _onDeviceTap(dynamic device) async {
    try {
      var result =
          await _aojDevicePlugin.connectToDevice(device) ?? 'Unknown platform version';
      print(result);
    } on PlatformException {
      print("Error");
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          //child: Text('Running on: $_platformVersion\n'),
          child: _devices.isNotEmpty
              ? ListView.builder(
            itemCount: _devices.length,
            itemBuilder: (context, index) {
              return ListTile(
                title: Text(_devices[index]["name"]),
                onTap: () => _onDeviceTap(_devices[index]),
              );
            },
          )
              : Center(child: Text('No devices found')),
        ),
      ),
    );
  }
}
