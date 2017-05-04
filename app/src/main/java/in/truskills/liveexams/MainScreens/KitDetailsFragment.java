package in.truskills.liveexams.MainScreens;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class KitDetailsFragment extends Fragment {


    String response,description,startDate,endDate,myDateOfStart,myDateOfEnd,price;
    ArrayList<String> examsPaidName=new ArrayList<>();
    ArrayList<String> examsFreeName=new ArrayList<>();
    ArrayList<String> examsPaidId=new ArrayList<>();
    ArrayList<String> examsFreeId=new ArrayList<>();
    ArrayList<String> coursesName=new ArrayList<>();
    ArrayList<String> coursesId=new ArrayList<>();
    Bundle b=new Bundle();

    TextView startDateText,endDateText,startDateValue,endDateValue,descriptionText,descriptionValue,priceText,priceValue;

    public KitDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kit_details, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        b=getArguments();
        response=b.getString("response");

        Log.d("responseInFragment", "onActivityCreated: "+response);

        startDateText=(TextView) getActivity().findViewById(R.id.startDateText);
        endDateText=(TextView) getActivity().findViewById(R.id.endDateText);
        startDateValue=(TextView) getActivity().findViewById(R.id.startDateValue);
        endDateValue=(TextView) getActivity().findViewById(R.id.endDateValue);
        descriptionText=(TextView) getActivity().findViewById(R.id.descriptionText);
        descriptionValue=(TextView) getActivity().findViewById(R.id.descriptionValue);
        priceText=(TextView) getActivity().findViewById(R.id.priceText);
        priceValue=(TextView) getActivity().findViewById(R.id.priceValue);

        Typeface tff1 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Regular.ttf");
        startDateText.setTypeface(tff1);
        endDateText.setTypeface(tff1);
        startDateValue.setTypeface(tff1);
        endDateValue.setTypeface(tff1);
        descriptionText.setTypeface(tff1);
        descriptionValue.setTypeface(tff1);
        priceText.setTypeface(tff1);
        priceValue.setTypeface(tff1);

        try {
            HashMap<String,String> map= MiscellaneousParser.getDetailsOfOneKit(response);
            price=map.get("price");
            description=map.get("description");
            startDate=map.get("startDate");
            endDate=map.get("endDate");
            myDateOfStart=MiscellaneousParser.parseDateForKit(startDate);
            myDateOfEnd=MiscellaneousParser.parseDateForKit(endDate);

            HashMap<String,ArrayList<String>> mapper=MiscellaneousParser.getExamsAndCoursesOfOneKit(response);
            examsPaidId=mapper.get("examsPaidId");
            examsPaidName=mapper.get("examsPaidName");
            examsFreeId=mapper.get("examsFreeId");
            examsFreeName=mapper.get("examsFreeName");
            coursesName=mapper.get("coursesName");
            coursesId=mapper.get("coursesId");

            startDateValue.setText(myDateOfStart);
            endDateValue.setText(myDateOfEnd);
            descriptionValue.setText(description);
            priceValue.setText(price);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
