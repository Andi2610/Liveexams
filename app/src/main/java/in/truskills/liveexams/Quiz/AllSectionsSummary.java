package in.truskills.liveexams.Quiz;

import android.app.AlertDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.R;

public class AllSectionsSummary extends AppCompatActivity {

    LinearLayoutManager linearLayoutManager;
    AllSectionsSummaryAdapter allSectionsSummaryAdapter;
    RecyclerView allSectionsList;
    ArrayList<String> sectionName;
    ArrayList<ArrayList<Integer>> questionArray;
    MySqlDatabase ob;
    Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sections_summary);

        allSectionsList=(RecyclerView)findViewById(R.id.allSectionsList);
        finishButton=(Button)findViewById(R.id.finishButton);

        ob=new MySqlDatabase(AllSectionsSummary.this);
        sectionName=new ArrayList<>();
        questionArray=new ArrayList<>();

        HashMap<String,ArrayList<String>> map=ob.getAllStringValuesPerSection();
        sectionName=map.get("SectionNameList");

        for(int i=0;i<sectionName.size();++i){
            int sI=ob.getIntValuesPerSectionBySerialNumber(i,MySqlDatabase.SectionIndex);
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


                                MySqlDatabase ob=new MySqlDatabase(AllSectionsSummary.this);

//                                JSONArray jsonArray1=ob.getResults(MySqlDatabase.TABLE_PER_SECTION);
//                                JSONArray jsonArray2=ob.getResults(MySqlDatabase.TABLE_PER_QUESTION);
//                                JSONArray jsonArray3=ob.getResults(MySqlDatabase.TABLE_PER_OPTION);
//                                JSONArray jsonArray4=ob.getResults(MySqlDatabase.RESULT_TABLE);

                                JSONArray jsonArray=ob.getQuizResult();
                                JSONObject jsonObject=new JSONObject();
                                try {
                                    jsonObject.put("result",jsonArray);
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

                                Intent intent = new Intent(AllSectionsSummary.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                                startActivity(intent);
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
