package in.truskills.liveexams.firebase;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import in.truskills.liveexams.Miscellaneous.ConstantsDefined;

//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Shivansh Gupta on 05-04-2017.
 */

public class TokenService
        extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token..
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.d("firebase", refreshedToken);
        sendRegistrationToServer(refreshedToken);
//        Toast.makeText(this, "myToken"+refreshedToken, Toast.LENGTH_SHORT).show();
    }

    private void sendRegistrationToServer(String token) {
        ConstantsDefined.sendToken(getBaseContext(),token);
    }
}
