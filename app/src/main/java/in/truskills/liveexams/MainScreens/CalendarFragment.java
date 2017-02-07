package in.truskills.liveexams.MainScreens;


import android.content.Context;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    String joinedExams,myStartDate,myDateOfStart,myEndDate,myDateOfEnd,myDuration,myDurationTime;
    String [] parts;
    SimpleDateFormat simpleDateFormat;
    Calendar calendar;
    Date date;
    int start_day,start_month,start_year,hour,minute,end_day,end_month,end_year;
    SharedPreferences prefs;
    HashMap<String, ArrayList<String>> mapper;
    int i;

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

//        getActivity().getActionBar().setTitle("CALENDAR");

        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        joinedExams = prefs.getString("joinedExams", "noJoinedExams");

        myExamsList = (RecyclerView) getActivity().findViewById(R.id.myExamsListForCalendar);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        valuesList = new ArrayList<>();
        calendarView = (CalendarView) getActivity().findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                month++;

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
                            }

                            if (start_year <= year && year <= end_year) {
                                if (start_month <= month && month <= end_month) {
                                    if (start_day <= dayOfMonth && dayOfMonth <= end_day) {
                                        values = new Values(mapper.get("ExamName").get(i), myDateOfStart, myDateOfEnd, myDurationTime, mapper.get("ExamId").get(i));
                                        valuesList.add(values);
                                        myExamsListAdapter = new MyExamsListAdapter(valuesList, getActivity());
                                        myExamsList.setAdapter(myExamsListAdapter);
                                        myExamsList.setLayoutManager(linearLayoutManager);
                                        myExamsList.setItemAnimator(new DefaultItemAnimator());
                                        myExamsListAdapter.notifyDataSetChanged();
                                    }else{
                                        valuesList.clear();
                                        myExamsListAdapter = new MyExamsListAdapter(valuesList, getActivity());
                                        myExamsList.setAdapter(myExamsListAdapter);
                                        myExamsList.setLayoutManager(linearLayoutManager);
                                        myExamsList.setItemAnimator(new DefaultItemAnimator());
                                        myExamsListAdapter.notifyDataSetChanged();
                                    }
                                }else{
                                    valuesList.clear();
                                    myExamsListAdapter = new MyExamsListAdapter(valuesList, getActivity());
                                    myExamsList.setAdapter(myExamsListAdapter);
                                    myExamsList.setLayoutManager(linearLayoutManager);
                                    myExamsList.setItemAnimator(new DefaultItemAnimator());
                                    myExamsListAdapter.notifyDataSetChanged();
                                }
                            }else{
                                valuesList.clear();
                                myExamsListAdapter = new MyExamsListAdapter(valuesList, getActivity());
                                myExamsList.setAdapter(myExamsListAdapter);
                                myExamsList.setLayoutManager(linearLayoutManager);
                                myExamsList.setItemAnimator(new DefaultItemAnimator());
                                myExamsListAdapter.notifyDataSetChanged();
                            }
                        }

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        }
    }
