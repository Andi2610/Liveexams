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
import android.util.Log;
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
import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.Miscellaneous.CheckForPermissions;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.R;
import io.fabric.sdk.android.Fabric;

//This Activity launches in case the user forgets his/her login password..

public class ForgotPassword extends AppCompatActivity {


    private static final String TWITTER_KEY = "fIx7W5i8xo9stQ8jhOHVLNdFB";
    private static final String TWITTER_SECRET = "JQ9IuPXWecyeMFK8VujoYePRHHfQllXNRvRYC6QatmCNt8l5FH";

    Button reset;
    String text;
    EditText newPassword;
    ProgressDialog dialog;
    RequestQueue requestQueue;
    TextView heading;

    //Public declaration of variables

    Handler h;
    AuthCallback authCallback;
    RelativeLayout containerForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);

        getSupportActionBar().setTitle("FORGOT PASSWORD");

        reset = (Button) findViewById(R.id.reset);
        newPassword = (EditText) findViewById(R.id.newPassword);
        containerForgotPassword = (RelativeLayout) findViewById(R.id.containerForgotPassword);
        heading = (TextView) findViewById(R.id.heading);
        containerForgotPassword.setVisibility(View.INVISIBLE);

        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        newPassword.setTypeface(tff1);
        heading.setTypeface(tff1);
        Typeface tff2 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        reset.setTypeface(tff2);

        h = new Handler();

        boolean result = CheckForPermissions.checkForSms(ForgotPassword.this);
        if (result) {
            getVerified();
        }
    }

    public AuthCallback getAuthCallback() {
        return authCallback;
    }

    public void successMethod(final String phoneNumber) {

        Toast.makeText(ForgotPassword.this, "Phone Number Verified Successfully..", Toast.LENGTH_SHORT).show();
        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int length = newPassword.getText().toString().length();
                if (length < 6) {
                    newPassword.setError("Minimum 6 characters required");
                } else {
                    newPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = newPassword.getText().toString();
                if (text.length() < 6) {
                    newPassword.setError("Minimum 6 characters required");
                } else {
                    requestQueue = Volley.newRequestQueue(getApplicationContext());

                    //Call change password api..
                    //Api to be connected to..
                    String url = ConstantsDefined.api + "changePassword";

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
                            if(dialog!=null)
                                dialog.dismiss();
                            //On getting the response..
                            try {
                                //Parse the login response..
                                HashMap<String, String> mapper = MiscellaneousParser.changePasswordParser(response);

                                //If successfull signup.. save the desired info in shared preferences..
                                if (mapper.get("success").equals("true")) {
                                    Toast.makeText(ForgotPassword.this, mapper.get("message"), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //In case the connection to the Api couldn't be established..
                            if(dialog!=null)
                                dialog.dismiss();
                            Toast.makeText(ForgotPassword.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            //Put all the required parameters for the post request..
                            Map<String, String> params = new HashMap<String, String>();
                            Log.d("phone", phoneNumber + "");
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

                } else {
                    //Displaying another toast if permission is not granted
                    Toast.makeText(this, "Oops you have denied the permission for sms\nGo to settings and grant them to automatic read OTP", Toast.LENGTH_LONG).show();
                }
                getVerified();
                break;
        }
    }

    public void getVerified() {

        Toast.makeText(this, "Enter your registered mobile number", Toast.LENGTH_LONG).show();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new TwitterCore(authConfig), new Digits.Builder().build());
        authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, final String phoneNumber) {
                // Do something with the session
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
                // Do something on failure
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
        super.onBackPressed();
        return true;
    }
}
