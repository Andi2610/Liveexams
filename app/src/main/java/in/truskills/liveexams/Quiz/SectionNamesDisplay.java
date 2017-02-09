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

import in.truskills.liveexams.R;

public class SectionNamesDisplay extends Activity {

    ArrayList<String> name;
    RecyclerView mySectionsList;
    int mySrno;
    String srNo;
    String myName="";
    HashMap<String,String> map;
    LinearLayoutManager linearLayoutManager;
    SectionNamesDisplayAdapter sectionNamesDisplayAdapter;
    MySqlDatabase ob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_section_names_display);

        srNo=getIntent().getStringExtra("serialNumber");
        mySrno=Integer.parseInt(srNo);


        mySectionsList=(RecyclerView) findViewById(R.id.mySectionsList);

        name=new ArrayList<>();
        map=new HashMap<>();

        ob=new MySqlDatabase(SectionNamesDisplay.this);

        HashMap<String,ArrayList<String>> map=ob.getAllStringValuesPerSection();
        name=map.get("SectionNameList");

        sectionNamesDisplayAdapter =new SectionNamesDisplayAdapter(name,SectionNamesDisplay.this,mySrno);

        linearLayoutManager=new LinearLayoutManager(this);
        mySectionsList.setLayoutManager(linearLayoutManager);
        mySectionsList.setItemAnimator(new DefaultItemAnimator());
        mySectionsList.setAdapter(sectionNamesDisplayAdapter);
        sectionNamesDisplayAdapter.notifyDataSetChanged();


    }
}
