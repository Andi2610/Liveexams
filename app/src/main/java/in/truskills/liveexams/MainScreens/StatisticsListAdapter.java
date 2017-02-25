package in.truskills.liveexams.MainScreens;

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

import java.util.List;

import in.truskills.liveexams.ParticularExamStatistics.AnswerPaperLoad;
import in.truskills.liveexams.ParticularExamStatistics.InitialInfo;
import in.truskills.liveexams.R;

/**
 * Created by 6155dx on 22-01-2017.
 */

public class StatisticsListAdapter extends RecyclerView.Adapter<StatisticsListAdapter.MyViewHolder>{

    List<Values> myList;
    Context c;
    SharedPreferences prefs;
    Handler h;
    ProgressDialog dialog;
    Values value;

    StatisticsListAdapter(List<Values> myList,Context c){
        this.myList=myList;
        this.c=c;
        setHasStableIds(true);
    }

    @Override
    public StatisticsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_layout_my_exams, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        value=myList.get(position);
        Typeface tff=Typeface.createFromAsset(c.getAssets(), "fonts/Comfortaa-Regular.ttf");
        Typeface tff2=Typeface.createFromAsset(c.getAssets(), "fonts/Comfortaa-Bold.ttf");
        holder.name.setText(value.getName());
        holder.name.setTypeface(tff2);
        holder.startDatevalue.setText(value.getStartDateValue());
        holder.startDatevalue.setTypeface(tff);
        holder.endDateValue.setText(value.getEndDateValue());
        holder.endDateValue.setTypeface(tff);
        holder.durationValue.setText(value.getDurationValue());
        holder.durationValue.setTypeface(tff);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                value=myList.get(holder.getAdapterPosition());
                Intent i =new Intent(c,AnswerPaperLoad.class);
                i.putExtra("examId", value.getExamId());
                i.putExtra("examName",value.getName());
                c.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView name,startDatevalue,endDateValue,durationValue;
        LinearLayout container;

        public MyViewHolder(View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.name);
            startDatevalue=(TextView)itemView.findViewById(R.id.startDateValue);
            endDateValue=(TextView)itemView.findViewById(R.id.endDateValue);
            durationValue=(TextView)itemView.findViewById(R.id.durationValue);
            container=(LinearLayout)itemView.findViewById(R.id.container);
        }
    }

    public void startMyActivity(String enrolled,String timestamp,String examDetails,String name,String examId){

        Bundle b=new Bundle();
        b.putString("enrolled",enrolled);
        b.putString("timestamp",timestamp);
        b.putString("examDetails",examDetails);
        b.putString("name",name);
        b.putString("examId",examId);
        Intent i=new Intent(c,InitialInfo.class);
        i.putExtra("bundle",b);
        i.putExtra("from","allExams");
        c.startActivity(i);
    }
}
