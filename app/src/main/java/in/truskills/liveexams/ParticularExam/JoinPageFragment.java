package in.truskills.liveexams.ParticularExam;


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
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.R;

//This is Join Fragment where a user can enroll for a new exam..

public class JoinPageFragment extends Fragment {

    //Declare variables..
    JoinPageInterface ob;
    TextView start_TimeJoinPage, end_TimeJoinPage, start_DateJoinPage, end_DateJoinPage, descriptionJoinPage, sponsorTextJoinPage;
    Spinner myLanguageJoinPage;
    String selectedLanguage, timestamp, examDetails, examId, Languages, examGiven;
    SharedPreferences prefs;
    Button join_button;
    Handler h;
    HashMap<String, String> mapper;
    Bundle b;
    ViewFlipper viewFlipperJoinPage;
    CustomSpinnerForDetailsAdapter customSpinnerForDetailsAdapter;

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

        start_DateJoinPage = (TextView) getActivity().findViewById(R.id.startDateJoinPage);
        end_DateJoinPage = (TextView) getActivity().findViewById(R.id.endDateJoinPage);
        start_TimeJoinPage = (TextView) getActivity().findViewById(R.id.startTimeJoinPage);
        end_TimeJoinPage = (TextView) getActivity().findViewById(R.id.endTimeJoinPage);
        sponsorTextJoinPage = (TextView) getActivity().findViewById(R.id.sponsorTextJoinPage);
        descriptionJoinPage = (TextView) getActivity().findViewById(R.id.descriptionJoinPage);
        myLanguageJoinPage = (Spinner) getActivity().findViewById(R.id.myLanguageJoinPage);
        join_button = (Button) getActivity().findViewById(R.id.join_button);

        viewFlipperJoinPage = (ViewFlipper) getActivity().findViewById(R.id.viewFlipperJoinPage);

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


        Typeface tff = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Regular.ttf");
        start_DateJoinPage.setTypeface(tff);
        end_DateJoinPage.setTypeface(tff);
        start_TimeJoinPage.setTypeface(tff);
        end_TimeJoinPage.setTypeface(tff);
        descriptionJoinPage.setTypeface(tff);
        sponsorTextJoinPage.setTypeface(tff);
        Typeface tff2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Bold.ttf");
        join_button.setTypeface(tff2);

        //Get arguments..
        b = getArguments();
        timestamp = b.getString("timestamp");
        examDetails = b.getString("examDetails");
        examId = b.getString("examId");
        examGiven = b.getString("examGiven");

        Answers.getInstance().logCustom(new CustomEvent("Join now page inspect")
                .putCustomAttribute("userName", prefs.getString("userName", ""))
                .putCustomAttribute("examId", examId));

        h = new Handler();

        //Parse the exam details..
        try {
            HashMap<String, String> mapper = MiscellaneousParser.join_start_Parser(examDetails);
            descriptionJoinPage.setText(mapper.get("Description"));

            String startDate = mapper.get("StartDate");
            String myStartDate = MiscellaneousParser.parseDate(startDate);
            String endDate = mapper.get("EndDate");
            String myEndDate = MiscellaneousParser.parseDate(endDate);
            String startTime = mapper.get("StartTime");
            String myStartTime = MiscellaneousParser.parseTimeForDetails(startTime);
            String endTime = mapper.get("EndTime");
            String myEndTime = MiscellaneousParser.parseTimeForDetails(endTime);
            Languages = mapper.get("Languages");

            start_DateJoinPage.setText(myStartDate);
            start_TimeJoinPage.setText(myStartTime);
            end_DateJoinPage.setText(myEndDate);
            end_TimeJoinPage.setText(myEndTime);


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

                ConstantsDefined.updateAndroidSecurityProvider(getActivity());
                ConstantsDefined.beforeVolleyConnect();

                final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                String url = ConstantsDefined.api + "enrollUser/" + prefs.getString("userId", "abc");
                StringRequest stringRequest = new StringRequest(Request.Method.PUT,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            mapper = MiscellaneousParser.enrollUserParser(response);
                            String success = mapper.get("success");
                            if (success.equals("true")) {
                                Log.e("messi", "onResponse: " + getActivity());

                                h.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String myJoinedExams = null;
                                        try {
                                            myJoinedExams = MiscellaneousParser.getJoinedExams(mapper.get("response"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        SharedPreferences.Editor e = prefs.edit();
                                        e.putString("joinedExams", myJoinedExams);
                                        e.apply();
                                    }
                                });
                            }else{
                                if(getActivity()!=null)
                                    Toast.makeText(getActivity(), "Something went wrong..\n" +
                                            "Please try again..", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //If connnection couldn't be made..
                        if(ConstantsDefined.isOnline(getActivity())){
                            //Do nothing..
                            if(getActivity()!=null)
                            Toast.makeText(getActivity(), "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                        }else{
                            if(getActivity()!=null)
                            Toast.makeText(getActivity(), "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                        }
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

        ArrayList<String> listOfLanguages = new ArrayList<>();
        try {
            listOfLanguages = MiscellaneousParser.getLanguagesPerExam(Languages);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        customSpinnerForDetailsAdapter = new CustomSpinnerForDetailsAdapter(getActivity(), listOfLanguages);

//        ArrayAdapter<String> adapterLanguage = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listOfLanguages);
        myLanguageJoinPage.setAdapter(customSpinnerForDetailsAdapter);

//        int index = customSpinnerForDetailsAdapter.getPosition(prefs.getString("language", "English"));
//        myLanguageJoinPage.setSelection(index);
        int index = customSpinnerForDetailsAdapter.getIndex(prefs.getString("language", "English"));
        myLanguageJoinPage.setSelection(index);

        myLanguageJoinPage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLanguage = adapterView.getSelectedItem().toString();
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