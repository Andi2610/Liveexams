package in.truskills.liveexams.ParticularExam;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.truskills.liveexams.Miscellaneous.QuestionPaperParser;
import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.R;

//This is Join Fragment where a user can enroll for a new exam..

public class JoinPageFragment extends Fragment {

    //Declare variables..
    JoinPageInterface ob;
    TextView startDetailsJoinPage, endDetailsJoinPage, descriptionJoinPage;
    Spinner myLanguageJoinPage;
    String selectedLanguage, timestamp, examDetails, examId;
    SharedPreferences prefs;
    Button join_button;
    Bundle b;

    public JoinPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_join_page, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get shared preferences..
        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        ob = (JoinPageInterface) getActivity();
        ob.changeTitleForJoinPage();

        startDetailsJoinPage = (TextView) getActivity().findViewById(R.id.startDetailsJoinPage);
        endDetailsJoinPage = (TextView) getActivity().findViewById(R.id.endDetailsJoinPage);
        descriptionJoinPage = (TextView) getActivity().findViewById(R.id.descriptionJoinPage);
        myLanguageJoinPage = (Spinner) getActivity().findViewById(R.id.myLanguageJoinPage);
        join_button = (Button) getActivity().findViewById(R.id.join_button);

        //Get arguments..
        b = getArguments();
        timestamp = b.getString("timestamp");
        examDetails = b.getString("examDetails");
        examId = b.getString("examId");

        //Parse the exam details..
        try {
                HashMap<String, String> mapper = VariablesDefined.join_start_Parser(examDetails);
                descriptionJoinPage.setText(mapper.get("Description"));
                startDetailsJoinPage.setText(mapper.get("StartDate") + "\n" + mapper.get("StartTime"));
                endDetailsJoinPage.setText(mapper.get("EndDate") + "\n" + mapper.get("EndTime"));
                Log.d("messi", "timestamp=" + timestamp);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ob = (JoinPageInterface) getActivity();

                //Enroll User
                final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                String url = VariablesDefined.api + "enrollUser/" + prefs.getString("userId", "abc");
                StringRequest stringRequest = new StringRequest(Request.Method.PUT,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response=", response.toString() + "");
                        try {
                            HashMap<String, String> mapper = VariablesDefined.enrollUserParser(response);
                            String success = mapper.get("success");
                            if (success.equals("true")) {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //If connnection couldn't be made..
                        Log.d("response", error.getMessage());
                        Toast.makeText(getActivity(), "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        //Set required parameters..
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("examId", examId);
                        params.put("language", selectedLanguage);
                        return params;
                    }
                };
                requestQueue.add(stringRequest);

                StartPageFragment f = new StartPageFragment();
                f.setArguments(b);
                ob.changeFragmentFromJoinPage(f, "name");
            }
        });

//        startDetailsJoinPage.setText("Thursday\n12th January 2017\n8:00 AM");
//        endDetailsJoinPage.setText("Saturday\n14th January 2017\n7:00 PM");

        ArrayList<String> listOfLanguages = new ArrayList<>();
        listOfLanguages.add("LANGUAGE");
        listOfLanguages.add("Hindi");
        listOfLanguages.add("Gujarati");
        listOfLanguages.add("English");

        ArrayAdapter<String> adapterLanguage = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listOfLanguages);
        myLanguageJoinPage.setAdapter(adapterLanguage);

        int index = adapterLanguage.getPosition(prefs.getString("language", "English"));
        myLanguageJoinPage.setSelection(index);

        myLanguageJoinPage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLanguage = adapterView.getItemAtPosition(i).toString();
                SharedPreferences.Editor e = prefs.edit();
                e.putString("language", selectedLanguage);
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
        switch (item.getItemId()) {
            //When Rules icon is clicked.. load Rules fragment through interface..
            case R.id.rulesIcon:
                ob = (JoinPageInterface) getActivity();
                RulesFragment f = new RulesFragment();
                ob.changeFragmentFromJoinPage(f, "RULES");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

//Interface used for interaction with JoinFragment..
interface JoinPageInterface {
    public void changeFragmentFromJoinPage(Fragment f, String title);

    public void changeTitleForJoinPage();

}