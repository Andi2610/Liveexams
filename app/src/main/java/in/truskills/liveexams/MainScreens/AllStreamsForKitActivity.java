package in.truskills.liveexams.MainScreens;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import in.truskills.liveexams.R;

public class AllStreamsForKitActivity extends AppCompatActivity implements ConnectivityReciever.ConnectivityReceiverListener{

    RecyclerView allStreamsList;
    //    FloatingActionButton floatingActionButton;
    LinearLayoutManager linearLayoutManager;
    AllStreamsForKitActivityListActivity streamsListAdapter;
    List<String> valuesList;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    Handler h;
    TextView noStreams;
    LinearLayout noConnectionLayout;
    Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_streams_for_kit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);

        getSupportActionBar().setTitle("SELECT YOUR FIELD");

        allStreamsList = (RecyclerView) findViewById(R.id.allStreamsForMyKitsListTemp);
        linearLayoutManager = new LinearLayoutManager(this);

//        floatingActionButton=(FloatingActionButton)getActivity().findViewById(R.id.fab);

        requestQueue = Volley.newRequestQueue(this);
        h = new Handler();

        noStreams = (TextView) findViewById(R.id.noStreams);
        Typeface tff = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        noStreams.setTypeface(tff);

        noConnectionLayout=(LinearLayout)findViewById(R.id.noConnectionLayoutForStreamsForMyKits);
        retryButton=(Button)findViewById(R.id.retryButtonForStreamsForMyKits);

        noStreams.setVisibility(View.GONE);
        noConnectionLayout.setVisibility(View.GONE);

        dialog = new ProgressDialog(AllStreamsForKitActivity.this);
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

    }

    public void setList(){


        valuesList=new ArrayList<>();

        dialog.show();

        ConstantsDefined.updateAndroidSecurityProvider(this);
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
                        streamsListAdapter = new AllStreamsForKitActivityListActivity(valuesList, AllStreamsForKitActivity.this);
                        allStreamsList.setLayoutManager(linearLayoutManager);
                        allStreamsList.setItemAnimator(new DefaultItemAnimator());
                        allStreamsList.setAdapter(streamsListAdapter);
                        streamsListAdapter.notifyDataSetChanged();
                        Toast.makeText(AllStreamsForKitActivity.this, "Something went wrong..\n" +
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
                streamsListAdapter = new AllStreamsForKitActivityListActivity(valuesList, AllStreamsForKitActivity.this);
                allStreamsList.setLayoutManager(linearLayoutManager);
                allStreamsList.setItemAnimator(new DefaultItemAnimator());
                allStreamsList.setAdapter(streamsListAdapter);
                streamsListAdapter.notifyDataSetChanged();

                if(dialog!=null)
                    dialog.dismiss();


                if(ConstantsDefined.isOnline(AllStreamsForKitActivity.this)){
                    //Do nothing..
                    Toast.makeText(AllStreamsForKitActivity.this, "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(AllStreamsForKitActivity.this, "Sorry! Couldn't connect", Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void populateList(List<String> list) {
        streamsListAdapter = new AllStreamsForKitActivityListActivity(list, AllStreamsForKitActivity.this);
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
