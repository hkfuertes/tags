import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import '../settings/settings_view.dart';

import 'package:flutter_nfc_kit/flutter_nfc_kit.dart';
import 'package:ndef/ndef.dart' as ndef;

class MainScreen extends StatefulWidget {
  const MainScreen({Key? key}) : super(key: key);

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  String? _id;
  int _timer = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(
        title: Text("NFC HA Tags"),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
              // Navigate to the settings page. If the user leaves and returns
              // to the app after it has been killed while running in the
              // background, the navigation stack is restored.
              Navigator.restorablePushNamed(context, SettingsView.routeName);
            },
          ),
        ],
        //backgroundColor: Colors.transparent,
        //elevation: 0,
      ),
      body: Column(
        children: [
          Expanded(
            child: Center(
              child: Text(_id ?? "<NFC_ID>"),
            ),
          ),
          Row(
            children: [
              Expanded(
                child: TextButton.icon(
                    onPressed: (_timer > 0) ? null : readTag,
                    icon: Icon(Icons.nfc),
                    label: (_timer > 0)
                        ? Text("Leyendo por " + _timer.toString() + "s.")
                        : Text("Leer")),
              ),
            ],
          )
        ],
      ),
    );
  }

  void readTag() async {
    var availability = await FlutterNfcKit.nfcAvailability;
    if (availability != NFCAvailability.available) {
      // oh-no
    }

    var scanDurationInSecs = 10;

    var tag = await FlutterNfcKit.poll(
        timeout: Duration(seconds: scanDurationInSecs),
        iosMultipleTagMessage: "Multiple tags found!",
        iosAlertMessage: "Scan your tag");

    _timer = scanDurationInSecs;
    Timer.periodic(Duration(seconds: scanDurationInSecs), (_) {
      setState(() => _timer--);
    });

    setState(() {
      _id = tag.id;
    });
  }
}
