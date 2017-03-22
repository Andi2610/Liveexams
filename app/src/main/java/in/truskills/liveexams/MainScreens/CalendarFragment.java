package in.truskills.liveexams.MainScreens;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.stacktips.view.CalendarListener;
import com.stacktips.view.CustomCalendarView;
import com.stacktips.view.DayDecorator;
import com.stacktips.view.DayView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.Miscellaneous.ConnectivityReciever;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.Miscellaneous.MyApplication;
import in.truskills.liveexams.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment implements ConnectivityReciever.ConnectivityReceiverListener {

    LinearLayoutManager linearLayoutManager;
    CalendarListAdapter calendarListAdapter;
    List<Values> valuesList;
    Values values;
    RecyclerView myExamsList;
    String myStartDate, myDateOfStart, myEndDate, myDateOfEnd, myDuration, myDurationTime, myStartTime, myEndTime, myJoinedExamssss = "", timestampppp = "";
    SimpleDateFormat simpleDateFormat;
    int curr_day, curr_month, curr_year, cm;
    SharedPreferences prefs;
    RequestQueue requestQueue;
    HashMap<String, ArrayList<String>> mapper;
    int i;
    Date myCurrDate;
    Calendar currentCalendar;
    Handler h;
    CustomCalendarView calendarView;
    Button retryButton;
    LinearLayout noConnectionLayout;
    TextView noConnectionText;
    ProgressDialog dialog;
    AVLoadingIndicatorView avLoadingIndicatorView;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        requestQueue = Volley.newRequestQueue(getActivity());
        h = new Handler();
//        avLoadingIndicatorView=(AVLoadingIndicatorView)getActivity().findViewById(R.id.aviForCalendar);
//        avLoadingIndicatorView.setVisibility(View.GONE);

        retryButton = (Button) getActivity().findViewById(R.id.retryButtonForCalendar);
        noConnectionLayout = (LinearLayout) getActivity().findViewById(R.id.noConnectionLayoutForCalendar);
        noConnectionText = (TextView) getActivity().findViewById(R.id.noConnectionTextForCalendar);
        Typeface tff1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Regular.ttf");
        retryButton.setTypeface(tff1);
        noConnectionText.setTypeface(tff1);
        noConnectionLayout.setVisibility(View.GONE);
        Answers.getInstance().logCustom(new CustomEvent("Calendar page inspect")
                .putCustomAttribute("userName", prefs.getString("userName", "")));

        myExamsList = (RecyclerView) getActivity().findViewById(R.id.myExamsListForCalendar);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        myExamsList.setLayoutManager(linearLayoutManager);
        myExamsList.setItemAnimator(new DefaultItemAnimator());
        calendarView = (CustomCalendarView) getActivity().findViewById(R.id.calendarView);

        calendarView.setVisibility(View.GONE);

        populateFirstTime();

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateFirstTime();
            }
        });
    }

    private class DisabledColorDecorator implements DayDecorator {
        @Override
        public void decorate(DayView dayView) {
//            if (CalendarUtils.isPastDay(dayView.getDate())) {
//                int color = Color.parseColor("#FFFFFF");
//                dayView.setTextColor(color);
//            }
            simpleDateFormat = new SimpleDateFormat("dd");
            int myD = Integer.parseInt(simpleDateFormat.format(dayView.getDate()));
            simpleDateFormat = new SimpleDateFormat("MM");
            int myM = Integer.parseInt(simpleDateFormat.format(dayView.getDate()));
            simpleDateFormat = new SimpleDateFormat("yyyy");
            int myY = Integer.parseInt(simpleDateFormat.format(dayView.getDate()));
            if (myD == curr_day && myM == curr_month && myY == curr_year)
                dayView.setBackgroundColor(Color.parseColor("#E0E0E0"));
        }
    }

    private class SetExamsColorDecorator implements DayDecorator {
        @Override
        public void decorate(DayView dayView) {
            Date date = dayView.getDate();
            try {
                getDurationOfEachExam(date, dayView);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void populateListForCalendar(final int d, final int m, final int y) {

//        avLoadingIndicatorView.show();

        valuesList = new ArrayList<>();
        ConstantsDefined.updateAndroidSecurityProvider(getActivity());
        ConstantsDefined.beforeVolleyConnect();
        String url = ConstantsDefined.api + "joinedExams/" + prefs.getString("userId", "");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
//                avLoadingIndicatorView.hide();
                try {
                    String myResponse = response.getJSONObject("response").toString();
                    JSONObject jsonObject = new JSONObject(myResponse);
                    String timestamp = jsonObject.getString("timestamp");
                    String myJoinedExams = jsonObject.getJSONArray("joinedExams").toString();
                    HashMap<String, ArrayList<String>> mapper = MiscellaneousParser.myExamsParser(myJoinedExams);
                    JSONArray arr = new JSONArray(myJoinedExams);
                    int length = arr.length();

                    valuesList=new ArrayList<>();
                    for (int i = 0; i < length; ++i) {

                        //If user is still enrolled to this exam..
                        if (mapper.get("leftExam").get(i).equals("false")) {

                            myStartDate = mapper.get("StartDate").get(i);
                            myEndDate = mapper.get("EndDate").get(i);
                            myDuration = mapper.get("ExamDuration").get(i);
                            myStartTime = mapper.get("StartTime").get(i);
                            myEndTime = mapper.get("EndTime").get(i);

                            myDateOfStart = MiscellaneousParser.parseDate(myStartDate);
                            myDateOfEnd = MiscellaneousParser.parseDate(myEndDate);
                            myDurationTime = MiscellaneousParser.parseDuration(myDuration);

                            String myTimeOfStart = MiscellaneousParser.parseTimeForDetails(myStartTime);
                            String myTimeOfEnd = MiscellaneousParser.parseTimeForDetails(myEndTime);
                            Log.d("myTimestamp=", timestamp);
//                            String myTimestamp=MiscellaneousParser.parseTimestamp(timestamp);
                            String myParsedDate = d + "/" + m + "/" + y;
                            String myTime = MiscellaneousParser.parseTimestampForTime(timestamp);

//                            Log.d("myTimestamp=",myTimestamp);

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date start_date = simpleDateFormat.parse(myDateOfStart);
                            Date end_date = simpleDateFormat.parse(myDateOfEnd);
                            Date middle_date = simpleDateFormat.parse(myParsedDate);

                            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("h-mm a");
                            Date start_time = simpleDateFormat2.parse(myTimeOfStart);
                            Date end_time = simpleDateFormat2.parse(myTimeOfEnd);
                            Date middle_time = simpleDateFormat2.parse(myTime);

                            if (!(middle_date.before(start_date) || middle_date.after(end_date))) {
                                if (middle_date.equals(end_date)) {
                                    if (middle_date.equals(myCurrDate)) {
                                        if (!middle_time.after(end_time)) {
                                            values = new Values(mapper.get("ExamName").get(i), myDateOfStart, myDateOfEnd, myDurationTime, mapper.get("ExamId").get(i));
                                            valuesList.add(values);
                                        }
                                    } else {
                                        values = new Values(mapper.get("ExamName").get(i), myDateOfStart, myDateOfEnd, myDurationTime, mapper.get("ExamId").get(i));
                                        valuesList.add(values);
                                    }
                                } else {
                                    values = new Values(mapper.get("ExamName").get(i), myDateOfStart, myDateOfEnd, myDurationTime, mapper.get("ExamId").get(i));
                                    valuesList.add(values);
                                }
                            }
                        }
                    }
                    calendarListAdapter = new CalendarListAdapter(valuesList, getActivity());
                    myExamsList.setAdapter(calendarListAdapter);
                    calendarListAdapter.notifyDataSetChanged();
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                avLoadingIndicatorView.hide();
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

    private void getDurationOfEachExam(Date date, DayView dayView) throws JSONException, ParseException {

        Log.d("joinedExamssss", myJoinedExamssss + "" + timestampppp);
        JSONArray arr = new JSONArray(myJoinedExamssss);
        HashMap<String, ArrayList<String>> mapper = MiscellaneousParser.myExamsParser(myJoinedExamssss);
        int length = arr.length();
        for (int i = 0; i < length; ++i) {

            //If user is still enrolled to this exam..
            if (mapper.get("leftExam").get(i).equals("false")) {

                myStartDate = mapper.get("StartDate").get(i);
                myEndDate = mapper.get("EndDate").get(i);
                myDuration = mapper.get("ExamDuration").get(i);
                myStartTime = mapper.get("StartTime").get(i);
                myEndTime = mapper.get("EndTime").get(i);

                myDateOfStart = MiscellaneousParser.parseDate(myStartDate);
                myDateOfEnd = MiscellaneousParser.parseDate(myEndDate);
                myDurationTime = MiscellaneousParser.parseDuration(myDuration);

                String myTimeOfStart = MiscellaneousParser.parseTimeForDetails(myStartTime);
                String myTimeOfEnd = MiscellaneousParser.parseTimeForDetails(myEndTime);

                String myTime = MiscellaneousParser.parseTimestampForTime(timestampppp);

                String strDate = date.toString();
                simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                Date date2 = simpleDateFormat.parse(strDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date2);
                int day2 = calendar.get(Calendar.DAY_OF_MONTH);
                int year2 = calendar.get(Calendar.YEAR);
                int month2 = calendar.get(Calendar.MONTH);
                month2++;
                String myParsedDate = day2 + "/" + month2 + "/" + year2;

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date start_date = simpleDateFormat.parse(myDateOfStart);
                Date end_date = simpleDateFormat.parse(myDateOfEnd);
                Date middle_date = simpleDateFormat.parse(myParsedDate);

                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("h-mm a");
                Date start_time = simpleDateFormat2.parse(myTimeOfStart);
                Date end_time = simpleDateFormat2.parse(myTimeOfEnd);
                Date middle_time = simpleDateFormat2.parse(myTime);

                if (!(middle_date.before(start_date) || middle_date.after(end_date))) {
                    if (middle_date.equals(end_date)) {
                        if (middle_date.equals(myCurrDate)) {
                            if (!middle_time.after(end_time)) {
                                dayView.setTextColor(Color.parseColor("#00B4A8"));
                            }
                        } else {
                            dayView.setTextColor(Color.parseColor("#00B4A8"));
                        }
                    } else {
                        dayView.setTextColor(Color.parseColor("#00B4A8"));
                    }
                }
            }
        }

    }

    public void afterResponse() throws ParseException {
        //Initialize calendar with date
        String myTimestamp = MiscellaneousParser.parseTimestamp(timestampppp);
        SimpleDateFormat simpleDateFormattt = new SimpleDateFormat("dd/MM/yyyy");
        myCurrDate = simpleDateFormattt.parse(myTimestamp);
        String parts[] = myTimestamp.split("/");
        curr_day = Integer.parseInt(parts[0]);
        curr_month = Integer.parseInt(parts[1]);
        curr_year = Integer.parseInt(parts[2]);
        currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(myCurrDate);

        populateListForCalendar(curr_day, curr_month, curr_year);

        calendarView.setFirstDayOfWeek(Calendar.MONDAY);

        //Show/hide overflow days of a month
        calendarView.setShowOverflowDate(false);

        //call refreshCalendar to update calendar the view
        calendarView.refreshCalendar(currentCalendar);

        //Handling custom calendar events
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                simpleDateFormat = new SimpleDateFormat("dd");
                int dd = Integer.parseInt(simpleDateFormat.format(date));
                simpleDateFormat = new SimpleDateFormat("MM");
                int mm = Integer.parseInt(simpleDateFormat.format(date));
                simpleDateFormat = new SimpleDateFormat("yyyy");
                int yy = Integer.parseInt(simpleDateFormat.format(date));
                populateListForCalendar(dd, mm, yy);
            }

            @Override
            public void onMonthChanged(Date date) {
                simpleDateFormat = new SimpleDateFormat("dd");
                int dd = Integer.parseInt(simpleDateFormat.format(date));
                simpleDateFormat = new SimpleDateFormat("MM");
                int mm = Integer.parseInt(simpleDateFormat.format(date));
                simpleDateFormat = new SimpleDateFormat("yyyy");
                int yy = Integer.parseInt(simpleDateFormat.format(date));
                if (curr_day == dd && curr_month == mm && curr_year == yy) {
                    populateListForCalendar(dd, mm, yy);
                } else {
                    valuesList = new ArrayList<>();
                    calendarListAdapter = new CalendarListAdapter(valuesList, getActivity());
                    myExamsList.setAdapter(calendarListAdapter);
                    calendarListAdapter.notifyDataSetChanged();
                }
            }
        });

        //Setting custom font
        final Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Regular.ttf");
        if (null != typeface) {
            calendarView.setCustomTypeface(typeface);
            calendarView.refreshCalendar(currentCalendar);
        }

        //adding calendar day decorators
        List decorators = new ArrayList<>();
        decorators.add(new DisabledColorDecorator());
        decorators.add(new SetExamsColorDecorator());
        calendarView.setDecorators(decorators);
        calendarView.refreshCalendar(currentCalendar);
    }

    public void populateFirstTime() {

        if(getActivity()!=null){
            dialog = new ProgressDialog(getActivity());
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        ConstantsDefined.updateAndroidSecurityProvider(getActivity());
        ConstantsDefined.beforeVolleyConnect();

        String url = ConstantsDefined.api + "joinedExams/" + prefs.getString("userId", "");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                noConnectionLayout.setVisibility(View.GONE);
                calendarView.setVisibility(View.VISIBLE);
                if(dialog!=null)
                dialog.dismiss();
                try {
                    String myResponse = response.getJSONObject("response").toString();
                    final JSONObject jsonObject = new JSONObject(myResponse);
                    timestampppp = jsonObject.getString("timestamp");
                    myJoinedExamssss = jsonObject.getJSONArray("joinedExams").toString();
                    afterResponse();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                noConnectionLayout.setVisibility(View.VISIBLE);
                if(dialog!=null)
                dialog.dismiss();
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

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            if (valuesList.isEmpty()) {
                populateFirstTime();
            }
        }
    }
}