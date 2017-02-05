package in.truskills.liveexams.Quiz;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;

import java.util.ArrayList;
import java.util.HashMap;

import in.truskills.liveexams.Miscellaneous.MySql;
import in.truskills.liveexams.R;

public class SectionNamesDisplay extends Activity {

    ArrayList<String> name;
    RecyclerView mySectionsList;
    int noOfSections,currentSection;
    String myName="";
    HashMap<String,String> map;
    LinearLayoutManager linearLayoutManager;
    AllSectionsSummaryAdapter allSectionsSummaryAdapter;
    MySql ob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_section_names_display);

        noOfSections=getIntent().getIntExtra("noOfSections",0);
        currentSection=getIntent().getIntExtra("currentSection",0);

        Log.d("here",noOfSections+" "+currentSection);

        mySectionsList=(RecyclerView) findViewById(R.id.mySectionsList);

        name=new ArrayList<>();
        map=new HashMap<>();

        ob=new MySql(SectionNamesDisplay.this);

        for(int i=0;i<noOfSections;++i){
            map=ob.getValuesPerSection(i);
            myName=map.get("SectionName");
            name.add(myName);
        }

        allSectionsSummaryAdapter=new AllSectionsSummaryAdapter(name,SectionNamesDisplay.this,currentSection);

        linearLayoutManager=new LinearLayoutManager(this);
        mySectionsList.setLayoutManager(linearLayoutManager);
        mySectionsList.setItemAnimator(new DefaultItemAnimator());
        mySectionsList.setAdapter(allSectionsSummaryAdapter);
        allSectionsSummaryAdapter.notifyDataSetChanged();


    }
}
