package in.truskills.liveexams.MainScreens;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;

import in.truskills.liveexams.Miscellaneous.SearchResultsActivity;
import in.truskills.liveexams.R;

/**
 * This is the activity for displaying the names of all authors corresponding to a particular field/stream in kit..
 *
 * Functions:
 * 1. onCreate() : for basic stuff and calling setList() function..
 * 2. setList() : for populating authors list obtained via intent..
 * 3. onCreateOptionsMenu() : for searching among authors..
 * 4. onSupportNavigateUp() : on back press on toolbar..
 */

public class AllAuthorsPerFieldForKitActivity extends AppCompatActivity {

    RecyclerView authorsList;
    LinearLayoutManager linearLayoutManager;
    AllAuthorsPerFieldForKitActivityListAdapter authorsListAdapter;
    ArrayList<String> list,filteredList,mykits;
    String response;
    HashMap<String,ArrayList<String>> map;
    Bundle b=new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_authors_per_field_for_kit);

        //For toolbar..
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);
        getSupportActionBar().setTitle("SELECT YOUR AUTHOR");

        //Render elements from layout..
        authorsList=(RecyclerView)findViewById(R.id.authorsListForMyKits);
        linearLayoutManager = new LinearLayoutManager(this);
        authorsList.setLayoutManager(linearLayoutManager);
        authorsList.setItemAnimator(new DefaultItemAnimator());
        list=new ArrayList<>();
        b=getIntent().getBundleExtra("bundle");
        list=b.getStringArrayList("list");

        // getting the list of bought kits from previous activity.
        mykits = b.getStringArrayList("mykits");

        response=b.getString("response");
        map=new HashMap<>();

        //Call function..
        setList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //For searching among authors list..

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
                        authorsListAdapter = new AllAuthorsPerFieldForKitActivityListAdapter(filteredList,mykits, AllAuthorsPerFieldForKitActivity.this,response);  // changed the constructor of allauthorsperfieldforkitactivitylistadapter to pass list of bought kits..
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
                        authorsListAdapter = new AllAuthorsPerFieldForKitActivityListAdapter(filteredList,mykits, AllAuthorsPerFieldForKitActivity.this,response);  // changed the constructor of allauthorsperfieldforkitactivitylistadapter to pass list of bought kits.
                        authorsList.setAdapter(authorsListAdapter);
                        authorsListAdapter.notifyDataSetChanged();
                    }
                    return true;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    public void setList(){

        //For populating authors list..
        list=new ArrayList<>();
        list=b.getStringArrayList("list");
        authorsListAdapter = new AllAuthorsPerFieldForKitActivityListAdapter(list,mykits, AllAuthorsPerFieldForKitActivity.this,response);  // changed the constructor of allauthorsperfieldforkitactivitylistadapter to pass list of bought kits.
        authorsList.setLayoutManager(linearLayoutManager);
        authorsList.setItemAnimator(new DefaultItemAnimator());
        authorsList.setAdapter(authorsListAdapter);
        authorsListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        //on back press on toolbar..
        finish();
        return true;
    }
}
