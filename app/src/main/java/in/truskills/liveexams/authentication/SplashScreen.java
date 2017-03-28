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
import in.truskills.liveexams.R;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class SplashScreen extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "fIx7W5i8xo9stQ8jhOHVLNdFB";
    private static final String TWITTER_SECRET = "JQ9IuPXWecyeMFK8VujoYePRHHfQllXNRvRYC6QatmCNt8l5FH";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Digits.Builder().build(), new Crashlytics(), new Twitter(authConfig));
        setContentView(R.layout.activity_splash_screen);
        TextView companyName=(TextView)findViewById(R.id.companyName);
        Typeface tff=Typeface.createFromAsset(getAssets(), "fonts/SF-Compact-Display-Light.otf");
        companyName.setTypeface(tff);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //Splash screen will be displayed for 2 sec
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {

                        ConstantsDefined.updateAndroidSecurityProvider(SplashScreen.this);
                        ConstantsDefined.beforeVolleyConnect();

                        RequestQueue requestQueue;

                        requestQueue = Volley.newRequestQueue(SplashScreen.this);

                        String url = ConstantsDefined.api2 + "apkVersionAndroid";
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                                url, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                Log.d("version", "onResponse: "+response);
                                try {
                                    String myVersion=response.getString("response");
                                    PackageInfo pInfo = null;
                                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                    String version = pInfo.versionName;

                                    if (myVersion.equals(version)){
                                        SharedPreferences prefs=getSharedPreferences("prefs", Context.MODE_PRIVATE);
                                        String state=prefs.getString("login","false");
                                        //If login=true, start MainActivity.java
                                        //Else start Signup_Login.java
                                        if(state.equals("true")){
                                            Intent i=new Intent(SplashScreen.this,MainActivity.class);
                                            startActivity(i);
                                        }else{
                                            Intent i=new Intent(SplashScreen.this,Signup_Login.class);
                                            startActivity(i);
                                        }
                                    }else{
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

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(SplashScreen.this, "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                        requestQueue.add(jsonObjectRequest);
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
    }
}
