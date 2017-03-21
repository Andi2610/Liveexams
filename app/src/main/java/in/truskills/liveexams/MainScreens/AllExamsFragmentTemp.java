package in.truskills.liveexams.MainScreens;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Typeface;
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
 * A simple {@link Fragment} subclass.
 */
public class AllExamsFragmentTemp extends Fragment implements ConnectivityReciever.ConnectivityReceiverListener{


    RecyclerView allExamsList;
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

    public AllExamsFragmentTemp() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_all_exams_fragment_temp, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        allExamsList = (RecyclerView) getActivity().findViewById(R.id.allExamsListTemp);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        requestQueue = Volley.newRequestQueue(getActivity());
        h = new Handler();

        searchExams = (TextView) getActivity().findViewById(R.id.searchExams);
        Typeface tff = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Regular.ttf");
        searchExams.setTypeface(tff);

        noConnectionLayout=(LinearLayout)getActivity().findViewById(R.id.noConnectionLayoutForAllExams);
        retryButton=(Button)getActivity().findViewById(R.id.retryButtonForAllExams);

        searchExams.setVisibility(View.GONE);
        noConnectionLayout.setVisibility(View.GONE);

        dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Fetching all exams.. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

        allExamsList.setLayoutManager(linearLayoutManager);
        allExamsList.setItemAnimator(new DefaultItemAnimator());

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("reached", "onClick: ");
                setList();
            }
        });

        valuesList=new ArrayList<>();

        setList();

    }

    public void setList(){


        valuesList=new ArrayList<>();

        //connect to joinedExams api..
        if(getActivity()!=null){
            dialog.show();
        }

        ConstantsDefined.updateAndroidSecurityProvider(getActivity());
        ConstantsDefined.beforeVolleyConnect();

        String url = "https://api.liveexams.in/data/allexams";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response)
            {

                Log.d("reached", "onResponse: ");

                if(dialog!=null)
                    dialog.dismiss();
                else{
                    Log.d("reached", "onResponse: nullDialog");
                }

                noConnectionLayout.setVisibility(View.GONE);


                try {

                    HashMap<String, String> map = MiscellaneousParser.allExamsApiParser(response);
                    String exams = map.get("exams");
                    String timestamp = map.get("timestamp");
                    HashMap<String, ArrayList<String>> mapper = MiscellaneousParser.allExamsParser(exams);
//                    Log.d("myExamName",mapper.get("ExamName").get(0)+"");
                    JSONArray jsonArray = new JSONArray(exams);
                    int length = jsonArray.length();
                    if (length == 0) {
                        valuesList.clear();
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                searchExams.setVisibility(View.VISIBLE);
                                noConnectionLayout.setVisibility(View.GONE);
                                populateList(valuesList);
                            }
                        });
                    } else {
                        searchExams.setVisibility(View.GONE);
                        noConnectionLayout.setVisibility(View.GONE);

                        valuesList = new ArrayList<Values>();
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
                    }

                }catch(Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("checkForError",error.toString());

                noConnectionLayout.setVisibility(View.VISIBLE);

                if(dialog!=null)
                    dialog.dismiss();


                if(ConstantsDefined.isOnline(getActivity())){
                    //Do nothing..
                    if(getActivity()!=null)
                        Toast.makeText(getActivity(), "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                }else{
                    if(getActivity()!=null)
                        Toast.makeText(getActivity(), "Sorry! Couldn't connect", Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void populateList(List<Values> list) {
        allExamsListAdapter = new AllExamsListAdapter(list, getActivity());
        allExamsList.setLayoutManager(linearLayoutManager);
        allExamsList.setItemAnimator(new DefaultItemAnimator());
        allExamsList.setAdapter(allExamsListAdapter);
        allExamsListAdapter.notifyDataSetChanged();

        if (list.isEmpty()) {
           searchExams.setVisibility(View.VISIBLE);
            noConnectionLayout.setVisibility(View.GONE);

        } else {
            searchExams.setVisibility(View.GONE);
            noConnectionLayout.setVisibility(View.GONE);

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.all_exams_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.search);
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
                        allExamsList.setAdapter(allExamsListAdapter);
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
                        allExamsList.setAdapter(allExamsListAdapter);
                        allExamsListAdapter.notifyDataSetChanged();
                    }
                    return true;
                }
            });
        }
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
}
