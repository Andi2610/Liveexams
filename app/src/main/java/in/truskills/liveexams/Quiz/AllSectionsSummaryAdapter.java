package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import in.truskills.liveexams.SqliteDatabases.QuizDatabase;
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

        ArrayList<Integer> types;
        QuizDatabase ob=new QuizDatabase(c);

        int sI=ob.getIntValuesPerSectionBySerialNumber(holder.getAdapterPosition(), QuizDatabase.SectionIndex);

        types=ob.getTypes(sI);

        holder.submittedQuestionsAllSummary.setText(types.get(0)+"");
        holder.reviewedTickedQuestionsAllSummary.setText(types.get(1)+"");
        holder.reviewedUntickedQuestionsAllSummary.setText(types.get(2)+"");
        holder.clearedQuestionsAllSummary.setText(types.get(3)+"");
        holder.notAttemptedQuestionsAllSummary.setText(types.get(4)+"");


        Typeface tff1=Typeface.createFromAsset(c.getAssets(), "fonts/Comfortaa-Bold.ttf");
        holder.mySectionName.setTypeface(tff1);
        Typeface tff2=Typeface.createFromAsset(c.getAssets(), "fonts/Comfortaa-Regular.ttf");
        holder.submittedQuestionsAllSummary.setTypeface(tff1);
        holder.reviewedTickedQuestionsAllSummary.setTypeface(tff1);
        holder.reviewedUntickedQuestionsAllSummary.setTypeface(tff1);
        holder.clearedQuestionsAllSummary.setTypeface(tff1);
        holder.notAttemptedQuestionsAllSummary.setTypeface(tff1);

        ArrayList<Integer> myType=ob.getTypesOfEachSection(sI);

        GridViewAdapter adapter=new GridViewAdapter(questionArray.get(holder.getAdapterPosition()),myType,c);
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

        holder.mySectionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessage=new Intent();
                // put the message in Intent
                //message=serial number of a section..
                int mySrNo=holder.getAdapterPosition();
                QuizDatabase quizDatabase =new QuizDatabase(c);
                int sI= quizDatabase.getIntValuesPerSectionBySerialNumber(mySrNo, QuizDatabase.SectionIndex);
                int my_fi= quizDatabase.getIntValuesPerQuestionBySiAndSrno(sI,0, QuizDatabase.FragmentIndex);
                intentMessage.putExtra("jumpTo",my_fi);
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

        TextView mySectionName,submittedQuestionsAllSummary,reviewedTickedQuestionsAllSummary,reviewedUntickedQuestionsAllSummary,clearedQuestionsAllSummary,notAttemptedQuestionsAllSummary;
        GridView gridView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mySectionName = (TextView) itemView.findViewById(R.id.mySectionName);
            submittedQuestionsAllSummary = (TextView) itemView.findViewById(R.id.submittedQuestionsAllSummary);
            reviewedTickedQuestionsAllSummary = (TextView) itemView.findViewById(R.id.reviewedTickedQuestionsAllSummary);
            reviewedUntickedQuestionsAllSummary = (TextView) itemView.findViewById(R.id.reviewedUntickedQuestionsAllSummary);
            clearedQuestionsAllSummary = (TextView) itemView.findViewById(R.id.clearedQuestionsAllSummary);
            notAttemptedQuestionsAllSummary = (TextView) itemView.findViewById(R.id.notAttemptedQuestionsAllSummary);

            gridView = (GridView) itemView.findViewById(R.id.gridView);
        }
    }
}
