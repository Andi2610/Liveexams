package in.truskills.liveexams.ParticularExam;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.Quiz.MySqlDatabase;
import in.truskills.liveexams.Quiz.QuestionPaperLoad;
import in.truskills.liveexams.Quiz.QuizMainActivity;
import in.truskills.liveexams.R;

//This is Start Fragment where a user can unenroll from an exam or start the quiz of the exam..

public class StartPageFragment extends Fragment {

    //Declare variables..
    StartPageInterface ob;
    TextView startDetails, endDetails, descriptionStartPage;
    Spinner myLanguage;
    String selectedLanguage, timestamp, examDetails, examId, name;
    SharedPreferences prefs;
    Button start_leave_button;
    Bundle b;
    Handler h;
    HashMap<String,String> mapper;
    ViewFlipper viewFlipper;

    public StartPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_start_page, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MySqlDatabase o=new MySqlDatabase(getActivity());

        //Get shared preferences..
        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        ob = (StartPageInterface) getActivity();
        ob.changeTitleForStartPage();

        h=new Handler();

        startDetails = (TextView) getActivity().findViewById(R.id.startDetails);
        endDetails = (TextView) getActivity().findViewById(R.id.endDetails);
        descriptionStartPage = (TextView) getActivity().findViewById(R.id.descriptionStartPage);
        myLanguage = (Spinner) getActivity().findViewById(R.id.myLanguage);
        start_leave_button = (Button) getActivity().findViewById(R.id.start_leave_button);
        viewFlipper=(ViewFlipper)getActivity().findViewById(R.id.viewFlipper);

        int[] resources = {
                R.drawable.first,
                R.drawable.second,
                R.drawable.third,
                R.drawable.fourth,
        };

        for (int i = 0; i < resources.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(resources[i]);
            viewFlipper.addView(imageView);
        }

        viewFlipper.setInAnimation(getActivity(), android.R.anim.fade_in);
        viewFlipper.setOutAnimation(getActivity(), android.R.anim.fade_out);

        viewFlipper.setAutoStart(true);
        viewFlipper.setFlipInterval(2000);

        Typeface tff=Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Regular.ttf");
        startDetails.setTypeface(tff);
        endDetails.setTypeface(tff);
        descriptionStartPage.setTypeface(tff);
        Typeface tff2=Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Bold.ttf");
        start_leave_button.setTypeface(tff2);

//
//        start_leave_button.setText("START");
//        start_leave_button.setBackgroundColor(Color.parseColor("#8DC640"));

        //Get arguments..
        b = getArguments();
        timestamp = b.getString("timestamp");

        examDetails = b.getString("examDetails");
        examId = b.getString("examId");

        Log.d("examDetails",examDetails+"");

        Answers.getInstance().logCustom(new CustomEvent("Start page inspect")
                .putCustomAttribute("userName",prefs.getString("userName",""))
                .putCustomAttribute("examId",examId));


        //Parse the exam details..
        try {
            HashMap<String, String> mapper = VariablesDefined.join_start_Parser(examDetails);
            descriptionStartPage.setText(mapper.get("Description"));
            String startDate=mapper.get("StartDate");
            String myStartDate=VariablesDefined.parseDate(startDate);
            String endDate=mapper.get("EndDate");
            String myEndDate=VariablesDefined.parseDate(endDate);
            String startTime=mapper.get("StartTime");

            Log.d("timeDetails","dateInitially="+myStartDate+"**"+myEndDate);

            String myStartTime=VariablesDefined.parseTimeForDetails(startTime);
            String endTime=mapper.get("EndTime");
            String myEndTime=VariablesDefined.parseTimeForDetails(endTime);

            Log.d("timeDetails","timeInitially="+myStartTime+"**"+myEndTime);


            String myTimestamp=VariablesDefined.parseTimestamp(timestamp);

            Log.d("timeDetails","timestampDateInitially="+myTimestamp);


            String myTime=VariablesDefined.parseTimestampForTime(timestamp);

            Log.d("timeDetails","timestampTimeInitially="+myTime);



            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
            Date start_date=simpleDateFormat.parse(myStartDate);
            Date end_date=simpleDateFormat.parse(myEndDate);
            Date middle_date=simpleDateFormat.parse(myTimestamp);

            Log.d("timeDetails","DateFinally"+start_date+"**"+middle_date+"**"+end_date);

            SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("h-mm a");
            Date start_time=simpleDateFormat2.parse(myStartTime);
            Date end_time=simpleDateFormat2.parse(myEndTime);
            Date middle_time=simpleDateFormat2.parse(myTime);

            Log.d("timeDetails","TimeFinally"+start_time+"**"+middle_time+"**"+end_time);

//            if(middle_date.before(start_date)){
//                Log.d("timeDetails","in1");
//            }else{
//                Log.d("timeDetails","in2");
//            }
//            if(middle_date.after(end_date)){
//                Log.d("timeDetails","in1");
//            }else{
//                Log.d("timeDetails","in2");
//            }
//            if(middle_time.before(start_time)){
//                Log.d("timeDetails","in1");
//            }else{
//                Log.d("timeDetails","in2");
//            }
//            if(middle_time.after(end_time)){
//                Log.d("timeDetails","in1");
//            }else{
//                Log.d("timeDetails","in2");
//            }

            if(!((middle_date.before(start_date) || middle_date.after(end_date)))){
                if(middle_date.equals(start_date)){
                    if(!middle_time.before(start_time)){
                        start_leave_button.setText("START");
                        start_leave_button.setBackgroundColor(Color.parseColor("#8DC640"));
                    }
                }else if(middle_date.equals(end_date)){
                    if(!middle_time.after(end_time)){
                        start_leave_button.setText("START");
                        start_leave_button.setBackgroundColor(Color.parseColor("#8DC640"));
                        endDetails.setBackgroundColor(Color.parseColor("#ef9a9a"));
                    }
                }else{
                    start_leave_button.setText("START");
                    start_leave_button.setBackgroundColor(Color.parseColor("#8DC640"));
                }
            }else if(middle_date.after(end_date)){
                start_leave_button.setText("EXAM IS OVER");
                start_leave_button.setBackgroundColor(Color.parseColor("#E0E0E0"));
            }else {
                start_leave_button.setText("LEAVE");
                start_leave_button.setBackgroundColor(Color.parseColor("#f44336"));
            }

//            start_leave_button.setText("START");
//            start_leave_button.setBackgroundColor(Color.parseColor("#8DC640"));

            Log.d("newTimestamp=",myTimestamp);

            startDetails.setText(myStartDate + "\n" + myStartTime);
            endDetails.setText(myEndDate + "\n" + myEndTime);
            name = mapper.get("ExamName");


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<String> listOfLanguages = new ArrayList<>();
        listOfLanguages.add("LANGUAGE");
        listOfLanguages.add("Hindi");
        listOfLanguages.add("Gujarati");
        listOfLanguages.add("English");

        ArrayAdapter<String> adapterLanguage = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listOfLanguages);
        myLanguage.setAdapter(adapterLanguage);

