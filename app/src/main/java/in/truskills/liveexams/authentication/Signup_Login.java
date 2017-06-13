package in.truskills.liveexams.authentication;

import android.Manifest;
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
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
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
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.Miscellaneous.CheckForPermissions;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.R;

/**
 * This Activity includes the code for sign up and login..
 *
 * Functions used:
 *
 * 1. onCreate()
 * 2. isGooglePlayServicesAvailable() : to check if google play services available or not so as to accordingly modify our signupDrawer layout..
 * 3. createLocationRequest() : for creating a new request for location fetch through fused location api..
 * 4. getCurrentLocation() : check if gps is on/off.. if on, fetch location.. else ask to enable it..
 * 5. startLocationUpdates() : to start google client api to work and get current location update..
 * 6. stopLocationUpdates() : to stop google client api from running and stop current location update..
 * 7. updateUI() :  get latitude and longitude of current location and call getAddress() method..
 * 8. getAddress() : get address associated with lat and lon to display in location field..
 * 9. signupValidation() :  This method is for validating the user's entered signup info before it is given for registering..
 * 10. signupFunction() : to check for sms permissions and call get verified function..
 * 11. getVerified() : checks and verify phone number through DIGITS implementation..
 * 12. getAuthCallback() : to return authCallback instance..
 * 13. signupFunction() : This method is for signing up i.e calling signup api..
 * 14. loginValidation() : This method is for validating the user's entered login info before it is given for logging in..
 * 15. loginFunction() : This method is for logging up i.e calling login api..
 * 16. getProfileImage() : Get image downloaded from amazon server..
 * 17. savebitmap() : to store a particular image in SD card..
 * 18. onClick() : Called when a button in the activity screen is pressed..
 * 19. onRequestPermissionsResult() : to check for permissions of sms and call method getVerified..
 * 20. onActivityResult() : This method is called after the return to this activity occurs from the device's gps settings screen..
 * 21. onBackPressed()
 * 22. onConnected() : start location updates when google api client is connected..
 * 23. onConnectionSuspended()
 * 24. onConnectionFailed
 * 25. onLocationChanged() : getting current location, start update time and call updateUI function..
 * 26. onResume() : start location updates if google api client is connected..
 *
 * API calls made:
 *
 * 1. /api/beforeSignup (GET request) : to get list of languages to choose from initially..
 * 2. https://maps.googleapis.com/maps/api/geocode/json?latlng=12,34,&key=56 : to get address associated with a particular lat and lon..
 * 3. /api/signup (POST request with parameters : userName,gender,password,emailAddress,mobileNumber,language,latitude,longitude,city) : for signup..
 * 4. /api/login (POST request with parameters : userName,password) : for login..
 */

