package in.truskills.liveexams.Quiz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

public class AllSectionsSummary extends AppCompatActivity {

    LinearLayoutManager linearLayoutManager;
    AllSectionsSummaryAdapter allSectionsSummaryAdapter;
    RecyclerView allSectionsList;
    ArrayList<String> sectionName;
    ArrayList<ArrayList<Integer>> questionArray;
    QuizDatabase ob;
    Button finishButton;
    String examId,userId,selectedLanguage;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sections_summary);

        allSectionsList=(RecyclerView)findViewById(R.id.allSectionsList);
        finishButton=(Button)findViewById(R.id.finishButton);

        examId=getIntent().getStringExtra("examId");
        userId=getIntent().getStringExtra("userId");
        selectedLanguage=getIntent().getStringExtra("selectedLanguage");
        requestQueue = Volley.newRequestQueue(getApplicationContext());


        ob=new QuizDatabase(AllSectionsSummary.this);
        sectionName=new ArrayList<>();
        questionArray=new ArrayList<>();

        HashMap<String,ArrayList<String>> map=ob.getAllStringValuesPerSection();
        sectionName=map.get("SectionNameList");

        for(int i=0;i<sectionName.size();++i){
            int sI=ob.getIntValuesPerSectionBySerialNumber(i, QuizDatabase.SectionIndex);
            HashMap<String,ArrayList<Integer>> my_map=ob.getAllIntValuesPerQuestionBySectionIndex(sI);
            ArrayList<Integer> my_fragment_index_list=new ArrayList<>();
            my_fragment_index_list=my_map.get("FragmentIndexList");
            questionArray.add(my_fragment_index_list);
        }

        allSectionsSummaryAdapter =new AllSectionsSummaryAdapter(sectionName,questionArray,AllSectionsSummary.this);

        linearLayoutManager=new LinearLayoutManager(this);
        allSectionsList.setLayoutManager(linearLayoutManager);
        allSectionsList.setItemAnimator(new DefaultItemAnimator());
        allSectionsList.setAdapter(allSectionsSummaryAdapter);
        allSectionsSummaryAdapter.notifyDataSetChanged();

        Typeface tff1=Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
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


                                QuizDatabase ob=new QuizDatabase(AllSectionsSummary.this);

//                                JSONArray jsonArray1=ob.getResults(QuizDatabase.TABLE_PER_SECTION);
//                                JSONArray jsonArray2=ob.getResults(QuizDatabase.TABLE_PER_QUESTION);
//                                JSONArray jsonArray3=ob.getResults(QuizDatabase.TABLE_PER_OPTION);
//                                JSONArray jsonArray4=ob.getResults(QuizDatabase.RESULT_TABLE);

                                JSONArray jsonArray=ob.getQuizResult();
                                final JSONObject jsonObject=new JSONObject();
                                try {
                                    jsonObject.put("result",jsonArray);
                                    jsonObject.put("selectedLanguage",selectedLanguage);
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                    String format = simpleDateFormat.format(new Date());
                                    jsonObject.put("date",format);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
//                                JSONObject jsonObject=new JSONObject();
//                                try {
//                                    jsonObject.put("sectionDetails",jsonArray1);
//                                    jsonObject.put("questionDetails",jsonArray2);
//                                    jsonObject.put("optionDetails",jsonArray3);
//                                    jsonObject.put("resultDetails",jsonArray4);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
                                try {
                                    File root = new File(Environment.getExternalStorageDirectory(), "Notes");
                                    if (!root.exists()) {
                                        root.mkdirs();
                                    }
                                    File gpxfile = new File(root, "databaseFile");
                                    FileWriter writer = new FileWriter(gpxfile);
                                    writer.append(jsonObject.toString());
                                    writer.flush();
                                    writer.close();
//                                    Toast.makeText(AllSectionsSummary.this, "Saved", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                //Api to be connected to..
                                String url = ConstantsDefined.api+"answerPaper";

                                final ProgressDialog progressDialog = new ProgressDialog(AllSectionsSummary.this);
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setMessage("Submitting your answers.. Please wait...");
                                progressDialog.setIndeterminate(true);
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                //Make a request..
                                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                        url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //On getting the response..
                                        progressDialog.dismiss();
                                        try {
                                            JSONObject jsonObject1=new JSONObject(response);
                                            String success=jsonObject1.getString("success");
                                            String result=jsonObject1.getString("response");
                                            if(success.equals("true")){
                                                Toast.makeText(AllSectionsSummary.this, result, Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(AllSectionsSummary.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                                                startActivity(intent);
                                            }else{
                                                JSONObject jsonObject2=new JSONObject(result);
                                                Toast.makeText(AllSectionsSummary.this, jsonObject2.getString("errmsg")+"", Toast.LENGTH_SHORT).show();
//                                                Toast.makeText(AllSectionsSummary.this, result, Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(AllSectionsSummary.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                                                startActivity(intent);
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
                                        progressDialog.dismiss();
                                        Log.d("error",error.toString()+"");
                                        Toast.makeText(AllSectionsSummary.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {

                                        //Put all the required parameters for the post request..
                                        Map<String,String> params = new HashMap<String, String>();
                                        params.put("userId",userId);
                                        params.put("examId",examId);
                                        params.put("answerPaper",jsonObject.toString());
                                        return params;
                                    }
                                };
                                requestQueue.add(stringRequest);
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
}
