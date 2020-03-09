package com.lekapin.flutrans.flutrans;

import android.content.Context;
import android.util.Log;

import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.UIKitCustomSetting;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutransPlugin
 */
public class FlutransPlugin implements MethodCallHandler, TransactionFinishedCallback {
    private static final String TAG = "FlutransPlugin";
    private final MethodChannel channel;
    private Context context;

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutrans");
        channel.setMethodCallHandler(new FlutransPlugin(registrar, channel));
    }

    private FlutransPlugin(Registrar registrar, MethodChannel channel) {
        this.channel = channel;
        this.context = registrar.activeContext();
    }

    @Override
    public void onMethodCall(
            MethodCall call,
            @SuppressWarnings("NullableProblems") Result result
    ) {
        if (call.method.equals("init")) {
            //noinspection ConstantConditions
            initMidtransSdk(
                    call.argument("client_key").toString(),
                    call.argument("base_url").toString()
            );
        } else if (call.method.equals("payment")) {
            String paymentParams = call.arguments();
            payment(paymentParams);
        } else {
            result.notImplemented();
        }
    }

    private void initMidtransSdk(String client_key, String base_url) {
        SdkUIFlowBuilder.init()
                .setClientKey(client_key) // client_key is mandatory
                .setContext(context) // context is mandatory
                .setTransactionFinishedCallback(this) // set transaction finish callback (sdk callback)
                .setMerchantBaseUrl(base_url) //set merchant url
                .enableLog(true) // enable sdk log
                .buildSDK();
    }

    private void payment(String paymentParams) {
        try {
            Log.d(TAG, paymentParams);

            JSONObject mPaymentParams = new JSONObject(paymentParams);
            JSONObject customerData = mPaymentParams.getJSONObject("customer");

            TransactionRequest transactionRequest = new TransactionRequest(
                    System.currentTimeMillis() + "",
                    mPaymentParams.getInt("total")
            );

            ArrayList<ItemDetails> itemList = new ArrayList<>();
            JSONArray itemData = mPaymentParams.getJSONArray("items");
            for (int i = 0; i < itemData.length(); i++) {
                JSONObject mItemData = itemData.getJSONObject(i);
                ItemDetails item = new ItemDetails(mItemData.getString("id"),
                        mItemData.getInt("price"),
                        mItemData.getInt("quantity"),
                        mItemData.getString("name")
                );
                itemList.add(item);
            }
            transactionRequest.setItemDetails(itemList);

            CustomerDetails customerDetails = new CustomerDetails();
            customerDetails.setFirstName(customerData.getString("first_name"));
            customerDetails.setLastName(customerData.getString("last_name"));
            customerDetails.setEmail(customerData.getString("email"));
            customerDetails.setPhone(customerData.getString("phone"));
            transactionRequest.setCustomerDetails(customerDetails);

            if (mPaymentParams.has("custom_field_1")) {
                transactionRequest.setCustomField1(mPaymentParams.getString("custom_field_1"));
            }
            if (mPaymentParams.has("custom_field_2")) {
                transactionRequest.setCustomField2(mPaymentParams.getString("custom_field_2"));
            }
            if (mPaymentParams.has("custom_field_3")) {
                transactionRequest.setCustomField3(mPaymentParams.getString("custom_field_3"));
            }

            UIKitCustomSetting uiKitCustomSetting = MidtransSDK.getInstance().getUIKitCustomSetting();
            if (mPaymentParams.has("skip_customer")) {
                uiKitCustomSetting.setSkipCustomerDetailsPages(mPaymentParams.getBoolean("skip_customer"));
            }
            MidtransSDK.getInstance().setUIKitCustomSetting(uiKitCustomSetting);

            MidtransSDK.getInstance().setTransactionRequest(transactionRequest);

            MidtransSDK.getInstance().startPaymentUiFlow(context);
        } catch (Exception e) {
            Log.d(TAG, "ERROR " + e.getMessage());
        }
    }

    @Override
    public void onTransactionFinished(TransactionResult transactionResult) {
        Map<String, Object> transactionResultData = new HashMap<>();
        transactionResultData.put("transactionCanceled", transactionResult.isTransactionCanceled());
        transactionResultData.put("status", transactionResult.getStatus());
        transactionResultData.put("source", transactionResult.getSource());
        transactionResultData.put("statusMessage", transactionResult.getStatusMessage());

        if (transactionResult.getResponse() != null) {
            Map<String, Object> transactionResultResponseData = new HashMap<>();
            transactionResultResponseData.put("alfamartExpireTime", transactionResult.getResponse().getAlfamartExpireTime());
            transactionResultResponseData.put("approvalCode", transactionResult.getResponse().getApprovalCode());
            transactionResultResponseData.put("bank", transactionResult.getResponse().getBank());
            transactionResultResponseData.put("bcaExpiration", transactionResult.getResponse().getBcaExpiration());
            transactionResultResponseData.put("bcaKlikBcaExpiration", transactionResult.getResponse().getBcaKlikBcaExpiration());
            transactionResultResponseData.put("bcaVaNumber", transactionResult.getResponse().getBcaVaNumber());
            transactionResultResponseData.put("bniExpiration", transactionResult.getResponse().getBniExpiration());
            transactionResultResponseData.put("bniVaNumber", transactionResult.getResponse().getBniVaNumber());
            transactionResultResponseData.put("companyCode", transactionResult.getResponse().getCompanyCode());
            transactionResultResponseData.put("currency", transactionResult.getResponse().getCurrency());
            transactionResultResponseData.put("deeplinkUrl", transactionResult.getResponse().getDeeplinkUrl());
            transactionResultResponseData.put("eci", transactionResult.getResponse().getEci());
            transactionResultResponseData.put("finishRedirectUrl", transactionResult.getResponse().getFinishRedirectUrl());
            transactionResultResponseData.put("fraudStatus", transactionResult.getResponse().getFraudStatus());
            transactionResultResponseData.put("gopayExpiration", transactionResult.getResponse().getGopayExpiration());
            transactionResultResponseData.put("gopayExpirationRaw", transactionResult.getResponse().getGopayExpirationRaw());
            transactionResultResponseData.put("grossAmount", transactionResult.getResponse().getGrossAmount());
            transactionResultResponseData.put("indomaretExpireTime", transactionResult.getResponse().getIndomaretExpireTime());
            transactionResultResponseData.put("installmentTerm", transactionResult.getResponse().getInstallmentTerm());
            transactionResultResponseData.put("kiosonExpireTime", transactionResult.getResponse().getKiosonExpireTime());
            transactionResultResponseData.put("mandiriBillExpiration", transactionResult.getResponse().getMandiriBillExpiration());
            transactionResultResponseData.put("maskedCard", transactionResult.getResponse().getMaskedCard());
            transactionResultResponseData.put("orderId", transactionResult.getResponse().getOrderId());
            transactionResultResponseData.put("paymentCode", transactionResult.getResponse().getPaymentCode());
            transactionResultResponseData.put("paymentCodeResponse", transactionResult.getResponse().getPaymentCodeResponse());
            transactionResultResponseData.put("paymentType", transactionResult.getResponse().getPaymentType());
            transactionResultResponseData.put("pdfUrl", transactionResult.getResponse().getPdfUrl());
            transactionResultResponseData.put("permataVaNumber", transactionResult.getResponse().getPermataVANumber());
            transactionResultResponseData.put("permataExpiration", transactionResult.getResponse().getPermataExpiration());
            transactionResultResponseData.put("pointBalance", transactionResult.getResponse().getPointBalance());
            transactionResultResponseData.put("pointBalanceAmount", transactionResult.getResponse().getPointBalanceAmount());
            transactionResultResponseData.put("pointRedeemAmount", transactionResult.getResponse().getPointRedeemAmount());
            transactionResultResponseData.put("qrCodeUrl", transactionResult.getResponse().getQrCodeUrl());
            transactionResultResponseData.put("redirectUrl", transactionResult.getResponse().getRedirectUrl());
            transactionResultResponseData.put("savedTokenId", transactionResult.getResponse().getSavedTokenId());
            transactionResultResponseData.put("savedTokenIdExpiredAt", transactionResult.getResponse().getSavedTokenIdExpiredAt());
            transactionResultResponseData.put("secureToken", transactionResult.getResponse().isSecureToken());
            transactionResultResponseData.put("statusCode", transactionResult.getResponse().getStatusCode());
            transactionResultResponseData.put("statusMessage", transactionResult.getResponse().getStatusMessage());
            transactionResultResponseData.put("transactionId", transactionResult.getResponse().getTransactionId());
            transactionResultResponseData.put("transactionStatus", transactionResult.getResponse().getTransactionStatus());
            transactionResultResponseData.put("transactionTime", transactionResult.getResponse().getTransactionTime());
            transactionResultResponseData.put("xlTunaiExpiration", transactionResult.getResponse().getXlTunaiExpiration());
            transactionResultResponseData.put("xlTunaiMerchantId", transactionResult.getResponse().getXlTunaiMerchantId());
            transactionResultResponseData.put("xlTunaiOrderId", transactionResult.getResponse().getXlTunaiOrderId());
            if (transactionResult.getResponse().getAccountNumbers() != null) {
                Map<String, String> transactionResultResponseAccountNumbersData = new HashMap<>();
                for (int i = 0; i < transactionResult.getResponse().getAccountNumbers().size(); i++) {
                    transactionResultResponseAccountNumbersData.put("bank", transactionResult.getResponse().getAccountNumbers().get(i).getBank());
                    transactionResultResponseAccountNumbersData.put("number", transactionResult.getResponse().getAccountNumbers().get(i).getAccountNumber());
                }
                transactionResultResponseData.put("accountNumbers", transactionResultResponseAccountNumbersData);
            }
            if (transactionResult.getResponse().getValidationMessages() != null) {
                Map<String, String> transactionResultResponseValidationMessagesData = new HashMap<>();
                for (int i = 0; i < transactionResult.getResponse().getValidationMessages().size(); i++) {
                    transactionResultResponseValidationMessagesData.put("message", transactionResult.getResponse().getValidationMessages().get(i));
                }
                transactionResultResponseData.put("validationMessages", transactionResultResponseValidationMessagesData);
            }

            transactionResultData.put("response", transactionResultResponseData);
        } else {
            //noinspection ConstantConditions
            transactionResultData.put("response", null);
        }

        channel.invokeMethod("onTransactionFinished", transactionResultData);
    }
}
