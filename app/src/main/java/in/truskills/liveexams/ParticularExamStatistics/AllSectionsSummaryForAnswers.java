package in.truskills.liveexams.ParticularExamStatistics;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import in.truskills.liveexams.Quiz.AllSectionsSummary;
import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.AnalyticsDatabase;

public class AllSectionsSummaryForAnswers extends AppCompatActivity {


    RecyclerView allSectionsListForanswers;
    ArrayList<ArrayList<Integer>> questionArray;
    AllSectionsSummaryAdapterForAnswers allSectionsSummaryAdapterForAnswers;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sections_summary_for_answers);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);

        getSupportActionBar().setTitle("SUMMARY");


        allSectionsListForanswers = (RecyclerView) findViewById(R.id.allSectionsListForAnswers);

        AnalyticsDatabase ob = new AnalyticsDatabase(this);
        ArrayList<String> name = ob.getAllStringValuesPerSection(AnalyticsDatabase.SectionName);

        questionArray = new ArrayList<>();

        for (int i = 0; i < name.size(); ++i) {
            ArrayList<Integer> listOfFi = ob.getIntValuesOfEachSection(i, AnalyticsDatabase.FragmentIndex);
            questionArray.add(listOfFi);
        }

        allSectionsSummaryAdapterForAnswers = new AllSectionsSummaryAdapterForAnswers(name, questionArray, AllSectionsSummaryForAnswers.this);

        linearLayoutManager = new LinearLayoutManager(this);
        allSectionsListForanswers.setLayoutManager(linearLayoutManager);
        allSectionsListForanswers.setItemAnimator(new DefaultItemAnimator());
        allSectionsListForanswers.setAdapter(allSectionsSummaryAdapterForAnswers);
        allSectionsSummaryAdapterForAnswers.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rules_menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rulesIcon:
                Intent i = new Intent(this, RulesInAnswers.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
