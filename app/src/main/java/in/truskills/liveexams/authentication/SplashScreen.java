package in.truskills.liveexams.authentication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.DocumentsContract;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.Digits;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.json.JSONException;
import org.json.JSONObject;

import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.Quiz.FeedbackActivity;
import in.truskills.liveexams.R;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.concurrency.AsyncTask;

/**
 * This is the launcher activity displayed for 2 secs..
 * It checks for apkAndroidVersion of the current app installed in the phone and the latest version in production..
 * In case of mismatch, it force redirects to play store for updation else the app won't open further..
 *
 * Functions used:
 *
 * 1. onCreate() : to show splash screen for 2 secs and the check android apk version..
 * 2. onPause() : finish this activity to ensure that this activity doesn't gets added to backstack..
 * 3. onBackPressed() : disable back button.. so do nothing here..
 *
 * API calls made:
 * 1.
 */

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Render layout file..
        setContentView(R.layout.activity_splash_screen);

        //Render elements from layout..
        TextView companyName=(TextView)findViewById(R.id.companyName);

        //Set typeface for desired texts in layout..
        Typeface tff=Typeface.createFromAsset(getAssets(), "fonts/SF-Compact-Display-Light.otf");
        companyName.setTypeface(tff);

        //Start a thread which runs for 2 secs and then do desired actions..
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //Splash screen will be displayed for 2 sec
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {

                    //After 2 secs.. compare current APK version of app and the latest one available..
                    //In case of mismatch, notify the user accordingly..
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //For https connection..
                            ConstantsDefined.updateAndroidSecurityProvider(SplashScreen.this);
                            ConstantsDefined.beforeVolleyConnect();

                            //Initialise requestQueue instance..
                            RequestQueue requestQueue;
                            requestQueue = Volley.newRequestQueue(SplashScreen.this);

                            //Api to be connected to..
                            String url = ConstantsDefined.api2 + "apkVersionAndroid";
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                                    url, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {

                                    try {
                                        //Parse the response..
                                        String success=response.getString("success");
                                        if(success.equals("true")){
                                            //If successful, compare the versions..
                                            String myVersion=response.getString("response");
                                            PackageInfo pInfo = null;
                                            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                            String version = pInfo.versionName;

                                            //If match found, start next activity..
                                            if (myVersion.equals(version)){
                                                SharedPreferences prefs=getSharedPreferences("prefs", Context.MODE_PRIVATE);
                                                String state=prefs.getString("login","false");
                                                //If login=true, start MainActivity.java
                                                //Else start Signup_Login.java
                                                if(state.equals("true")){
                                                    Intent i=new Intent(SplashScreen.this,MainActivity.class);
                                                    startActivity(i);
//                                                    Intent i=new Intent(SplashScreen.this,FeedbackActivity.class);
//                                                    startActivity(i);
                                                }else{
                                                    Intent i=new Intent(SplashScreen.this,Signup_Login.class);
                                                    startActivity(i);
                                                }
                                            }else{
                                                //Else direct user to playstore for updation..
                                                AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this, R.style.AppCompatAlertDialogStyle);
                                                builder.setTitle("Updated App Not Installed");  // GPS not found
                                                builder.setMessage("You will be directed to play store now..\nPlease update your app"); // Want to enable?
                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Intent ii=new Intent((Intent.ACTION_VIEW));
                                                        ii.setData(Uri.parse("market://details?id=in.truskills.liveexams"));
                                                        startActivity(ii);
                                                        finish();
                                                    }
                                                });
                                                builder.setCancelable(false);
                                                builder.create().show();
                                            }
                                        }else{
                                            //Else display error message..
                                            Toast.makeText(SplashScreen.this, "An unexpected error occurred..\nPlease try again..", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (PackageManager.NameNotFoundException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    //If could not connect to api, display error message and close the app..
                                    Toast.makeText(SplashScreen.this, "Sorry no internet connection..Please try again..", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                            requestQueue.add(jsonObjectRequest);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Splash screen will not be added to backstack.
        finish();
    }

    @Override
    public void onBackPressed() {
        //Do nothing on back pressed..
    }
}
