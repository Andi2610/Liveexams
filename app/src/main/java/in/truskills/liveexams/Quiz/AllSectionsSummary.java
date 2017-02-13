package in.truskills.liveexams.Quiz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

                                JSONArray jsonArray=ob.getResults();
                                Log.d("database",jsonArray.toString());

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
