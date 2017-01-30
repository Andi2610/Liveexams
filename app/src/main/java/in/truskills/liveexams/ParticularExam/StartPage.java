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

import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartPage extends Fragment {

    StartPageInterface ob;
    TextView startDetails,endDetails,descriptionStartPage;
    Spinner myLanguage;
    String selectedLanguage,timestamp,examDetails,examId;
    SharedPreferences prefs;
    Button start_leave_button;
    Bundle b;

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
        Log.d("response","inOnAcCrStart");

        ob=(StartPageInterface) getActivity();
        ob.changeTitleForStartPage();

        startDetails=(TextView)getActivity().findViewById(R.id.startDetails);
        endDetails=(TextView)getActivity().findViewById(R.id.endDetails);
        descriptionStartPage=(TextView)getActivity().findViewById(R.id.descriptionStartPage);
        myLanguage=(Spinner)getActivity().findViewById(R.id.myLanguage);
        start_leave_button=(Button)getActivity().findViewById(R.id.start_leave_button);

//        startDetails.setText("Thursday\n12th January 2017\n8:00 AM");
//        endDetails.setText("Saturday\n14th January 2017\n7:00 PM");
//
//        start_leave_button.setText("START");
//        start_leave_button.setBackgroundColor(Color.parseColor("#8DC640"));

        b=getArguments();

        timestamp=b.getString("timestamp");
        examDetails=b.getString("examDetails");
        examId=b.getString("examId");

        start_leave_button.setText("LEAVE");

        try {
            HashMap<String,String> mapper= VariablesDefined.join_start_Parser(examDetails);
            descriptionStartPage.setText(mapper.get("Description"));
            startDetails.setText(mapper.get("StartDate")+"\n"+mapper.get("StartTime"));
            endDetails.setText(mapper.get("EndDate")+"\n"+mapper.get("EndTime"));

            Log.d("response",timestamp+"**"+mapper.get("StartTime"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        start_leave_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start_leave_button.getText().equals("LEAVE")){
                    //Unenroll user

                    final RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
                    String url=VariablesDefined.api+"unenrollUser/"+prefs.getString("userId","abc");
                    StringRequest stringRequest = new StringRequest(Request.Method.PUT,
                            url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("response=",response.toString()+"");

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("response", error.getMessage());
                            Toast.makeText(getActivity(), "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String,String> getParams(){
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("examId",examId);
                            return params;
                        }
                    };
                    requestQueue.add(stringRequest);
                }else{
                    //Start Quiz
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
        switch (item.getItemId()){
            case R.id.rulesIcon:
                ob=(StartPageInterface)getActivity();
                Rules f=new Rules();
                ob.changeFragmentFromStartPage(f,"RULES");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("response","inResStart");

    }
}

interface StartPageInterface{
    public void changeFragmentFromStartPage(Fragment f,String title);
    public void changeTitleForStartPage();
}
