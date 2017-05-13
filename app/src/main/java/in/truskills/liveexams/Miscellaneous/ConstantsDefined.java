package in.truskills.liveexams.Miscellaneous;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

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
//    public static final String api = "https://api.liveexams.in/api/";
    public static final String api = "http://35.154.110.122:3002/api/";
    public static final String api2 = "http://35.154.110.122:3002/api/";
    public static final String apiForKit = "http://35.154.110.122:3002/access/";

    public static String accessCode="AVYH69EC95CJ91HYJC";
    public static String workingKey="DDEF305F5B417483D35E56BF860FAF0B";
    public static String merchantId="6223";
    public static String rsaUrl="http://rsa.liveexams.in/GetRSA.php";
    public static String cancelUrl="http://122.182.6.216/merchant/ccavResponseHandler.jsp";
    public static String redirectUrl="http://122.182.6.216/merchant/ccavResponseHandler.jsp";
    public static String currency="INR";

    //Url for image..
    public static final String imageUrl = "https://s3.ap-south-1.amazonaws.com/live-exams/";

    public static final String profileImageUrl="https://s3.ap-south-1.amazonaws.com/live-exams/students/";
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
                Log.d("hostName", "verify: "+hostname);
                if( hostname.equals("api.liveexams.in")||
                        hostname.equals("s3.ap-south-1.amazonaws.com")||
                        hostname.equals("maps.googleapis.com")||
                        hostname.equals("live-exams.s3-ap-south-1.amazonaws.com")||
                        hostname.equals("reports.crashlytics.com"))
                    return true;

                return false;
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

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }

    public static void sendToken(Context c, final String token){

        ConstantsDefined.beforeVolleyConnect();

        final SharedPreferences prefs=c.getSharedPreferences("prefs",Context.MODE_PRIVATE);

        RequestQueue requestQueue = Volley.newRequestQueue(c);

        String myurl = ConstantsDefined.api+"setFcmId";

        //Make a request..
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                myurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("firebase", "onResponse: "+response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("firebase", "onErrorResponse: ");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Put all the required parameters for the post request..
                Map<String, String> params = new HashMap<String, String>();
                Log.d("firebase", "getParams: "+prefs.getString("userId",""));
                Log.d("firebase", "getParams: "+token);
                params.put("userId",prefs.getString("userId",""));
                params.put("fcmId",token);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
