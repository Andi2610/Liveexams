package in.truskills.liveexams.ParticularExam;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.truskills.liveexams.R;

//Fragment for the rules of a particular exam..
public class RulesFragment extends Fragment {


    TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8,tv9,tv10;

    public RulesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rules, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv1 = (TextView) getActivity().findViewById(R.id.tv1);
        tv2 = (TextView) getActivity().findViewById(R.id.tv2);
        tv3 = (TextView) getActivity().findViewById(R.id.tv3);
        tv4 = (TextView) getActivity().findViewById(R.id.tv4);
        tv5 = (TextView) getActivity().findViewById(R.id.tv5);
        tv6 = (TextView) getActivity().findViewById(R.id.tv6);
        tv7 = (TextView) getActivity().findViewById(R.id.tv7);
        tv8 = (TextView) getActivity().findViewById(R.id.tv8);
        tv9 = (TextView) getActivity().findViewById(R.id.tv9);
        tv10 = (TextView) getActivity().findViewById(R.id.tv10);

        Typeface tff = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Regular.ttf");
        tv1.setTypeface(tff);
        tv2.setTypeface(tff);
        tv3.setTypeface(tff);
        tv4.setTypeface(tff);
        tv5.setTypeface(tff);
        tv6.setTypeface(tff);
        tv7.setTypeface(tff);
        tv8.setTypeface(tff);
        tv9.setTypeface(tff);
        tv10.setTypeface(tff);

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

}
