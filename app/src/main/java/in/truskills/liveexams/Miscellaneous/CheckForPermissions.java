package in.truskills.liveexams.Miscellaneous;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import in.truskills.liveexams.MainScreens.MainActivity;

/**
 * Created by Shivansh Gupta on 16-02-2017.
 */

public class CheckForPermissions {

    public static final int STORAGE_PERMISSION_CODE = 1;
    public static final int CAMERA_PERMISSION_CODE = 2;
    public static final int LOCATION_PERMISSION_CODE = 3;
    public static final int SMS_PERMISSION_CODE = 4;
    public static final int WRITE_STORAGE_CODE = 5;
    public static final int VIBRATE_CODE = 6;

    public static boolean checkForGallery(Context c){
        int result = ContextCompat.checkSelfPermission(c, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            //Permission is already given..
            return true;
        }else{
            //Request storage permission..
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) c,Manifest.permission.READ_EXTERNAL_STORAGE)){
                //If the user has denied the permission previously your code will come to this block
                //Here you can explain why you need this permission
                //Explain here why you need this permission
                Toast.makeText(c, "This permission is needed to read phone's storage", Toast.LENGTH_SHORT).show();
            }
            //And finally ask for the permission
            ActivityCompat.requestPermissions((Activity)c,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }

        return false;
    }

    public static boolean checkForCamera(Context c){
        int result = ContextCompat.checkSelfPermission(c, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED){
            //Permission is already given..
            return true;
        }else{
            //Request storage permission..
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) c,Manifest.permission.CAMERA)){
                //If the user has denied the permission previously your code will come to this block
                //Here you can explain why you need this permission
                //Explain here why you need this permission
                Toast.makeText(c, "This permission is needed to capture images", Toast.LENGTH_SHORT).show();
            }
            //And finally ask for the permission
            ActivityCompat.requestPermissions((Activity)c,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }

        return false;
    }

    public static boolean checkForLocation(Context c){
        int result1 = ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED){
            //Permission is already given..
            return true;
        }else{
            //Request storage permission..
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) c,Manifest.permission.ACCESS_FINE_LOCATION)){
                //If the user has denied the permission previously your code will come to this block
                //Here you can explain why you need this permission
                //Explain here why you need this permission
                Toast.makeText(c, "This permission is needed to access device's location", Toast.LENGTH_SHORT).show();
            }
            //And finally ask for the permission
            ActivityCompat.requestPermissions((Activity)c,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_CODE);
        }
        return false;
    }

    public static boolean checkForSms(Context c){
        int result = ContextCompat.checkSelfPermission(c, Manifest.permission.RECEIVE_SMS);
        if (result == PackageManager.PERMISSION_GRANTED){
            //Permission is already given..
            return true;
        }else{
            //Request storage permission..
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) c,Manifest.permission.RECEIVE_SMS)){
                //If the user has denied the permission previously your code will come to this block
                //Here you can explain why you need this permission
                //Explain here why you need this permission
                Toast.makeText(c, "This permission is needed to automatic read OTP", Toast.LENGTH_SHORT).show();
            }
            //And finally ask for the permission
            ActivityCompat.requestPermissions((Activity)c,new String[]{Manifest.permission.RECEIVE_SMS},SMS_PERMISSION_CODE);
        }
        return false;
    }

    public static boolean checkForWriteStorage(Context c){
        int result = ContextCompat.checkSelfPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            //Permission is already given..
            return true;
        }else{
            //Request storage permission..
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) c,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                //If the user has denied the permission previously your code will come to this block
                //Here you can explain why you need this permission
                //Explain here why you need this permission
                Toast.makeText(c, "This permission is needed to write external storage", Toast.LENGTH_SHORT).show();
            }
            //And finally ask for the permission
            ActivityCompat.requestPermissions((Activity)c,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_STORAGE_CODE);
        }
        return false;
    }

    public static boolean checkForVibrate(Context c){
        int result = ContextCompat.checkSelfPermission(c, Manifest.permission.VIBRATE);
        if (result == PackageManager.PERMISSION_GRANTED){
            //Permission is already given..
            return true;
        }else{
            //Request storage permission..
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) c,Manifest.permission.VIBRATE)){
                //If the user has denied the permission previously your code will come to this block
                //Here you can explain why you need this permission
                //Explain here why you need this permission
                Toast.makeText(c, "This permission is needed to trigger vibration", Toast.LENGTH_SHORT).show();
            }
            //And finally ask for the permission
            ActivityCompat.requestPermissions((Activity)c,new String[]{Manifest.permission.VIBRATE},VIBRATE_CODE);
        }
        return false;
    }
}
