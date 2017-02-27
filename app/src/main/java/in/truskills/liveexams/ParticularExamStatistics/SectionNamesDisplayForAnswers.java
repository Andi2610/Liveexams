package in.truskills.liveexams.ParticularExamStatistics;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;

import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.AnalyticsDatabase;

public class SectionNamesDisplayForAnswers extends Activity {

    RecyclerView mySectionsListForAnswers;
    SectionNamesdisplayAdapterForAnswers sectionNamesDisplayAdapterForAnswers;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_section_names_display_for_answers);

        int si=getIntent().getIntExtra("sectionIndex",0);

        AnalyticsDatabase ob=new AnalyticsDatabase(this);

        ArrayList<String> name=ob.getAllStringValuesPerSection(AnalyticsDatabase.SectionName);

        mySectionsListForAnswers=(RecyclerView) findViewById(R.id.mySectionsListForAnswers);
        TextView sectionTextForAnswers=(TextView)findViewById(R.id.sectionTextForAnswers);
        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        sectionTextForAnswers.setTypeface(tff1);


        sectionNamesDisplayAdapterForAnswers =new SectionNamesdisplayAdapterForAnswers(name,SectionNamesDisplayForAnswers.this,si);

        linearLayoutManager=new LinearLayoutManager(this);
        mySectionsListForAnswers.setLayoutManager(linearLayoutManager);
        mySectionsListForAnswers.setItemAnimator(new DefaultItemAnimator());
        mySectionsListForAnswers.setAdapter(sectionNamesDisplayAdapterForAnswers);
        sectionNamesDisplayAdapterForAnswers.notifyDataSetChanged();
    }
}
