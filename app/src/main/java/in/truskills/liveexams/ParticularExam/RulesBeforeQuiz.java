package in.truskills.liveexams.ParticularExam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.Quiz.QuestionPaperLoadDevelopment;
import in.truskills.liveexams.R;

/**
 * This activity is shown to display rules of quiz before starting question paper load, once the user starts the exam..
 *
 * Functions:
 * 1. onCreate() : For basic tasks and for handling continue and exit button..
 * 2. onResume() : Resume animation..
 * 3. onPause() : Pause animation..
 * 4. onBackPressed() : Pause animation + onBackPressed..
 * 5. onSupportNavigationUp() : onBackPressed on toolbar..
 */

public class RulesBeforeQuiz extends AppCompatActivity {

    TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8,tv9,tv10;
    Button continueButton,exitButton;
    String examId,paperName,selectedLanguage,myDate,myUrl,name;
    SharedPreferences prefs;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rules_before_quiz);

        //Set toolbar..
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);
        getSupportActionBar().setTitle("RULES");

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
        continueButton = (Button) findViewById(R.id.continueButton);
        exitButton = (Button) findViewById(R.id.exitButton);
        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        //Set typeface of required fields..
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
        continueButton.setTypeface(tff);
        exitButton.setTypeface(tff);

        //Get intent variables..
        examId = getIntent().getStringExtra("examId");
        paperName = getIntent().getStringExtra("name");
        selectedLanguage = getIntent().getStringExtra("language");
        myDate = getIntent().getStringExtra("date");
        myUrl = getIntent().getStringExtra("url");
        name = getIntent().getStringExtra("name");

        //On continuing, start questionPaperLoad..
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ConstantsDefined.updateAndroidSecurityProvider(RulesBeforeQuiz.this);
                ConstantsDefined.beforeVolleyConnect();

                String url = ConstantsDefined.api + "joinedExams/" + prefs.getString("userId", "") + examId ;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        url, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String success = response.getString("success");
                            if (success.equals("true")) {
                                String myResponse = response.getJSONObject("response").toString();
                                JSONObject jsonObject = new JSONObject(myResponse);

                            }
                        }
                                catch(JSONException e){
                                    e.printStackTrace();
                                }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("checkForError",error.toString());



                        if(ConstantsDefined.isOnline(getBaseContext())){
                            //Do nothing..
                            if(RulesBeforeQuiz.this !=null)
                                Toast.makeText(getBaseContext(), "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                        }else{
                            if(RulesBeforeQuiz.this !=null)
                                Toast.makeText(getBaseContext(), "Sorry! Couldn't connect", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                requestQueue.add(jsonObjectRequest);

                //if()compare that boolean variable if it's false
                Intent i = new Intent(RulesBeforeQuiz.this, QuestionPaperLoadDevelopment.class);
                i.putExtra("examId", examId);
                i.putExtra("name", name);
                i.putExtra("language", selectedLanguage);
                i.putExtra("date",myDate);
                i.putExtra("url",myUrl);
                startActivity(i);
                finish();
                //else toast u r giving same exam from different device
            }
        });

        //On exiting, call onBackPressed..
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected void onResume()
    {
        super.onResume();
        //Resume animation..
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    protected void onPause()
    {
        super.onPause();
        //Pause animation..
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Pause animation..
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    @Override
    public boolean onSupportNavigateUp() {
        //On backPress on toolbar..
        super.onBackPressed();
        return true;
    }
}
