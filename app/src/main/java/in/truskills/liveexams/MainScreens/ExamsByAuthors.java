package in.truskills.liveexams.MainScreens;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;

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
import in.truskills.liveexams.Miscellaneous.SearchResultsActivity;
import in.truskills.liveexams.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExamsByAuthors extends Fragment {


    RecyclerView examsByAuthorsList;
    LinearLayoutManager linearLayoutManager;
    AllExamsListAdapter allExamsListAdapter;
    List<Values> valuesList, filteredList;
    Values values;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    Handler h;
    SearchView searchView;
    String myStartDate, myEndDate, myDateOfStart, myDateOfEnd, myDuration, myDurationTime, myStartTime, myEndTime;
    TextView searchExams;
    LinearLayout noConnectionLayout;
    Button retryButton;
    String author, response;


    public ExamsByAuthors() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_exams_by_authors, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        examsByAuthorsList = (RecyclerView) getActivity().findViewById(R.id.examsByAuthorsList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        examsByAuthorsList.setLayoutManager(linearLayoutManager);
        examsByAuthorsList.setItemAnimator(new DefaultItemAnimator());
        author = getArguments().getString("author");
        response = getArguments().getString("response");

        valuesList=new ArrayList<>();

        h=new Handler();

        setList();
    }

    public void populateList(List<Values> list) {
        allExamsListAdapter = new AllExamsListAdapter(list, getActivity());
        examsByAuthorsList.setAdapter(allExamsListAdapter);
        allExamsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.all_exams_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.searchAllExams);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        MenuItemCompat.setOnActionExpandListener(menuItem,
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
            searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName((getActivity().getApplicationContext()), SearchResultsActivity.class)));
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
                        allExamsListAdapter = new AllExamsListAdapter(filteredList, getActivity());
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
                        allExamsListAdapter = new AllExamsListAdapter(filteredList, getActivity());
                        examsByAuthorsList.setAdapter(allExamsListAdapter);
                        allExamsListAdapter.notifyDataSetChanged();
                    }
                    return true;
                }
            });
        }
    }


    public void setList(){

        valuesList=new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonObject1=jsonObject.getJSONObject("response");
            String timestamp = jsonObject1.getString("timestamp");
            HashMap<String, ArrayList<String>> mapper = MiscellaneousParser.getExamsByAuthors(jsonObject, author);
            JSONArray jsonArray = jsonObject1.getJSONArray("exams");
            int length = jsonArray.length();
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
                Log.d("myTimestamp=", timestamp);
                String myTimestamp = MiscellaneousParser.parseTimestamp(timestamp);
                String myTime = MiscellaneousParser.parseTimestampForTime(timestamp);

                Log.d("myTimestamp=", myTimestamp);

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

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
