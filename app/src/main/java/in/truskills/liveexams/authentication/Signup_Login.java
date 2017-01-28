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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.MiscellaneousScreens.VariablesDefined;
import in.truskills.liveexams.R;
import in.truskills.liveexams.MiscellaneousScreens.TermsAndConditions;

public class Signup_Login extends AppCompatActivity implements View.OnClickListener {

    //Public declaration of variables
    Button loginPressed, signupPressed, locationIcon;
    SlidingDrawer signupDrawer, loginDrawer;
    RelativeLayout signupLayout;
    EditText loginName, loginPassword, signupName, signupEmail, signupPassword, signupConfirmPassword, signupMobile;
    TextView termsAndConditionsPressed, forgotPasswordPressed, signupLocation;
    Spinner signupLanguage, signupGender;
    LocationManager locationManager;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String lat, lon, selectedLanguage, selectedGender,name,gender,email,password,confirmPassword,location,mobile,language,login_name,login_password;
    int code = 123;
    ImageView signupHandleImage, loginHandleImage;
    RequestQueue requestQueue;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the layout of this activity
        setContentView(R.layout.activity_signup__login);

        //Shared Preferences for user's selected language
        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        //Initialise the request for Volley connection
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //Attach all the variables used in layout
        loginPressed = (Button) findViewById(R.id.loginPressed);
        signupPressed = (Button) findViewById(R.id.signupPressed);
        locationIcon = (Button) findViewById(R.id.locationIcon);
        forgotPasswordPressed = (TextView) findViewById(R.id.forgotPasswordPressed);
        termsAndConditionsPressed = (TextView) findViewById(R.id.termsAndConditionsPressed);
        signupLocation = (TextView) findViewById(R.id.signupLocation);
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

        //Set OnClickListener on all buttons used
        loginPressed.setOnClickListener(this);
        signupPressed.setOnClickListener(this);
        forgotPasswordPressed.setOnClickListener(this);
        termsAndConditionsPressed.setOnClickListener(this);
        locationIcon.setOnClickListener(this);

        ArrayList<String> listOfLanguages = new ArrayList<>();
        listOfLanguages.add("LANGUAGE");
        listOfLanguages.add("English");
        listOfLanguages.add("Hindi");

        //List of Gender
        ArrayList<String> listOfGender = new ArrayList<>();
        listOfGender.add("GENDER");
        listOfGender.add("Male");
        listOfGender.add("Female");

        ArrayAdapter<String> adapterLanguage = new ArrayAdapter<String>(this, R.layout.spinner_item, listOfLanguages);
        signupLanguage.setAdapter(adapterLanguage);

        //Set Adapter for Gender Spinner
        ArrayAdapter<String> adapterGender = new ArrayAdapter<String>(this, R.layout.spinner_item, listOfGender);
        signupGender.setAdapter(adapterGender);

        signupLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLanguage = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        signupGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGender = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        signupDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                loginDrawer.setVisibility(View.GONE);
                signupHandleImage.setImageResource(R.drawable.down_arrow);
                signupName.setError("Required");
                signupEmail.setError(null);
                signupMobile.setError(null);
                signupPassword.setError(null);
                signupConfirmPassword.setError(null);
                signupName.setText("");
                signupEmail.setText("");
                signupMobile.setText("");
                signupPassword.setText("");
                signupLocation.setText("LOCATION");
                signupConfirmPassword.setText("");
                signupGender.setSelection(0);
                signupLanguage.setSelection(0);

