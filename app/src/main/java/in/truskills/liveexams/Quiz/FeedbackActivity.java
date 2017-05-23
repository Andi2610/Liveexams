package in.truskills.liveexams.Quiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.R;

/**
 * This is for feedback page..
 *
 * Functions:
 * 1. onCreate() : for basic stuff and call setQuestions() method..
 * 2. setQuestions() : for calling feedbackQuestions api and getting a list of questions to be displayed..
 * 3. setFeedback() : to prepare feedback response and call postFeedback api..
 * 4. onSuppportNavigationUp() : to handle cross button press on toolbar..
 * 5. onBackPressed() : do nothing.. to disable back button..
 *
 * API calls made:
 * 1. /api/feedbackQuestions : (GET api) : to get feedback questions for displaying..
 * 2. /api/postFeedback : (POST api with parameters: feedback,examId,userId): to submit feedback response to server..
 */

public class FeedbackActivity extends AppCompatActivity {

    RecyclerView feedbackList;
    FeedbackListAdapter feedbackListAdapter;
    Button submit,cancel;
    TextView heading;
    RequestQueue requestQueue;
    ArrayList<String> questionIdList=new ArrayList<>();
    ArrayList<String> questionTextList=new ArrayList<>();
    ArrayList<String> questionTopicList=new ArrayList<>();
    ArrayList<String> questionNumberList=new ArrayList<>();
    Handler h;
    JSONArray array;
    ProgressDialog dialog;
    AVLoadingIndicatorView aviLoad;
    String examId,userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        //For toolbar..
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        getSupportActionBar().setTitle("FEEDBACK");

        //Get intent variables..
        examId=getIntent().getStringExtra("examId");
        userId=getIntent().getStringExtra("userId");

        aviLoad=(AVLoadingIndicatorView)findViewById(R.id.aviLoad);

        h=new Handler();

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);

        //Render elements from layout..
        submit=(Button)findViewById(R.id.submit);
        cancel=(Button)findViewById(R.id.cancel);
        heading=(TextView) findViewById(R.id.heading);

        //set typeface..
        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        heading.setTypeface(tff1);

        //For feedback list..
        feedbackList=(RecyclerView)findViewById(R.id.feedbackList);
        feedbackList.setLayoutManager(linearLayoutManager);
        feedbackList.setItemAnimator(new DefaultItemAnimator());

        //On submit button click,call setFeedback method to prepare feedback response and send to server..
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    setFeedback();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        //On cancel button press, start Main activity clearing all other activities on stack..
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                startActivity(intent);
                finish();
            }
        });

        //Call this method to set questions display onn feedback page by calling feedbackQuestions api..
        setQuestions();
    }

    public void setFeedback() throws JSONException {

        //Show dialog..
        dialog = new ProgressDialog(FeedbackActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Submitting your feedback.. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        //Get feedback from adapter..
        HashMap<Integer,String> map=FeedbackListAdapter.getFeedback();

        array = new JSONArray();

        for (int i=0;i<questionIdList.size();++i){

            //Prepare feedback response..
            JSONObject j=new JSONObject();
            j.put("questionId",questionIdList.get(i));
            j.put("questionTopic",questionTopicList.get(i));
            int c=i+1;
            j.put("questionNumber",questionNumberList.get(i));
            j.put("feedback",map.get(c));
            array.put(j);
        }

        //For https connection..
        ConstantsDefined.updateAndroidSecurityProvider(FeedbackActivity.this);
        ConstantsDefined.beforeVolleyConnect();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        String url=ConstantsDefined.api+"postFeedback";
        //Make a request..
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Show dialog..
                if(dialog!=null)
                    dialog.dismiss();

                try {

                    //Parse response..
                    JSONObject j=new JSONObject(response);
                    String success=j.getString("success");
                    if(success.equals("true")){

                        //If true.. feedback submitted..
                        //Else show error message..
                        Toast.makeText(FeedbackActivity.this, "Feedback submitted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                        startActivity(intent);
                        finish();

                    }else{

                        //show message and start main activity..
                        Toast.makeText(FeedbackActivity.this, "There was some problem submitting the feedback", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                        startActivity(intent);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //In case the connection to the Api couldn't be established..
                if(dialog!=null)
                    dialog.dismiss();

                Toast.makeText(FeedbackActivity.this, "Sorry no internet connection..\nFeedback couldn't be submitted..", Toast.LENGTH_SHORT).show();

                //Start MainActivity..
                Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                startActivity(intent);
                finish();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Put all the required parameters for the post request..
                Map<String, String> params = new HashMap<String, String>();
                params.put("feedback",array.toString());
                params.put("examId",examId);
                params.put("userId",userId);
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    public void setQuestions(){

        //Show dialog..
        aviLoad.show();

        //For https connection..
        ConstantsDefined.updateAndroidSecurityProvider(FeedbackActivity.this);
        ConstantsDefined.beforeVolleyConnect();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url=ConstantsDefined.api+"feedbackQuestions";
        //Make a request..
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Hide dialog..
                aviLoad.hide();

                try {

                    //Parse response..
                    JSONObject j=new JSONObject(response);
                    String success=j.getString("success");
                    if(success.equals("true")){

                        //If success, populate the list of questions..
                        HashMap<String,ArrayList<String>> map= MiscellaneousParser.feedbackQuestionsParser(response);
                        questionIdList=map.get("id");
                        questionTextList=map.get("text");
                        questionTopicList=map.get("topic");
                        questionNumberList=map.get("number");
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                feedbackListAdapter= new FeedbackListAdapter(questionTextList,questionIdList,questionTopicList,questionNumberList, FeedbackActivity.this);
                                feedbackList.setAdapter(feedbackListAdapter);
                                feedbackListAdapter.notifyDataSetChanged();
                            }
                        });

                    }else{
                        //Else start MainActivity..
                        Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                        startActivity(intent);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //In case the connection to the Api couldn't be established..

                //Hide dialog..
                aviLoad.hide();

                //Start Main Activity..
                Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                startActivity(intent);
                finish();
            }
        });
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        //On cross button press, finish the activity and start Main activity..
        Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
        startActivity(intent);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        //Back pressing of phone is not allowed..
    }

}
