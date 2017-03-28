package in.truskills.liveexams.Miscellaneous;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

public class ShutDownService extends BroadcastReceiver {

    public ShutDownService() {
    }

    SharedPreferences dataPrefs,allow;

    @Override
    public void onReceive(Context context, Intent intent) {

//        QuizDatabase ob=new QuizDatabase(context);
//        SharedPreferences dataPrefs=context.getSharedPreferences("dataPrefs",Context.MODE_PRIVATE);
//        SharedPreferences quizPrefs=context.getSharedPreferences("quizPrefs",Context.MODE_PRIVATE);
////        SharedPreferences prefs=context.getSharedPreferences("dataPrefs",Context.MODE_PRIVATE);
//        Log.d("switchOff", "onReceive: inSwitchOff");
//        String folder_main = ".LiveExams";
//        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
//        if (f.exists()) {
//            ConstantsDefined.deleteDir(f);
//        }
//        ob.deleteMyTable();
//        SharedPreferences.Editor e=dataPrefs.edit();
//        e.clear();
//        e.apply();
//        e=quizPrefs.edit();
//        e.clear();
//        e.apply();

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
