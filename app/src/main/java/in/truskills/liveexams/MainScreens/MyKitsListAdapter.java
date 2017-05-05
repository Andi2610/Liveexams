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
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.ParticularExam.ParticularExamMainActivity;
import in.truskills.liveexams.Quiz.FeedbackActivity;
import in.truskills.liveexams.R;

//This is the list adapter for my enrolled exams which will direct to start page whenever an item in the list is clicked..

public class MyKitsListAdapter extends RecyclerView.Adapter<MyKitsListAdapter.MyViewHolder> {

    //Declare variables..
    List<Values> myList;
    Context c;
    Values value;
    SharedPreferences prefs;
    ProgressDialog dialog;
    Handler h;
    RequestQueue requestQueue;
    String enrolled, timestamp, examDetails, examId, examGiven;
    MyKitsInterface ob;

    MyKitsListAdapter(List<Values> myList, Context c,MyKitsInterface ob) {
        this.myList = myList;
        this.c = c;
        this.ob=ob;
    }

    @Override
    public MyKitsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_layout_for_kit, parent, false);
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
//        holder.durationValue.setText(value.getDurationValue());
//        holder.durationValue.setTypeface(tff);
        holder.startDateText.setTypeface(tff);
        holder.endDateText.setTypeface(tff);
//        holder.durationText.setTypeface(tff);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
//                Intent i =new Intent(c,FeedbackActivity.class);
//                c.startActivity(i);

                SharedPreferences allow=c.getSharedPreferences("allow",Context.MODE_PRIVATE);
                Log.d("prefsAllow",allow.getInt("allow",1)+"");

                if(allow.getInt("allow",1)==0){
                    if(c!=null)
                        Toast.makeText(c, "Your last paper submission is pending..\nPlease wait for few seconds before continuing..", Toast.LENGTH_SHORT).show();
                }else{
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

                    String url = ConstantsDefined.apiForKit + "getProductKitDetails/"+ value.getExamId()+"/"+prefs.getString("userId","");
                    StringRequest stringRequest = new StringRequest(Request.Method.GET,
                            url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("responseInFragment", "onResponse: "+response);

                            if(dialog!=null)
                                dialog.dismiss();
                            try {

                                JSONObject jsonObject=new JSONObject(response);
                                String success=jsonObject.getString("success");
                                if(success.equals("true")){

                                    KitDetailsFragment f=new KitDetailsFragment();
                                    Bundle b=new Bundle();
                                    Log.d("responseInFragment", "onResponse: "+jsonObject.getJSONObject("response").toString());
                                    b.putString("response",jsonObject.getJSONObject("response").toString());
                                    b.putString("from","home");
                                    f.setArguments(b);
                                    ob.changeFragmentFromMyKits(f,value.getName(),"KitDetailsFragmentFromHome");

                                }else{
                                    if(c!=null)
                                        Toast.makeText(c, "Something went wrong..\n" +
                                                "Please try again..", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
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
                    });
                    requestQueue.add(stringRequest);
                }
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
//            durationValue = (TextView) itemView.findViewById(R.id.durationValue);
            startDateText = (TextView) itemView.findViewById(R.id.startDateText);
            endDateText = (TextView) itemView.findViewById(R.id.endDateText);
//            durationText = (TextView) itemView.findViewById(R.id.durationText);
            container = (LinearLayout) itemView.findViewById(R.id.container);
        }
    }
}
