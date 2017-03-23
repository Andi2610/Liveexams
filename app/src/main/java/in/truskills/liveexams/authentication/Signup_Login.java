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
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.DragEvent;
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

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.security.ProviderInstaller;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.Miscellaneous.CheckForPermissions;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.R;
import in.truskills.liveexams.Miscellaneous.TermsAndConditions;
import io.fabric.sdk.android.Fabric;

//This activity includes the code for Signup and Login

public class Signup_Login extends AppCompatActivity implements View.OnClickListener, com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


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
    TextView termsAndConditionsPressed, forgotPasswordPressed, signupLocation, sentence, successfullRegister, signupLanguageAlternate;
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
    ProgressDialog dialog;
    String accessKeyId="AKIAIJUGKGFIXTWNTTQA",
            secretAccessKey= "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";
    AWSCredentials credentials;
    AmazonS3 s3client;

    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;


    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;

    //Called when the activity is created..
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the layout of this activity
        setContentView(R.layout.activity_signup__login);

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();

        ConstantsDefined.updateAndroidSecurityProvider(this);
        ConstantsDefined.beforeVolleyConnect();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //Shared Preferences for user's selected language
        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        SharedPreferences.Editor e = prefs.edit();
        e.putInt("signup", 0);
        e.apply();

        //Initialise the request for Volley connection
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
        locationField = (LinearLayout) findViewById(R.id.locationField);

        final Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        registerHandleButton.setTypeface(tff1);
        loginHandleButton.setTypeface(tff1);

        listOfLanguages = new ArrayList<>();
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

                ConstantsDefined.updateAndroidSecurityProvider(Signup_Login.this);
                ConstantsDefined.beforeVolleyConnect();

                //Api to be connected to..
                String url = ConstantsDefined.api + "beforeSignup";

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
                        if (dialog != null)
                            dialog.dismiss();
                        try {
                            //Parse the signup response..

                            signupLanguageAlternate.setVisibility(View.GONE);
                            signupLanguage.setVisibility(View.VISIBLE);
                            listOfLanguages = MiscellaneousParser.beforeSignupParser(response);
                            CustomSpinnerAdapter customAdapter = new CustomSpinnerAdapter(getApplicationContext(), listOfLanguages);
                            signupLanguage.setAdapter(customAdapter);
                            signupLanguage.performClick();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //If connection could not be made..
                        Log.d("checkForError", error.toString());
                        if (dialog != null)
                            dialog.dismiss();
                        if (ConstantsDefined.isOnline(Signup_Login.this)) {
                            //Do nothing..
                            Toast.makeText(Signup_Login.this, "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Signup_Login.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                        }
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

        CustomSpinnerAdapter customAdapter = new CustomSpinnerAdapter(getApplicationContext(), listOfGender);
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

                SharedPreferences.Editor e = prefs.edit();
                e.putInt("signup", 0);
                e.apply();

                locationField.setEnabled(true);


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
                        if (l < 2) {
                            signupName.setError("Required", dr);
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
                        if (!TextUtils.isEmpty(text) && Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                            signupEmail.setError(null);
                        } else {
                            signupEmail.setError("Enter valid Email", dr);
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
                            signupMobile.setError("Enter valid phone number", dr);
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
                            signupPassword.setError("Minimum 6 characters required", dr);
                        } else {
                            signupPassword.setError(null);
                            String confirm = signupConfirmPassword.getText().toString();
                            if (confirm.equals(signupPassword.getText().toString()))
                                signupConfirmPassword.setError(null);
                            else
                                signupConfirmPassword.setError("Do not match with Password", dr);
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
                            signupConfirmPassword.setError("Do not match with Password", dr);
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
            }
        });

        signupDrawer.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                Toast.makeText(Signup_Login.this, "dragged", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //When the user opens the login drawer..
        loginDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {

                if (prefs.getInt("signup", 0) == 1) {
                    successfullRegister.setVisibility(View.VISIBLE);
                    successfullRegister.startAnimation(slide_down);
                    successfullRegister.setTypeface(tff1);
                    SharedPreferences.Editor e = prefs.edit();
                    e.putInt("signup", 0);
                    e.apply();
                } else {
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
                            loginName.setError("Required", dr);
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
                            loginPassword.setError("Required", dr);
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

                loginName.setText("");
                loginPassword.setText("");

            }
        });


    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Signup_Login.this, R.style.AppCompatAlertDialogStyle);
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
//                fetchLocation();
                mGoogleApiClient.connect();
                locationField.setEnabled(false);
                updateUI();
            }
        }
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
        if (!name.equals("") && !gender.equals("GENDER") && !location.equals("LOCATION") && signupLanguageAlternate.getVisibility() == View.GONE && mobile.length() == 10 && password.length() >= 6 && password.equals(confirmPassword) && (!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            signupFunction();
        } else {
            //Else display desired error messages..
            if (name.equals(""))
                signupName.setError("Required", dr);
            if (!(!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()))
                signupEmail.setError("Enter valid email", dr);
            if (gender.equals("GENDER"))
                Toast.makeText(this, "Choose your gender", Toast.LENGTH_SHORT).show();
            if (location.equals("LOCATION"))
                Toast.makeText(this, "Choose your location", Toast.LENGTH_SHORT).show();
            if (signupLanguageAlternate.getVisibility() == View.VISIBLE)
                Toast.makeText(this, "Choose your language", Toast.LENGTH_SHORT).show();
            if (mobile.length() != 10)
                signupMobile.setError("Enter valid phone number", dr);
            if (password.length() < 6)
                signupPassword.setError("Minimum 6 characters required", dr);
            if (!password.equals(confirmPassword))
                signupConfirmPassword.setError("Do not match with Password", dr);
        }
    }

    //This method is for validating the user's entered login info before it is given for logging in..
    public void loginValidation() {
        login_name = loginName.getText().toString();
        login_password = loginPassword.getText().toString();
        if (!login_name.equals("") && !login_password.equals("")){
            boolean checkForWrite=CheckForPermissions.checkForWriteStorage(this);
            if(checkForWrite)
                loginFunction();
        }
        else {
            if (login_name.equals(""))
                loginName.setError("Required", dr);
            if (login_password.equals(""))
                loginPassword.setError("Required", dr);
        }
    }

    //This method is for signing up i.e calling signup api..
    public void signupFunction() {

        boolean result = CheckForPermissions.checkForSms(Signup_Login.this);
        if (result) {
            getVerified();
        }
    }

    public void getVerified() {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());
        authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {

                mobile = phoneNumber.substring(3);

                Log.d("phone", "success: "+mobile);

                // Do something with the session
                Toast.makeText(Signup_Login.this, "Phone Number Verified Successfully..", Toast.LENGTH_SHORT).show();

                ConstantsDefined.updateAndroidSecurityProvider(Signup_Login.this);
                ConstantsDefined.beforeVolleyConnect();

                //Api to be connected to..
                String url = ConstantsDefined.api + "signup";

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
                        if (dialog != null)
                            dialog.dismiss();
                        try {
                            //Parse the signup response..

                            Log.d("myResponse=", response);

                            HashMap<String, String> mapper = MiscellaneousParser.signupParser(response);
                            if (mapper.get("success").equals("true")) {
                                SharedPreferences.Editor e = prefs.edit();
                                e.putInt("signup", 1);
                                e.apply();
                                Answers.getInstance().logCustom(new CustomEvent("Signup successfull")
                                        .putCustomAttribute("userName", name));
                                signupDrawer.close();
                                loginDrawer.open();
                            } else {
//                                JSONObject jo = new JSONObject(mapper.get("response"));
//                                String errmsg = jo.getString("errmsg");
                                String errmsg=mapper.get("response");
                                Toast.makeText(Signup_Login.this, "Couldn't Signup: " + errmsg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //If connection could not be made..
                        if (dialog != null)
                            dialog.dismiss();
                        if (ConstantsDefined.isOnline(Signup_Login.this)) {
                            //Do nothing..
                            Toast.makeText(Signup_Login.this, "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Signup_Login.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {

                        //Attach parameters required..
                        Map<String, String> params = new HashMap<String, String>();

//                        Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                                R.drawable.camera);
//
//                        String defaultImage = BitmapToString(icon);

//                        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor e = prefs.edit();
//                        e.putString("navImage", defaultImage);
//                        e.apply();
                        params.put("userName", name);
                        params.put("gender", gender);
                        params.put("password", password);
                        params.put("emailAddress", email);
                        params.put("mobileNumber", mobile);
                        params.put("language", language);
                        params.put("latitude", lat);
                        params.put("longitude", lon);
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
                .withPhoneNumber("+91" + mobile);

        Digits.authenticate(authConfigBuilder.build());
    }

    //This method is for logging up i.e calling login api..
    public void loginFunction() {

        ConstantsDefined.updateAndroidSecurityProvider(Signup_Login.this);
        ConstantsDefined.beforeVolleyConnect();

        //Api to be connected to..
        String url = ConstantsDefined.api + "login";

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
                if (dialog != null)
                    dialog.dismiss();
                try {
                    //Parse the login response..
                    HashMap<String, String> mapper = MiscellaneousParser.loginParser(response);
                    //If successfull signup.. save the desired info in shared preferences..
                    if (mapper.get("success").equals("true")) {

                        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                R.drawable.camera);

                        File f=savebitmap(icon);

                        String myPath=f.getPath();

                        SharedPreferences.Editor ee = prefs.edit();
                        ee.putString("navImage", myPath);
                        ee.apply();

                        SharedPreferences.Editor e = prefs.edit();
                        e.putString("userId", mapper.get("id"));
                        e.putString("userName", mapper.get("userName"));
                        e.putString("emailAddress", mapper.get("emailAddress"));
                        e.putString("language", mapper.get("language"));
                        e.putString("profileImageUrl", mapper.get("profileImageUrl"));
                        e.putString("joinedExams", mapper.get("joinedExams"));
                        e.putString("login", "true");
                        e.putString("navImage", myPath);
                        e.apply();
                        Answers.getInstance().logCustom(new CustomEvent("Login successfull")
                                .putCustomAttribute("userName", mapper.get("userName")));

                        getProfileImage(mapper.get("profileImageUrl"), mapper.get("id"));

                    } else {
                        //Display error message..
                        Toast.makeText(Signup_Login.this, mapper.get("response"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //In case the connection to the Api couldn't be established..
                if (dialog != null)
                    dialog.dismiss();
                if (ConstantsDefined.isOnline(Signup_Login.this)) {
                    //Do nothing..
                    Toast.makeText(Signup_Login.this, "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Signup_Login.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Put all the required parameters for the post request..
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName", loginName.getText().toString());
                params.put("password", loginPassword.getText().toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CheckForPermissions.LOCATION_PERMISSION_CODE:
                //If permission is granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Check if the device's GPS is on or not..
                    if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                        //If it is off, ask the user to enable it..
                        AlertDialog.Builder builder = new AlertDialog.Builder(Signup_Login.this, R.style.AppCompatAlertDialogStyle);
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
//                        fetchLocation();
                        mGoogleApiClient.connect();
                        locationField.setEnabled(false);
                        updateUI();
                    }
                } else {
                    //Displaying another toast if permission is not granted
                    Toast.makeText(this, "Oops you have denied the permission for location\nGo to settings and grant them", Toast.LENGTH_LONG).show();
                }
                break;
            case CheckForPermissions.SMS_PERMISSION_CODE:
                //If permission is granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    //Displaying another toast if permission is not granted
                    Toast.makeText(this, "Oops you have denied the permission for sms\nGo to settings and grant them to automatic read OTP", Toast.LENGTH_LONG).show();
                }
                getVerified();
                break;
            case CheckForPermissions.WRITE_STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loginFunction();
                } else {
                    //Displaying another toast if permission is not granted
                    Toast.makeText(this, "Oops you have denied the permission for write to storage\n" +
                            "Go to settings and grant them", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    //This method is called after the return to this activity occurs from the device's gps settings screen..
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //If the request code is same..
        if (requestCode == code) {
            mGoogleApiClient.connect();
            updateUI();
//            fetchLocation();
        }
    }

    public AuthCallback getAuthCallback() {
        return authCallback;
    }

    public void getAddress(double lati, double longi) {

        lat = lati + "";
        lon = longi + "";

        //Api to be connected to..
        String url = ConstantsDefined.urlForLocationFetch + lat + "," + lon + "&key=" + ConstantsDefined.MAP_API_KEY;

        dialog = new ProgressDialog(Signup_Login.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Fetching your location.. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        //Make a request..
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //On getting the response..
                if (dialog != null)
                    dialog.dismiss();
                try {
                    String ans = MiscellaneousParser.locationParser(response);
                    signupLocation.setText(ans);
                    stopLocationUpdates();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //In case the connection to the Api couldn't be established..
                if (dialog != null)
                    dialog.dismiss();
                if (ConstantsDefined.isOnline(Signup_Login.this)) {
                    //Do nothing..
                    Toast.makeText(Signup_Login.this, "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Signup_Login.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestQueue.add(stringRequest);

    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        Log.d("check", "onLocationChanged: ");
//        Log.d("location",location.getLatitude()+" "+location.getLongitude());
//        getAddress(location.getLatitude(),location.getLongitude());
//    }

//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//        Log.d("check", "onStatusChanged: ");
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//        Log.d("check", "onProviderEnabled: ");
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//        Log.d("check", "onProviderDisabled: ");
//
//    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
//        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
//        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PendingResult<Status> pendingResult;
        if (mGoogleApiClient.isConnected()) {
            pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {
            String lati = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());
//            signupLocation.setText("At Time: " + mLastUpdateTime + "\n" +
//                    "Latitude: " + lat + "\n" +
//                    "Longitude: " + lng + "\n" +
//                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
//                    "Provider: " + mCurrentLocation.getProvider());
            getAddress(Double.parseDouble(lati), Double.parseDouble(lng));
        } else {
            Log.d(TAG, "location is null ...............");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d(TAG, "Location update stopped .......................");
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }

    public void getProfileImage(String myUrl, String id) {

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Preparing your profile pic.. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        final String urlToConnect = ConstantsDefined.profileImageUrl + id+".jpg";

        RequestQueue requestQ = Volley.newRequestQueue(getApplicationContext());

        ImageRequest ir = new ImageRequest(urlToConnect, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {

                try {
                    File f=savebitmap(bitmap);
                    String myPath=f.getPath();

                    SharedPreferences.Editor e = prefs.edit();
                    e.putString("navImage", myPath);
                    e.apply();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("exception", "onResponse: "+e.toString());
                }

                if (dialog != null)
                    dialog.dismiss();

                Toast.makeText(Signup_Login.this, "Welcome to Live Exams", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Signup_Login.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        if (dialog != null)
                            dialog.dismiss();
                        Toast.makeText(Signup_Login.this, "Sorry! Profile pic couldn't be fetched..", Toast.LENGTH_SHORT).show();
                        Toast.makeText(Signup_Login.this, "Welcome to Live Exams", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Signup_Login.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
        requestQ.add(ir);

    }

    public static File savebitmap(Bitmap bmp) throws Exception {

        String folder_main = "LiveExamsProfileImage";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);

        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }

        String pp = f.getAbsolutePath();
        File file = new File(pp
                + File.separator + "profileImage.jpg");
        file.createNewFile();
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(bytes.toByteArray());
        fo.close();

        return f;
    }


}
