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
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.Miscellaneous.SearchResultsActivity;
import in.truskills.liveexams.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AuthorFragment extends Fragment {


    RecyclerView authorsList;
    LinearLayoutManager linearLayoutManager;
    AuthorsListAdapter authorsListAdapter;
    AuthorInterface authorInterface;
    ArrayList<String> list,filteredList;
    String response;
    HashMap<String,ArrayList<String>> map;

    public AuthorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_author, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        authorsList=(RecyclerView)getActivity().findViewById(R.id.authorsList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        authorsList.setLayoutManager(linearLayoutManager);
        authorsList.setItemAnimator(new DefaultItemAnimator());
        list=getArguments().getStringArrayList("list");

        response=getArguments().getString("response");
        map=new HashMap<>();

        authorInterface=(AuthorInterface)getActivity();

        authorsListAdapter = new AuthorsListAdapter(list, getActivity(),authorInterface,response);
        authorsList.setLayoutManager(linearLayoutManager);
        authorsList.setItemAnimator(new DefaultItemAnimator());
        authorsList.setAdapter(authorsListAdapter);
        authorsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.all_exams_menu, menu);
        SearchManager searchManager=null;
        if(getActivity()!=null){
            searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
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
                            authorsListAdapter = new AuthorsListAdapter(filteredList, getActivity(),authorInterface,response);
                            authorsList.setAdapter(authorsListAdapter);
                            authorsListAdapter.notifyDataSetChanged();

                        }else{
                            s = s.toString().toLowerCase();
                            filteredList = new ArrayList<>();

                            for (int i = 0; i < list.size(); i++) {

                                final String text = list.get(i).toLowerCase();
                                if (text.contains(s)) {

                                    filteredList.add(list.get(i));
                                }
                            }
                            authorsListAdapter = new AuthorsListAdapter(filteredList, getActivity(),authorInterface,response);
                            authorsList.setAdapter(authorsListAdapter);
                            authorsListAdapter.notifyDataSetChanged();
                        }
                        return true;
                    }
                });
            }
        }
    }

    public void setList(){
        list=getArguments().getStringArrayList("list");
        authorsListAdapter = new AuthorsListAdapter(list, getActivity(),authorInterface,response);
        authorsList.setLayoutManager(linearLayoutManager);
        authorsList.setItemAnimator(new DefaultItemAnimator());
        authorsList.setAdapter(authorsListAdapter);
        authorsListAdapter.notifyDataSetChanged();
    }
}

interface AuthorInterface{
    public void changeFromAuthor(Fragment f,String title,String response,String value,ArrayList<String> myList);
}