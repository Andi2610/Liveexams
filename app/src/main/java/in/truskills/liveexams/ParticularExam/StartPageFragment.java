package in.truskills.liveexams.ParticularExam;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.Miscellaneous.CheckForPermissions;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.SqliteDatabases.QuizDatabase;
import in.truskills.liveexams.R;

/**
 * This is Start Fragment where a user can un enroll from an exam or start the quiz of the exam..
 *
 * Functions:
 * * Functions:
 * 1. onCreateView() : for layout inflation and menu for rules on toolbar..
 * 2. onActivityCreated() : for showing exam details and for clicking on start/leave button handling...
 * 3. onCreateOptionsMenu() : for showing rules menu..
 * 4. onOptionsItemSelected() :  click on rules button to load rules fragment..
 * 5. onRequestPermissionsResult() : for checking for write,camera,vibrate permissions..
 * 6. getDate() : to get current date to determine as to  when the user gave the exam..
 * 7. afterConnect() : to start rulesBeforeQuiz activity..
 *
 * API calls made:
 * 1. /api/unenrollUser/userId : (POST api with parameters: examId) : to unenroll a user form an exam on LEAVE button click..
 * 2. /api/getS3Url : (GET api) : to get S3 url..
 * 3. /api/getTimeStamp : (GET api) :  to get current timestamp..
 *
 */

public class StartPageFragment extends Fragment {

    //Declare variables..
    StartPageInterface ob;
    TextView descriptionStartPage, start_Time, end_Time, start_Date, end_Date, sponsorText;
    Spinner myLanguage;
    String selectedLanguage, timestamp, examDetails, examId, name, Languages, examGiven,myDate,myUrl;
    SharedPreferences prefs,dataPrefs,quizPrefs,firstTime,firstTimeForRules;
    Button start_leave_button;
    Bundle b;
    Handler h;
    HashMap<String, String> mapper;
    ViewFlipper viewFlipper;
    RequestQueue requestQueue;
    QuizDatabase o;
    ProgressDialog myDialog;
    CustomSpinnerForDetailsAdapter customSpinnerForDetailsAdapter;

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

        //Show dialog..
        if(getActivity()!=null){
            myDialog = new ProgressDialog(getActivity());
            myDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            myDialog.setMessage("Loading. Please wait...");
            myDialog.setIndeterminate(true);
            myDialog.setCancelable(false);
        }

        //Get shared preferences..
        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        dataPrefs = getActivity().getSharedPreferences("dataPrefs", Context.MODE_PRIVATE);
        quizPrefs = getActivity().getSharedPreferences("quizPrefs", Context.MODE_PRIVATE);
        firstTime = getActivity().getSharedPreferences("firstTime", Context.MODE_PRIVATE);
        firstTimeForRules = getActivity().getSharedPreferences("firstTimeForRules", Context.MODE_PRIVATE);

        o = new QuizDatabase(getActivity());

        ob = (StartPageInterface) getActivity();
        ob.changeTitleForStartPage();

        h = new Handler();

        //Render elements from layout..
        start_Date = (TextView) getActivity().findViewById(R.id.startDate);
        end_Date = (TextView) getActivity().findViewById(R.id.endDate);
        start_Time = (TextView) getActivity().findViewById(R.id.startTime);
        end_Time = (TextView) getActivity().findViewById(R.id.endTime);
        sponsorText = (TextView) getActivity().findViewById(R.id.sponsorText);
        descriptionStartPage = (TextView) getActivity().findViewById(R.id.descriptionStartPage);
        myLanguage = (Spinner) getActivity().findViewById(R.id.myLanguage);
        start_leave_button = (Button) getActivity().findViewById(R.id.start_leave_button);
        viewFlipper = (ViewFlipper) getActivity().findViewById(R.id.viewFlipper);

        //For view flipper..
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

        //Set typeface..
        Typeface tff = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Regular.ttf");
        start_Date.setTypeface(tff);
        end_Date.setTypeface(tff);
        start_Time.setTypeface(tff);
        end_Time.setTypeface(tff);
        sponsorText.setTypeface(tff);
        descriptionStartPage.setTypeface(tff);
        Typeface tff2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Bold.ttf");
        start_leave_button.setTypeface(tff2);

        //Get arguments..
        b = getArguments();
        timestamp = b.getString("timestamp");
        examDetails = b.getString("examDetails");
        examId = b.getString("examId");
        examGiven = b.getString("examGiven");

        //For Answers..
        Answers.getInstance().logCustom(new CustomEvent("Start page inspect")
                .putCustomAttribute("userName", prefs.getString("userName", ""))
                .putCustomAttribute("examId", examId));


