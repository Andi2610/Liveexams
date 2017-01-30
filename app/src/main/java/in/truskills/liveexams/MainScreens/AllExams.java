package in.truskills.liveexams.MainScreens;


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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.R;


public class AllExams extends Fragment {


    RecyclerView allExamsList;
    LinearLayoutManager linearLayoutManager;
    AllExamsListAdapter allExamsListAdapter;
    List<Values> valuesList,filteredList;
    Values values;
    RequestQueue requestQueue;
    Handler h;
    SearchView searchView;

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
        allExamsList=(RecyclerView)getActivity().findViewById(R.id.allExamsList);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        requestQueue = Volley.newRequestQueue(getActivity());
        h=new Handler();

        valuesList=new ArrayList<>();

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
        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
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

                    if(s.equals("")){
                        allExamsListAdapter=new AllExamsListAdapter(filteredList,getActivity());
                        allExamsList.setAdapter(allExamsListAdapter);
                        allExamsListAdapter.notifyDataSetChanged();
                    }else{
                        String url = VariablesDefined.api + "searchExams/" + s;
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                                url, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    HashMap<String, ArrayList<String>> mapper = VariablesDefined.allExamsParser(response);
                                    int length = response.getJSONArray("response").length();
                                    if(length==0){
                                        filteredList.clear();
                                        h.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                populateList(filteredList);
                                            }
                                        });
                                    }
                                    else{
                                        for (int i = 0; i < length; ++i) {
                                            values = new Values(mapper.get("ExamName").get(i), mapper.get("StartDate").get(i), mapper.get("EndDate").get(i), mapper.get("ExamDuration").get(i),mapper.get("ExamId").get(i));
                                            filteredList.add(values);
                                            h.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    populateList(filteredList);
                                                }
                                            });

                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getActivity(), "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                        requestQueue.add(jsonObjectRequest);
                    }
                    return true;
                }
            });
        }
    }

    public void populateList(List<Values> list){
        allExamsListAdapter=new AllExamsListAdapter(list,getActivity());
        allExamsList.setAdapter(allExamsListAdapter);
        allExamsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("response","inOnResAllExaFrag");
        filteredList=new ArrayList<>();

        if(searchView==null){
            allExamsListAdapter=new AllExamsListAdapter(filteredList,getActivity());
            allExamsList.setAdapter(allExamsListAdapter);
            allExamsListAdapter.notifyDataSetChanged();
        }else{
            String url = VariablesDefined.api + "searchExams/" + searchView.getQuery();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    url, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        HashMap<String, ArrayList<String>> mapper = VariablesDefined.allExamsParser(response);
                        int length = response.getJSONArray("response").length();
                        if(length==0){
                            filteredList.clear();
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    populateList(filteredList);
                                }
                            });
                        }
                        else{
                            for (int i = 0; i < length; ++i) {
                                values = new Values(mapper.get("ExamName").get(i), mapper.get("StartDate").get(i), mapper.get("EndDate").get(i), mapper.get("ExamDuration").get(i),mapper.get("ExamId").get(i));
                                filteredList.add(values);
                                h.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        populateList(filteredList);
                                    }
                                });

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        }
    }
}
