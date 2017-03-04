package in.truskills.liveexams.authentication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.Miscellaneous.CheckForPermissions;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.R;
import in.truskills.liveexams.Miscellaneous.TermsAndConditions;
import io.fabric.sdk.android.Fabric;

//This activity includes the code for Signup and Login

public class Signup_Login extends AppCompatActivity implements View.OnClickListener {


    private static final String TWITTER_KEY = "fIx7W5i8xo9stQ8jhOHVLNdFB";
    private static final String TWITTER_SECRET = "JQ9IuPXWecyeMFK8VujoYePRHHfQllXNRvRYC6QatmCNt8l5FH";

    //Public declaration of variables

    AuthCallback authCallback;
    LinearLayout locationField;
    Button loginPressed, registerHandleButton, loginHandleButton;
    Button signupPressed;
    SlidingDrawer signupDrawer, loginDrawer;
    RelativeLayout signupLayout;
    EditText loginName, loginPassword, signupName, signupEmail, signupPassword, signupConfirmPassword, signupMobile;
    TextView termsAndConditionsPressed, forgotPasswordPressed, signupLocation, sentence,successfullRegister,signupLanguageAlternate;
    Spinner signupLanguage, signupGender;
    LocationManager locationManager;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String lat, lon, selectedLanguage, selectedGender, name, gender, email, password, confirmPassword, location, mobile, language, login_name, login_password;
    int code = 123;
    ImageView signupHandleImage, loginHandleImage;
    RequestQueue requestQueue;
    SharedPreferences prefs;
    ImageView app_logo;
    ArrayList<String> listOfLanguages;
    Animation slide_down;
    Drawable dr;
    ProgressDialog dialog,progress;

    //Called when the activity is created..
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the layout of this activity
        setContentView(R.layout.activity_signup__login);

        //Shared Preferences for user's selected language
        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        SharedPreferences.Editor e=prefs.edit();
        e.putInt("signup",0);
        e.apply();

