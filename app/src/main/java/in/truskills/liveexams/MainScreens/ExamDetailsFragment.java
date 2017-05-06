package in.truskills.liveexams.MainScreens;


import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.ParticularExam.CustomSpinnerForDetailsAdapter;
import in.truskills.liveexams.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExamDetailsFragment extends Fragment {


    //Declare variables..
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

    public ExamDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exam_details, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        start_DateJoinPage = (TextView) getActivity().findViewById(R.id.startDateDetailsPage);
        end_DateJoinPage = (TextView) getActivity().findViewById(R.id.endDateDetailsPage);
        start_TimeJoinPage = (TextView) getActivity().findViewById(R.id.startTimeDetailsPage);
        end_TimeJoinPage = (TextView) getActivity().findViewById(R.id.endTimeDetailsPage);
        sponsorTextJoinPage = (TextView) getActivity().findViewById(R.id.sponsorTextDetailsPage);
        descriptionJoinPage = (TextView) getActivity().findViewById(R.id.descriptionDetailsPage);
        myLanguageJoinPage = (Spinner) getActivity().findViewById(R.id.myLanguageDetailsPage);

        viewFlipperJoinPage = (ViewFlipper) getActivity().findViewById(R.id.viewFlipperDetailsPage);

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

        //Get arguments..
        b = getArguments();
        examDetails = b.getString("examDetails");

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

        ArrayList<String> listOfLanguages = new ArrayList<>();
        try {
            listOfLanguages = MiscellaneousParser.getLanguagesPerExam(Languages);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        customSpinnerForDetailsAdapter = new CustomSpinnerForDetailsAdapter(getActivity(), listOfLanguages);
        myLanguageJoinPage.setAdapter(customSpinnerForDetailsAdapter);
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
}
