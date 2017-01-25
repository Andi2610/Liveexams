package in.truskills.liveexams.MainScreens;


import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

import in.truskills.liveexams.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllExams extends Fragment {


    RecyclerView allExamsList;
    LinearLayoutManager linearLayoutManager;
    AllExamsListAdapter allExamsListAdapter;
    List<Values> valuesList,filteredList;
    Values values;

    public AllExams() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_all_exams, container, false);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("here","inOnAcCr");
        allExamsList=(RecyclerView)getActivity().findViewById(R.id.allExamsList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        valuesList=new ArrayList<>();

        values=new Values("ABC","20/04/2016","23/04/2016","3 Hours");
        valuesList.add(values);
        values=new Values("XYZ","10/05/2015","11/05/2015","1 Hour");
        valuesList.add(values);
        values=new Values("DEF","20/04/2016","23/04/2016","3 Hours");
        valuesList.add(values);
        values=new Values("GHI","10/05/2015","11/05/2015","1 Hour");
        valuesList.add(values);
        values=new Values("JEE MAIN","20/04/2016","23/04/2016","3 Hours");
        valuesList.add(values);
        values=new Values("JEE ADVANCED","10/05/2015","11/05/2015","1 Hour");
        valuesList.add(values);
        values=new Values("CLAT","20/04/2016","23/04/2016","3 Hours");
        valuesList.add(values);
        values=new Values("GMAT","10/05/2015","11/05/2015","1 Hour");
        valuesList.add(values);
        Log.d("here","size"+valuesList.size());
        allExamsListAdapter=new AllExamsListAdapter(valuesList,getActivity());

        allExamsList.setLayoutManager(linearLayoutManager);
        allExamsList.setItemAnimator(new DefaultItemAnimator());
        allExamsList.setAdapter(allExamsListAdapter);
        allExamsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.all_exams_menu, menu);
        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem=menu.findItem(R.id.searchAllExams);
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

                            filteredList.add(new Values(valuesList.get(i).name,valuesList.get(i).startDateValue,valuesList.get(i).endDateValue,valuesList.get(i).durationValue));
                        }
                    }
                    allExamsListAdapter=new AllExamsListAdapter(filteredList,getActivity());
                    allExamsList.setAdapter(allExamsListAdapter);
                    allExamsListAdapter.notifyDataSetChanged();
                    return true;
                }
            });
        }
    }

}
