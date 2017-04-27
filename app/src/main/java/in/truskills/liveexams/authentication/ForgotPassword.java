package in.truskills.liveexams.authentication;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;
import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.Miscellaneous.CheckForPermissions;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.R;

/**
 * This Activity launches in case the user forgets his/her login password..
 * Functions used:
 * 1. onCreate() : check for sms permissions.. If not present, call onRequestPermissionsResult().. else call getVerified() method.
 * 2. onRequestPermissionsResult() : to check for permissions of sms and call method getVerified() whether permissions present or not..
 * 3. getAuthCallback() : to return authCallback instance..
 * 4. getVerified() : checks and verify phone number through DIGITS implementation..
 * 5. successMethod() : called when phone number is successfully verified; it calls changePassword api to reset password..
 * 6. onSupportNavigationUp() : handles the back button press on toolbar of the app..
 */

public class ForgotPassword extends AppCompatActivity {

    //Variables declared..
    Button reset;
    String text;
    EditText newPassword;
    ProgressDialog dialog;
    RequestQueue requestQueue;
    TextView heading;
    Handler h;
    AuthCallback authCallback;
    RelativeLayout containerForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Render layout file..
        setContentView(R.layout.activity_forgot_password);

        //Set toolbar with custom back button and title..
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);
        getSupportActionBar().setTitle("FORGOT PASSWORD");

        //Render all elements from layout..
        reset = (Button) findViewById(R.id.reset);
        newPassword = (EditText) findViewById(R.id.newPassword);
        containerForgotPassword = (RelativeLayout) findViewById(R.id.containerForgotPassword);
        heading = (TextView) findViewById(R.id.heading);
        containerForgotPassword.setVisibility(View.INVISIBLE);

        //Set typeface of required texts in layout..
        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        newPassword.setTypeface(tff1);
        heading.setTypeface(tff1);
        Typeface tff2 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        reset.setTypeface(tff2);

        //Initialise rest of the variables..
        h = new Handler();

        //Check for SMS permissions.. If not present, call onRequestPermissionsResult().. else call getVerified() method.
        boolean result = CheckForPermissions.checkForSms(ForgotPassword.this);
        if (result) {
            getVerified();
        }
    }

    public AuthCallback getAuthCallback() {
        return authCallback;
    }

    public void successMethod(final String phoneNumber) {

        //Display success toast message..
        Toast.makeText(ForgotPassword.this, "Phone Number Verified Successfully..", Toast.LENGTH_SHORT).show();

        //Check if new entered password (while typing) is of minimum 6 characters or not..
        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //New password should be min of 6 chars..
                int length = newPassword.getText().toString().length();
                if (length < 6) {

                    //If length<6, display error message..
                    newPassword.setError("Minimum 6 characters required");
                } else {

                    //Else erase the error message..
                    newPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //When reset password is pressed..
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get new password and call changePassword api to change it for the current user..
                text = newPassword.getText().toString();

                //If length<6, show error message..
                if (text.length() < 6) {
                    newPassword.setError("Minimum 6 characters required");
                } else {
                    //Else call changePassword api..

                    //Required for https connection..
                    ConstantsDefined.updateAndroidSecurityProvider(ForgotPassword.this);
                    ConstantsDefined.beforeVolleyConnect();

                    //Initialise requestQueue instance and url to be connected to..
                    requestQueue = Volley.newRequestQueue(getApplicationContext());
                    String url = ConstantsDefined.api + "changePassword";

                    //While api is being connected to, display apppropriate dialog box..
                    dialog = new ProgressDialog(ForgotPassword.this);
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("Changing your password. Please wait...");
                    dialog.setIndeterminate(true);
                    dialog.setCancelable(false);
                    dialog.show();

                    //Make a request..
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            //Dismiss dialog box on successful response..
                            if (dialog != null)
                                dialog.dismiss();

                            //On getting the response..
                            try {

                                //Parse the login response..
                                HashMap<String, String> mapper = MiscellaneousParser.changePasswordParser(response);

                                //Finish activity with desired toast message (whether success or failure)..
                                if (mapper.get("success").equals("true")) {
                                    Toast.makeText(ForgotPassword.this, mapper.get("message"), Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(ForgotPassword.this, "Something went wrong..\nPlease try again..", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            //In case the connection to the Api couldn't be established..

                            //Dismiss the dialog box..
                            if (dialog != null)
                                dialog.dismiss();

                            //Display appropriate toast message depending upon internet connectivity was a reason for failure or something else..
                            if(ConstantsDefined.isOnline(ForgotPassword.this)){
                                Toast.makeText(ForgotPassword.this, "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(ForgotPassword.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            //Put all the required parameters for the post request..
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("mobileNumber", phoneNumber.substring(3));
                            params.put("newPassword", text);
                            return params;
                        }
                    };
                    requestQueue.add(stringRequest);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CheckForPermissions.SMS_PERMISSION_CODE:
                //If permission is granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Do nothing..
                } else {
                    //Displaying another toast if permission is not granted
                    Toast.makeText(this, "Oops you have denied the permission for sms\nGo to settings and grant them to automatic read OTP", Toast.LENGTH_LONG).show();
                }
                //Call getVerified method always irrespective of permissions present or not..
                getVerified();
                break;
        }
    }

    public void getVerified() {

        //Display desired toast to let user know that only registered mobile number should be entered..
        Toast.makeText(this, "Enter your registered mobile number", Toast.LENGTH_LONG).show();

        authCallback = new AuthCallback() {

            @Override
            public void success(DigitsSession session, final String phoneNumber) {

                //In case phone number gets verified.. call successMethod() function..
                h.post(new Runnable() {
                    @Override
                    public void run() {

                        successMethod(phoneNumber);
                        containerForgotPassword.setVisibility(View.VISIBLE);

                    }
                });
            }

            @Override
            public void failure(DigitsException exception) {
                //In case phone number isn't verified, finish() this activity displaying appropriate toast..
                Toast.makeText(ForgotPassword.this, "Couldn't verify phone number", Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        Digits.clearActiveSession();
        getAuthCallback();
        AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                .withAuthCallBack(authCallback)
                .withPhoneNumber("+91");
        Digits.authenticate(authConfigBuilder.build());
    }

    @Override
    public boolean onSupportNavigateUp() {
        //Go back..i.e. backstack activity.. i.e. Signup_Login activity..
        super.onBackPressed();
        return true;
    }
}
