package in.truskills.liveexams.Miscellaneous;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Shivansh Gupta on 24-02-2017.
 */

public class ConstantsDefined {
    //Api used to connect to the server..
    public static final String api = "http://35.154.110.122:3002/api/";
    //Url for image..
    public static final String imageUrl = "https://s3.ap-south-1.amazonaws.com/live-exams/";
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
}
