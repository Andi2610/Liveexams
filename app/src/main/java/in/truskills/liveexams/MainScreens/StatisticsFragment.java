package in.truskills.liveexams.MainScreens;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.Miscellaneous.SearchResultsActivity;
import in.truskills.liveexams.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {


    RecyclerView statisticsList;
    LinearLayoutManager linearLayoutManager;
    StatisticsListAdapter statisticsListAdapter;
    List<Values> valuesList,filteredList;
    Values values;
    RequestQueue requestQueue;
    Handler h;
    SearchView searchView;
    String myStartDate,myEndDate,myDateOfStart,myDateOfEnd,myDuration,myDurationTime;
    SharedPreferences prefs;
    TextView noExams,noConnectionText;
    Button retryButton;
    LinearLayout noConnectionLayout;
    ProgressDialog dialog;

    public StatisticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        statisticsList=(RecyclerView)getActivity().findViewById(R.id.statisticsList);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        requestQueue = Volley.newRequestQueue(getActivity());
        h=new Handler();
        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        noExams=(TextView)getActivity().findViewById(R.id.noExams);
        Typeface tff=Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Regular.ttf");
        noExams.setTypeface(tff);

        statisticsList.setLayoutManager(linearLayoutManager);
        statisticsList.setItemAnimator(new DefaultItemAnimator());

        retryButton = (Button) getActivity().findViewById(R.id.retryButtonForStatistics);
        noConnectionLayout=(LinearLayout)getActivity().findViewById(R.id.noConnectionLayoutForStatistics);
        noConnectionText=(TextView)getActivity().findViewById(R.id.noConnectionTextForStatistics);
        Typeface tff1=Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Regular.ttf");
        retryButton.setTypeface(tff1);
        noConnectionText.setTypeface(tff1);
        noConnectionLayout.setVisibility(View.GONE);
        noExams.setVisibility(View.GONE);

        setList();

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setList();
            }
        });
    }

    public void setList(){
        valuesList=new ArrayList<>();
        dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.show();

        //Get data for list populate..
        String url = ConstantsDefined.api + "getAnalyzedExams/" + prefs.getString("userId","");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                noConnectionLayout.setVisibility(View.GONE);
                try {
                    HashMap<String, ArrayList<String>> mapper = MiscellaneousParser.analyzedExamsParser(response);
                    int length = response.getJSONArray("response").length();
                    Log.d("analyzed",response+"");
                    dialog.dismiss();
                    if (length == 0) {
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                populateList(valuesList);
                            }
                        });
                    } else {
                        valuesList = new ArrayList<Values>();
                        for (int i = 0; i < length; ++i) {
                            myStartDate = mapper.get("StartDate").get(i);
                            myEndDate = mapper.get("EndDate").get(i);
                            myDuration = mapper.get("ExamDuration").get(i);

                            Log.d("myDate=", myStartDate + " ****** " + myEndDate + " **** " + myDuration);

                            myDateOfStart = MiscellaneousParser.parseDate(myStartDate);
                            myDateOfEnd = MiscellaneousParser.parseDate(myEndDate);
                            myDurationTime = MiscellaneousParser.parseDuration(myDuration);

                            Log.d("myDate=", myDateOfStart + " ****** " + myDateOfEnd + " **** " + myDurationTime);


                            values = new Values(mapper.get("ExamName").get(i), myDateOfStart, myDateOfEnd, myDurationTime, mapper.get("ExamId").get(i));
                            valuesList.add(values);
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    populateList(valuesList);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("myError",e+"");
                    dialog.dismiss();
                }
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.d("myError",error+"");
//                Toast.makeText(getActivity(), "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                noConnectionLayout.setVisibility(View.VISIBLE);
            }
        });
        requestQueue.add(jsonObjectRequest);
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
                    s = s.toString().toLowerCase();
                    filteredList = new ArrayList<>();

                    for (int i = 0; i < valuesList.size(); i++) {

                        final String text = valuesList.get(i).name.toLowerCase();
                        if (text.contains(s)) {

                            filteredList.add(new Values(valuesList.get(i).name, valuesList.get(i).startDateValue, valuesList.get(i).endDateValue, valuesList.get(i).durationValue, valuesList.get(i).examId));
                        }
                    }
                    populateList(filteredList);
                    return true;
                }
            });
        }
    }

    public void populateList(List<Values> list){
        statisticsListAdapter=new StatisticsListAdapter(list,getActivity());
        statisticsList.setAdapter(statisticsListAdapter);
        statisticsListAdapter.notifyDataSetChanged();
        if (list.isEmpty()) {
            noExams.setVisibility(View.VISIBLE);
        } else {
            noExams.setVisibility(View.GONE);
        }
    }
}
