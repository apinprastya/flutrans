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

  Future<void> init(String clientId, String url,
      {String env = 'production'}) async {
    await _channel.invokeMethod("init", {
      "client_key": clientId,
      "base_url": url,
      "env": env,
    });
    return Future.value(null);
  }

  Future<void> makePayment(MidtransTransaction transaction) async {
    /*int total = 0;
    transaction.items.forEach((v) => total += (v.price * v.quantity));
    if (total != transaction.total)
      throw "Transaction total and items total not equal";*/
    await _channel.invokeMethod("payment", jsonEncode(transaction.toJson()));
    return Future.value(null);
  }

  Future<void> makeDirectPaymentWithToken(
      int method, String token, bool skipCustomer) async {
    await _channel.invokeMethod("directpaymentwithtoken",
        {"method": method, "token": token, "skipCustomer": skipCustomer});
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
  MidtransTransaction(
    this.total,
    this.customer,
    this.items, {
    this.customField1,
    this.skipCustomer = false,
  });
  Map<String, dynamic> toJson() {
    return {
      "total": total,
      "skip_customer": skipCustomer,
      "items": items.map((v) => v.toJson()).toList(),
      "customer": customer.toJson(),
      "custom_field_1": customField1,
    };
  }
}

class TransactionFinished {
  final bool transactionCanceled;
  final String status;
  final String source;
  final String statusMessage;
  final String response;
  TransactionFinished(
    this.transactionCanceled,
    this.status,
    this.source,
    this.statusMessage,
    this.response,
  );
}

class PaymentMethod {
  static int CREDIT_CARD = 0;
  static int BANK_TRANSFER = 1;
  static int BANK_TRANSFER_BCA = 2;
  static int BANK_TRANSFER_MANDIRI = 3;
  static int BANK_TRANSFER_PERMATA = 4;
  static int BANK_TRANSFER_BNI = 5;
  static int BANK_TRANSFER_OTHER = 6;
  static int GO_PAY = 7;
  static int BCA_KLIKPAY = 8;
  static int KLIKBCA = 9;
  static int MANDIRI_CLICKPAY = 10;
  static int MANDIRI_ECASH = 11;
  static int EPAY_BRI = 12;
  static int CIMB_CLICKS = 13;
  static int INDOMARET = 14;
  static int KIOSON = 15;
  static int GIFT_CARD_INDONESIA = 16;
  static int INDOSAT_DOMPETKU = 17;
  static int TELKOMSEL_CASH = 18;
  static int XL_TUNAI = 19;
  static int DANAMON_ONLINE = 20;
  static int AKULAKU = 21;
  static int ALFAMART = 22;
}
