package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.Miscellaneous.SubmitAnswerPaper;
import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

/**
 * This is for rules display inside quiz..
 *
 * Functions:
 * 1. onCreate() : for basic stuff..
 * 2. onSupportNavigateUp(),onResume(),onPause(),onBackPressed() : for navigation and its handling with quiz submission..
 */
public class RulesInQuiz extends AppCompatActivity {

    private static final String TAG = "checkkkkk-InRules";
    TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8,tv9,tv10;
    SharedPreferences quizPrefs,dataPrefs;
    QuizDatabase ob;
    public static boolean visible;
    Handler h;
    Thread t;
    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules_in_quiz);

        //For toolbar..
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);
        getSupportActionBar().setTitle("RULES");

        //shared preferences..
        quizPrefs=getSharedPreferences("quizPrefs", Context.MODE_PRIVATE);
        dataPrefs=getSharedPreferences("dataPrefs", Context.MODE_PRIVATE);

        h=new Handler();

        ob=new QuizDatabase(this);

        //Render elements from layout..
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        tv6 = (TextView) findViewById(R.id.tv6);
        tv7 = (TextView) findViewById(R.id.tv7);
        tv8 = (TextView) findViewById(R.id.tv8);
        tv9 = (TextView) findViewById(R.id.tv9);
        tv10 = (TextView) findViewById(R.id.tv10);

        //Set typeface..
        Typeface tff = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        tv1.setTypeface(tff);
        tv2.setTypeface(tff);
        tv3.setTypeface(tff);
        tv4.setTypeface(tff);
        tv5.setTypeface(tff);
        tv6.setTypeface(tff);
        tv7.setTypeface(tff);
        tv8.setTypeface(tff);
        tv9.setTypeface(tff);
        tv10.setTypeface(tff);
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
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        Log.d(TAG, "onPause: ");
        if(quizPrefs.getInt("exit",0)==0){
            //Do nothing..
        }else{
            //Do this for finishing quiz after 1 minute..
            visible=false;
            handler.postDelayed(sendData,ConstantsDefined.time);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        SharedPreferences.Editor e=quizPrefs.edit();
        e.putInt("exit",0);
        e.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        Log.d(TAG, "onResume: ");
        SharedPreferences.Editor e=quizPrefs.edit();
        e.putInt("exit",1);
        e.apply();
        visible=true;
        handler.removeCallbacks(sendData);

        SharedPreferences.Editor ee = dataPrefs.edit();
        ee.putInt("submit", 0);
        ee.apply();
    }

    private final Runnable sendData = new Runnable() {
        public void run() {
            if (visible) {
                //Do nothing..
            } else {

                //Submit answer paper..
                JSONArray jsonArray = ob.getQuizResult();
                final JSONObject jsonObject = new JSONObject();
                String selectedLanguage = dataPrefs.getString("selectedLanguage", "");
                String myDate = dataPrefs.getString("date", "");
                String userId = dataPrefs.getString("userId", "");
                String examId = dataPrefs.getString("examId", "");

                SharedPreferences.Editor ee = dataPrefs.edit();
                ee.putInt("submit", 1);
                ee.apply();

                try {
                    jsonObject.put("result", jsonArray);
                    jsonObject.put("selectedLanguage", selectedLanguage);
                    jsonObject.put("date", myDate);

                    SubmitAnswerPaper submitAnswerPaper = new SubmitAnswerPaper();
                    submitAnswerPaper.submit(ob, RulesInQuiz.this, jsonObject.toString(), userId, examId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

    };
}
