package in.truskills.liveexams.authentication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import in.truskills.liveexams.R;

//This Activity launches in case the user forgets his/her login password..

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }
}
