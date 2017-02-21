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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    String joinedExams, myStartDate, myDateOfStart, myEndDate, myDateOfEnd, myDuration, myDurationTime;
    SimpleDateFormat simpleDateFormat;
    int start_day, start_month, start_year, end_day, end_month, end_year,curr_day,curr_month,curr_year,cm;
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
        curr_day=currentCalendar.get(Calendar.DAY_OF_MONTH);
        cm=currentCalendar.get(Calendar.MONTH);
        curr_year=currentCalendar.get(Calendar.YEAR);

        curr_month=cm+1;
        populateListForCalendar(curr_day,curr_month,curr_year);

//        Log.d("current",currentCalendar.get(Calendar.DAY_OF_MONTH)+"");


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
                simpleDateFormat=new SimpleDateFormat("dd");
                int dd=Integer.parseInt(simpleDateFormat.format(date));
                simpleDateFormat=new SimpleDateFormat("MM");
                int mm=Integer.parseInt(simpleDateFormat.format(date));
                simpleDateFormat=new SimpleDateFormat("yyyy");
                int yy=Integer.parseInt(simpleDateFormat.format(date));
                populateListForCalendar(dd,mm,yy);
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
//            if (CalendarUtils.isPastDay(dayView.getDate())) {
//                int color = Color.parseColor("#FFFFFF");
//                dayView.setTextColor(color);
//            }
            simpleDateFormat=new SimpleDateFormat("dd");
            int myD=Integer.parseInt(simpleDateFormat.format(dayView.getDate()));
            simpleDateFormat=new SimpleDateFormat("MM");
            int myM=Integer.parseInt(simpleDateFormat.format(dayView.getDate()));
            simpleDateFormat=new SimpleDateFormat("yyyy");
            int myY=Integer.parseInt(simpleDateFormat.format(dayView.getDate()));
            if(myD==curr_day&&myM==curr_month&&myY==curr_year)
                dayView.setBackgroundColor(Color.parseColor("#E0E0E0"));
        }
    }

    private class SetExamsColorDecorator implements DayDecorator {
        @Override
        public void decorate(DayView dayView) {
            Date date=dayView.getDate();
            getDurationOfEachExam(date,dayView);
        }
    }

    private void populateListForCalendar(int d,int m,int y) {
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
                        myDuration = mapper.get("ExamDuration").get(i);

                        myDateOfStart=VariablesDefined.parseDate(myStartDate);
                        myDateOfEnd=VariablesDefined.parseDate(myEndDate);
                        myDurationTime=VariablesDefined.parseDuration(myDuration);

                        String array[]=myDateOfStart.split("/");
                        start_day=Integer.parseInt(array[0]);
                        start_month=Integer.parseInt(array[1]);
                        start_year=Integer.parseInt(array[2]);

                        array=myDateOfEnd.split("/");
                        end_day=Integer.parseInt(array[0]);
                        end_month=Integer.parseInt(array[1]);
                        end_year=Integer.parseInt(array[2]);

                        String myParsedDate = d + "/" + m + "/" + y;

                        simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
                        Date start_date=simpleDateFormat.parse(myDateOfStart);
                        Date end_date=simpleDateFormat.parse(myDateOfEnd);
                        Date middle_date=simpleDateFormat.parse(myParsedDate);

                        if(!(middle_date.before(start_date) || middle_date.after(end_date))){
                            Log.d("dates","inBetween");
                            values = new Values(mapper.get("ExamName").get(i), myDateOfStart, myDateOfEnd, myDurationTime, mapper.get("ExamId").get(i));
                            valuesList.add(values);
                            myExamsListAdapter = new MyExamsListAdapter(valuesList, getActivity());
                            myExamsList.setAdapter(myExamsListAdapter);
                            myExamsListAdapter.notifyDataSetChanged();
                        }else{
                            Log.d("dates","NotBetween");
                            if (valuesList.isEmpty()) {
                                valuesList.clear();
                                myExamsListAdapter = new MyExamsListAdapter(valuesList, getActivity());
                                myExamsList.setAdapter(myExamsListAdapter);
                                myExamsListAdapter.notifyDataSetChanged();
                            }
                        }

                        Log.d("dates",myDateOfStart+" ** "+myParsedDate+" ** "+myDateOfEnd);
                    }
                }

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void getDurationOfEachExam(Date date,DayView dayView) {
        if (!joinedExams.equals("noJoinedExams")) {
            try {
                mapper = VariablesDefined.myExamsParser(joinedExams);
                JSONArray arr = new JSONArray(joinedExams);
                int length = arr.length();
                ArrayList<Integer> list=new ArrayList<>();
                for (i = 0; i < length; ++i) {
                    if (mapper.get("leftExam").get(i).equals("false")) {
                        myStartDate = mapper.get("StartDate").get(i);
                        myEndDate = mapper.get("EndDate").get(i);

                        myDateOfStart=VariablesDefined.parseDate(myStartDate);
                        myDateOfEnd=VariablesDefined.parseDate(myEndDate);

                       String array[]=myDateOfStart.split("/");
                        start_day=Integer.parseInt(array[0]);
                        start_month=Integer.parseInt(array[1]);
                        start_year=Integer.parseInt(array[2]);

                        array=myDateOfEnd.split("/");
                        end_day=Integer.parseInt(array[0]);
                        end_month=Integer.parseInt(array[1]);
                        end_year=Integer.parseInt(array[2]);

                        String strDate=date.toString();
                        simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                        Date date2 = simpleDateFormat.parse(strDate);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date2);
                        int day2 = calendar.get(Calendar.DAY_OF_MONTH);
                        int year2 = calendar.get(Calendar.YEAR);
                        int month2 = calendar.get(Calendar.MONTH);
                        month2++;
                        String myParsedDate = day2 + "/" + month2 + "/" + year2;

                        simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
                        Date start_date=simpleDateFormat.parse(myDateOfStart);
                        Date end_date=simpleDateFormat.parse(myDateOfEnd);
                        Date middle_date=simpleDateFormat.parse(myParsedDate);

                        if(!(middle_date.before(start_date) || middle_date.after(end_date))){
                            Log.d("dates","inBetween");
                            dayView.setTextColor(Color.parseColor("#00B4A8"));
                            list.add(1);
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