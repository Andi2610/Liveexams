package in.truskills.liveexams.Quiz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.Miscellaneous.SubmitAnswerPaper;
import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

/**
 * This is for all sections summary page where finish button is also visible..
 *
 * Functions:
 * 1. onCreate() : for basic stuff, finish button click & first time indication..
 * 2. makeInvisible() : hide the first time indication stuff..
 * 3. submit() : for submitting quiz by calling answerPaper api..
 * 4. onSupportNavigateUp() : for back button press on toolbar..
 * 5. onCreateOptionsMenu() and onOptionsItemSelected() : for rules option menu page..
 * 6. onPause() , onResume(), onBackPressed() : for navigation and its handling with quiz submission..
 * 7. onDestroy() : submit the quiz..
 *
 * API calls made :
 * 1. /api/answerPaper : (POST api with parameters userId, examId and answerPaper) : for submitting answer paper..
 */

public class AllSectionsSummary extends AppCompatActivity {

    private static final String TAG = "checkkkkk-InSummary";
    LinearLayoutManager linearLayoutManager;
    AllSectionsSummaryAdapter allSectionsSummaryAdapter;
    RecyclerView allSectionsList;
    long start, end, diff;
    ArrayList<String> sectionName;
    ArrayList<ArrayList<Integer>> questionArray;
    QuizDatabase ob;
    Button finishButton;
    String examId, userId, selectedLanguage,myDate;
    RequestQueue requestQueue;
    SharedPreferences prefs,dataPrefs,quizPrefs,allow,firstTime,firstTimeForRules;
    Handler h;
    Thread t;
    public static boolean visible;
    LinearLayout awareLayoutForRules;
    TextView textForAwareForRules;
    ImageView imageForAwareForRules;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sections_summary);

        //onResume animation..
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

        //Set toolbar..
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);
        getSupportActionBar().setTitle("SUMMARY");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        start=getIntent().getLongExtra("start",0);

        //All shared preferences..
        prefs=getSharedPreferences("prefs",Context.MODE_PRIVATE);
        dataPrefs=getSharedPreferences("dataPrefs",Context.MODE_PRIVATE);
        quizPrefs=getSharedPreferences("quizPrefs",Context.MODE_PRIVATE);
        allow=getSharedPreferences("allow",Context.MODE_PRIVATE);
        firstTime=getSharedPreferences("firstTime",Context.MODE_PRIVATE);
        firstTimeForRules=getSharedPreferences("firstTimeForRules",Context.MODE_PRIVATE);

        //Render layouts..
        awareLayoutForRules=(LinearLayout)findViewById(R.id.awareLayoutForRules);
        textForAwareForRules=(TextView)findViewById(R.id.textForAwareForRules);
        imageForAwareForRules=(ImageView)findViewById(R.id.imageForAwareForRules);
        allSectionsList = (RecyclerView) findViewById(R.id.allSectionsList);
        finishButton = (Button) findViewById(R.id.finishButton);

        //Get intent variables..
        examId = getIntent().getStringExtra("examId");
        userId = getIntent().getStringExtra("userId");
        selectedLanguage = getIntent().getStringExtra("selectedLanguage");
        myDate = getIntent().getStringExtra("date");

        h = new Handler();
        ob = new QuizDatabase(AllSectionsSummary.this);
        sectionName = new ArrayList<>();
        questionArray = new ArrayList<>();

        HashMap<String, ArrayList<String>> map = ob.getAllStringValuesPerSection();
        sectionName = map.get("SectionNameList");
        for (int i = 0; i < sectionName.size(); ++i) {
            int sI = ob.getIntValuesPerSectionBySerialNumber(i, QuizDatabase.SectionIndex);
            HashMap<String, ArrayList<Integer>> my_map = ob.getAllIntValuesPerQuestionBySectionIndex(sI);
            ArrayList<Integer> my_fragment_index_list ;
            my_fragment_index_list = my_map.get("FragmentIndexList");
            questionArray.add(my_fragment_index_list);
        }

        //For all sections summary adapter..
        allSectionsSummaryAdapter = new AllSectionsSummaryAdapter(sectionName, questionArray, AllSectionsSummary.this);
        linearLayoutManager = new LinearLayoutManager(this);
        allSectionsList.setLayoutManager(linearLayoutManager);
        allSectionsList.setItemAnimator(new DefaultItemAnimator());
        allSectionsList.setAdapter(allSectionsSummaryAdapter);
        allSectionsSummaryAdapter.notifyDataSetChanged();

        //Set typeface..
        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        finishButton.setTypeface(tff1);
        textForAwareForRules.setTypeface(tff1);

        //Finish button click calls submit() function..
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AllSectionsSummary.this);
                builder.setMessage("Are you sure you want to exit the quiz?")
                        .setTitle("ALERT")
                        .setIcon(R.drawable.alert_icon)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                submit();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        //Show indication for RULES the first time quiz is displayed..
        if(firstTimeForRules.getInt("firstTimeForRules",1)==1){

            SharedPreferences.Editor eee=firstTimeForRules.edit();
            eee.putInt("firstTimeForRules",0);
            eee.apply();

            allSectionsList.setAlpha((float)0.2);
            finishButton.setAlpha((float)0.2);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(5000);
                    }catch (Exception ee){
                        ee.printStackTrace();
                    }finally {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                makeInvisible();
                            }
                        });

                    }
                }
            }).start();

        }else{
            //hide..
            makeInvisible();
        }
    }

    public void makeInvisible(){
        awareLayoutForRules.setVisibility(View.GONE);
        allSectionsList.setAlpha((float)1);
        finishButton.setAlpha((float)1);

    }

    public void submit() {

        //Update time for the last page..
        updateTimeForPreviousPage();

        //Prepare the answers to be submitted to the server..
        JSONArray jsonArray = ob.getQuizResult();
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", jsonArray);
            jsonObject.put("selectedLanguage", selectedLanguage);
            jsonObject.put("date", myDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Store that submit button has been clicked.. because..
        //in case the paper isn't submitted due to net problem, then it can happen once the internet resumes..
        SharedPreferences.Editor e=dataPrefs.edit();
        e.putInt("submit",1);
        e.apply();

        //For https connection..
        ConstantsDefined.updateAndroidSecurityProvider(this);
        ConstantsDefined.beforeVolleyConnect();

        //Url to be connected to..
        String myurl = ConstantsDefined.api + "answerPaper";

        //Show dialog..
        final ProgressDialog progressDialog = new ProgressDialog(AllSectionsSummary.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Submitting your answers.. Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        //Make a request..
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                myurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //On getting the response..
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject1 = new JSONObject(response);
                    String success = jsonObject1.getString("success");
                    String result = jsonObject1.getString("response");
                    if (success.equals("true")) {

                        //Delete table..
                        ob.deleteMyTable();

                        //Clear shared preferences..
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

                        //Show appropriate toast message..
                        Toast.makeText(AllSectionsSummary.this, result+"\n" +
                                "Result will be generated after the exam duration ends..", Toast.LENGTH_LONG).show();

                        //Start feedback activity clearing all other previous activities on stack ..
                        Intent intent = new Intent(AllSectionsSummary.this, FeedbackActivity.class);
                        intent.putExtra("examId",examId);
                        intent.putExtra("userId",userId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                        startActivity(intent);
                        finish();

                    } else {

                        //Delete table..
                        ob.deleteMyTable();

                        //Clear shared preferences..
                        SharedPreferences.Editor e=dataPrefs.edit();
                        e.clear();
                        e.apply();

                        //Show appropriate toast message..
                        Toast.makeText(AllSectionsSummary.this, "Something went wrong..\n" +
                                "Paper couldn't be submitted..", Toast.LENGTH_LONG).show();

                        //Start feedback activity..
                        Intent intent = new Intent(AllSectionsSummary.this, FeedbackActivity.class);
                        intent.putExtra("examId",examId);
                        intent.putExtra("userId",userId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Hide dialog..
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //In case the connection to the Api couldn't be established..

                //Hide dialog..
                if(progressDialog!=null)
                progressDialog.dismiss();

                //Show error message..
                if(ConstantsDefined.isOnline(AllSectionsSummary.this)){
                    Toast.makeText(AllSectionsSummary.this, "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                }else{

                    //Do this to prevent user from starting any other quiz once, this answer paper is submitted first..
                    SharedPreferences.Editor e=allow.edit();
                    e.putInt("allow",0);
                    e.apply();

                    //Show appropriate toast message..
                    Toast.makeText(AllSectionsSummary.this, "Sorry! No internet connection\nYour answers will be submitted once reconnected to internet", Toast.LENGTH_LONG).show();

                    //Start MainActivity clearing all other activities from stack..
                    Intent intent = new Intent(AllSectionsSummary.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                    startActivity(intent);
                    finish();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Put all the required parameters for the post request..
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", userId);
                params.put("examId", examId);
                params.put("answerPaper", jsonObject.toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {

        //On pressing back button on toolbar..
        super.onBackPressed();

        //Do this to indicate the user is not leaving the quiz, but only leaving the activity..
        //So don't submit the quiz..
        SharedPreferences.Editor e=quizPrefs.edit();
        e.putInt("exit",0);
        e.apply();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //For rules menu..
        getMenuInflater().inflate(R.menu.rules_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //On click on rules icon.. open RulesInQuiz activity..
            case R.id.rulesIcon:

                //Do this to indicate the user is not leaving the quiz, but only leaving the activity..
                //So don't submit the quiz..
                SharedPreferences.Editor e=quizPrefs.edit();
                e.putInt("exit",0);
                e.apply();

                //Start activity..
                Intent i = new Intent(this, RulesInQuiz.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //onPause animation..
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        if(quizPrefs.getInt("exit",0)==0){
            //Do nothing..
        }else{
            visible=false;
            int ans=dataPrefs.getInt("submit",0);
            if(ans!=0)
                handler.postDelayed(sendData,ConstantsDefined.time);
        }

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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        SharedPreferences.Editor e=quizPrefs.edit();
        e.putInt("exit",0);
        e.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
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
                    submitAnswerPaper.submit(ob, AllSectionsSummary.this, jsonObject.toString(), userId, examId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void updateTimeForPreviousPage(){

        int preIndex = quizPrefs.getInt("previousIndex", 0);
        int preSi = ob.getIntValuesPerQuestionByFragmentIndex(preIndex, QuizDatabase.SectionIndex);
        int preQi = ob.getIntValuesPerQuestionByFragmentIndex(preIndex, QuizDatabase.QuestionIndex);
        int prevTempSr = Integer.parseInt(ob.getValuesForResult(preSi, preQi, QuizDatabase.TempAnswerSerialNumber));
        int prevQS = Integer.parseInt(ob.getValuesForResult(preSi, preQi, QuizDatabase.QuestionStatus));

        Log.d("myData", prevTempSr + " " + prevQS);

        //Update time spent..
        end = System.currentTimeMillis();
        diff = end - start;
        diff = diff / 1000;
        String myTime = ob.getValuesForResult(preSi, preQi, QuizDatabase.TimeSpent);
        long time = Long.parseLong(myTime);
        time = time + diff;
        ob.updateValuesForResult(preSi, preQi, QuizDatabase.TimeSpent, time + "");

    }
}
