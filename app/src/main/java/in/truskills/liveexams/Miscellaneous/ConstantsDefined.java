package in.truskills.liveexams.Miscellaneous;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Shivansh Gupta on 24-02-2017.
 */

public class ConstantsDefined {
    //Api used to connect to the server..
    public static final String api = "https://api.liveexams.in/api/";
//    public static final String api = "http://35.154.110.122:3002/api/";

    //Url for image..
    public static final String imageUrl = "https://s3.ap-south-1.amazonaws.com/live-exams/";

    public static final String profileImageUrl="https://s3.ap-south-1.amazonaws.com/live-exams/Users/";
    //Constants for flashphoner..
    public static final String STARTSTREAMING = "startStreaming";
    public static final String STARTEDSTREAMING = "startedStreaming";
    public static final String STOPSTREAMING = "stopStreaming";
    public static final String STOPPEDSTREAMING = "stoppedStreaming";
    public static final String GETINFO = "getInfo";
    public static final String SETINFO = "setInfo";
    public static final String RECONNECT = "reconnect";
    public static final String DISCONNECT = "disconnect";
    public static final String CONNECT = "connect";
    public static final String STUDENTLEFT = "studentLeft";
    public static String FLASHPHONER="flashphoner";
    public static String socketConnectionUrl="https://api.liveexams.in/";

    public static final String MAP_API_KEY = "AIzaSyDChC607te-i2CMIweUJcnhBMuoW9cq7W8";
    public static final String urlForLocationFetch="https://maps.googleapis.com/maps/api/geocode/json?latlng=";

    public static final long time=60000;

    public static boolean isOnline(Context context) {
        boolean connected=true;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;


        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }

    public static void beforeVolleyConnect(){
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    public static void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            if(callingActivity!=null)
            ProviderInstaller.installIfNeeded(callingActivity);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        }
    }
}
