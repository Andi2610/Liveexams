package in.truskills.liveexams.Miscellaneous;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.Digits;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.R;
import in.truskills.liveexams.authentication.Signup_Login;
import io.fabric.sdk.android.Fabric;

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //Splash screen will be displayed for 2 sec
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
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
}
