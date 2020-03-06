import 'dart:async';
import 'dart:convert';
import 'package:flutter/services.dart';

typedef Future<void> MidtransCallback(TransactionFinished transactionFinished);

class Flutrans {
  MidtransCallback finishCallback;
  static Flutrans _instance = Flutrans._internal();
  static const MethodChannel _channel = const MethodChannel('flutrans');

  Flutrans._internal() {
    _channel.setMethodCallHandler(_channelHandler);
  }

  factory Flutrans() {
    return _instance;
  }

  Future<dynamic> _channelHandler(MethodCall methodCall) async {
    if (methodCall.method == "onTransactionFinished") {
      if (finishCallback != null) {
        await finishCallback(TransactionFinished(
          methodCall.arguments['transactionCanceled'],
          methodCall.arguments['status'],
          methodCall.arguments['source'],
          methodCall.arguments['statusMessage'],
          methodCall.arguments['response'],
        ));
      }
    }
    return Future.value(null);
  }

  void setFinishCallback(MidtransCallback callback) {
    finishCallback = callback;
  }

  Future<void> init(String clientId, String url) async {
    await _channel
        .invokeMethod("init", {"client_key": clientId, "base_url": url});
    return Future.value(null);
  }

  Future<void> makePayment(MidtransTransaction transaction) async {
    await _channel.invokeMethod("payment", jsonEncode(transaction.toJson()));
    return Future.value(null);
  }
}

class MidtransCustomer {
  final String firstName;
  final String lastName;
  final String email;
  final String phone;

  MidtransCustomer(this.firstName, this.lastName, this.email, this.phone);

  MidtransCustomer.fromJson(Map<String, dynamic> json)
      : firstName = json["first_name"],
        lastName = json["last_name"],
        email = json["email"],
        phone = json["phone"];

  Map<String, dynamic> toJson() {
    return {
      "first_name": firstName,
      "last_name": lastName,
      "email": email,
      "phone": phone,
    };
  }
}

class MidtransItem {
  final String id;
  final int price;
  final int quantity;
  final String name;

  MidtransItem(this.id, this.price, this.quantity, this.name);

  MidtransItem.fromJson(Map<String, dynamic> json)
      : id = json["id"],
        price = json["price"],
        quantity = json["quantity"],
        name = json["name"];

  Map<String, dynamic> toJson() {
    return {
      "id": id,
      "price": price,
      "quantity": quantity,
      "name": name,
    };
  }
}

class MidtransTransaction {
  final int total;
  final MidtransCustomer customer;
  final List<MidtransItem> items;
  final bool skipCustomer;
  final String customField1;
  final String customField2;
  final String customField3;

  MidtransTransaction(
    this.total,
    this.customer,
    this.items, {
    this.skipCustomer = false,
    this.customField1,
    this.customField2,
    this.customField3,
  });

  Map<String, dynamic> toJson() {
    return {
      "total": total,
      "customer": customer,
      "items": items.map((v) => v.toJson()).toList(),
      "skip_customer": skipCustomer,
      "custom_field_1": customField1,
      "custom_field_2": customField2,
      "custom_field_3": customField3,
    };
  }
}

class TransactionFinished {
  final bool transactionCanceled;
  final String status;
  final String source;
  final String statusMessage;
  final Map<dynamic, dynamic> response;

  TransactionFinished(this.transactionCanceled, this.status, this.source,
      this.statusMessage, this.response);
}
