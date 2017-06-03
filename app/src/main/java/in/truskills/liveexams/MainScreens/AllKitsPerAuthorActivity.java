package in.truskills.liveexams.MainScreens;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.Miscellaneous.SearchResultsActivity;
import in.truskills.liveexams.R;

public class AllKitsPerAuthorActivity extends AppCompatActivity {

    RecyclerView examsByAuthorsList;
    LinearLayoutManager linearLayoutManager;
    AllKitsPerAuthorActivityListAdapter allExamsListAdapter;
    List<Values> valuesList, filteredList;
    Values values;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    Handler h;
    SearchView searchView;
    String myStartDate, myEndDate, myDateOfStart, myDateOfEnd, myDuration, myDurationTime="0", myStartTime, myEndTime,mykitid;
    TextView noExamsPresent;
    LinearLayout noConnectionLayout;
    Button retryButton;
    String author, response;
    Bundle b;
    ArrayList<String> mykits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_kits_per_author);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);

        getSupportActionBar().setTitle("ADD NEW KITS");


        examsByAuthorsList = (RecyclerView) findViewById(R.id.kitsByAuthorsList);
        linearLayoutManager = new LinearLayoutManager(this);
        examsByAuthorsList.setLayoutManager(linearLayoutManager);
        examsByAuthorsList.setItemAnimator(new DefaultItemAnimator());
        b=getIntent().getBundleExtra("bundle");
        author = b.getString("author");
        response = b.getString("response");

        //getting the list of bought kits
        mykits = b.getStringArrayList("mykits");

        noExamsPresent=(TextView)findViewById(R.id.noKitsPresent);

        Typeface tff = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        noExamsPresent.setTypeface(tff);
        noExamsPresent.setVisibility(View.GONE);

        valuesList=new ArrayList<>();

        h=new Handler();

        setList();

    }

    public void populateList(List<Values> list) {
        allExamsListAdapter = new AllKitsPerAuthorActivityListAdapter(list, this);
        examsByAuthorsList.setAdapter(allExamsListAdapter);
        allExamsListAdapter.notifyDataSetChanged();
        if(list.size()==0)
            noExamsPresent.setVisibility(View.VISIBLE);
        else
            noExamsPresent.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.all_exams_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.searchAllExams).getActionView();
        searchView.setSearchableInfo( searchManager.getSearchableInfo(getComponentName()) );

        final MenuItem searchItem = menu.findItem(R.id.searchAllExams);

        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        // Return true to allow the action view to expand
                        searchView.requestFocus();
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        // When the action view is collapsed, reset the query
                        setList();
                        searchView.clearFocus();
                        // Return true to allow the action view to collapse
                        return true;
                    }
                });

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName((getApplicationContext()), SearchResultsActivity.class)));
            searchView.setQueryHint("Type here..");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {

                    if(s.equals("")){
                        filteredList = new ArrayList<>();
                        allExamsListAdapter = new AllKitsPerAuthorActivityListAdapter(filteredList, AllKitsPerAuthorActivity.this);
                        examsByAuthorsList.setAdapter(allExamsListAdapter);
                        allExamsListAdapter.notifyDataSetChanged();

                    }else{
                        s = s.toString().toLowerCase();
                        filteredList = new ArrayList<>();

                        for (int i = 0; i < valuesList.size(); i++) {

                            final String text = valuesList.get(i).name.toLowerCase();
                            if (text.contains(s)) {

                                filteredList.add(new Values(valuesList.get(i).name, valuesList.get(i).startDateValue, valuesList.get(i).endDateValue, valuesList.get(i).durationValue, valuesList.get(i).examId));
                            }
                        }
                        allExamsListAdapter = new AllKitsPerAuthorActivityListAdapter(filteredList, AllKitsPerAuthorActivity.this);
                        examsByAuthorsList.setAdapter(allExamsListAdapter);
                        allExamsListAdapter.notifyDataSetChanged();
                    }
                    return true;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }


    public void setList(){

        valuesList=new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.d("myExams", "setList: "+jsonObject);

            JSONObject jsonObject1=jsonObject.getJSONObject("response");
            HashMap<String, ArrayList<String>> mapper = MiscellaneousParser.getKitsByAuthors(jsonObject, author);
            JSONArray jsonArray = jsonObject1.getJSONArray("productKits");
            Log.d("myExams", "setList: "+jsonArray);
            ArrayList<String> listForLength=mapper.get("StartDate");
            int length = listForLength.size();
            if(length==0)
                noExamsPresent.setVisibility(View.VISIBLE);
            else{
                noExamsPresent.setVisibility(View.GONE);
                for (int i = 0; i < length; ++i) {
                    myStartDate = mapper.get("StartDate").get(i);
                    myEndDate = mapper.get("EndDate").get(i);
                    mykitid = mapper.get("ExamId").get(i); //getting all the id of productkits

                    myDateOfStart = MiscellaneousParser.parseDateForKit(myStartDate);
                    myDateOfEnd = MiscellaneousParser.parseDateForKit(myEndDate);

                    if (mykits.contains(mykitid)) {  //if bought then do nothing else display
                        //do nothing
                    } else {
                        values = new Values(mapper.get("ExamName").get(i), myDateOfStart, myDateOfEnd, myDurationTime, mapper.get("ExamId").get(i));
                        valuesList.add(values);
                    }
                }

                h.post(new Runnable() {
                    @Override
                    public void run() {
                        populateList(valuesList);
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
