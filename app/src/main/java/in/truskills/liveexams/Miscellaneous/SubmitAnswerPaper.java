package in.truskills.liveexams.Miscellaneous;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
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

import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

/**
 * Created by Shivansh Gupta on 14-03-2017.
 */

public class SubmitAnswerPaper {

    SharedPreferences dataPrefs,quizPrefs,allow;

    public void submit(final QuizDatabase ob, final Context context, final String result, final String userId, final String examId){

        dataPrefs=context.getSharedPreferences("dataPrefs",Context.MODE_PRIVATE);
        quizPrefs=context.getSharedPreferences("quizPrefs",Context.MODE_PRIVATE);
        allow=context.getSharedPreferences("allow",Context.MODE_PRIVATE);

        ConstantsDefined.updateAndroidSecurityProvider((Activity) context);
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
                    String result = jsonObject1.getString("response");
                    if (success.equals("true")) {
                        String folder_main = "LiveExams";
                        Toast.makeText(context, "Your answer paper has been submitted..", Toast.LENGTH_LONG).show();
                        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                        if (f.exists()) {
                            deleteDir(f);
                        }
                        ob.deleteMyTable();
                        SharedPreferences.Editor e=dataPrefs.edit();
                        e.clear();
                        e.apply();
                        SharedPreferences.Editor ee=quizPrefs.edit();
                        ee.clear();
                        ee.apply();
                        SharedPreferences.Editor eee=allow.edit();
                        eee.putInt("allow",1);
                        ee.apply();
                        Intent intent = new Intent(context.getApplicationContext(), SplashScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                        ((Activity)context).finish();

                    } else {
                        ob.deleteMyTable();
                        SharedPreferences.Editor e=dataPrefs.edit();
                        e.clear();
                        e.apply();
                        String folder_main = "LiveExams";
                        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                        if (f.exists()) {
                            deleteDir(f);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("errors", "onErrorResponse: "+error);
                String folder_main = "LiveExams";
                File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                if (f.exists()) {
                    deleteDir(f);
                }
            }
        });
        requestQueue.add(stringRequest);
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

}