        int index = adapterLanguage.getPosition(prefs.getString("language", "English"));
        myLanguage.setSelection(index);

        myLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLanguage = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        start_leave_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If start time of quiz hasn't reached..
                if (start_leave_button.getText().equals("LEAVE")) {
                    Answers.getInstance().logCustom(new CustomEvent("Leave button clicked")
                            .putCustomAttribute("userName",prefs.getString("userName",""))
                            .putCustomAttribute("examId",examId));
                    //Unenroll user
                    final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                    String url = VariablesDefined.api + "unenrollUser/" + prefs.getString("userId", "abc");
                    StringRequest stringRequest = new StringRequest(Request.Method.PUT,
                            url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                    mapper = VariablesDefined.unenrollUserParser(response);
                                    String success = mapper.get("success");
                                    if (success.equals("true")) {
                                        //Get Joined Exams Result
                                        h.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                String myJoinedExams= null;
                                                try {
                                                    myJoinedExams = VariablesDefined.getJoinedExams(mapper.get("response"));
                                                    SharedPreferences.Editor e=prefs.edit();
                                                    e.putString("joinedExams",myJoinedExams);
                                                    e.apply();
                                                    ob = (StartPageInterface) getActivity();
                                                    JoinPageFragment f = new JoinPageFragment();
                                                    f.setArguments(b);
                                                    ob.changeFragmentFromStartPage(f, "name");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
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
                            //If the connection couldn't be made..
                            Toast.makeText(getActivity(), "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            //Set required parameters..
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("examId", examId);
                            return params;
                        }
                    };
                    requestQueue.add(stringRequest);
                } else if(start_leave_button.getText().equals("START")){
                    Answers.getInstance().logCustom(new CustomEvent("Start button clicked")
                            .putCustomAttribute("userName",prefs.getString("userName",""))
                            .putCustomAttribute("examId",examId));
                    //Check if a valid language has been chosen from the list..
                    if(selectedLanguage.equals("LANGUAGE"))
                        //If not chosen..
                        Toast.makeText(getActivity(), "Please select a language", Toast.LENGTH_SHORT).show();
                    else{
                        //Else if chosen..
                        //Start Quiz
//                        Intent i = new Intent(getActivity(), QuizMainActivity.class);
//                        i.putExtra("examId", examId);
//                        i.putExtra("name", name);
//                        i.putExtra("language", selectedLanguage);
//                        getActivity().startActivity(i);

                        Intent i=new Intent(getActivity(), QuestionPaperLoad.class);
                        i.putExtra("examId", examId);
                        i.putExtra("name", name);
                        i.putExtra("language", selectedLanguage);
                        startActivity(i);
                    }
                }else{
                    Toast.makeText(getActivity(), "This exam is over", Toast.LENGTH_SHORT).show();
                }
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
            case R.id.rulesIcon:
                //When Rules icon is clicked.. load Rules fragment through interface..
                ob = (StartPageInterface) getActivity();
                RulesFragment f = new RulesFragment();
                ob.changeFragmentFromStartPage(f, "RULES");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

interface StartPageInterface {
    public void changeFragmentFromStartPage(Fragment f, String title);

    public void changeTitleForStartPage();
}
