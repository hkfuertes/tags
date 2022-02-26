import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:nfchatags/src/ha_conector.dart';
import './settings/settings_view.dart';

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
  HAConnector _connector =
      HAConnector(ip: "10.9.8.254", port: "8123", pat: "", name: "Madrid");

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
        body: ListView(
          children: [
            ListTile(
              trailing: Icon(Icons.home),
              title: Text(_connector.name),
              subtitle: Text(_connector.ip),
            ),
            ListTile(
              trailing: Icon(Icons.nfc),
              title: Text("Leer"),
              subtitle:
                  Text((_id != null) ? "Ultima: " + _id! : "Leer Etiqueta NFC"),
              onTap: () async {
                showDialog(
                    context: context, builder: (_) => _createReadDialog());
                var id = await readTag(nonfccallback: () {
                  Navigator.of(context).pop();
                });
                Navigator.of(context).pop();
                setState(() {
                  _id = id;
                });
              },
            ),
            if (_id != null)
              ListTile(
                onTap: () async {
                  if (_id != null) await _connector.tagRead(_id!);
                },
                trailing: Icon(Icons.send),
                title: Text("Enviar"),
                subtitle: Text(_id!),
              )
          ],
        ));
  }

  AlertDialog _createReadDialog() {
    var dialog = AlertDialog(
      content: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Center(
              child: Icon(
            Icons.nfc,
            size: 128,
          )),
        ],
      ),
    );

    return dialog;
  }

  Future<String?> readTag({nonfccallback}) async {
    var availability = await FlutterNfcKit.nfcAvailability;
    if (availability != NFCAvailability.available) {
      if (nonfccallback != null) nonfccallback();
    }

    var scanDurationInSecs = 10;

    try {
      var tag = await FlutterNfcKit.poll(
          timeout: Duration(seconds: scanDurationInSecs),
          iosMultipleTagMessage: "Multiple tags found!",
          iosAlertMessage: "Scan your tag");

      await FlutterNfcKit.finish();
      return tag.id;
    } catch (e) {
      await FlutterNfcKit.finish();
      return null;
    }
  }
}
