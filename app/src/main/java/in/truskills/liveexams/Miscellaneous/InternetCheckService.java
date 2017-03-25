package in.truskills.liveexams.Miscellaneous;

import android.app.Activity;
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

    SharedPreferences dataPrefs,allow;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        if(isConnected){
//            Toast.makeText(context, "Internet Connection Lost", Toast.LENGTH_LONG).show();
        }
        else{
//            Toast.makeText(context, "Internet Connected", Toast.LENGTH_LONG).show();
            QuizDatabase ob=new QuizDatabase(context);
            dataPrefs=context.getSharedPreferences("dataPrefs",Context.MODE_PRIVATE);
            allow=context.getSharedPreferences("allow",Context.MODE_PRIVATE);
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
                final JSONObject jsonObject = new JSONObject();
                String selectedLanguage=dataPrefs.getString("selectedLanguage","");
                String myDate=dataPrefs.getString("date","");
                String userId=dataPrefs.getString("userId","");
                String examId=dataPrefs.getString("examId","");

                try {
                    jsonObject.put("result", jsonArray);
                    jsonObject.put("selectedLanguage", selectedLanguage);
                    jsonObject.put("date", myDate);
//                    Toast.makeText(context, "user:"+userId+" exam:"+examId, Toast.LENGTH_SHORT).show();
                    Log.d("response", "onReceive: "+userId+" "+examId);
                    SubmitAnswerPaper submitAnswerPaper=new SubmitAnswerPaper();
                    submitAnswerPaper.submit(ob,context,jsonObject.toString(),userId,examId);
//                    submit(ob,context,jsonObject.toString(),userId,examId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
