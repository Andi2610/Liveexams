package in.truskills.liveexams.MainScreens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.ParticularExam.ParticularExamMainActivity;
import in.truskills.liveexams.R;

//This is the list adapter for my enrolled exams which will direct to start page whenever an item in the list is clicked..

public class MyExamsListAdapter extends RecyclerView.Adapter<MyExamsListAdapter.MyViewHolder> {

    //Declare variables..
    List<Values> myList;
    Context c;
    Values value;
    SharedPreferences prefs;
    Handler h;
    RequestQueue requestQueue;
    String enrolled, timestamp, examDetails, examId;

    MyExamsListAdapter(List<Values> myList, Context c) {
        this.myList = myList;
        this.c = c;
    }

    @Override
    public MyExamsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_layout_my_exams, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        value = myList.get(position);
        holder.name.setText(value.getName());
        holder.startDatevalue.setText(value.getStartDateValue());
        holder.endDateValue.setText(value.getEndDateValue());
        holder.durationValue.setText(value.getDurationValue());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                value = myList.get(holder.getAdapterPosition());
                requestQueue = Volley.newRequestQueue(c);
                prefs = c.getSharedPreferences("prefs", Context.MODE_PRIVATE);

                //Get exam details of the exam on which the user has clicked..
                String url = VariablesDefined.api + "examDetails";
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Parse Exam details..
                            HashMap<String, String> mapper = VariablesDefined.examDetailsParser(response);

                            //Get it's variables..
                            enrolled = mapper.get("enrolled");
                            timestamp = mapper.get("timestamp");
                            examDetails = mapper.get("examDetails");

                            examId = value.getExamId();

                            h = new Handler();
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    //Create a bundle to be passed to particular main activity..
                                    Bundle b = new Bundle();
                                    b.putString("enrolled", enrolled);
                                    b.putString("timestamp", timestamp);
                                    b.putString("examDetails", examDetails);
                                    b.putString("name", value.getName());
                                    b.putString("examId", examId);
                                    Intent i = new Intent(c, ParticularExamMainActivity.class);
                                    i.putExtra("bundle", b);
                                    i.putExtra("from","home");
                                    ((MainActivity)c).startActivityForResult(i,10);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //If connection couldn't be made..
                        Toast.makeText(c, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        //Set required parameters..
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("userId", prefs.getString("userId", "abc"));
                        params.put("examId", value.getExamId());
                        return params;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name, startDatevalue, endDateValue, durationValue;
        LinearLayout container;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            startDatevalue = (TextView) itemView.findViewById(R.id.startDateValue);
            endDateValue = (TextView) itemView.findViewById(R.id.endDateValue);
            durationValue = (TextView) itemView.findViewById(R.id.durationValue);
            container = (LinearLayout) itemView.findViewById(R.id.container);
        }
    }
}
