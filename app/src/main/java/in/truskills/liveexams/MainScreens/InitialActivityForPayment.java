package in.truskills.liveexams.MainScreens;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.R;

public class InitialActivityForPayment extends AppCompatActivity {

    TextView accessCodeText,accessCode,merchantIdText,merchantId,currencyText,currency,amountText,customerIdentifierText;
    EditText customerIdentifier,amount;
    Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_for_payment);

        accessCodeText=(TextView)findViewById(R.id.accessCodeText);
        accessCode=(TextView)findViewById(R.id.accessCode);
        merchantId=(TextView)findViewById(R.id.merchantId);
        merchantIdText=(TextView)findViewById(R.id.merchantIdText);
        currencyText=(TextView)findViewById(R.id.currencyText);
        currency=(TextView)findViewById(R.id.currency);
        amountText=(TextView)findViewById(R.id.amountText);
        customerIdentifierText=(TextView)findViewById(R.id.customerIdentifierText);
        customerIdentifier=(EditText)findViewById(R.id.customerIdentifier);
        amount=(EditText)findViewById(R.id.amount);
        nextButton=(Button)findViewById(R.id.nextButton);

        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        accessCode.setTypeface(tff1);
        merchantId.setTypeface(tff1);
        currency.setTypeface(tff1);
        amount.setTypeface(tff1);
        customerIdentifier.setTypeface(tff1);
        Typeface tff2 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        accessCodeText.setTypeface(tff2);
        merchantIdText.setTypeface(tff2);
        currencyText.setTypeface(tff2);
        amountText.setTypeface(tff2);
        customerIdentifierText.setTypeface(tff2);
        nextButton.setTypeface(tff2);

        accessCode.setText(ConstantsDefined.accessCode);
        merchantId.setText(ConstantsDefined.merchantId);
        amount.setText("1.00");
        customerIdentifier.setText("customerIdentifier");

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String vAmount = amount.getText().toString().trim();
                if(!vAmount.equals("")){
                    Intent intent = new Intent(InitialActivityForPayment.this,BillingShippingActivity.class);
                    intent.putExtra("amount", amount.getText()).toString().trim();
                    intent.putExtra("customerIdentifier",customerIdentifier.getText()).toString().trim();
                    startActivity(intent);
                }else{
                    Toast.makeText(InitialActivityForPayment.this, "Please fill a valid amount", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
