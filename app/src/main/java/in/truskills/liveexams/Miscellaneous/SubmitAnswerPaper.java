package in.truskills.liveexams.Miscellaneous;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.QuizDatabase;
import in.truskills.liveexams.authentication.SplashScreen;

/**
 * Created by Shivansh Gupta on 14-03-2017.
 */

public class SubmitAnswerPaper {

    SharedPreferences dataPrefs,quizPrefs,allow,firstTime,firstTimeForRules;
    Handler h;

    public void submit(final QuizDatabase ob, final Context context, final String result, final String userId, final String examId){

        dataPrefs=context.getSharedPreferences("dataPrefs",Context.MODE_PRIVATE);
        quizPrefs=context.getSharedPreferences("quizPrefs",Context.MODE_PRIVATE);
        firstTime=context.getSharedPreferences("firstTime",Context.MODE_PRIVATE);
        firstTimeForRules=context.getSharedPreferences("firstTimeForRules",Context.MODE_PRIVATE);
        h=new Handler();

//        ConstantsDefined.updateAndroidSecurityProvider((Activity) context);
        ConstantsDefined.beforeVolleyConnect();

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String myurl = ConstantsDefined.api + "answerPaper";
        Map<String, String> params = new HashMap<String, String>();
        Log.d("params", "getParams: "+userId+" "+examId+" "+result);
        params.put("userId", userId);
        params.put("examId", examId);
        params.put("answerPaper", result);
        JSONObject parameters = new JSONObject(params);
        //Make a request..
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST,
                myurl, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //On getting the response..
                try {
//                    JSONObject jsonObject1 = new JSONObject(response);
                    JSONObject jsonObject1=response;
                    Log.d("response", "onResponse: "+response);
                    String success = jsonObject1.getString("success");
                    Log.d("response", "onResponse: "+success);
                    final String result = jsonObject1.getString("response");
                    if (success.equals("true")) {
                        String folder_main = ".LiveExams";
                        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                        if (f.exists()) {
                            ConstantsDefined.deleteDir(f);
                        }
                        ob.deleteMyTable();
                        SharedPreferences.Editor e=dataPrefs.edit();
                        e.clear();
                        e.apply();
                        SharedPreferences.Editor ee=quizPrefs.edit();
                        ee.clear();
                        ee.apply();
                        SharedPreferences.Editor eee=firstTime.edit();
                        eee.clear();
                        eee.apply();
                        SharedPreferences.Editor eeeee=firstTimeForRules.edit();
                        eeeee.clear();
                        eeeee.apply();
                        allow=context.getSharedPreferences("allow",Context.MODE_PRIVATE);
                        Log.d("prefsAllowBefore",allow.getInt("allow",1)+"");

                        SharedPreferences.Editor eeee=allow.edit();
                        eeee.putInt("allow",1);
                        eeee.apply();
                        Log.d("prefsAllowAfter",allow.getInt("allow",1)+"");

                        Intent intent = new Intent(context.getApplicationContext(), SplashScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);

                        h.post(new Runnable() {
                            @Override
                            public void run() {

                                final Intent emptyIntent = new Intent(context,SplashScreen.class);
                                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(context)
                                                .setSmallIcon(R.drawable.app_icon)
                                                .setContentTitle("LiveExams")
                                                .setContentText(result)
                                                .setContentIntent(pendingIntent); //Required on Gingerbread and below

                                mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                                mBuilder.setAutoCancel(true);

                                NotificationManager notificationManager = (NotificationManager)context. getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(1, mBuilder.build());
                            }
                        });

                    } else {
                        Toast.makeText(context, "Something went wrong..\n" +
                                "Paper couldn't be submitted..", Toast.LENGTH_LONG).show();

                        ob.deleteMyTable();
                        SharedPreferences.Editor e=dataPrefs.edit();
                        e.clear();
                        e.apply();
                        String folder_main = ".LiveExams";
                        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                        if (f.exists()) {
                            ConstantsDefined.deleteDir(f);
                        }

                        h.post(new Runnable() {
                            @Override
                            public void run() {

                                final Intent emptyIntent = new Intent(context,SplashScreen.class);
                                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(context)
                                                .setSmallIcon(R.drawable.app_icon)
                                                .setContentTitle("LiveExams")
                                                .setContentText("Something went wrong..\n" +
                                                        "Paper couldn't be submitted..")
                                                .setContentIntent(pendingIntent); //Required on Gingerbread and below

                                mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                                mBuilder.setAutoCancel(true);

                                NotificationManager notificationManager = (NotificationManager)context. getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(1, mBuilder.build());
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("errors", "onErrorResponse: "+error);
                String folder_main = ".LiveExams";
                File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                if (f.exists()) {
                    ConstantsDefined.deleteDir(f);
                }
                allow=context.getSharedPreferences("allow",Context.MODE_PRIVATE);
                SharedPreferences.Editor e=allow.edit();
                e.putInt("allow",0);
                e.apply();

                final Intent emptyIntent = new Intent(context,SplashScreen.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.app_icon)
                                .setContentTitle("LiveExams")
                                .setContentText("Something went wrong..\n" +
                                        "Paper couldn't be submitted..\nIt will be submitted once internet resumes..")
                                .setContentIntent(pendingIntent); //Required on Gingerbread and below

                mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                mBuilder.setAutoCancel(true);

                NotificationManager notificationManager = (NotificationManager)context. getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, mBuilder.build());

                Intent intent = new Intent(context.getApplicationContext(), SplashScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        });
        requestQueue.add(stringRequest);
    }

}
