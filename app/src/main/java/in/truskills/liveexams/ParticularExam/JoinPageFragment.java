package in.truskills.liveexams.ParticularExam;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.Miscellaneous.QuestionPaperParser;
import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.R;

//This is Join Fragment where a user can enroll for a new exam..

public class JoinPageFragment extends Fragment {

    //Declare variables..
    JoinPageInterface ob;
    TextView startDetailsJoinPage, endDetailsJoinPage, descriptionJoinPage;
    Spinner myLanguageJoinPage;
    String selectedLanguage, timestamp, examDetails, examId;
    SharedPreferences prefs;
    Button join_button;
    Handler h;
    HashMap<String,String> mapper;
    Bundle b;
    ViewFlipper viewFlipperJoinPage;

    public JoinPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_join_page, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get shared preferences..
        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        ob = (JoinPageInterface) getActivity();
        ob.changeTitleForJoinPage();

        startDetailsJoinPage = (TextView) getActivity().findViewById(R.id.startDetailsJoinPage);
        endDetailsJoinPage = (TextView) getActivity().findViewById(R.id.endDetailsJoinPage);
        descriptionJoinPage = (TextView) getActivity().findViewById(R.id.descriptionJoinPage);
        myLanguageJoinPage = (Spinner) getActivity().findViewById(R.id.myLanguageJoinPage);
        join_button = (Button) getActivity().findViewById(R.id.join_button);

        viewFlipperJoinPage=(ViewFlipper)getActivity().findViewById(R.id.viewFlipperJoinPage);

        int[] resources = {
                R.drawable.first,
                R.drawable.second,
                R.drawable.third,
                R.drawable.fourth,
        };

        for (int i = 0; i < resources.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(resources[i]);
            viewFlipperJoinPage.addView(imageView);
        }

        viewFlipperJoinPage.setInAnimation(getActivity(), android.R.anim.fade_in);
        viewFlipperJoinPage.setOutAnimation(getActivity(), android.R.anim.fade_out);

        viewFlipperJoinPage.setAutoStart(true);
        viewFlipperJoinPage.setFlipInterval(2000);


        Typeface tff=Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Regular.ttf");
        startDetailsJoinPage.setTypeface(tff);
        endDetailsJoinPage.setTypeface(tff);
        descriptionJoinPage.setTypeface(tff);
        Typeface tff2=Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Bold.ttf");
        join_button.setTypeface(tff2);

        //Get arguments..
        b = getArguments();
        timestamp = b.getString("timestamp");
        examDetails = b.getString("examDetails");
        examId = b.getString("examId");

        h=new Handler();

        //Parse the exam details..
        try {
                HashMap<String, String> mapper = VariablesDefined.join_start_Parser(examDetails);
                descriptionJoinPage.setText(mapper.get("Description"));

            String startDate=mapper.get("StartDate");
            String myStartDate=VariablesDefined.parseDate(startDate);
            String endDate=mapper.get("EndDate");
            String myEndDate=VariablesDefined.parseDate(endDate);
            String startTime=mapper.get("StartTime");
            String myStartTime=VariablesDefined.parseTimeForDetails(startTime);
            String endTime=mapper.get("EndTime");
            String myEndTime=VariablesDefined.parseTimeForDetails(endTime);

            startDetailsJoinPage.setText(myStartDate + "\n\n" + myStartTime);
            endDetailsJoinPage.setText(myEndDate + "\n\n" + myEndTime);



        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ob = (JoinPageInterface) getActivity();

                //Enroll User
                final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                String url = VariablesDefined.api + "enrollUser/" + prefs.getString("userId", "abc");
                StringRequest stringRequest = new StringRequest(Request.Method.PUT,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            mapper = VariablesDefined.enrollUserParser(response);
                            String success = mapper.get("success");
                            if (success.equals("true")) {
                                Log.e("messi", "onResponse: "+getActivity() );

                                h.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String myJoinedExams= null;
                                        try {
                                            myJoinedExams = VariablesDefined.getJoinedExams(mapper.get("response"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        SharedPreferences.Editor e=prefs.edit();
                                        e.putString("joinedExams",myJoinedExams);
                                        e.apply();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //If connnection couldn't be made..
                        Toast.makeText(getActivity(), "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        //Set required parameters..
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("examId", examId);
                        params.put("language", selectedLanguage);
                        return params;
                    }
                };
                requestQueue.add(stringRequest);

                StartPageFragment f = new StartPageFragment();
                f.setArguments(b);
                ob.changeFragmentFromJoinPage(f, "name");
            }
        });

//        startDetailsJoinPage.setText("Thursday\n12th January 2017\n8:00 AM");
//        endDetailsJoinPage.setText("Saturday\n14th January 2017\n7:00 PM");

        ArrayList<String> listOfLanguages = new ArrayList<>();
        listOfLanguages.add("LANGUAGE");
        listOfLanguages.add("Hindi");
        listOfLanguages.add("Gujarati");
        listOfLanguages.add("English");

        ArrayAdapter<String> adapterLanguage = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listOfLanguages);
        myLanguageJoinPage.setAdapter(adapterLanguage);

        int index = adapterLanguage.getPosition(prefs.getString("language", "English"));
        myLanguageJoinPage.setSelection(index);

        myLanguageJoinPage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLanguage = adapterView.getItemAtPosition(i).toString();
                SharedPreferences.Editor e = prefs.edit();
                e.putString("language", selectedLanguage);
                e.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.rules_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //When Rules icon is clicked.. load Rules fragment through interface..
            case R.id.rulesIcon:
                ob = (JoinPageInterface) getActivity();
                RulesFragment f = new RulesFragment();
                ob.changeFragmentFromJoinPage(f, "RULES");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

//Interface used for interaction with JoinFragment..
interface JoinPageInterface {
    public void changeFragmentFromJoinPage(Fragment f, String title);

    public void changeTitleForJoinPage();

}