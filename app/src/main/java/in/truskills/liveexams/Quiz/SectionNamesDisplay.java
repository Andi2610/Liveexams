package in.truskills.liveexams.Quiz;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.Miscellaneous.SubmitAnswerPaper;
import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

/**
 * This is for section names display dialog..
 */

public class SectionNamesDisplay extends Activity {

    ArrayList<String> name;
    RecyclerView mySectionsList;
    int mySrno;
    String srNo;
    HashMap<String, String> map;
    LinearLayoutManager linearLayoutManager;
    SectionNamesDisplayAdapter sectionNamesDisplayAdapter;
    QuizDatabase ob;
    SharedPreferences quizPrefs,dataPrefs;
    public static boolean visible;
    Handler h;
    Thread t;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_section_names_display);

        //To prevent dialog from dismissing on touch on anywhere outside..
        this.setFinishOnTouchOutside(false);

        srNo = getIntent().getStringExtra("serialNumber");
        mySrno = Integer.parseInt(srNo);

        h=new Handler();

        //shared preferences..
        quizPrefs=getSharedPreferences("quizPrefs", Context.MODE_PRIVATE);
        dataPrefs=getSharedPreferences("dataPrefs", Context.MODE_PRIVATE);

        //render elements from layout..
        mySectionsList = (RecyclerView) findViewById(R.id.mySectionsList);
        TextView sectionText = (TextView) findViewById(R.id.sectionText);

        //set typeface..
        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        sectionText.setTypeface(tff1);

        name = new ArrayList<>();
        map = new HashMap<>();

        ob = new QuizDatabase(SectionNamesDisplay.this);

        HashMap<String, ArrayList<String>> map = ob.getAllStringValuesPerSection();
        name = map.get("SectionNameList");

        //for section names display adapter..
        sectionNamesDisplayAdapter = new SectionNamesDisplayAdapter(name, SectionNamesDisplay.this, mySrno);
        linearLayoutManager = new LinearLayoutManager(this);
        mySectionsList.setLayoutManager(linearLayoutManager);
        mySectionsList.setItemAnimator(new DefaultItemAnimator());
        mySectionsList.setAdapter(sectionNamesDisplayAdapter);
        sectionNamesDisplayAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences.Editor e=quizPrefs.edit();
        e.putInt("exit",0);
        e.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences.Editor e=quizPrefs.edit();
        e.putInt("exit",1);
        e.apply();
        visible=true;
        handler.removeCallbacks(sendData);

        SharedPreferences.Editor ee = dataPrefs.edit();
        ee.putInt("submit", 0);
        ee.apply();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(quizPrefs.getInt("exit",0)==0){
            //Do nothing..
        }else{
            visible=false;
            handler.postDelayed(sendData,ConstantsDefined.time);
        }
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
                    submitAnswerPaper.submit(ob, SectionNamesDisplay.this, jsonObject.toString(), userId, examId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

    };

}
