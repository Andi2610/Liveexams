package in.truskills.liveexams.Quiz;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

public class SectionNamesDisplay extends Activity {

    ArrayList<String> name;
    RecyclerView mySectionsList;
    int mySrno;
    String srNo;
    String myName = "";
    HashMap<String, String> map;
    LinearLayoutManager linearLayoutManager;
    SectionNamesDisplayAdapter sectionNamesDisplayAdapter;
    QuizDatabase ob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_section_names_display);

        srNo = getIntent().getStringExtra("serialNumber");
        mySrno = Integer.parseInt(srNo);


        mySectionsList = (RecyclerView) findViewById(R.id.mySectionsList);
        TextView sectionText = (TextView) findViewById(R.id.sectionText);
        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        sectionText.setTypeface(tff1);

        name = new ArrayList<>();
        map = new HashMap<>();

        ob = new QuizDatabase(SectionNamesDisplay.this);

        HashMap<String, ArrayList<String>> map = ob.getAllStringValuesPerSection();
        name = map.get("SectionNameList");

        sectionNamesDisplayAdapter = new SectionNamesDisplayAdapter(name, SectionNamesDisplay.this, mySrno);

        linearLayoutManager = new LinearLayoutManager(this);
        mySectionsList.setLayoutManager(linearLayoutManager);
        mySectionsList.setItemAnimator(new DefaultItemAnimator());
        mySectionsList.setAdapter(sectionNamesDisplayAdapter);
        sectionNamesDisplayAdapter.notifyDataSetChanged();


    }
}