        //Initialise the request for Volley connection
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);


        //Attach all the variables used in layout
        loginPressed = (Button) findViewById(R.id.loginPressed);
        signupPressed = (Button) findViewById(R.id.signupPressed);
        registerHandleButton = (Button) findViewById(R.id.registerHandleButton);
        loginHandleButton = (Button) findViewById(R.id.loginHandleButton);
        forgotPasswordPressed = (TextView) findViewById(R.id.forgotPasswordPressed);
        signupLanguageAlternate = (TextView) findViewById(R.id.signupLanguageAlternate);
        termsAndConditionsPressed = (TextView) findViewById(R.id.termsAndConditionsPressed);
        signupLocation = (TextView) findViewById(R.id.signupLocation);
        sentence = (TextView) findViewById(R.id.sentence);
        successfullRegister = (TextView) findViewById(R.id.successfullRegister);
        signupDrawer = (SlidingDrawer) findViewById(R.id.signupDrawer);
        loginDrawer = (SlidingDrawer) findViewById(R.id.loginDrawer);
        signupLayout = (RelativeLayout) findViewById(R.id.signupLayout);
        loginName = (EditText) findViewById(R.id.loginName);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        signupName = (EditText) findViewById(R.id.signupName);
        signupEmail = (EditText) findViewById(R.id.signupEmail);
        signupPassword = (EditText) findViewById(R.id.signupPassword);
        signupConfirmPassword = (EditText) findViewById(R.id.signupConfirmPassword);
        signupMobile = (EditText) findViewById(R.id.signupMobile);
        signupHandleImage = (ImageView) findViewById(R.id.signupHandleImage);
        loginHandleImage = (ImageView) findViewById(R.id.loginHandleImage);
        signupLanguage = (Spinner) findViewById(R.id.signupLanguage);
        signupGender = (Spinner) findViewById(R.id.signupGender);
        app_logo = (ImageView) findViewById(R.id.app_logo);
        locationField=(LinearLayout)findViewById(R.id.locationField);

        final Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        registerHandleButton.setTypeface(tff1);
        loginHandleButton.setTypeface(tff1);

        listOfLanguages=new ArrayList<>();
        dr = getResources().getDrawable(R.drawable.required_icon);
        //add an error icon to yur drawable files
        dr.setBounds(0, 0, 50, 50);

        Typeface tff2 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        loginName.setTypeface(tff2);
        loginPassword.setTypeface(tff2);
        forgotPasswordPressed.setTypeface(tff2);
        loginPressed.setTypeface(tff2);
        signupPressed.setTypeface(tff2);
        signupName.setTypeface(tff2);
        signupMobile.setTypeface(tff2);
        signupEmail.setTypeface(tff2);
        signupPassword.setTypeface(tff2);
        signupConfirmPassword.setTypeface(tff2);
        signupLocation.setTypeface(tff2);
        termsAndConditionsPressed.setTypeface(tff2);
        sentence.setTypeface(tff2);
        signupLanguageAlternate.setTypeface(tff2);


        //Set OnClickListener on all buttons used
        loginPressed.setOnClickListener(this);
        signupPressed.setOnClickListener(this);
        forgotPasswordPressed.setOnClickListener(this);
        termsAndConditionsPressed.setOnClickListener(this);
        locationField.setOnClickListener(this);

        signupLanguageAlternate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Api to be connected to..
                String url = ConstantsDefined.api+"beforeSignup";

                dialog = new ProgressDialog(Signup_Login.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Fetching languages.. Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();

                //Make a request..
                StringRequest stringRequest = new StringRequest(Request.Method.GET,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Parse the signup response..

                            Log.d("myResponse=",response);
                            dialog.dismiss();
                            signupLanguageAlternate.setVisibility(View.GONE);
                            signupLanguage.setVisibility(View.VISIBLE);
                            listOfLanguages= MiscellaneousParser.beforeSignupParser(response);
                            CustomSpinnerAdapter customAdapter=new CustomSpinnerAdapter(getApplicationContext(),listOfLanguages);
                            signupLanguage.setAdapter(customAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //If connection could not be made..
                        dialog.dismiss();
                        Toast.makeText(Signup_Login.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(stringRequest);
            }
        });

        //List of Gender
        ArrayList<String> listOfGender = new ArrayList<>();
        listOfGender.add("GENDER");
        listOfGender.add("Male");
        listOfGender.add("Female");

        CustomSpinnerAdapter customAdapter=new CustomSpinnerAdapter(getApplicationContext(),listOfGender);
        signupGender.setAdapter(customAdapter);

//        //Set Adapter for Gender Spinner
//        ArrayAdapter<String> adapterGender = new ArrayAdapter<String>(this, R.layout.spinner_item, listOfGender);
//        signupGender.setAdapter(adapterGender);

        signupLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLanguage = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Handles the operation of changing the gender selection..
        signupGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGender = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //When the user opens the Signup drawer..
        signupDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {

                SharedPreferences.Editor e=prefs.edit();
                e.putInt("signup",0);
                e.apply();

                //Hide the login drawer..
                loginDrawer.setVisibility(View.GONE);
                app_logo.setVisibility(View.GONE);
                signupLanguageAlternate.setVisibility(View.VISIBLE);
                signupLanguage.setVisibility(View.GONE);

                //Change the arrow button: from up to down, for the user to know that the drawer can be closed on sliding down..
                signupHandleImage.setImageResource(R.drawable.down_arrow);

                //Set the validation message for rest of the fields as null..
                signupEmail.setError(null);
                signupMobile.setError(null);
                signupPassword.setError(null);
                signupConfirmPassword.setError(null);

                //Clear the previous content of all the fields..
                signupName.setText("");
                signupEmail.setText("");
                signupMobile.setText("");
                signupPassword.setText("");
                signupLocation.setText("LOCATION");
                signupConfirmPassword.setText("");
                signupGender.setSelection(0);
//                signupLanguage.setSelection(0);

                //Add the text change listener on the required fields to show the validation error message to the user at appropriate times..
                //signupName: userName field cannot be empty..
                //signupEmail: email should be a valid one..
                //signupMobile: number should be a valid 10 digit number..
                //signupPassword: password should be of minimum 6 characters..
                //signupConfirmPassword: confirm Password should be same as Password..

                //These messages occur while the user is typing in the field..

                signupName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        int l = signupName.getText().toString().length();
                        if (l < 1) {
                            signupName.setError("Required",dr);
                        } else {
                            signupName.setError(null);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                signupEmail.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String text = signupEmail.getText().toString();
                        if (!TextUtils.isEmpty(text) && android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                            signupEmail.setError(null);
                        } else {
                            signupEmail.setError("Enter valid Email",dr);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                signupMobile.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        int length = signupMobile.getText().toString().length();
                        if (length != 10)
                            signupMobile.setError("Enter valid phone number",dr);
                        else
                            signupMobile.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                signupPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        int length = signupPassword.getText().toString().length();
                        if (length < 6) {
                            signupPassword.setError("Minimum 6 characters required",dr);
                        } else {
                            signupPassword.setError(null);
                            String confirm = signupConfirmPassword.getText().toString();
                            if (confirm.equals(signupPassword.getText().toString()))
                                signupConfirmPassword.setError(null);
                            else
                                signupConfirmPassword.setError("Do not match with Password",dr);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                signupConfirmPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String text = signupConfirmPassword.getText().toString();
                        String passText = signupPassword.getText().toString();
                        if (!text.equals(passText))
                            signupConfirmPassword.setError("Do not match with Password",dr);
                        else
                            signupConfirmPassword.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        });

        //When the user closes the signup drawer..
        signupDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {

                //Show the login drawer..
                loginDrawer.setVisibility(View.VISIBLE);
                app_logo.setVisibility(View.VISIBLE);


                //Rechange the arrow button: from down to up again..
                signupHandleImage.setImageResource(R.drawable.up_arrow);
            }
        });

        //When the user opens the login drawer..
        loginDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {

                if(prefs.getInt("signup",0)==1){
                    successfullRegister.setVisibility(View.VISIBLE);
                    successfullRegister.startAnimation(slide_down);
                    successfullRegister.setTypeface(tff1);
                    SharedPreferences.Editor e=prefs.edit();
                    e.putInt("signup",0);
                    e.apply();
                }else{
                    successfullRegister.setVisibility(View.GONE);
                }

                //Hide the signup drawer..
                signupDrawer.setVisibility(View.GONE);
                app_logo.setVisibility(View.GONE);
                signupLayout.setBackgroundColor(Color.parseColor("#E0E0E0"));

                //Change the arrow button: from up to down, for the user to know that the drawer can be closed on sliding down..
                loginHandleImage.setImageResource(R.drawable.down_arrow);

                //Clear the previous content of both the fields..
                loginName.setText("");
                loginPassword.setText("");

                //Add text change listener on both the fields to show appropriate validation error messages..
                //loginName: userName field cannot be empty..
                //loginPassword: password cannot be empty..

                //These messages occur while the user is typing in the field..

                loginName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        int l = loginName.getText().toString().length();
                        if (l < 1) {
                            loginName.setError("Required",dr);
                        } else {
                            loginName.setError(null);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                loginPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        int l = loginPassword.getText().toString().length();
                        if (l < 1) {
                            loginPassword.setError("Required",dr);
                        } else {
                            loginPassword.setError(null);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        });

        //When the user closes the login drawer..
        loginDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {

                //Show the signup drawer..
                signupDrawer.setVisibility(View.VISIBLE);
                app_logo.setVisibility(View.VISIBLE);
                signupLayout.setBackgroundColor(Color.parseColor("#0C1D36"));

                //Rechange the arrow button: from down to up again..
                loginHandleImage.setImageResource(R.drawable.up_arrow);
            }
        });
    }

    //Called when a button in the activity screen is pressed..
    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {

            //If login button is pressed..Check for login validation..
            case R.id.loginPressed:
                Answers.getInstance().logCustom(new CustomEvent("Login button pressed"));
                loginValidation();
                break;

            //If signup button is pressed..Check for signup validation..
            case R.id.signupPressed:
                Answers.getInstance().logCustom(new CustomEvent("Signup button pressed"));
                signupValidation();
                break;

            //If forgot password button is pressed..Launch the ForgotPassword activity..
            case R.id.forgotPasswordPressed:
                i = new Intent(Signup_Login.this, ForgotPassword.class);
                startActivity(i);
                break;

            //If terms and condition button is pressed..Launch the  TermsAndConditions activity..
            case R.id.termsAndConditionsPressed:
                i = new Intent(Signup_Login.this, TermsAndConditions.class);
                startActivity(i);
                break;

            //If location icon is pressed..Call the getCurrentLocation method..
            case R.id.locationField:
                getCurrentLocation();
                break;
        }
    }

    //Called when the back button of the phone is pressed..
    @Override
    public void onBackPressed() {

        //If any drawer whether signup or login is opened, then close it.. otherwise exit the application..

        if (signupDrawer.isOpened())
            signupDrawer.close();
        else if (loginDrawer.isOpened())
            loginDrawer.close();
        else super.onBackPressed();
    }

    //This method includes code for calculating user's current location based on GPS tracking..
    public void getCurrentLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean result = CheckForPermissions.checkForLocation(Signup_Login.this);
        if (result) {
            //Check if the device's GPS is on or not..
            if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                //If it is off, ask the user to enable it..
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("NETWORK PROVIDER NOT ENABLED");  // GPS not found
                builder.setMessage("Want to enable?"); // Want to enable?
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Launch the gps settings screen of the phone..
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        //Wait for the result to come in return..
                        //After returning from the setting screen to our signup_login screen.. check for the activity result by calling onActivityResult method..
                        startActivityForResult(intent, code);
                    }
                });
                builder.setNegativeButton("NO", null);
                builder.create().show();
            } else {
                fetchLocation();
            }
        }
    }

    public void fetchLocation() {
        progress = new ProgressDialog(Signup_Login.this);
        progress.setMessage("Fetching your current location....");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                lat = location.getLatitude() + "";
                lon = location.getLongitude() + "";
                Log.d("addresses",lat+" "+lon);
                Geocoder geocoder = new Geocoder(Signup_Login.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Log.d("addresses",addresses+"");
                } catch (IOException e) {
                    e.printStackTrace();
                    if (!((Activity) Signup_Login.this).isFinishing()) {
                        if (progress.isShowing())
                            progress.dismiss();
                        Toast.makeText(Signup_Login.this, "Sorry!! No internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
                if (addresses != null) {
                    if (progress.isShowing())
                        progress.dismiss();
                    String ans = addresses.get(0).getAddressLine(0);
                    signupLocation.setText(addresses.get(0).getAddressLine(2)+"");
                } else {
                    Toast.makeText(Signup_Login.this, "Sorry!! No internet connection", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    //This method is for validating the user's entered signup info before it is given for registering..
    public void signupValidation() {

        //Get the value of all the fields..
        name = signupName.getText().toString();
        gender = selectedGender;
        email = signupEmail.getText().toString();
        mobile = signupMobile.getText().toString();
        password = signupPassword.getText().toString();
        confirmPassword = signupConfirmPassword.getText().toString();
        location = signupLocation.getText().toString();
        language = selectedLanguage;

        //If all valid.. signup..
        if (!name.equals("") && !gender.equals("GENDER") && !location.equals("LOCATION") && signupLanguageAlternate.getVisibility()==View.GONE && mobile.length() == 10 && password.length() >= 6 && password.equals(confirmPassword) && (!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            signupFunction();
        } else {
            //Else display desired error messages..
            if (name.equals(""))
                signupName.setError("Required",dr);
            if (!(!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()))
                signupEmail.setError("Enter valid email",dr);
            if (gender.equals("GENDER"))
                Toast.makeText(this, "Choose your gender", Toast.LENGTH_SHORT).show();
            if (location.equals("LOCATION"))
                Toast.makeText(this, "Choose your location", Toast.LENGTH_SHORT).show();
            if (signupLanguageAlternate.getVisibility()==View.VISIBLE)
                Toast.makeText(this, "Choose your language", Toast.LENGTH_SHORT).show();
            if (mobile.length() != 10)
                signupMobile.setError("Enter valid phone number",dr);
            if (password.length() < 6)
                signupPassword.setError("Minimum 6 characters required",dr);
            if (!password.equals(confirmPassword))
                signupConfirmPassword.setError("Do not match with Password",dr);
        }
    }

    //This method is for validating the user's entered login info before it is given for logging in..
    public void loginValidation() {
        login_name = loginName.getText().toString();
        login_password=loginPassword.getText().toString();
        if(!login_name.equals("")&&!login_password.equals(""))
            loginFunction();
        else{
            if(login_name.equals(""))
                loginName.setError("Required",dr);
            if(login_password.equals(""))
                loginPassword.setError("Required",dr);
        }
    }

    //This method is for signing up i.e calling signup api..
    public void signupFunction() {

        boolean result=CheckForPermissions.checkForSms(Signup_Login.this);
        if(result){
            getVerified();
        }
    }

    public void getVerified(){
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());
        authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // Do something with the session
                Toast.makeText(Signup_Login.this, "Phone Number Verified Successfully..", Toast.LENGTH_SHORT).show();

                //Api to be connected to..
                String url = ConstantsDefined.api+"signup";

                dialog = new ProgressDialog(Signup_Login.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Signing up. Please wait...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();

                //Make a request..
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Parse the signup response..

                            Log.d("myResponse=",response);
                            dialog.dismiss();

                            HashMap<String ,String> mapper= MiscellaneousParser.signupParser(response);
//                            Toast.makeText(Signup_Login.this, mapper.get("response"), Toast.LENGTH_SHORT).show();
                            Log.d("response",mapper.get("response"));
                            if(mapper.get("success").equals("true")){
//                                Toast.makeText(Signup_Login.this, "Signup Successfull", Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor e=prefs.edit();
                                e.putInt("signup",1);
                                e.apply();
                                Answers.getInstance().logCustom(new CustomEvent("Signup successfull")
                                        .putCustomAttribute("userName",name));
                                signupDrawer.close();
                                loginDrawer.open();
                            }else{
                                JSONObject jo=new JSONObject(mapper.get("response"));
                                String errmsg=jo.getString("errmsg");
                                Toast.makeText(Signup_Login.this, "Couldn't Signup: "+errmsg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //If connection could not be made..
                        dialog.dismiss();
                        Toast.makeText(Signup_Login.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String,String> getParams(){

                        //Attach parameters required..
                        Map<String,String> params = new HashMap<String, String>();

                        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                R.drawable.camera);

                        String defaultImage=BitmapToString(icon);

                        SharedPreferences prefs=getSharedPreferences("prefs",Context.MODE_PRIVATE);
                        SharedPreferences.Editor e=prefs.edit();
                        e.putString("navImage",defaultImage);
                        e.apply();
                        params.put("userName",name);
                        params.put("gender",gender);
                        params.put("password",password);
                        params.put("emailAddress",email);
                        params.put("mobileNumber",mobile);
                        params.put("language",language);
                        params.put("latitude",lat);
                        params.put("longitude",lon);
//                params.put("profileImageUrl",defaultImage);
                        return params;
                    }
                };
                requestQueue.add(stringRequest);

            }

            @Override
            public void failure(DigitsException exception) {
                // Do something on failure
                Toast.makeText(Signup_Login.this, "failure", Toast.LENGTH_SHORT).show();

            }
        };

        getAuthCallback();

        Digits.clearActiveSession();

        AuthConfig.Builder authConfigBuilder = new AuthConfig.Builder()
                .withAuthCallBack(authCallback)
                .withPhoneNumber("+91"+mobile);

        Digits.authenticate(authConfigBuilder.build());
    }

    //This method is for logging up i.e calling login api..
    public void loginFunction() {

        //Api to be connected to..
        String url = ConstantsDefined.api+"login";

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Logging in. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        //Make a request..
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //On getting the response..
                Intent i = null;
                try {
                    //Parse the login response..
                    HashMap<String ,String> mapper= MiscellaneousParser.loginParser(response);
                    dialog.dismiss();
                    //If successfull signup.. save the desired info in shared preferences..
                    if(mapper.get("success").equals("true")) {

                        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                R.drawable.ic_add_a_photo_white_24dp);

                        String defaultImage = BitmapToString(icon);

                        SharedPreferences.Editor e=prefs.edit();
                        e.putString("userId",mapper.get("id"));
                        e.putString("userName",mapper.get("userName"));
                        e.putString("emailAddress",mapper.get("emailAddress"));
                        e.putString("language",mapper.get("language"));
                        e.putString("profileImageUrl",mapper.get("profileImageUrl"));
                        e.putString("joinedExams",mapper.get("joinedExams"));
                        e.putString("login","true");
                        e.putString("navImage",defaultImage);
                        e.apply();
                        Answers.getInstance().logCustom(new CustomEvent("Login successfull")
                        .putCustomAttribute("userName",mapper.get("userName")));
                        Toast.makeText(Signup_Login.this, "Welcome to Live Exams", Toast.LENGTH_SHORT).show();
                        i = new Intent(Signup_Login.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }else{
                        //Display error message..
                        Toast.makeText(Signup_Login.this, mapper.get("response"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //In case the connection to the Api couldn't be established..
                dialog.dismiss();
                Log.d("error",error.toString()+"");
                Toast.makeText(Signup_Login.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Put all the required parameters for the post request..
                Map<String,String> params = new HashMap<String, String>();
                params.put("userName",loginName.getText().toString());
                params.put("password",loginPassword.getText().toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    //This method is use whenever a bitmap is to be converted into string..
    public String BitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] b=baos.toByteArray();
        String temp= Base64.encodeToString(b,Base64.DEFAULT);
        return temp;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CheckForPermissions.LOCATION_PERMISSION_CODE:
                //If permission is granted
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Check if the device's GPS is on or not..
                    if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                        //If it is off, ask the user to enable it..
                        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("NETWORK PROVIDER NOT ENABLED");  // GPS not found
                        builder.setMessage("Want to enable?"); // Want to enable?
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Launch the gps settings screen of the phone..
                                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                //Wait for the result to come in return..
                                //After returning from the setting screen to our signup_login screen.. check for the activity result by calling onActivityResult method..
                                startActivityForResult(intent, code);
                            }
                        });
                        builder.setNegativeButton("NO", null);
                        builder.create().show();
                    } else {
                        fetchLocation();
                    }
                }else{
                    //Displaying another toast if permission is not granted
                    Toast.makeText(this,"Oops you have denied the permission for location\nGo to settings and grant them", Toast.LENGTH_LONG).show();
                }
                break;
            case CheckForPermissions.SMS_PERMISSION_CODE:
                //If permission is granted
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else{
                    //Displaying another toast if permission is not granted
                    Toast.makeText(this,"Oops you have denied the permission for sms\nGo to settings and grant them to automatic read OTP", Toast.LENGTH_LONG).show();
                }
                getVerified();
                break;
        }
    }

    //This method is called after the return to this activity occurs from the device's gps settings screen..
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //If the request code is same..
        if (requestCode == code) {
//            //If gps is on..
//            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//
//                //Check for explicit permissions in android versions starting from Marshmallow..
//                if (ActivityCompat.checkSelfPermission(Signup_Login.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Signup_Login.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                    //If permission is not granted..Request for permission..and check for user's response in onRequestPermissions method..
//                    ActivityCompat.requestPermissions(Signup_Login.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
//                } else {
//
//                    //If permissions are granted already, fetch the current location..
//                    final ProgressDialog progress = new ProgressDialog(Signup_Login.this);
//                    progress.setMessage("Fetching your current location....");
//                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                    progress.setIndeterminate(true);
//                    progress.setCancelable(false);
//                    progress.show();
//                    LocationListener locationListener = new LocationListener() {
//                        @Override
//                        public void onLocationChanged(Location location) {
//
//                            lat = location.getLatitude() + "";
//                            lon = location.getLongitude() + "";
//                            Geocoder geocoder = new Geocoder(Signup_Login.this, Locale.getDefault());
//                            List<Address> addresses = null;
//                            try {
//                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            if (addresses != null) {
//                                if (progress.isShowing())
//                                    progress.dismiss();
//                                String ans = addresses.get(0).getAddressLine(0);
//                                signupLocation.setText(addresses.get(0).getAddressLine(2));
//                            } else {
//                                Toast.makeText(Signup_Login.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onStatusChanged(String s, int i, Bundle bundle) {
//
//                        }
//
//                        @Override
//                        public void onProviderEnabled(String s) {
//
//                        }
//
//                        @Override
//                        public void onProviderDisabled(String s) {
//
//                        }
//                    };
//                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//                }
//            }
            fetchLocation();
        }
    }

    public AuthCallback getAuthCallback(){
        return authCallback;
    }
}
