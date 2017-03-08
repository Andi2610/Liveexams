package in.truskills.liveexams.ParticularExamStatistics;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.AnalyticsDatabase;
import io.fabric.sdk.android.services.settings.AnalyticsSettingsData;

/**
 * Created by Shivansh Gupta on 27-02-2017.
 */

public class AllSectionsSummaryAdapterForAnswers extends RecyclerView.Adapter<AllSectionsSummaryAdapterForAnswers.MyViewHolder> {


    ArrayList<String> sectionName;
    ArrayList<ArrayList<Integer>> questionArray;
    Context c;

    AllSectionsSummaryAdapterForAnswers(ArrayList<String> sectionName,ArrayList<ArrayList<Integer>> questionArray,Context c){
        this.sectionName=sectionName;
        this.questionArray=questionArray;
        this.c=c;
    }

    @Override
    public AllSectionsSummaryAdapterForAnswers.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_layout_for_all_sections_summary_for_answers, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AllSectionsSummaryAdapterForAnswers.MyViewHolder holder, int position) {
        holder.mySectionName.setText(sectionName.get(holder.getAdapterPosition()));

        ArrayList<Integer> types;
        AnalyticsDatabase ob=new AnalyticsDatabase(c);

        int sI=holder.getAdapterPosition();
        types=ob.getIntValuesOfEachSection(sI,AnalyticsDatabase.QuestionStatus);

        String score=ob.getValuesPerSection(sI,AnalyticsDatabase.SectionWiseMarks);
        String totalScore=ob.getValuesPerSection(sI,AnalyticsDatabase.SectionMarks);
        String attempted=ob.getValuesPerSection(sI,AnalyticsDatabase.SectionWiseAttemptedQuestions);
        int totalQuestions=types.size();
        String time=ob.getValuesPerSection(sI,AnalyticsDatabase.SectionWiseTimeSpent);
        String totalTime=ob.getValuesPerSection(sI,AnalyticsDatabase.SectionTime);
        String rank=ob.getValuesPerSection(sI,AnalyticsDatabase.SectionWiseRank);
        String totalRank="100";

        holder.scoreSectionWise.setText("SCORE:  "+score+"/"+totalScore);
        holder.attemptedSectionWise.setText("ATTEMPTED:  "+attempted+"/"+totalQuestions);
        holder.timeSectionWise.setText("TIME:  "+time+"/"+totalTime);
        holder.rankSectionWise.setText("RANK:  "+rank+"/"+totalRank);

//        WindowManager wm = (WindowManager)    c.getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//
//        ViewGroup.LayoutParams param = holder.scoreSectionWise.getLayoutParams();
//        double temp=Double.parseDouble(score)/Double.parseDouble(totalScore);
//
//        Log.d("width", "onBindViewHolder: "+temp+" "+score+" "+totalScore);
//
//        temp=temp*display.getWidth();
//
//        Log.d("width", "onBindViewHolder: "+temp);
//
//        param.width=45;
//
//        Log.d("width", "onBindViewHolder: "+param.width);

//        holder.scoreSectionWiseBackground.setLayoutParams(param);

        ArrayList<Integer> myTypes=ob.getTypes(sI);

        holder.submittedQuestionsAllSummary.setText(myTypes.get(0)+"");
        holder.reviewedTickedQuestionsAllSummary.setText(myTypes.get(1)+"");
        holder.reviewedUntickedQuestionsAllSummary.setText(myTypes.get(2)+"");
        holder.clearedQuestionsAllSummary.setText(myTypes.get(3)+"");
        holder.notAttemptedQuestionsAllSummary.setText(myTypes.get(4)+"");

        Typeface tff1=Typeface.createFromAsset(c.getAssets(), "fonts/Comfortaa-Bold.ttf");
        holder.mySectionName.setTypeface(tff1);
        Typeface tff2=Typeface.createFromAsset(c.getAssets(), "fonts/Comfortaa-Regular.ttf");
        holder.submittedQuestionsAllSummary.setTypeface(tff1);
        holder.reviewedTickedQuestionsAllSummary.setTypeface(tff1);
        holder.reviewedUntickedQuestionsAllSummary.setTypeface(tff1);
        holder.clearedQuestionsAllSummary.setTypeface(tff1);
        holder.notAttemptedQuestionsAllSummary.setTypeface(tff1);
        holder.scoreSectionWise.setTypeface(tff2);
        holder.attemptedSectionWise.setTypeface(tff2);
        holder.timeSectionWise.setTypeface(tff2);
        holder.rankSectionWise.setTypeface(tff2);


//        ArrayList<Integer> myType=ob.getTypesOfEachSection(sI);

        GrideViewAdapterForAnswers adapter=new GrideViewAdapterForAnswers(questionArray.get(holder.getAdapterPosition()),types,c);
        holder.gridView.setAdapter(adapter);
        holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int post, long id) {

                Intent intentMessage=new Intent();
                // put the message in Intent
                ArrayList<Integer> myFi=questionArray.get(holder.getAdapterPosition());
                intentMessage.putExtra("jumpTo",myFi.get(post));
                ((AllSectionsSummaryForAnswers)c).setResult(2,intentMessage);
                ((AllSectionsSummaryForAnswers)c).finish();
            }
        });

        holder.mySectionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessage=new Intent();
                // put the message in Intent
                //message=serial number of a section..
                AnalyticsDatabase analyticsDatabase=new AnalyticsDatabase(c);
                int sI=holder.getAdapterPosition();
                int fi=Integer.parseInt(analyticsDatabase.getStringValuesPerQuestion(sI,0,AnalyticsDatabase.FragmentIndex));
                intentMessage.putExtra("jumpTo",fi);
                ((AllSectionsSummaryForAnswers)c).setResult(2,intentMessage);
                ((AllSectionsSummaryForAnswers)c).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return sectionName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mySectionName,submittedQuestionsAllSummary,reviewedTickedQuestionsAllSummary,reviewedUntickedQuestionsAllSummary,clearedQuestionsAllSummary,notAttemptedQuestionsAllSummary;
        TextView scoreSectionWise,attemptedSectionWise,timeSectionWise,rankSectionWise;
//        TextView scoreSectionWiseBackground;
        GridView gridView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mySectionName = (TextView) itemView.findViewById(R.id.mySectionName);
            submittedQuestionsAllSummary = (TextView) itemView.findViewById(R.id.submittedQuestionsAllSummary);
            reviewedTickedQuestionsAllSummary = (TextView) itemView.findViewById(R.id.reviewedTickedQuestionsAllSummary);
            reviewedUntickedQuestionsAllSummary = (TextView) itemView.findViewById(R.id.reviewedUntickedQuestionsAllSummary);
            clearedQuestionsAllSummary = (TextView) itemView.findViewById(R.id.clearedQuestionsAllSummary);
            notAttemptedQuestionsAllSummary = (TextView) itemView.findViewById(R.id.notAttemptedQuestionsAllSummary);
            scoreSectionWise = (TextView) itemView.findViewById(R.id.scoreSectionWise);
//            scoreSectionWiseBackground = (TextView) itemView.findViewById(R.id.scoreSectionWiseBackground);
            attemptedSectionWise = (TextView) itemView.findViewById(R.id.attemptedSectionWise);
            timeSectionWise = (TextView) itemView.findViewById(R.id.timeSectionWise);
            rankSectionWise = (TextView) itemView.findViewById(R.id.rankSectionWise);
            gridView = (GridView) itemView.findViewById(R.id.gridView);
        }
    }
}
