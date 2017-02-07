package in.truskills.liveexams.Quiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import in.truskills.liveexams.R;

public class AllSectionsSummary extends AppCompatActivity {

    LinearLayoutManager linearLayoutManager;
    AllSectionsSummaryAdapter allSectionsSummaryAdapter;
    RecyclerView allSectionsList;
    Bundle b;
    int noOfSections,questionArray[];
    ArrayList<String> sectionName;
    MySqlDatabase ob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sections_summary);

        b=getIntent().getBundleExtra("bundle");
        noOfSections=b.getInt("noOfSections");
        questionArray=b.getIntArray("questionArray");

        allSectionsList=(RecyclerView)findViewById(R.id.allSectionsList);

        ob=new MySqlDatabase(AllSectionsSummary.this);
        sectionName=new ArrayList<>();


        for(int i=0;i<noOfSections;++i){
            HashMap<String,String> map=ob.getValuesPerSection(i);
            String name=map.get("SectionName");
            sectionName.add(name);
        }

        allSectionsSummaryAdapter =new AllSectionsSummaryAdapter(sectionName,questionArray,AllSectionsSummary.this);

        linearLayoutManager=new LinearLayoutManager(this);
        allSectionsList.setLayoutManager(linearLayoutManager);
        allSectionsList.setItemAnimator(new DefaultItemAnimator());
        allSectionsList.setAdapter(allSectionsSummaryAdapter);
        allSectionsSummaryAdapter.notifyDataSetChanged();
    }
}
