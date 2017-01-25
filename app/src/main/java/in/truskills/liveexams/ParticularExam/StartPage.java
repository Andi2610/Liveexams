package in.truskills.liveexams.ParticularExam;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import in.truskills.liveexams.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartPage extends Fragment {

    StartPageInterface ob;
    TextView startDetails,endDetails;
    Spinner myLanguage;
    String selectedLanguage;
    SharedPreferences prefs;
    Button start_leave_button;

    public StartPage() {
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
        prefs=getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        startDetails=(TextView)getActivity().findViewById(R.id.startDetails);
        endDetails=(TextView)getActivity().findViewById(R.id.endDetails);
        myLanguage=(Spinner)getActivity().findViewById(R.id.myLanguage);
        start_leave_button=(Button)getActivity().findViewById(R.id.start_leave_button);

        startDetails.setText("Thursday\n12th January 2017\n8:00 AM");
        endDetails.setText("Saturday\n14th January 2017\n7:00 PM");

        start_leave_button.setText("START");
        start_leave_button.setBackgroundColor(Color.parseColor("#8DC640"));

        ArrayList<String> listOfLanguages=new ArrayList<>();
        listOfLanguages.add("LANGUAGE");
        listOfLanguages.add("Hindi");
        listOfLanguages.add("Gujarati");
        listOfLanguages.add("English");

        ArrayAdapter<String> adapterLanguage=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,listOfLanguages);
        myLanguage.setAdapter(adapterLanguage);

        int index=adapterLanguage.getPosition(prefs.getString("language","English"));
        myLanguage.setSelection(index);

        myLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLanguage=adapterView.getItemAtPosition(i).toString();
                SharedPreferences.Editor e=prefs.edit();
                e.putString("language",selectedLanguage);
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
        switch (item.getItemId()){
            case R.id.rulesIcon:
                ob=(StartPageInterface)getActivity();
                Rules f=new Rules();
                ob.changeFragmentFromStartPage(f,"RULES");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

interface StartPageInterface{
    public void changeFragmentFromStartPage(Fragment f,String title);
}
