package in.truskills.liveexams.Quiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.Miscellaneous.SplashScreen;
import in.truskills.liveexams.Miscellaneous.SubmitAnswerPaper;
import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

public class SectionNamesDisplay extends Activity {

    ArrayList<String> name;
    RecyclerView mySectionsList;
    int mySrno;
    String srNo;
    String myName = "";
    HashMap<String, String> map;
    LinearLayoutManager linearLayoutManager;
    SectionNamesDisplayAdapter sectionNamesDisplayAdapter;
    QuizDatabase ob;
    SharedPreferences quizPrefs,dataPrefs;
    public static boolean visible;
    Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_section_names_display);

        this.setFinishOnTouchOutside(false);

        srNo = getIntent().getStringExtra("serialNumber");
        mySrno = Integer.parseInt(srNo);
        h=new Handler();

        quizPrefs=getSharedPreferences("quizPrefs", Context.MODE_PRIVATE);
        dataPrefs=getSharedPreferences("dataPrefs", Context.MODE_PRIVATE);

        mySectionsList = (RecyclerView) findViewById(R.id.mySectionsList);
        TextView sectionText = (TextView) findViewById(R.id.sectionText);
        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        sectionText.setTypeface(tff1);

        name = new ArrayList<>();
        map = new HashMap<>();

        ob = new QuizDatabase(SectionNamesDisplay.this);

        HashMap<String, ArrayList<String>> map = ob.getAllStringValuesPerSection();
        name = map.get("SectionNameList");

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
    }

    @Override
    protected void onPause() {
        super.onPause();
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
                                        submitAnswerPaper.submit(ob,SectionNamesDisplay.this,jsonObject.toString(),userId,examId);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
//                                    Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(intent);
//                                    finish();
                                }
                            }
                        });
                    }
                }
            }).start();
        }
    }
}
