package in.truskills.liveexams.MainScreens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import in.truskills.liveexams.R;
import in.truskills.liveexams.dto.CardTypeDTO;
import in.truskills.liveexams.dto.EMIOptionDTO;
import in.truskills.liveexams.dto.PaymentOptionDTO;

public class BillingShippingActivity extends AppCompatActivity {

    Intent initialScreen;

    Map<String,ArrayList<CardTypeDTO>> cardsList = new LinkedHashMap<String,ArrayList<CardTypeDTO>>();
    ArrayList<PaymentOptionDTO> payOptionList = new ArrayList<PaymentOptionDTO>();
    ArrayList<EMIOptionDTO> emiOptionList = new ArrayList<EMIOptionDTO>();

    private ProgressDialog pDialog;

    String selectedPaymentOption;
    CardTypeDTO selectedCardType;

    private EditText billingName, billingAddress,billingCountry, billingState, billingCity, billingZip, billingTel, billingEmail,
            deliveryName, deliveryAddress, deliveryCountry, deliveryState, deliveryCity, deliveryZip,
            deliveryTel, redirectUrl, cancelUrl, cardNumber, cardCvv, expiryMonth, expiryYear, issuingBank,
            rsaKeyUrl,vCardCVV;

    private CheckBox saveCard;

    private Map<String,String> paymentOptions = new LinkedHashMap<String,String>();

    private TextView orderId;

    private JSONObject jsonRespObj;

    private String emiPlanId, emiTenureId, amount, currency, cardName, allowedBins;

    int counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_shipping);
    }
}
