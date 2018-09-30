import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutrans/flutrans.dart';

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool isMakePayment = false;
  final flutrans = Flutrans();
  @override
  void initState() {
    super.initState();
    flutrans.init("YOUR_CLIENT_ID", "YOUR_URL_BASE");
    flutrans.setFinishCallback(_callback);
  }

  _makePayment() {
    setState(() {
      isMakePayment = true;
    });
    flutrans
        .makePayment(
          MidtransTransaction(
              7500,
              MidtransCustomer(
                  "Apin", "Prastya", "apin.klas@gmail.com", "085235419949"),
              [
                MidtransItem(
                  "5c18ea1256f67560cb6a00cdde3c3c7a81026c29",
                  7500,
                  2,
                  "USB FlashDisk",
                )
              ],
              skipCustomer: true,
              customField1: "ANYCUSTOMFIELD"),
        )
        .catchError((err) => print("ERROR $err"));
  }

  Future<void> _callback(TransactionFinished finished) async {
    setState(() {
      isMakePayment = false;
    });
    return Future.value(null);
  }

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: const Text('Plugin example app'),
        ),
        body: new Center(
          child: isMakePayment
              ? CircularProgressIndicator()
              : RaisedButton(
                  child: Text("Make Payment"),
                  onPressed: () => _makePayment(),
                ),
        ),
      ),
    );
  }
}
