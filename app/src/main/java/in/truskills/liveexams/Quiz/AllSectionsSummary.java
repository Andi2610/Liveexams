package in.truskills.liveexams.Quiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.Miscellaneous.MyApplication;
import in.truskills.liveexams.Miscellaneous.SplashScreen;
import in.truskills.liveexams.Miscellaneous.SubmitAnswerPaper;
import in.truskills.liveexams.ParticularExamStatistics.RulesInAnswers;
import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

public class AllSectionsSummary extends AppCompatActivity {

    private static final String TAG = "checkkkkk-InSummary";
    LinearLayoutManager linearLayoutManager;
    AllSectionsSummaryAdapter allSectionsSummaryAdapter;
    RecyclerView allSectionsList;
    ArrayList<String> sectionName;
    ArrayList<ArrayList<Integer>> questionArray;
    QuizDatabase ob;
    Button finishButton;
    String examId, userId, selectedLanguage,myDate;
    RequestQueue requestQueue;
    SharedPreferences prefs,dataPrefs,quizPrefs,allow;
    Handler h;
    public static boolean visible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sections_summary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        requestQueue = Volley.newRequestQueue(getApplicationContext());

        prefs=getSharedPreferences("prefs",Context.MODE_PRIVATE);
        dataPrefs=getSharedPreferences("dataPrefs",Context.MODE_PRIVATE);
        quizPrefs=getSharedPreferences("quizPrefs",Context.MODE_PRIVATE);
        allow=getSharedPreferences("allow",Context.MODE_PRIVATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);

        getSupportActionBar().setTitle("SUMMARY");

        allSectionsList = (RecyclerView) findViewById(R.id.allSectionsList);
        finishButton = (Button) findViewById(R.id.finishButton);
        h = new Handler();

        examId = getIntent().getStringExtra("examId");
        userId = getIntent().getStringExtra("userId");
        selectedLanguage = getIntent().getStringExtra("selectedLanguage");
        myDate = getIntent().getStringExtra("date");

        ob = new QuizDatabase(AllSectionsSummary.this);
        sectionName = new ArrayList<>();
        questionArray = new ArrayList<>();

        HashMap<String, ArrayList<String>> map = ob.getAllStringValuesPerSection();
        sectionName = map.get("SectionNameList");

        for (int i = 0; i < sectionName.size(); ++i) {
            int sI = ob.getIntValuesPerSectionBySerialNumber(i, QuizDatabase.SectionIndex);
            HashMap<String, ArrayList<Integer>> my_map = ob.getAllIntValuesPerQuestionBySectionIndex(sI);
            ArrayList<Integer> my_fragment_index_list = new ArrayList<>();
            my_fragment_index_list = my_map.get("FragmentIndexList");
            questionArray.add(my_fragment_index_list);
        }

        allSectionsSummaryAdapter = new AllSectionsSummaryAdapter(sectionName, questionArray, AllSectionsSummary.this);

        linearLayoutManager = new LinearLayoutManager(this);
        allSectionsList.setLayoutManager(linearLayoutManager);
        allSectionsList.setItemAnimator(new DefaultItemAnimator());
        allSectionsList.setAdapter(allSectionsSummaryAdapter);
        allSectionsSummaryAdapter.notifyDataSetChanged();

        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        finishButton.setTypeface(tff1);

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
    }

    public void submit() {
//        ob.getAllValues();
        JSONArray jsonArray = ob.getQuizResult();
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", jsonArray);
            jsonObject.put("selectedLanguage", selectedLanguage);
            jsonObject.put("date", myDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        ob.setSubmitTrue();

        SharedPreferences.Editor e=dataPrefs.edit();
        e.putInt("submit",1);
        e.apply();

//        try {
//            File root = new File(Environment.getExternalStorageDirectory(), "MyResult");
//            if (!root.exists()) {
//                root.mkdirs();
//            }
//            File gpxfile = new File(root, "MyResult");
//            FileWriter writer = new FileWriter(gpxfile);
//            writer.append(jsonObject.toString());
//            writer.flush();
//            writer.close();
////                                    Toast.makeText(AllSectionsSummary.this, "Saved", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //Api to be connected to..
        ConstantsDefined.updateAndroidSecurityProvider(this);
        ConstantsDefined.beforeVolleyConnect();

        String myurl = ConstantsDefined.api + "answerPaper";
        Log.d("myurl", "submit: "+myurl);

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
                        String folder_main = "LiveExams";
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
                        Toast.makeText(AllSectionsSummary.this, result, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AllSectionsSummary.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                        startActivity(intent);
                        finish();
                    } else {
                        JSONObject jsonObject2 = new JSONObject(result);
                        ob.deleteMyTable();
                        SharedPreferences.Editor e=dataPrefs.edit();
                        e.clear();
                        e.apply();
                        String folder_main = "LiveExams";
                        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                        if (f.exists()) {
                            deleteDir(f);
                        }
                        Toast.makeText(AllSectionsSummary.this, jsonObject2.getString("errmsg") + "", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AllSectionsSummary.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //In case the connection to the Api couldn't be established..
                if(progressDialog!=null)
                progressDialog.dismiss();
                Log.d("error", error.toString() + "");

                if(ConstantsDefined.isOnline(AllSectionsSummary.this)){
                    //Do nothing..
                    Toast.makeText(AllSectionsSummary.this, "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                }else{
                    SharedPreferences.Editor e=allow.edit();
                    e.putInt("allow",0);
                    e.apply();
                    Toast.makeText(AllSectionsSummary.this, "Sorry! No internet connection\nYour answers will be submitted once reconnected to internet", Toast.LENGTH_LONG).show();
                    String folder_main = "LiveExams";
                    File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                    if (f.exists()) {
                        deleteDir(f);
                    }
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

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        SharedPreferences.Editor e=quizPrefs.edit();
        e.putInt("exit",0);
        e.apply();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rules_menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rulesIcon:
                SharedPreferences.Editor e=quizPrefs.edit();
                e.putInt("exit",0);
                e.apply();
                Intent i = new Intent(this, RulesInQuiz.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                                        submitAnswerPaper.submit(ob,AllSectionsSummary.this,jsonObject.toString(),userId,examId);
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

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences.Editor e=quizPrefs.edit();
        e.putInt("exit",1);
        e.apply();
        visible=true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences.Editor e=quizPrefs.edit();
        e.putInt("exit",0);
        e.apply();
    }
}
