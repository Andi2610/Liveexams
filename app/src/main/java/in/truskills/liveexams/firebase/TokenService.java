package in.truskills.liveexams.firebase;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import in.truskills.liveexams.Miscellaneous.ConstantsDefined;

/**
 * This is for sending token to firebase for fcm notification for each unique phone device..
 */

public class TokenService
        extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token..
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        ConstantsDefined.sendToken(getBaseContext(),token);
    }
}
