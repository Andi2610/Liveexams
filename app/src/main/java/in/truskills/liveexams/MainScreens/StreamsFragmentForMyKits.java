package in.truskills.liveexams.MainScreens;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.Miscellaneous.ConnectivityReciever;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.Miscellaneous.SearchResultsActivity;
import in.truskills.liveexams.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StreamsFragmentForMyKits extends Fragment implements ConnectivityReciever.ConnectivityReceiverListener{


    RecyclerView allStreamsList;
    //    FloatingActionButton floatingActionButton;
    LinearLayoutManager linearLayoutManager;
    StreamsFragmentForMyKitsListAdapter streamsListAdapter;
    List<String> valuesList;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    Handler h;
    TextView noStreams;
    LinearLayout noConnectionLayout;
    Button retryButton;
    StreamInterfaceForMyKits streamInterface;


    public StreamsFragmentForMyKits() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_streams_fragment_for_my_kits, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        allStreamsList = (RecyclerView) getActivity().findViewById(R.id.allStreamsForMyKitsListTemp);
        linearLayoutManager = new LinearLayoutManager(getActivity());

//        floatingActionButton=(FloatingActionButton)getActivity().findViewById(R.id.fab);

        requestQueue = Volley.newRequestQueue(getActivity());
        h = new Handler();

        noStreams = (TextView) getActivity().findViewById(R.id.noStreams);
        Typeface tff = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Regular.ttf");
        noStreams.setTypeface(tff);

        noConnectionLayout=(LinearLayout)getActivity().findViewById(R.id.noConnectionLayoutForStreamsForMyKits);
        retryButton=(Button)getActivity().findViewById(R.id.retryButtonForStreamsForMyKits);

        noStreams.setVisibility(View.GONE);
        noConnectionLayout.setVisibility(View.GONE);

        dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Fetching data.. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

        allStreamsList.setLayoutManager(linearLayoutManager);
        allStreamsList.setItemAnimator(new DefaultItemAnimator());

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("reached", "onClick: ");
                setList();
            }
        });

        valuesList=new ArrayList<>();

        setList();

        streamInterface=(StreamInterfaceForMyKits) getActivity();

//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AllExamsFragmentTemp f=new AllExamsFragmentTemp();
//                String title="ADD NEW EXAMS";
//                streamInterface.changeFromStream(f,title);
//            }
//        });

    }

    public void setList(){


        valuesList=new ArrayList<>();

        if(getActivity()!=null){
            dialog.show();
        }

        ConstantsDefined.updateAndroidSecurityProvider(getActivity());
        ConstantsDefined.beforeVolleyConnect();

        String url = ConstantsDefined.apiForKit+"getStreamNames";
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

                    String success=response.getString("success");
                    if(success.equals("true")){
                        ArrayList<String> ans= MiscellaneousParser.getStreamNamesForMyKitsParser(response);
                        int length = ans.size();
                        if (length == 0) {
                            valuesList.clear();
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    noStreams.setVisibility(View.VISIBLE);
                                    noConnectionLayout.setVisibility(View.GONE);
                                    populateList(valuesList);
                                }
                            });
                        } else {
                            noStreams.setVisibility(View.GONE);
                            noConnectionLayout.setVisibility(View.GONE);
                            valuesList=ans;

                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    populateList(valuesList);
                                }
                            });
                        }
                    }else{
                        noConnectionLayout.setVisibility(View.VISIBLE);
                        noStreams.setVisibility(View.GONE);
                        valuesList=new ArrayList<>();
                        streamsListAdapter = new StreamsFragmentForMyKitsListAdapter(valuesList, getActivity(),streamInterface);
                        allStreamsList.setLayoutManager(linearLayoutManager);
                        allStreamsList.setItemAnimator(new DefaultItemAnimator());
                        allStreamsList.setAdapter(streamsListAdapter);
                        streamsListAdapter.notifyDataSetChanged();
                        if(getActivity()!=null)
                            Toast.makeText(getActivity(), "Something went wrong..\n" +
                                    "Please try again..", Toast.LENGTH_SHORT).show();
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
                noStreams.setVisibility(View.GONE);
                valuesList=new ArrayList<>();
                streamsListAdapter = new StreamsFragmentForMyKitsListAdapter(valuesList, getActivity(),streamInterface);
                allStreamsList.setLayoutManager(linearLayoutManager);
                allStreamsList.setItemAnimator(new DefaultItemAnimator());
                allStreamsList.setAdapter(streamsListAdapter);
                streamsListAdapter.notifyDataSetChanged();

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

    public void populateList(List<String> list) {
        streamsListAdapter = new StreamsFragmentForMyKitsListAdapter(list, getActivity(),streamInterface);
        allStreamsList.setLayoutManager(linearLayoutManager);
        allStreamsList.setItemAnimator(new DefaultItemAnimator());
        allStreamsList.setAdapter(streamsListAdapter);
        streamsListAdapter.notifyDataSetChanged();

        if (list.isEmpty()) {
            noStreams.setVisibility(View.VISIBLE);
            noConnectionLayout.setVisibility(View.GONE);

        } else {
            noStreams.setVisibility(View.GONE);
            noConnectionLayout.setVisibility(View.GONE);

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

interface StreamInterfaceForMyKits{
    public void changeFromStreamForMyKits(Fragment f,String title);
}
