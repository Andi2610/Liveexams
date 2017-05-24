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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.Miscellaneous.ConnectivityReciever;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.Miscellaneous.SearchResultsActivity;
import in.truskills.liveexams.R;

/**
 * This activity is for displaying all exams of a particular author..
 *
 * Functions:
 * 1. onCreate() : for basic stuff and calling setList()..
 * 2. setList() : for preparing data to populate in list..
 * 3. populateList() : for populating the list of exams..
 * 4. onNetworkConnectionChanged() : for handling internet connect/disconnect..
 * 5. onResume() : for connecting to connectivity receiver..
 * 6. onCreateOptionsMenu() : for searching..
 * 7. onSupportNavigateUp() : for back press on toolbar..
 *
 */

public class AllExamsPerAuthorActivity extends AppCompatActivity implements ConnectivityReciever.ConnectivityReceiverListener {

    RecyclerView examsByAuthorsList;
    LinearLayoutManager linearLayoutManager;
    AllExamsPerAuthorActivityListAdapter allExamsListAdapter;
    List<Values> valuesList, filteredList;
    Values values;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    Handler h;
    String myStartDate, myEndDate, myDateOfStart, myDateOfEnd, myDuration, myDurationTime, myStartTime, myEndTime;
    TextView noExamsPresent;
    String author, response;
    Bundle b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_exams_per_author);

        //For toolbar..
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);
        getSupportActionBar().setTitle("ADD NEW EXAMS");

        //Render elements from layout..
        noExamsPresent=(TextView)findViewById(R.id.noExamsPresent);
        examsByAuthorsList = (RecyclerView) findViewById(R.id.examsByAuthorsList);
        linearLayoutManager = new LinearLayoutManager(this);
        examsByAuthorsList.setLayoutManager(linearLayoutManager);
        examsByAuthorsList.setItemAnimator(new DefaultItemAnimator());

        //Get intent variables..
        b=getIntent().getBundleExtra("bundle");
        author = b.getString("author");
        response = b.getString("response");

        //Set typeface..
        Typeface tff = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        noExamsPresent.setTypeface(tff);
        noExamsPresent.setVisibility(View.GONE);

        valuesList=new ArrayList<>();
        h=new Handler();

        //Call function..
        setList();
    }

    public void setList(){

        //For setting list of exams through parsing of response obtained via intent..

        valuesList=new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonObject1=jsonObject.getJSONObject("response");
            String timestamp = jsonObject1.getString("timestamp");
            HashMap<String, ArrayList<String>> mapper = MiscellaneousParser.getExamsByAuthors(jsonObject, author);
            JSONArray jsonArray = jsonObject1.getJSONArray("exams");
            ArrayList<String> listForLength=mapper.get("StartDate");
            int length = listForLength.size();
            if(length==0)
                noExamsPresent.setVisibility(View.VISIBLE);
            else{
                noExamsPresent.setVisibility(View.GONE);
                for (int i = 0; i < length; ++i) {
                    myStartDate = mapper.get("StartDate").get(i);
                    myEndDate = mapper.get("EndDate").get(i);
                    myDuration = mapper.get("ExamDuration").get(i);
                    myStartTime = mapper.get("StartTime").get(i);
                    myEndTime = mapper.get("EndTime").get(i);

                    Log.d("myDate=", myStartDate + " ****** " + myEndDate + " **** " + myDuration);

                    myDateOfStart = MiscellaneousParser.parseDate(myStartDate);
                    myDateOfEnd = MiscellaneousParser.parseDate(myEndDate);
                    myDurationTime = MiscellaneousParser.parseDuration(myDuration);

                    String myTimeOfStart = MiscellaneousParser.parseTimeForDetails(myStartTime);
                    String myTimeOfEnd = MiscellaneousParser.parseTimeForDetails(myEndTime);
                    String myTimestamp = MiscellaneousParser.parseTimestamp(timestamp);
                    String myTime = MiscellaneousParser.parseTimestampForTime(timestamp);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date start_date = simpleDateFormat.parse(myDateOfStart);
                    Date end_date = simpleDateFormat.parse(myDateOfEnd);
                    Date middle_date = simpleDateFormat.parse(myTimestamp);

                    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("h-mm a");
                    Date start_time = simpleDateFormat2.parse(myTimeOfStart);
                    Date end_time = simpleDateFormat2.parse(myTimeOfEnd);
                    Date middle_time = simpleDateFormat2.parse(myTime);

                    if (middle_date.before(start_date)) {
                        values = new Values(mapper.get("ExamName").get(i), myDateOfStart, myDateOfEnd, myDurationTime, mapper.get("ExamId").get(i));
                        valuesList.add(values);
                    } else if (middle_date.before(end_date) || middle_date.equals(end_date)) {
                        if (middle_date.equals(end_date)) {
                            if (!middle_time.after(end_time)) {
                                values = new Values(mapper.get("ExamName").get(i), myDateOfStart, myDateOfEnd, myDurationTime, mapper.get("ExamId").get(i));
                                valuesList.add(values);
                            }
                        } else {
                            values = new Values(mapper.get("ExamName").get(i), myDateOfStart, myDateOfEnd, myDurationTime, mapper.get("ExamId").get(i));
                            valuesList.add(values);
                        }
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

    public void populateList(List<Values> list) {

        //Populate list..
        allExamsListAdapter = new AllExamsPerAuthorActivityListAdapter(list, this);
        examsByAuthorsList.setAdapter(allExamsListAdapter);
        allExamsListAdapter.notifyDataSetChanged();
        if(list.size()==0)
            noExamsPresent.setVisibility(View.VISIBLE);
        else
            noExamsPresent.setVisibility(View.GONE);
    }

    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            if (valuesList.isEmpty()) {
                setList();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        valuesList = new ArrayList<>();
        setList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //For searching among exams..

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
                        allExamsListAdapter = new AllExamsPerAuthorActivityListAdapter(filteredList, AllExamsPerAuthorActivity.this);
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
                        allExamsListAdapter = new AllExamsPerAuthorActivityListAdapter(filteredList, AllExamsPerAuthorActivity.this);
                        examsByAuthorsList.setAdapter(allExamsListAdapter);
                        allExamsListAdapter.notifyDataSetChanged();
                    }
                    return true;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        //On back press on toolbar..
        finish();
        return true;
    }
}