                signupName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        int l = signupName.getText().toString().length();
                        Log.d("here", "length=" + l);
                        if (l < 1) {
                            signupName.setError("Required");
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
                            signupEmail.setError("Enter valid Email");
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
                            signupMobile.setError("Enter valid phone number");
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
                            signupPassword.setError("Minimum 6 characters required");
                        } else {
                            signupPassword.setError(null);
                            String confirm = signupConfirmPassword.getText().toString();
                            if (confirm.equals(signupPassword.getText().toString()))
                                signupConfirmPassword.setError(null);
                            else
                                signupConfirmPassword.setError("Do not match with Password");
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
                            signupConfirmPassword.setError("Do not match with Password");
                        else
                            signupConfirmPassword.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        });
        signupDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                loginDrawer.setVisibility(View.VISIBLE);
                signupHandleImage.setImageResource(R.drawable.up_arrow);
            }
        });
        loginDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                signupDrawer.setVisibility(View.GONE);
                signupLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                loginHandleImage.setImageResource(R.drawable.down_arrow);
                loginName.setError("Required");
                loginName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        int l = loginName.getText().toString().length();
                        Log.d("here", "length=" + l);
                        if (l < 1) {
                            loginName.setError("Required");
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
                        Log.d("here", "length=" + l);
                        if (l < 1) {
                            loginPassword.setError("Required");
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
        loginDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                signupDrawer.setVisibility(View.VISIBLE);
                signupLayout.setBackgroundColor(Color.parseColor("#0C1D36"));
                loginHandleImage.setImageResource(R.drawable.up_arrow);
                loginName.setText("");
                loginPassword.setText("");
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.loginPressed:
                loginValidation();
                break;
            case R.id.signupPressed:
                signupValidation();
                break;
            case R.id.forgotPasswordPressed:
                i = new Intent(Signup_Login.this, ForgotPassword.class);
                startActivity(i);
                break;
            case R.id.termsAndConditionsPressed:
                i = new Intent(Signup_Login.this, TermsAndConditions.class);
                startActivity(i);
                break;
            case R.id.locationIcon:
                getCurrentLocation();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (signupDrawer.isOpened())
            signupDrawer.close();
        else if (loginDrawer.isOpened())
            loginDrawer.close();
        else super.onBackPressed();
    }

    public void getCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("NETWORK PROVIDER NOT ENABLED");  // GPS not found
            builder.setMessage("Want to enable?"); // Want to enable?
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, code);
                }
            });
            builder.setNegativeButton("NO", null);
            builder.create().show();
        } else {
            if (ActivityCompat.checkSelfPermission(Signup_Login.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Signup_Login.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Signup_Login.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                final ProgressDialog progress = new ProgressDialog(Signup_Login.this);
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
                        Geocoder geocoder = new Geocoder(Signup_Login.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                            if (!((Activity) Signup_Login.this).isFinishing()) {
                                if (progress.isShowing())
                                    progress.dismiss();
                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(Signup_Login.this, R.style.AppCompatAlertDialogStyle);
                                builder.setMessage("Sorry no internet connection..");
                                builder
                                        .setTitle("ERROR")
                                        .setPositiveButton("OK", null)
                                        .setNegativeButton("CANCEL", null);
                                builder.show();
                            }
                        }
                        if (addresses != null) {
                            if (progress.isShowing())
                                progress.dismiss();
                            String ans = addresses.get(0).getAddressLine(0);
                            signupLocation.setText(addresses.get(0).getAddressLine(2));
                        } else {
                            AlertDialog.Builder builder =
                                    new AlertDialog.Builder(Signup_Login.this, R.style.AppCompatAlertDialogStyle);
                            builder.setMessage("Sorry no internet connection..");
                            builder
                                    .setTitle("ERROR")
                                    .setPositiveButton("OK", null)
                                    .setNegativeButton("CANCEL", null);
                            builder.show();
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
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    public void signupValidation() {
        name = signupName.getText().toString();
        gender = selectedGender;
        email = signupEmail.getText().toString();
        mobile = signupMobile.getText().toString();
        password = signupPassword.getText().toString();
        confirmPassword = signupConfirmPassword.getText().toString();
        location = signupLocation.getText().toString();
        language = selectedLanguage;

        if (!name.equals("") && !gender.equals("GENDER") && !location.equals("LOCATION") && !language.equals("LANGUAGE") && mobile.length() == 10 && password.length() >= 6 && password.equals(confirmPassword) && (!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            signupFunction();
        } else {
            if (name.equals(""))
                signupName.setError("Required");
            if (!(!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()))
                signupEmail.setError("Enter valid email");
            if (gender.equals("GENDER"))
                Toast.makeText(this, "Choose your gender", Toast.LENGTH_SHORT).show();
            if (location.equals("LOCATION"))
                Toast.makeText(this, "Choose your location", Toast.LENGTH_SHORT).show();
            if (language.equals("LANGUAGE"))
                Toast.makeText(this, "Choose your language", Toast.LENGTH_SHORT).show();
            if (mobile.length() != 10)
                signupMobile.setError("Enter valid phone number");
            if (password.length() < 6)
                signupPassword.setError("Minimum 6 characters required");
            if (!password.equals(confirmPassword))
                signupConfirmPassword.setError("Do not match with Password");
        }
    }

    public void loginValidation() {
        login_name = loginName.getText().toString();
        login_password=loginPassword.getText().toString();
        if(!login_name.equals("")&&!login_password.equals(""))
            loginFunction();
        else{
            if(login_name.equals(""))
                loginName.setError("Required");
            if(login_password.equals(""))
                loginPassword.setError("Required");
        }
    }

    public void signupFunction() {
        String url = VariablesDefined.api+"signup";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response=",response.toString()+"");
                try {
                    HashMap<String ,String> mapper=VariablesDefined.signupParser(response);
                    Toast.makeText(Signup_Login.this, mapper.get("response"), Toast.LENGTH_SHORT).show();
                    if(mapper.get("success").equals("true"))
                        signupDrawer.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("response", error.getMessage());
                Toast.makeText(Signup_Login.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.camera);

                String defaultImage=BitmapToString(icon);

                SharedPreferences prefs=getSharedPreferences("prefs",Context.MODE_PRIVATE);
                prefs.getString("navImage",defaultImage);
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

    public void loginFunction() {
        String url = VariablesDefined.api+"login";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response=",response.toString()+"");
                Intent i = null;
                try {
                    HashMap<String ,String> mapper=VariablesDefined.loginParser(response);
                    if(mapper.get("success").equals("true")) {
                        Log.d("response","inIf");
                        SharedPreferences.Editor e=prefs.edit();
                        e.putString("id",mapper.get("id"));
                        e.putString("emailAddress",mapper.get("emailAddress"));
                        e.putString("language",mapper.get("language"));
                        e.putString("profileImageUrl",mapper.get("profileImageUrl"));
                        e.apply();
                        Toast.makeText(Signup_Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        i = new Intent(Signup_Login.this, MainActivity.class);
                        i.putExtra("joinedExams",mapper.get("joinedExams"));
                        startActivity(i);
                        finish();
                    }else{
                        Log.d("response","inElse");
                        Toast.makeText(Signup_Login.this, mapper.get("response"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("response", error.getMessage()+"");
                Toast.makeText(Signup_Login.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("userName",loginName.getText().toString());
                params.put("password",loginPassword.getText().toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public String BitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] b=baos.toByteArray();
        String temp= Base64.encodeToString(b,Base64.DEFAULT);
        return temp;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(Signup_Login.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Signup_Login.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Signup_Login.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                    } else {
                        final ProgressDialog progress = new ProgressDialog(Signup_Login.this);
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
                                Geocoder geocoder = new Geocoder(Signup_Login.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    if (!((Activity) Signup_Login.this).isFinishing()) {
                                        if (progress.isShowing())
                                            progress.dismiss();
                                        AlertDialog.Builder builder =
                                                new AlertDialog.Builder(Signup_Login.this, R.style.AppCompatAlertDialogStyle);
                                        builder.setMessage("Sorry no internet connection..");
                                        builder
                                                .setTitle("ERROR")
                                                .setPositiveButton("OK", null)
                                                .setNegativeButton("CANCEL", null);
                                        builder.show();
                                    }
                                }
                                if (addresses != null) {
                                    if (progress.isShowing())
                                        progress.dismiss();
                                    String ans = addresses.get(0).getAddressLine(0);
                                    signupLocation.setText(addresses.get(0).getAddressLine(2));
                                } else {
                                    AlertDialog.Builder builder =
                                            new AlertDialog.Builder(Signup_Login.this, R.style.AppCompatAlertDialogStyle);
                                    builder.setMessage("Sorry no internet connection..");
                                    builder
                                            .setTitle("ERROR")
                                            .setPositiveButton("OK", null)
                                            .setNegativeButton("CANCEL", null);
                                    builder.show();
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
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                    }
                } else {
                    //else code
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == code) {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(Signup_Login.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Signup_Login.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Signup_Login.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                } else {
                    final ProgressDialog progress = new ProgressDialog(Signup_Login.this);
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
                            Geocoder geocoder = new Geocoder(Signup_Login.this, Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (addresses != null) {
                                if (progress.isShowing())
                                    progress.dismiss();
                                String ans = addresses.get(0).getAddressLine(0);
                                signupLocation.setText(addresses.get(0).getAddressLine(2));
                            } else {
                                Toast.makeText(Signup_Login.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
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
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }
}
