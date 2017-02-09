package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import in.truskills.liveexams.R;

/**
 * Created by Shivansh Gupta on 06-02-2017.
 */

public class AllSectionsSummaryAdapter extends RecyclerView.Adapter<AllSectionsSummaryAdapter.MyViewHolder>{

    ArrayList<String> sectionName;
    ArrayList<ArrayList<Integer>> questionArray;
    Context c;

    AllSectionsSummaryAdapter(ArrayList<String> sectionName,ArrayList<ArrayList<Integer>> questionArray,Context c){
        this.sectionName=sectionName;
        this.questionArray=questionArray;
        this.c=c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_layout_for_all_sections_summary, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.mySectionName.setText(sectionName.get(holder.getAdapterPosition()));
        GridViewAdapter adapter=new GridViewAdapter(questionArray.get(holder.getAdapterPosition()),c);
        holder.gridView.setAdapter(adapter);
        holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int post, long id) {

                Intent intentMessage=new Intent();
                // put the message in Intent
                ArrayList<Integer> myFi=questionArray.get(holder.getAdapterPosition());
                intentMessage.putExtra("jumpTo",myFi.get(post));
                ((AllSectionsSummary)c).setResult(2,intentMessage);
                ((AllSectionsSummary)c).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return sectionName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mySectionName;
        GridView gridView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mySectionName = (TextView) itemView.findViewById(R.id.mySectionName);
            gridView = (GridView) itemView.findViewById(R.id.gridView);
        }
    }
}
