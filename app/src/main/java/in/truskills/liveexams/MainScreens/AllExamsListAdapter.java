package in.truskills.liveexams.MainScreens;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import in.truskills.liveexams.ParticularExam.ParticularExamMainActivity;
import in.truskills.liveexams.R;

/**
 * Created by 6155dx on 22-01-2017.
 */

public class AllExamsListAdapter extends RecyclerView.Adapter<AllExamsListAdapter.MyViewHolder>{

    List<Values> myList;
    Context c;

    AllExamsListAdapter(List<Values> myList,Context c){
        this.myList=myList;
        this.c=c;
    }

    @Override
    public AllExamsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_layout_my_exams, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Values value=myList.get(position);
        holder.name.setText(value.getName());
        holder.startDatevalue.setText(value.getStartDateValue());
        holder.endDateValue.setText(value.getEndDateValue());
        holder.durationValue.setText(value.getDurationValue());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(c, ParticularExamMainActivity.class);
                i.putExtra("joined",false);
                i.putExtra("name",holder.name.getText());
                c.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
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
}
