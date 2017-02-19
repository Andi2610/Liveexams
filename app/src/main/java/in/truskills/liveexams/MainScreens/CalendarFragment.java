package in.truskills.liveexams.MainScreens;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.stacktips.view.CalendarListener;
import com.stacktips.view.CustomCalendarView;
import com.stacktips.view.DayDecorator;
import com.stacktips.view.DayView;
import com.stacktips.view.utils.CalendarUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {


    LinearLayoutManager linearLayoutManager;
    MyExamsListAdapter myExamsListAdapter;
    List<Values> valuesList;
    Values values;
    RecyclerView myExamsList;
    CalendarView calendarView;
    String joinedExams, myStartDate, myDateOfStart, myEndDate, myDateOfEnd, myDuration, myDurationTime;
    String[] parts;
    SimpleDateFormat simpleDateFormat;
    Calendar calendar;
    Date date;
    int start_day, start_month, start_year, hour, minute, end_day, end_month, end_year;
    SharedPreferences prefs;
    HashMap<String, ArrayList<String>> mapper;
    int i;
    Calendar currentCalendar;

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
        joinedExams = prefs.getString("joinedExams", "noJoinedExams");

        Answers.getInstance().logCustom(new CustomEvent("Calendar page inspect")
                .putCustomAttribute("userName", prefs.getString("userName", "")));

        myExamsList = (RecyclerView) getActivity().findViewById(R.id.myExamsListForCalendar);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        myExamsList.setLayoutManager(linearLayoutManager);
        myExamsList.setItemAnimator(new DefaultItemAnimator());
        CustomCalendarView calendarView = (CustomCalendarView) getActivity().findViewById(R.id.calendarView);

        //Initialize calendar with date
        currentCalendar = Calendar.getInstance(Locale.getDefault());

        //Show Monday as first date of week
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);

        //Show/hide overflow days of a month
        calendarView.setShowOverflowDate(false);

        //call refreshCalendar to update calendar the view
        calendarView.refreshCalendar(currentCalendar);

        //Handling custom calendar events
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                SimpleDateFormat myDay = new SimpleDateFormat("dd");
                SimpleDateFormat myMonth = new SimpleDateFormat("MM");
                SimpleDateFormat myYear = new SimpleDateFormat("yyyy");
                int d = Integer.parseInt(myDay.format(date));
                int m = Integer.parseInt(myMonth.format(date));
                int y = Integer.parseInt(myYear.format(date));
                populateListForCalendar(d, m, y);
            }

            @Override
            public void onMonthChanged(Date date) {
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

    private class DisabledColorDecorator implements DayDecorator {
        @Override
        public void decorate(DayView dayView) {
            if (CalendarUtils.isPastDay(dayView.getDate())) {
                int color = Color.parseColor("#FFFFFF");
                dayView.setTextColor(color);
            }
        }
    }

    private class SetExamsColorDecorator implements DayDecorator {
        @Override
        public void decorate(DayView dayView) {
            Date date=dayView.getDate();
            SimpleDateFormat myDay = new SimpleDateFormat("dd");
            SimpleDateFormat myMonth = new SimpleDateFormat("MM");
            SimpleDateFormat myYear = new SimpleDateFormat("yyyy");
            int d = Integer.parseInt(myDay.format(date));
            int m = Integer.parseInt(myMonth.format(date));
            int y = Integer.parseInt(myYear.format(date));
            getDurationOfEachExam(d,m,y,dayView);
        }
    }

    private void populateListForCalendar(int day, int month, int year) {
        valuesList = new ArrayList<>();
        if (!joinedExams.equals("noJoinedExams")) {
            try {
                mapper = VariablesDefined.myExamsParser(joinedExams);
                JSONArray arr = new JSONArray(joinedExams);
                int length = arr.length();
                for (i = 0; i < length; ++i) {
                    if (mapper.get("leftExam").get(i).equals("false")) {

                        myStartDate = mapper.get("StartDate").get(i);
                        myEndDate = mapper.get("EndDate").get(i);

                        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                        date = simpleDateFormat.parse(myStartDate);
                        calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        start_day = calendar.get(Calendar.DAY_OF_MONTH);
                        start_year = calendar.get(Calendar.YEAR);
                        start_month = calendar.get(Calendar.MONTH);
                        start_month++;
                        myDateOfStart = start_day + "/" + start_month + "/" + start_year;

                        date = simpleDateFormat.parse(myEndDate);
                        calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        end_day = calendar.get(Calendar.DAY_OF_MONTH);
                        end_year = calendar.get(Calendar.YEAR);
                        end_month = calendar.get(Calendar.MONTH);
                        end_month++;
                        myDateOfEnd = end_day + "/" + end_month + "/" + end_year;

                        myDuration = mapper.get("ExamDuration").get(i);
                        parts = myDuration.split("-");
                        hour = Integer.parseInt(parts[0]);
                        minute = Integer.parseInt(parts[1]);

                        if (minute == 0) {
                            myDurationTime = hour + " hours";
                        } else {
                            myDurationTime = hour + " hours " + minute + " minutes";
                        }

//                                Log.d("Calendar", start_year + "<=" + year + "<=" + end_year);
//                                Log.d("Calendar", start_month + "<=" + month + "<=" + end_month);
//                                Log.d("Calendar", start_day + "<=" + day + "<=" + end_day + " " + mapper.get("ExamName").get(i));

                        if (start_year <= year && year <= end_year) {
                            if (start_month <= month && month <= end_month) {
                                if (start_day <= day && day <= end_day) {
                                    values = new Values(mapper.get("ExamName").get(i), myDateOfStart, myDateOfEnd, myDurationTime, mapper.get("ExamId").get(i));
                                    valuesList.add(values);
                                    myExamsListAdapter = new MyExamsListAdapter(valuesList, getActivity());
                                    myExamsList.setAdapter(myExamsListAdapter);
                                    myExamsListAdapter.notifyDataSetChanged();
                                    Log.d("joinedExams", mapper.get("ExamName").get(i) + " " + valuesList.size());
                                } else {
                                    if (valuesList.isEmpty()) {
                                        valuesList.clear();
                                        myExamsListAdapter = new MyExamsListAdapter(valuesList, getActivity());
                                        myExamsList.setAdapter(myExamsListAdapter);
                                        myExamsListAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                if (valuesList.isEmpty()) {
                                    valuesList.clear();
                                    myExamsListAdapter = new MyExamsListAdapter(valuesList, getActivity());
                                    myExamsList.setAdapter(myExamsListAdapter);
                                    myExamsListAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            if (valuesList.isEmpty()) {
                                valuesList.clear();
                                myExamsListAdapter = new MyExamsListAdapter(valuesList, getActivity());
                                myExamsList.setAdapter(myExamsListAdapter);
                                myExamsListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void getDurationOfEachExam(int d,int m,int y,DayView dayView) {
        if (!joinedExams.equals("noJoinedExams")) {
            try {
                mapper = VariablesDefined.myExamsParser(joinedExams);
                JSONArray arr = new JSONArray(joinedExams);
                int length = arr.length();
                for (i = 0; i < length; ++i) {
                    if (mapper.get("leftExam").get(i).equals("false")) {
                        myStartDate = mapper.get("StartDate").get(i);
                        myEndDate = mapper.get("EndDate").get(i);

                        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                        date = simpleDateFormat.parse(myStartDate);
                        calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        start_day = calendar.get(Calendar.DAY_OF_MONTH);
                        start_year = calendar.get(Calendar.YEAR);
                        start_month = calendar.get(Calendar.MONTH);
                        start_month++;

                        date = simpleDateFormat.parse(myEndDate);
                        calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        end_day = calendar.get(Calendar.DAY_OF_MONTH);
                        end_year = calendar.get(Calendar.YEAR);
                        end_month = calendar.get(Calendar.MONTH);
                        end_month++;


                        Log.d("myDate=",d+"*"+start_day+" "+m+"*"+start_month+" "+y+"*"+start_year);
                        if(d==start_day&&m==start_month&&y==start_year){
                            int start=d;
                            while(start<=end_day){
                                dayView.setTextColor(Color.parseColor("#f44336"));
                                ++start;
                            }
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }
}
