package in.truskills.liveexams.Miscellaneous;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.Digits;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Shivansh Gupta on 28-02-2017.
 */

public class MyApplication extends MultiDexApplication {

    private static MyApplication mInstance;
    private static final String TWITTER_KEY = "fIx7W5i8xo9stQ8jhOHVLNdFB";
    private static final String TWITTER_SECRET = "JQ9IuPXWecyeMFK8VujoYePRHHfQllXNRvRYC6QatmCNt8l5FH";


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new TwitterCore(authConfig), new Digits.Builder().build());
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReciever.ConnectivityReceiverListener listener) {
        ConnectivityReciever.connectivityReceiverListener = listener;
    }

}