public class Signup_Login extends AppCompatActivity implements View.OnClickListener, com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //Declaration of variables
    AuthCallback authCallback;
    String city="abcd";
    LinearLayout locationField,locationFieldEditText;
    Button loginPressed, registerHandleButton, loginHandleButton;
    Button signupPressed;
    SlidingDrawer signupDrawer, loginDrawer;
    RelativeLayout signupLayout;
    EditText loginName, loginPassword, signupName,signupFullName, signupEmail, signupPassword, signupConfirmPassword, signupMobile,signupLocationEditText;
    TextView termsAndConditionsPressed, forgotPasswordPressed, signupLocation, sentence, successfullRegister, signupLanguageAlternate;
    Spinner signupLanguage, signupGender;
    LocationManager locationManager;
    String lat="0.00", lon="0.00", selectedLanguage, selectedGender, name, gender, email, password, confirmPassword, location, mobile, language, login_name, login_password;
    int code = 123;
    ImageView signupHandleImage, loginHandleImage;
    RequestQueue requestQueue;
    SharedPreferences prefs;
    ImageView app_logo;
    ArrayList<String> listOfLanguages;
    Animation slide_down;
    String fullName;
    Drawable dr;
    ProgressDialog dialog;
    public static boolean available=true;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Render layout file..
        setContentView(R.layout.activity_signup__login);

        //Render all elements from layout..
        locationFieldEditText=(LinearLayout)findViewById(R.id.locationFieldEditText);
        locationField = (LinearLayout) findViewById(R.id.locationField);
        signupLocationEditText=(EditText)findViewById(R.id.signupLocationEditText);
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
        signupFullName= (EditText) findViewById(R.id.signupFullName);
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

        //Set typeface of required texts in layout..
        final Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        registerHandleButton.setTypeface(tff1);
        loginHandleButton.setTypeface(tff1);
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
        signupLocationEditText.setTypeface(tff2);
        signupFullName.setTypeface(tff2);

        //A variable determining google play services is available or not..
        available=true;

        //If google play services is unavailable, edit text should be displayed to enable user enter their location manually..
        if (!isGooglePlayServicesAvailable()) {
            available=false;
            locationFieldEditText.setVisibility(View.VISIBLE);
            locationField.setVisibility(View.GONE);
        }

        //If google play services is available, TextView should be displayed to display location fetched automatically..
        if(available){
            createLocationRequest();
            locationFieldEditText.setVisibility(View.GONE);
            locationField.setVisibility(View.VISIBLE);
        }

        //Initialise googleApiClient instance..
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //Shared preferences : prefs : signup = 0 since, signup is not done at present by user..
        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putInt("signup", 0);
        e.apply();

        //Initialise the requestQueue instance for Volley connection
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //For animation..
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        //Initialise other variables..
        listOfLanguages = new ArrayList<>();

        //add an error icon to yur drawable files
        dr = getResources().getDrawable(R.drawable.required_icon);
        dr.setBounds(0, 0, 50, 50);

        //Set OnClickListener on all buttons used
        loginPressed.setOnClickListener(this);
        signupPressed.setOnClickListener(this);
        forgotPasswordPressed.setOnClickListener(this);
        termsAndConditionsPressed.setOnClickListener(this);
        locationField.setOnClickListener(this);

        //Initially.. message for successful register should not be displayed, if directly login is opened..
        successfullRegister.setVisibility(View.GONE);

        //On click on signupLanguageAlternate textView, call beforeSignup api to display languages for selection..
        signupLanguageAlternate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //For https connection thtough volley..
                ConstantsDefined.updateAndroidSecurityProvider(Signup_Login.this);
                ConstantsDefined.beforeVolleyConnect();

                //Api to be connected to..
                String url = ConstantsDefined.api + "beforeSignup";

                //Progress dialog while the connection is being made..
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

                        //Dismiss the dialog..
                        if (dialog != null)
                            dialog.dismiss();

                        try {
                            //Parse the signup response..
                            JSONObject jsonObject=new JSONObject(response);
                            String success=jsonObject.getString("success");

                            //If successful, populate languages spinner, else display error message..
                            if(success.equals("true")){
                                signupLanguageAlternate.setVisibility(View.GONE);
                                signupLanguage.setVisibility(View.VISIBLE);
                                listOfLanguages = MiscellaneousParser.beforeSignupParser(response);
                                CustomSpinnerAdapter customAdapter = new CustomSpinnerAdapter(getApplicationContext(), listOfLanguages);
                                signupLanguage.setAdapter(customAdapter);
                                signupLanguage.performClick();
                            }else{
                                Toast.makeText(Signup_Login.this, "Something went wrong..\n" +
                                        "Please try again..", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //If connection to api could not be made..

                        //Dismiss dialog box..
                        if (dialog != null)
                            dialog.dismiss();

                        //Display appropriate toast message depending upon internet connectivity was a reason for failure or something else..
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

        //List for "Gender" spinner..
        ArrayList<String> listOfGender = new ArrayList<>();
        listOfGender.add("GENDER");
        listOfGender.add("Male");
        listOfGender.add("Female");
        CustomSpinnerAdapter customAdapter = new CustomSpinnerAdapter(getApplicationContext(), listOfGender);
        signupGender.setAdapter(customAdapter);

        //Handles the operation of changing the language selection..
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

                //Enable it once.. so that after one click it's click can be disabled..
                locationField.setEnabled(true);

                //Hide the login drawer..
                loginDrawer.setVisibility(View.GONE);
                app_logo.setVisibility(View.GONE);
                signupLanguageAlternate.setVisibility(View.VISIBLE);
                signupLanguage.setVisibility(View.GONE);

                //Change the arrow button: from up to down, for the user to know that the drawer can be closed on sliding down..
                signupHandleImage.setImageResource(R.drawable.down_arrow);

                //Clear the previous content of all the fields..
                signupName.setText("");
                signupFullName.setText("");
                signupEmail.setText("");
                signupMobile.setText("");
                signupPassword.setText("");
                signupLocation.setText("LOCATION");
                signupConfirmPassword.setText("");
                signupGender.setSelection(0);

                //Set the validation message for rest of the fields as null..
                signupName.setError(null);
                signupFullName.setError(null);
                signupEmail.setError(null);
                signupMobile.setError(null);
                signupPassword.setError(null);
                signupConfirmPassword.setError(null);

                //Add the text change listener on the required fields to show the validation error message to the user at appropriate times..
                //signupName: userName length should be between 2-20 characters and noo spaces are allowed..
                //signupFullName: this field should not be empty..
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
                        if ((l < 2 && l>20)|| signupName.getText().toString().contains(" ")) {
                            signupName.setError("No spaces allowed and length should be between 2 and 20 characters", dr);
                        } else {
                            signupName.setError(null);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                signupFullName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        int l = signupFullName.getText().toString().length();
                        if ((l < 1)) {
                            signupFullName.setError("Required", dr);
                        } else {
                            signupFullName.setError(null);
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

                //Clear the previous content of all the fields..
                signupName.setText("");
                signupEmail.setText("");
                signupMobile.setText("");
                signupPassword.setText("");
                signupLocation.setText("LOCATION");
                signupConfirmPassword.setText("");
                signupGender.setSelection(0);

                //Set the validation message for rest of the fields as null..
                signupName.setError(null);
                signupEmail.setError(null);
                signupMobile.setError(null);
                signupPassword.setError(null);
                signupConfirmPassword.setError(null);

            }
        });

        //When the use
        //
        // r opens the login drawer..
        loginDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {

                //Check if the user has come to login screen after successful signup or otherwise..
                if (prefs.getInt("signup", 0) == 1) {
                    //If the entry is after successful signup, display successful signup message..
                    successfullRegister.setVisibility(View.VISIBLE);
                    successfullRegister.startAnimation(slide_down);
                    successfullRegister.setTypeface(tff1);
                    SharedPreferences.Editor e = prefs.edit();
                    e.putInt("signup", 0);
                    e.apply();
                } else {
                    //Else don't show it..
                    successfullRegister.setVisibility(View.GONE);
                }

                //Hide the signup drawer..
                signupDrawer.setVisibility(View.GONE);
                app_logo.setVisibility(View.GONE);
                signupLayout.setBackgroundColor(Color.parseColor("#E0E0E0"));

                //Change the arrow button: from up to down, for the user to know that the drawer can be closed on sliding down..
                loginHandleImage.setImageResource(R.drawable.down_arrow);

                //Clear the previous data..
                loginName.setText("");
                loginPassword.setText("");
                loginName.setError(null);
                loginPassword.setError(null);

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
                successfullRegister.setVisibility(View.GONE);

                //Rechange the arrow button: from down to up again..
                loginHandleImage.setImageResource(R.drawable.up_arrow);

                //Clear previous data..
                loginName.setText("");
                loginPassword.setText("");
                loginName.setError(null);
                loginPassword.setError(null);

            }
        });


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

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void getCurrentLocation() {

        //Initialise location manager instance..
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Check for location permissions and act accordingly..
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

                //Connect so that location updates can be started..
                mGoogleApiClient.connect();
                //disable click so as to not allow user to click multiple times and
                locationField.setEnabled(false);
                //Populate location field..
                updateUI();
            }
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PendingResult<Status> pendingResult;
        if (mGoogleApiClient.isConnected()) {
            pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private void updateUI() {
        if (null != mCurrentLocation) {
            String lati = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());
            getAddress(Double.parseDouble(lati), Double.parseDouble(lng));
        }
    }

    public void getAddress(double lati, double longi) {

        //Update these variables to be sent as parameters in API call..
        lat = lati + "";
        lon = longi + "";

        //Api to be connected to..
        String url = ConstantsDefined.urlForLocationFetch + lat + "," + lon + "&key=" + ConstantsDefined.MAP_API_KEY;

        //Display progress dialog..
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

                //Dismiss the dialog box..
                if (dialog != null)
                    dialog.dismiss();
                try {
                    //Parse the response and populte the field..
                    String ans = MiscellaneousParser.locationParser(response);
                    signupLocation.setText(ans);
                    //stop location updates..
                    stopLocationUpdates();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //In case the connection to the Api couldn't be established..

                //Dismiss dialog box..
                if (dialog != null)
                    dialog.dismiss();

                //Display appropriate toast message depending upon internet connectivity was a reason for failure or something else..
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

    public void signupValidation() {

        //Get the value of all the fields..
        name = signupName.getText().toString();
        int len=name.length();
        fullName=signupFullName.getText().toString();
        gender = selectedGender;
        email = signupEmail.getText().toString();
        mobile = signupMobile.getText().toString();
        password = signupPassword.getText().toString();
        confirmPassword = signupConfirmPassword.getText().toString();
        //Depending upon whether for location, edit text was displayed or text view was displayed..
        if(available)
            location = signupLocation.getText().toString();
        else{
            location=signupLocationEditText.getText().toString();
            city=location;
        }
        language = selectedLanguage;

        //If all valid.. signup..
        if ((len>=2&&len<=20&&(!name.contains(" "))) && !fullName.equals("")&& !gender.equals("GENDER") && !location.equals("LOCATION") && signupLanguageAlternate.getVisibility() == View.GONE && mobile.length() == 10 && password.length() >= 6 && password.equals(confirmPassword) && (!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            //signupFunction();
            ConstantsDefined.updateAndroidSecurityProvider(Signup_Login.this);
            ConstantsDefined.beforeVolleyConnect();
            String url = ConstantsDefined.api + "/checkBeforeSignUp";
            dialog = new ProgressDialog(Signup_Login.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Verifying your details. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();


            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {
                String msg;

                @Override
                public void onResponse(String response) {
                    if (dialog != null)
                        dialog.dismiss();
                    try {
                        //Parse the response..
                        Log.e("Verification",response);
                        HashMap<String, String> mapper = MiscellaneousParser.checkBeforeSignUpParser(response);
                        if (mapper.get("success").equals("true")) {
                            String result = mapper.get("response");
                            HashMap<String, String> var = new HashMap<>();
                            JSONObject jsonObject = new JSONObject(result);
                            Boolean username = jsonObject.getBoolean("userName");
                            Boolean mobilenumber = jsonObject.getBoolean("mobileNumber");
                            Boolean emailaddress = jsonObject.getBoolean("emailAddress");
                            if (username == false && mobilenumber == false && emailaddress == false) {
                                signupFunction();
                            } else {
                                if (username == true) {
                                    if (mobilenumber == false && emailaddress == false) {
                                        msg = "Username Already Exists";
                                    } else {
                                        if (mobilenumber == true && emailaddress == false) {
                                            msg = "Username and Mobilenumber Already Exists";
                                        } else {
                                            if (mobilenumber == false && emailaddress == true) {
                                                msg = "Username and EmailAddress Already Exists";
                                            } else {
                                                msg = "Username, MobileNumber and Email Address already Exists";
                                            }

                                        }
                                    }
                                } else {
                                    if (mobilenumber == true && emailaddress == false) {
                                        msg = "Mobilenumber Already Exists";
                                    } else {
                                        if (mobilenumber == false && emailaddress == true) {
                                            msg = "EmailAddress Already Exists";
                                        } else {
                                            msg = "MobileNumber and Email Address already Exists";
                                        }
                                    }
                                }
                            }
                            Toast.makeText(Signup_Login.this, msg, Toast.LENGTH_LONG).show();
                        } else {
                            String errmsg = mapper.get("response");
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
                    Map<String, String> params = new HashMap<>();
                    params.put("userName", name);
                    params.put("emailAddress", email);
                    params.put("mobileNumber", mobile);
                    return params;
                }
            };
            requestQueue.add(stringRequest);


        }else {
            //Else display desired error messages..
            if(fullName.equals(""))
                signupFullName.setError("Required");
            if ((len < 2 && len>20)|| name.contains(" "))
                signupName.setError("No spaces allowed and length should be between 2 and 20 characters", dr);
            if (!(!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()))
                signupEmail.setError("Enter valid email", dr);
            if (gender.equals("GENDER"))
                Toast.makeText(this, "Choose your gender", Toast.LENGTH_SHORT).show();
            if (location.equals("LOCATION"))
                Toast.makeText(this, "Enter your location", Toast.LENGTH_SHORT).show();
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

    public void signupFunction() {

        boolean result = CheckForPermissions.checkForSms(Signup_Login.this);
        if (result) {
            getVerified();
        }
    }

    public void getVerified() {

        //Check for verification of entered mobile number..
        authCallback = new AuthCallback() {

            @Override
            public void success(DigitsSession session, String phoneNumber) {

                //Remove +91 from phone number obtained from the session..
                mobile = phoneNumber.substring(3);

                //Display successful message and call signup api..
                Toast.makeText(Signup_Login.this, "Phone Number Verified Successfully..", Toast.LENGTH_SHORT).show();

                //For https connection..
                ConstantsDefined.updateAndroidSecurityProvider(Signup_Login.this);
                ConstantsDefined.beforeVolleyConnect();

                //Api to be connected to..
                String url = ConstantsDefined.api + "signup";

                //Show progress dialog..
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
                        Map<String, String> params = new HashMap<>();
                        params.put("userName", name);
                        params.put("gender", gender);
                        params.put("password", password);
                        params.put("emailAddress", email);
                        params.put("mobileNumber", mobile);
                        params.put("language", language);
                        params.put("latitude", lat);
                        params.put("longitude", lon);
                        params.put("city",city);
                        return params;
                    }
                };
                requestQueue.add(stringRequest);

            }

            @Override
            public void failure(DigitsException exception) {
                // Display error message on failure
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

    public AuthCallback getAuthCallback() {
        return authCallback;
    }

    public void loginValidation() {

        //Get text from fields..
        login_name = loginName.getText().toString();
        login_password = loginPassword.getText().toString();

        //Check for their required validation.. if all valid.. call login function..
        if (!login_name.equals("") && !login_password.equals("")){
            boolean checkForWrite=CheckForPermissions.checkForWriteStorage(this);
            if(checkForWrite)
                loginFunction();
        }
        else {
            //Else display error messages..
            if (login_name.equals(""))
                loginName.setError("Required", dr);
            if (login_password.equals(""))
                loginPassword.setError("Required", dr);
        }
    }

    public void loginFunction() {

        //For https connection..
        ConstantsDefined.updateAndroidSecurityProvider(Signup_Login.this);
        ConstantsDefined.beforeVolleyConnect();

        //Api to be connected to..
        String url = ConstantsDefined.api + "login";

        //Show dialog box..
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

                //Dismiss dialog box..
                if (dialog != null)
                    dialog.dismiss();

                try {
                    //Parse the login response..
                    HashMap<String, String> mapper = MiscellaneousParser.loginParser(response);

                    //If successfull signup.. save the desired info in shared preferences..
                    if (mapper.get("success").equals("true")) {

                        //Store default image in shared prefs..
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

                        //For fabrics analysis..
                        Answers.getInstance().logCustom(new CustomEvent("Login successfull")
                                .putCustomAttribute("userName", mapper.get("userName")));

                        //Get profile image from amazon server..
                        getProfileImage(mapper.get("profileImageUrl"), mapper.get("id"));

                        //Send FCM token for firebase messaging service..
                        ConstantsDefined.sendToken(Signup_Login.this, FirebaseInstanceId.getInstance().getToken());

                    } else {

                        //Display error message..
                        Toast.makeText(Signup_Login.this, mapper.get("response"), Toast.LENGTH_SHORT).show();
                    }
                }  catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //In case the connection to the Api couldn't be established..

                //Dismiss dialog box..
                if (dialog != null)
                    dialog.dismiss();

                //Display appropriate toast message depending upon internet connectivity was a reason for failure or something else..
                if (ConstantsDefined.isOnline(Signup_Login.this)) {
                    Toast.makeText(Signup_Login.this, "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Signup_Login.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Put all the required parameters for the post request..
                Map<String, String> params = new HashMap<>();
                params.put("userName", loginName.getText().toString());
                params.put("password", loginPassword.getText().toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getProfileImage(String myUrl, String id) {

        //Show dialog box..
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Preparing your profile pic.. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        //api to be connected to..
        final String urlToConnect = ConstantsDefined.profileImageUrl + id+".jpg";
        RequestQueue requestQ = Volley.newRequestQueue(getApplicationContext());
        ImageRequest ir = new ImageRequest(urlToConnect, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {

                //In case of successful response..

                try {

                    //Save this image in a folder in SD card..
                    File f=savebitmap(bitmap);
                    String myPath=f.getPath();
                    SharedPreferences.Editor e = prefs.edit();
                    e.putString("navImage", myPath);
                    e.apply();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Dismiss dialog box..
                if (dialog != null)
                    dialog.dismiss();

                //Display welcome message..
                Toast.makeText(Signup_Login.this, "Welcome to Live Exams", Toast.LENGTH_SHORT).show();

                //Start MainActivity..
                Intent i = new Intent(Signup_Login.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {

                        //In case a connection could not be made..

                        //Dismiss dialog box..
                        if (dialog != null)
                            dialog.dismiss();

                        //Display error message.. and start MainActivity with default profile image..
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

        //Create folder..
        String folder_main = ".LiveExamsProfileImage";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);

        //Create a file..
        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }

        //Save the image in the file..
        String pp = f.getAbsolutePath();
        File file = new File(pp
                + File.separator + "profileImage.jpg");
        file.createNewFile();
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(bytes.toByteArray());
        fo.close();
        return f;
    }

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

                if(available){
                    getCurrentLocation();
                }

                break;
        }
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
                    //Do nothing..
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //If the request code is same..
        if (requestCode == code) {
            mGoogleApiClient.connect();
            updateUI();
//            fetchLocation();
        }
    }

    @Override
    public void onBackPressed() {

        //If any drawer whether signup or login is opened, then close it.. otherwise exit the application..

        if (signupDrawer.isOpened())
            signupDrawer.close();
        else if (loginDrawer.isOpened())
            loginDrawer.close();
        else super.onBackPressed();

    }

    @Override
    public void onConnected(Bundle bundle) {

        //When google api client is connected, start location updates..
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {

        //Get desired data and call updateUI method..
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();

        //If connnected.. start location updates..
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

}
