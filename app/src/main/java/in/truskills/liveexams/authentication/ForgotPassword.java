package in.truskills.liveexams.authentication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.R;
import io.fabric.sdk.android.Fabric;

//This Activity launches in case the user forgets his/her login password..

public class ForgotPassword extends AppCompatActivity {


    private static final String TWITTER_KEY = "fIx7W5i8xo9stQ8jhOHVLNdFB";
    private static final String TWITTER_SECRET = "JQ9IuPXWecyeMFK8VujoYePRHHfQllXNRvRYC6QatmCNt8l5FH";

    Button reset;
    String text;
    EditText newPassword;
    RequestQueue requestQueue;

    //Public declaration of variables

    AuthCallback authCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        reset=(Button)findViewById(R.id.reset);
        newPassword=(EditText)findViewById(R.id.newPassword);

        TwitterAuthConfig authConfig =  new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());
        authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, final String phoneNumber) {
                // Do something with the session
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
                        text=newPassword.getText().toString();
                        if(text.length()<6){
                            newPassword.setError("Minimum 6 characters required");
                        }else{
                            requestQueue = Volley.newRequestQueue(getApplicationContext());

                            //Call change password api..
                            //Api to be connected to..
                            String url = VariablesDefined.api+"changePassword";

                            //Make a request..
                            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                    url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //On getting the response..
                                    Intent i = null;
                                    try {
                                        //Parse the login response..
                                        HashMap<String ,String> mapper=VariablesDefined.changePasswordParser(response);
                                        //If successfull signup.. save the desired info in shared preferences..
                                        if(mapper.get("success").equals("true")) {
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
                                    Toast.makeText(ForgotPassword.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {

                                    //Put all the required parameters for the post request..
                                    Map<String,String> params = new HashMap<String, String>();
                                    params.put("mobileNumber",phoneNumber);
                                    params.put("newPassword",text);
                                    return params;
                                }
                            };
                            requestQueue.add(stringRequest);
                        }
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
        getAuthCallback();

        AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                .withAuthCallBack(authCallback)
                .withPhoneNumber("+91");

        Digits.authenticate(authConfigBuilder.build());
    }

    public AuthCallback getAuthCallback(){
        return authCallback;
    }
}