        //Parse the exam details..
        try {

            //Parsev examDetails..
            HashMap<String, String> mapper = MiscellaneousParser.join_start_Parser(examDetails);
            descriptionStartPage.setText(mapper.get("Description"));
            String startDate = mapper.get("StartDate");
            String myStartDate = MiscellaneousParser.parseDate(startDate);
            String endDate = mapper.get("EndDate");
            String myEndDate = MiscellaneousParser.parseDate(endDate);
            String startTime = mapper.get("StartTime");
            Languages = mapper.get("Languages");
            String myStartTime = MiscellaneousParser.parseTimeForDetails(startTime);
            String endTime = mapper.get("EndTime");
            String myEndTime = MiscellaneousParser.parseTimeForDetails(endTime);
            String myTimestamp = MiscellaneousParser.parseTimestamp(timestamp);
            String myTime = MiscellaneousParser.parseTimestampForTime(timestamp);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date start_date = simpleDateFormat.parse(myStartDate);
            Date end_date = simpleDateFormat.parse(myEndDate);
            Date middle_date = simpleDateFormat.parse(myTimestamp);
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("h-mm a");
            Date start_time = simpleDateFormat2.parse(myStartTime);
            Date end_time = simpleDateFormat2.parse(myEndTime);
            Date middle_time = simpleDateFormat2.parse(myTime);

            //Check whether exam is over,live or upcoming..
            if (examGiven.equals("true")) {
                start_leave_button.setText("EXAM IS OVER");
                start_leave_button.setBackgroundColor(Color.parseColor("#E0E0E0"));
            } else {
                if (!((middle_date.before(start_date) || middle_date.after(end_date)))) {
                    if (middle_date.equals(start_date)) {
                        if (!middle_time.before(start_time)) {
                            start_leave_button.setText("START");
                            start_leave_button.setBackgroundColor(Color.parseColor("#8DC640"));
                        } else {
                            start_leave_button.setText("LEAVE");
                           // Intent i = new Intent(getActivity(), MainActivity.class);
                            //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                           // getActivity().startActivity(i);
                            start_leave_button.setBackgroundColor(Color.parseColor("#f44336"));
                        }
                    } else if (middle_date.equals(end_date)) {
                        if (!middle_time.after(end_time)) {
                            start_leave_button.setText("START");
                            start_leave_button.setBackgroundColor(Color.parseColor("#8DC640"));
                            end_Date.setTextColor(Color.parseColor("#f44336"));
                            end_Time.setTextColor(Color.parseColor("#f44336"));
                        } else {
                            start_leave_button.setText("EXAM IS OVER");
                            start_leave_button.setBackgroundColor(Color.parseColor("#E0E0E0"));
                        }
                    } else {
                        start_leave_button.setText("START");
                        start_leave_button.setBackgroundColor(Color.parseColor("#8DC640"));
                    }
                } else if (middle_date.after(end_date)) {
                    start_leave_button.setText("EXAM IS OVER");
                    start_leave_button.setBackgroundColor(Color.parseColor("#E0E0E0"));
                } else {
                    start_leave_button.setText("LEAVE");
                    //Intent i = new Intent(getActivity(), MainActivity.class);
                    //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //getActivity().startActivity(i);
                    start_leave_button.setBackgroundColor(Color.parseColor("#f44336"));
                }
            }

            //Set data to different text views..
            start_Date.setText(myStartDate);
            start_Time.setText(myStartTime);
            end_Date.setText(myEndDate);
            end_Time.setText(myEndTime);
            name = mapper.get("ExamName");

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //For language spinner..
        ArrayList<String> listOfLanguages = new ArrayList<>();
        try {
            listOfLanguages = MiscellaneousParser.getLanguagesPerExam(Languages);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        customSpinnerForDetailsAdapter = new CustomSpinnerForDetailsAdapter(getActivity(), listOfLanguages);
        myLanguage.setAdapter(customSpinnerForDetailsAdapter);
        int index = customSpinnerForDetailsAdapter.getIndex(prefs.getString("language", "English"));
        myLanguage.setSelection(index);
        myLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLanguage = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Click on start/leave button..
        start_leave_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If start time of quiz hasn't reached.. call unenroll api..as LEAVE is pressed..
                if (start_leave_button.getText().equals("LEAVE")) {

                    //For answers..
                    Answers.getInstance().logCustom(new CustomEvent("Leave button clicked")
                            .putCustomAttribute("userName", prefs.getString("userName", ""))
                            .putCustomAttribute("examId", examId));

                    //For https connection..
                    ConstantsDefined.updateAndroidSecurityProvider(getActivity());
                    ConstantsDefined.beforeVolleyConnect();
                    //Unenroll user
                    final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                    String url = ConstantsDefined.api + "unenrollUser/" + prefs.getString("userId", "abc");
                    StringRequest stringRequest = new StringRequest(Request.Method.PUT,
                            url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                //Parse the response..
                                mapper = MiscellaneousParser.unenrollUserParser(response);
                                String success = mapper.get("success");
                                //If success.. unenrolled successfully..go to Join Page fragment..
                                //Else show error message..
                                if (success.equals("true")) {

                                    h.post(new Runnable() {
                                        @Override
                                        public void run() {
                                                ob = (StartPageInterface) getActivity();
                                                JoinPageFragment f = new JoinPageFragment();
                                                f.setArguments(b);
                                                ob.changeFragmentFromStartPage(f, "name");
                                        }
                                    });
                                }else{
                                    if(getActivity()!=null)
                                        Toast.makeText(getActivity(), "An unexpeted error occurred..\nPlease try again..", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //If the connection couldn't be made..
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
                            return params;
                        }
                    };
                    requestQueue.add(stringRequest);
                } else if (start_leave_button.getText().equals("START")) {

                    //Exam is live..

                    //Check for write permissions..
                    boolean statusForWriteStorage = CheckForPermissions.checkForWriteStorage(getActivity());
                    if (statusForWriteStorage) {
                        //Check for camera permissions..
                        boolean statusForCamera = CheckForPermissions.checkForCamera(getActivity());
                        if(statusForCamera){
                            boolean statusForVibrate = CheckForPermissions.checkForVibrate(getActivity());
                            if(statusForVibrate){
                                //Delete table..
                                o.deleteMyTable();

                                //Clear all shared preferences..
                                SharedPreferences.Editor e=dataPrefs.edit();
                                e.clear();
                                e.apply();
                                SharedPreferences.Editor ee=quizPrefs.edit();
                                ee.clear();
                                ee.apply();
                                SharedPreferences.Editor eee=firstTime.edit();
                                eee.clear();
                                eee.apply();
                                SharedPreferences.Editor eeeee=firstTimeForRules.edit();
                                eeeee.clear();
                                eeeee.apply();

                                //Get current date to store as to when the user is attempting the quiz..
                                getDate();
                            }

                        }
                    }

                    //For answers..
                    Answers.getInstance().logCustom(new CustomEvent("Start button clicked")
                            .putCustomAttribute("userName", prefs.getString("userName", ""))
                            .putCustomAttribute("examId", examId));

                } else {

                    //Exam is over..
                    if(getActivity()!=null)
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CheckForPermissions.WRITE_STORAGE_CODE:
                //If permission is granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    boolean statusForCamera = CheckForPermissions.checkForCamera(getActivity());

                    if(statusForCamera){
                        //Check if a valid language has been chosen from the list..
                            //Else if chosen..
                            //Start Quiz

                        boolean statusForVibrate = CheckForPermissions.checkForVibrate(getActivity());

                        if(statusForVibrate) {

                            //Delete table..
                            o.deleteMyTable();

                            //Clear shared preferences..
                            SharedPreferences.Editor e=dataPrefs.edit();
                            e.clear();
                            e.apply();
                            SharedPreferences.Editor ee=quizPrefs.edit();
                            ee.clear();
                            ee.apply();
                            SharedPreferences.Editor eee=firstTime.edit();
                            eee.clear();
                            eee.apply();
                            SharedPreferences.Editor eeeee=firstTimeForRules.edit();
                            eeeee.clear();
                            eeeee.apply();

                            //Get current date to store as to when the user is attempting the quiz..
                            getDate();
                        }
                    }else{
                        //Displaying another toast if permission is not granted
                        if(getActivity()!=null)
                        Toast.makeText(getActivity(), "Oops you have denied the permission for camera\nGo to settings and grant them", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //Displaying another toast if permission is not granted
                    if(getActivity()!=null)
                    Toast.makeText(getActivity(), "Oops you have denied the permission for write to storage\nGo to settings and grant them", Toast.LENGTH_LONG).show();
                }
                break;
            case CheckForPermissions.CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean statusForVibrate = CheckForPermissions.checkForVibrate(getActivity());

                    if(statusForVibrate) {

                        //Delete table..
                        o.deleteMyTable();

                        //Clear shared preferences..
                        SharedPreferences.Editor e=dataPrefs.edit();
                        e.clear();
                        e.apply();
                        SharedPreferences.Editor ee=quizPrefs.edit();
                        ee.clear();
                        ee.apply();
                        SharedPreferences.Editor eee=firstTime.edit();
                        eee.clear();
                        eee.apply();
                        SharedPreferences.Editor eeeee=firstTimeForRules.edit();
                        eeeee.clear();
                        eeeee.apply();

                        //Get current date to store as to when the user is attempting the quiz..
                        getDate();
                    }

                } else {
                    //Displaying another toast if permission is not granted
                    if(getActivity()!=null)
                    Toast.makeText(getActivity(), "Oops you have denied the permission for camera\nGo to settings and grant them", Toast.LENGTH_LONG).show();
                }
            break;

            case CheckForPermissions.VIBRATE_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean statusForVibrate = CheckForPermissions.checkForVibrate(getActivity());

                    if(statusForVibrate){

                        //Delete table..
                        o.deleteMyTable();

                        //Clear shared preferences..
                        SharedPreferences.Editor e=dataPrefs.edit();
                        e.clear();
                        e.apply();
                        SharedPreferences.Editor ee=quizPrefs.edit();
                        ee.clear();
                        ee.apply();
                        SharedPreferences.Editor eee=firstTime.edit();
                        eee.clear();
                        eee.apply();
                        SharedPreferences.Editor eeeee=firstTimeForRules.edit();
                        eeeee.clear();
                        eeeee.apply();

                        //Get current date to store as to when the user is attempting the quiz..
                        getDate();
                    }
                } else {
                    //Displaying another toast if permission is not granted
                    if(getActivity()!=null)
                        Toast.makeText(getActivity(), "Oops you have denied the permission for triggering vibration\nGo to settings and grant them", Toast.LENGTH_LONG).show();
                }

                break;

        }
    }

    public void getDate(){

        //Show dialog..
        myDialog.show();

        //For https connection..
        ConstantsDefined.updateAndroidSecurityProvider(getActivity());
        ConstantsDefined.beforeVolleyConnect();

        //Make a request..
        requestQueue = Volley.newRequestQueue(getActivity());
        String url = ConstantsDefined.api + "getTimeStamp";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    String success=response.getString("success");
                    if(success.equals("true")){
                        //Parse response..
                        final String myResponse = response.getJSONObject("response").toString();
                        JSONObject jsonObject = new JSONObject(myResponse);
                        String timestamp = jsonObject.getString("timestamp");
                        myDate = MiscellaneousParser.parseTimestamp(timestamp);
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                //For getting s3 url..
                                afterResponse(myDate);
                            }
                        });
                    }else{
                        //Show error message...
                        if(getActivity()!=null)
                            Toast.makeText(getActivity(), "Something went wrong..\n" +
                                    "Please try again..", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(myDialog!=null)
                    myDialog.dismiss();
                if(ConstantsDefined.isOnline(getActivity())){
                    //Do nothing..
                    if(getActivity()!=null)
                    Toast.makeText(getActivity(), "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                }else{
                    if(getActivity()!=null)
                    Toast.makeText(getActivity(), "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void afterResponse(final String myDate){

        //For https connection..
        ConstantsDefined.updateAndroidSecurityProvider(getActivity());
        ConstantsDefined.beforeVolleyConnect();

        //Make a request..
        requestQueue = Volley.newRequestQueue(getActivity());
        String url = ConstantsDefined.api + "getS3Url";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    String success=response.getString("success");
                    if(success.equals("true")){
                        //If success.. store url..
                        myUrl=response.getString("response");

                        //For development..
                        //Otherwise comment this..
                        //myUrl="https://s3.ap-south-1.amazonaws.com/live-exams-local/";
                        Log.d("myDateeeee", "run: "+myUrl);
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                if(myDialog!=null)
                                    myDialog.dismiss();

                                //For starting new activity for showing rules before quiz..
                                afterConnect(myDate,myUrl);
                            }
                        });
                    }else{
                        //Show error message..
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

                //Dismiss dialog..
                if(myDialog!=null)
                    myDialog.dismiss();

                //Show error message..
                if(ConstantsDefined.isOnline(getActivity())){
                    if(getActivity()!=null)
                    Toast.makeText(getActivity(), "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                }else{
                    if(getActivity()!=null)
                    Toast.makeText(getActivity(), "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void afterConnect(String myDate,String myUrl){

        //Start new activity..
        Intent i = new Intent(getActivity(), RulesBeforeQuiz.class);
        i.putExtra("examId", examId);
        i.putExtra("name", name);
        i.putExtra("language", selectedLanguage);
        i.putExtra("date",myDate);
        i.putExtra("url",myUrl);
        startActivity(i);
    }



}

interface StartPageInterface {
    void changeFragmentFromStartPage(Fragment f, String title);

    void changeTitleForStartPage();
}
