package in.truskills.liveexams.MainScreens;


import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.R;

public class HomeFragment extends Fragment {

    Button add;
    RecyclerView myExamsList;
    LinearLayoutManager linearLayoutManager;
    MyExamsListAdapter myExamsListAdapter;
    List<Values> valuesList,filteredList;
    Values values;
    HomeInterface ob;
    String joinedExams,myStartDate,myDateOfStart,myEndDate,myDateOfEnd,myDuration,myDurationTime;
    String [] parts;
    SimpleDateFormat simpleDateFormat;
    Calendar calendar;
    Date date;
    int day,month,year,hour,minute;
    SharedPreferences prefs;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prefs=getActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE);
        joinedExams=prefs.getString("joinedExams","noJoinedExams");

        Log.d("messi",joinedExams);

        add=(Button)getActivity().findViewById(R.id.add);
        myExamsList=(RecyclerView)getActivity().findViewById(R.id.myExamsList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        valuesList=new ArrayList<>();

        if(!joinedExams.equals("noJoinedExams")){
            try {
                HashMap<String, ArrayList<String>> mapper=VariablesDefined.myExamsParser(joinedExams);
                JSONArray arr=new JSONArray(joinedExams);
                int length=arr.length();
                for(int i=0;i<length;++i){
                    if(mapper.get("leftExam").get(i).equals("false")){

                        myStartDate=mapper.get("StartDate").get(i);
                        myEndDate=mapper.get("EndDate").get(i);

                        simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

                        date=simpleDateFormat.parse(myStartDate);
                        calendar= Calendar.getInstance();
                        calendar.setTime(date);
                        day=calendar.get(Calendar.DAY_OF_MONTH);
                        year=calendar.get(Calendar.YEAR);
                        month=calendar.get(Calendar.MONTH);
                        month++;
                        myDateOfStart=day+"/"+month+"/"+year;

                        date=simpleDateFormat.parse(myEndDate);
                        calendar= Calendar.getInstance();
                        calendar.setTime(date);
                        day=calendar.get(Calendar.DAY_OF_MONTH);
                        year=calendar.get(Calendar.YEAR);
                        month=calendar.get(Calendar.MONTH);
                        month++;
                        myDateOfEnd=day+"/"+month+"/"+year;

                        myDuration=mapper.get("ExamDuration").get(i);
                        parts = myDuration.split("-");
                        hour=Integer.parseInt(parts[0]);
                        minute=Integer.parseInt(parts[1]);

                        if(minute==0){
                            myDurationTime=hour+" hours";
                        }else{
                            myDurationTime=hour+" hours "+minute+" minutes";
                        }

                        values=new Values(mapper.get("ExamName").get(i),myDateOfStart,myDateOfEnd,myDurationTime,mapper.get("ExamId").get(i));
                        valuesList.add(values);
                    }
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }


        myExamsListAdapter=new MyExamsListAdapter(valuesList,getActivity());

        myExamsList.setLayoutManager(linearLayoutManager);
        myExamsList.setItemAnimator(new DefaultItemAnimator());
        myExamsList.setAdapter(myExamsListAdapter);
        myExamsListAdapter.notifyDataSetChanged();

        if(valuesList.isEmpty()){
            add.setVisibility(View.VISIBLE);
        }
        else {
            add.setVisibility(View.GONE);
        }

        ob=(HomeInterface)getActivity();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllExamsFragment f = new AllExamsFragment();
                ob.changeFragmentFromHome(f);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem=menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        if(searchView!=null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName((getActivity().getApplicationContext()), SearchResultsActivity.class)));
            searchView.setQueryHint("Search here..");
            searchView.setIconified(true);
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

                            filteredList.add(new Values(valuesList.get(i).name,valuesList.get(i).startDateValue,valuesList.get(i).endDateValue,valuesList.get(i).durationValue,valuesList.get(i).examId));
                        }
                    }
                    myExamsListAdapter=new MyExamsListAdapter(filteredList,getActivity());
                    myExamsList.setAdapter(myExamsListAdapter);
                    myExamsListAdapter.notifyDataSetChanged();
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.addIcon:
                AllExamsFragment f = new AllExamsFragment();
                ob.changeFragmentFromHome(f);
                break;
        }
        return super.onOptionsItemSelected(item);

    }
}
interface HomeInterface{
    public void changeFragmentFromHome(Fragment f);
}
