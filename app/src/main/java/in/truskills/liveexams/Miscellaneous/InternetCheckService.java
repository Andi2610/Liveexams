package in.truskills.liveexams.Miscellaneous;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

public class InternetCheckService extends BroadcastReceiver {
    public InternetCheckService() {
    }

    SharedPreferences dataPrefs;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        if(isConnected){
            Toast.makeText(context, "Internet Connection Lost", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(context, "Internet Connected", Toast.LENGTH_LONG).show();
            QuizDatabase ob=new QuizDatabase(context);
            dataPrefs=context.getSharedPreferences("dataPrefs",Context.MODE_PRIVATE);
            Log.d("status", "onReceive: "+dataPrefs.getInt("submit",0));
            int ans=dataPrefs.getInt("submit",0);
            if(ob==null){
                Log.d("status", "onReceive: null");
            }else{
                Log.d("status", "onReceive: notNull");
            }
//            boolean ans=ob.getStatusOfResultTable();
            Log.d("status", "onReceive: "+ans);
            if(ans==0){
//                Toast.makeText(context, "Table empty", Toast.LENGTH_LONG).show();
            }else{
                JSONArray jsonArray = ob.getQuizResult();
//                Toast.makeText(context, "Table exists", Toast.LENGTH_LONG).show();
                final JSONObject jsonObject = new JSONObject();
//                String selectedLanguage=ob.getDataFromDataTable(QuizDatabase.selectedLanguage);
//                String myDate=ob.getDataFromDataTable(QuizDatabase.date);
//                String userId=ob.getDataFromDataTable(QuizDatabase.userId);
//                String examId=ob.getDataFromDataTable(QuizDatabase.examId);

                String selectedLanguage=dataPrefs.getString("selectedLanguage","");
                String myDate=dataPrefs.getString("date","");
                String userId=dataPrefs.getString("userId","");
                String examId=dataPrefs.getString("examId","");

                Log.d("dataInCheck", "onCreate: "+selectedLanguage+" "+myDate+" "+userId+" "+examId);
                try {
                    jsonObject.put("result", jsonArray);
                    jsonObject.put("selectedLanguage", selectedLanguage);
                    jsonObject.put("date", myDate);
//                    Toast.makeText(context, "user:"+userId+" exam:"+examId, Toast.LENGTH_SHORT).show();
                    Log.d("response", "onReceive: "+userId+" "+examId);
                    submit(ob,context,jsonObject.toString(),userId,examId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void submit(final QuizDatabase ob,final Context context,final String result,final String userId,final String examId){
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
                        Toast.makeText(context, "Your answer paper has been submitted..", Toast.LENGTH_SHORT).show();
                        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                        if (f.exists()) {
                            deleteDir(f);
                        }
                        ob.deleteMyTable();
                        SharedPreferences.Editor e=dataPrefs.edit();
                        e.clear();
                        e.apply();

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
        })
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//                //Put all the required parameters for the post request..
//                Map<String, String> params = new HashMap<String, String>();
//                Log.d("params", "getParams: "+userId+" "+examId);
//                params.put("userId", userId);
//                params.put("examId", examId);
//                params.put("answerPaper", result);
//                return params;
//            }
//        }
                ;
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
