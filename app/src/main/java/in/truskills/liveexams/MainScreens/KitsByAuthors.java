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
public class KitsByAuthors extends Fragment {


    RecyclerView examsByAuthorsList;
    LinearLayoutManager linearLayoutManager;
    AllKitsListAdapter allExamsListAdapter;
    List<Values> valuesList, filteredList;
    Values values;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    Handler h;
    SearchView searchView;
    String myStartDate, myEndDate, myDateOfStart, myDateOfEnd, myDuration, myDurationTime="0", myStartTime, myEndTime;
    TextView noExamsPresent;
    LinearLayout noConnectionLayout;
    Button retryButton;
    String author, response;
    KitsByAuthorsInterface ob;


    public KitsByAuthors() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_kits_by_authors, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        examsByAuthorsList = (RecyclerView) getActivity().findViewById(R.id.kitsByAuthorsList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        examsByAuthorsList.setLayoutManager(linearLayoutManager);
        examsByAuthorsList.setItemAnimator(new DefaultItemAnimator());
        author = getArguments().getString("author");
        response = getArguments().getString("response");

        ob=(KitsByAuthorsInterface)getActivity();

        noExamsPresent=(TextView)getActivity().findViewById(R.id.noKitsPresent);

        Typeface tff = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Regular.ttf");
        noExamsPresent.setTypeface(tff);
        noExamsPresent.setVisibility(View.GONE);

        valuesList=new ArrayList<>();

        h=new Handler();

        setList();
    }

    public void populateList(List<Values> list) {
        allExamsListAdapter = new AllKitsListAdapter(list, getActivity(),ob);
        examsByAuthorsList.setAdapter(allExamsListAdapter);
        allExamsListAdapter.notifyDataSetChanged();
        if(list.size()==0)
            noExamsPresent.setVisibility(View.VISIBLE);
        else
            noExamsPresent.setVisibility(View.GONE);
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
                        allExamsListAdapter = new AllKitsListAdapter(filteredList, getActivity(),ob);
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
                        allExamsListAdapter = new AllKitsListAdapter(filteredList, getActivity(),ob);
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

                    myDateOfStart=MiscellaneousParser.parseDateForKit(myStartDate);
                    myDateOfEnd=MiscellaneousParser.parseDateForKit(myEndDate);

                    values = new Values(mapper.get("ExamName").get(i), myDateOfStart, myDateOfEnd, myDurationTime, mapper.get("ExamId").get(i));
                    valuesList.add(values);
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
}

interface KitsByAuthorsInterface{
    public void changeFromKitsByAuthors(Fragment f,String title);
}
