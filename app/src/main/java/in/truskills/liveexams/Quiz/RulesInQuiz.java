package in.truskills.liveexams.Quiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.Miscellaneous.SplashScreen;
import in.truskills.liveexams.Miscellaneous.SubmitAnswerPaper;
import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

public class RulesInQuiz extends AppCompatActivity {

    private static final String TAG = "checkkkkk-InRules";
    TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8;
    SharedPreferences quizPrefs,dataPrefs;
    QuizDatabase ob;
    public static boolean visible;
    Handler h;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules_in_quiz);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        quizPrefs=getSharedPreferences("quizPrefs", Context.MODE_PRIVATE);
        dataPrefs=getSharedPreferences("dataPrefs", Context.MODE_PRIVATE);

        h=new Handler();

        ob=new QuizDatabase(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);

        getSupportActionBar().setTitle("RULES");

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        tv6 = (TextView) findViewById(R.id.tv6);
        tv7 = (TextView) findViewById(R.id.tv7);
        tv8 = (TextView) findViewById(R.id.tv8);

        Typeface tff = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        tv1.setTypeface(tff);
        tv2.setTypeface(tff);
        tv3.setTypeface(tff);
        tv4.setTypeface(tff);
        tv5.setTypeface(tff);
        tv6.setTypeface(tff);
        tv7.setTypeface(tff);
        tv8.setTypeface(tff);
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        SharedPreferences.Editor e=quizPrefs.edit();
        e.putInt("exit",0);
        e.apply();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        if(quizPrefs.getInt("exit",0)==0){
//            Toast.makeText(this, "don'tSubmitQuiz", Toast.LENGTH_SHORT).show();
        }else{
            visible=false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(ConstantsDefined.time);
                    }catch (Exception e){

                    }finally {
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                if(visible){

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

                                        SubmitAnswerPaper submitAnswerPaper=new SubmitAnswerPaper();
                                        submitAnswerPaper.submit(ob,RulesInQuiz.this,jsonObject.toString(),userId,examId);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ((Activity)RulesInQuiz.this).finish();
                                    Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                }
            }).start();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed: ");
        SharedPreferences.Editor e=quizPrefs.edit();
        e.putInt("exit",0);
        e.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        SharedPreferences.Editor e=quizPrefs.edit();
        e.putInt("exit",1);
        e.apply();
        visible=true;
    }
}
