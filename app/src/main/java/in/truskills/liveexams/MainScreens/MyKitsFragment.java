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
import java.util.*;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.Miscellaneous.ConnectivityReciever;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.Miscellaneous.MyApplication;
import in.truskills.liveexams.Miscellaneous.SearchResultsActivity;
import in.truskills.liveexams.R;

//This is the home fragment in which myExams list is loaded..

public class MyKitsFragment extends Fragment implements ConnectivityReciever.ConnectivityReceiverListener {

    //Declare variables..
    Button addForMyKits;
    RecyclerView myKitsList;
    LinearLayoutManager linearLayoutManager;
    MyKitsListAdapter myKitsListAdapter;
    List<Values> valuesList, filteredList;
    Values values;
    MyKitsInterface ob;
    String myStartDate, myDateOfStart, myEndDate, myDateOfEnd, myDuration, myDurationTime, myStartTime, myEndTime;
    SharedPreferences prefs;
    RequestQueue requestQueue;
    Handler h;
    LinearLayout noConnectionLayoutForMyKits;
    Button retryButtonForMyKits;
    TextView noConnectionText;
    ProgressDialog dialog;

    public MyKitsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_my_kits, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get shared preferences..
        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(getActivity());
        h = new Handler();
        valuesList = new ArrayList<>();

        //Initialise the variables..
        addForMyKits = (Button) getActivity().findViewById(R.id.addForKits);
        retryButtonForMyKits = (Button) getActivity().findViewById(R.id.retryButtonForMyKits);
        noConnectionLayoutForMyKits = (LinearLayout) getActivity().findViewById(R.id.noConnectionLayoutForMyKits);
        noConnectionText = (TextView) getActivity().findViewById(R.id.noConnectionText);
        Typeface tff1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Regular.ttf");
        retryButtonForMyKits.setTypeface(tff1);
        noConnectionText.setTypeface(tff1);
        noConnectionLayoutForMyKits.setVisibility(View.GONE);
        addForMyKits.setVisibility(View.GONE);
        myKitsList = (RecyclerView) getActivity().findViewById(R.id.myKitsList);
//        add.setVisibility(View.GONE);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        ob = (MyKitsInterface) getActivity();

//        setList();

        addForMyKits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StreamsFragmentForMyKits f = new StreamsFragmentForMyKits();
                ob.changeFragmentFromMyKits(f);
            }
        });

        retryButtonForMyKits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setList();
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        SearchManager searchManager=null;
        if(getActivity()!=null){
            searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
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
                            myKitsListAdapter = new MyKitsListAdapter(filteredList, getActivity());
                            myKitsList.setAdapter(myKitsListAdapter);
                            myKitsListAdapter.notifyDataSetChanged();

                        }else{
                            s = s.toString().toLowerCase();
                            filteredList = new ArrayList<>();

                            for (int i = 0; i < valuesList.size(); i++) {

                                final String text = valuesList.get(i).name.toLowerCase();
                                if (text.contains(s)) {

                                    filteredList.add(new Values(valuesList.get(i).name, valuesList.get(i).startDateValue, valuesList.get(i).endDateValue, valuesList.get(i).durationValue, valuesList.get(i).examId));
                                }
                            }
                            myKitsListAdapter = new MyKitsListAdapter(filteredList, getActivity());
                            myKitsList.setAdapter(myKitsListAdapter);
                            myKitsListAdapter.notifyDataSetChanged();
                        }
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addIcon:
                StreamsFragmentForMyKits f = new StreamsFragmentForMyKits();
                ob.changeFragmentFromMyKits(f);
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    public void setList() {

        valuesList=new ArrayList<>();

        //connect to joinedExams api..
        if(getActivity()!=null){
            dialog = new ProgressDialog(getActivity());
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        ConstantsDefined.updateAndroidSecurityProvider(getActivity());
        ConstantsDefined.beforeVolleyConnect();

        String url = ConstantsDefined.api + "joinedExams/" + prefs.getString("userId", "");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                noConnectionLayoutForMyKits.setVisibility(View.GONE);
                addForMyKits.setVisibility(View.GONE);
                if(dialog!=null)
                    dialog.dismiss();
                try {

                    String success=response.getString("success");
                    if(success.equals("true")){
                        String myResponse = response.getJSONObject("response").toString();
                        JSONObject jsonObject = new JSONObject(myResponse);
                        String timestamp = jsonObject.getString("timestamp");
                        String myJoinedExams = jsonObject.getJSONArray("joinedExams").toString();
                        HashMap<String, ArrayList<String>> mapper = MiscellaneousParser.myExamsParser(myJoinedExams);
                        JSONArray arr = new JSONArray(myJoinedExams);
                        int length = arr.length();
                        for (int i = 0; i < length; ++i) {

                            //If user is still enrolled to this exam..
                            if (mapper.get("leftExam").get(i).equals("false")) {

                                myStartDate = mapper.get("StartDate").get(i);
                                myEndDate = mapper.get("EndDate").get(i);
                                myDuration = mapper.get("ExamDuration").get(i);
                                myStartTime = mapper.get("StartTime").get(i);
                                myEndTime = mapper.get("EndTime").get(i);

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
                        }
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                populateList(valuesList);
                            }
                        });
                    }else{

                        noConnectionLayoutForMyKits.setVisibility(View.VISIBLE);
                        addForMyKits.setVisibility(View.GONE);
                        if(getActivity()!=null)
                            Toast.makeText(getActivity(), "Something went wrong..\n" +
                                    "Please try again..", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("checkForError",error.toString());

                noConnectionLayoutForMyKits.setVisibility(View.VISIBLE);
                addForMyKits.setVisibility(View.GONE);
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
        myKitsListAdapter = new MyKitsListAdapter(list, getActivity());
        myKitsList.setLayoutManager(linearLayoutManager);
        myKitsList.setItemAnimator(new DefaultItemAnimator());
        myKitsList.setAdapter(myKitsListAdapter);
        myKitsListAdapter.notifyDataSetChanged();

        if (list.isEmpty()) {
            addForMyKits.setVisibility(View.VISIBLE);
            noConnectionLayoutForMyKits.setVisibility(View.GONE);
        } else {
            addForMyKits.setVisibility(View.GONE);
            noConnectionLayoutForMyKits.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            if (valuesList.isEmpty()) {
                setList();
            }
        }
    }
}

interface MyKitsInterface {
    public void changeFragmentFromMyKits(Fragment f);
}
