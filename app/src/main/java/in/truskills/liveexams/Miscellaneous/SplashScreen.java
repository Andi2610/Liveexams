package in.truskills.liveexams.Miscellaneous;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import in.truskills.liveexams.R;
import in.truskills.liveexams.authentication.Signup_Login;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    //Start MainActivity after 2 sec
                    Intent i=new Intent(SplashScreen.this,Signup_Login.class);
                    startActivity(i);
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
