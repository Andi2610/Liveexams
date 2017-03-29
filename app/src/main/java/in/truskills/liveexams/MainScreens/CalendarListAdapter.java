package in.truskills.liveexams.MainScreens;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
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
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.ParticularExam.ParticularExamMainActivity;
import in.truskills.liveexams.R;

//This is the list adapter for my enrolled exams which will direct to start page whenever an item in the list is clicked..

public class CalendarListAdapter extends RecyclerView.Adapter<CalendarListAdapter.MyViewHolder> {

    //Declare variables..
    List<Values> myList;
    Context c;
    Values value;
    SharedPreferences prefs;
    ProgressDialog dialog;
    Handler h;
    RequestQueue requestQueue;
    String enrolled, timestamp, examDetails, examId, examGiven;

    CalendarListAdapter(List<Values> myList, Context c) {
        this.myList = myList;
        this.c = c;
    }

    @Override
    public CalendarListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_layout_my_exams, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        value = myList.get(position);
        Typeface tff = Typeface.createFromAsset(c.getAssets(), "fonts/Comfortaa-Regular.ttf");
        Typeface tff2 = Typeface.createFromAsset(c.getAssets(), "fonts/Comfortaa-Bold.ttf");
        holder.name.setText(value.getName());
        holder.name.setTypeface(tff2);
        holder.startDatevalue.setText(value.getStartDateValue());
        holder.startDatevalue.setTypeface(tff);
        holder.endDateValue.setText(value.getEndDateValue());
        holder.endDateValue.setTypeface(tff);
        holder.durationValue.setText(value.getDurationValue());
        holder.durationValue.setTypeface(tff);
        holder.startDateText.setTypeface(tff);
        holder.endDateText.setTypeface(tff);
        holder.durationText.setTypeface(tff);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                value = myList.get(holder.getAdapterPosition());
                requestQueue = Volley.newRequestQueue(c);
                prefs = c.getSharedPreferences("prefs", Context.MODE_PRIVATE);

                if(c!=null){
                    dialog = new ProgressDialog(c);
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("Loading. Please wait...");
                    dialog.setIndeterminate(true);
                    dialog.setCancelable(false);
                    dialog.show();
                }

                ConstantsDefined.updateAndroidSecurityProvider((Activity) c);
                ConstantsDefined.beforeVolleyConnect();

                //Get exam details of the exam on which the user has clicked..
                String url = ConstantsDefined.api + "examDetails";
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(dialog!=null)
                        dialog.dismiss();
                        try {
                            //Parse Exam details..

                            JSONObject jsonObject=new JSONObject(response);
                            String success=jsonObject.getString("success");
                            if(success.equals("true")){
                                HashMap<String, String> mapper = MiscellaneousParser.examDetailsParser(response);

                                //Get it's variables..
                                enrolled = mapper.get("enrolled");
                                timestamp = mapper.get("timestamp");
                                examDetails = mapper.get("examDetails");
                                examGiven = mapper.get("examGiven");

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
                                        b.putString("examGiven", examGiven);
                                        Intent i = new Intent(c, ParticularExamMainActivity.class);
                                        i.putExtra("bundle", b);
                                        i.putExtra("from", "calendar");
                                        ((MainActivity) c).startActivity(i);
//                                    ((MainActivity)c).startActivityForResult(i,11);
                                    }
                                });
                            }else{
                                if(c!=null)
                                    Toast.makeText(c, "An unexpected error occured..\nPlease try again..", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //If connection couldn't be made..
                        if(dialog!=null)
                        dialog.dismiss();
                        if(ConstantsDefined.isOnline(c)){
                            //Do nothing..
                            if(c!=null)
                            Toast.makeText(c, "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                        }else{
                            if(c!=null)
                            Toast.makeText(c, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                        }
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

        public TextView name, startDatevalue, endDateValue, durationValue, startDateText, endDateText, durationText;
        LinearLayout container;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            startDatevalue = (TextView) itemView.findViewById(R.id.startDateValue);
            endDateValue = (TextView) itemView.findViewById(R.id.endDateValue);
            durationValue = (TextView) itemView.findViewById(R.id.durationValue);
            startDateText = (TextView) itemView.findViewById(R.id.startDateText);
            endDateText = (TextView) itemView.findViewById(R.id.endDateText);
            durationText = (TextView) itemView.findViewById(R.id.durationText);
            container = (LinearLayout) itemView.findViewById(R.id.container);
        }
    }
}
