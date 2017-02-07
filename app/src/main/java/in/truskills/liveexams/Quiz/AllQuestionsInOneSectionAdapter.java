package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.truskills.liveexams.R;

/**
 * Created by Shivansh Gupta on 06-02-2017.
 */

public class AllQuestionsInOneSectionAdapter extends RecyclerView.Adapter<AllQuestionsInOneSectionAdapter.MyViewHolder> {

    ArrayList<Integer> myListOfQuestion;
    Context c;
    int pos;
    setValueOfPager ob;

    AllQuestionsInOneSectionAdapter(ArrayList<Integer> myListOfQuestion,Context c,int pos){
        this.myListOfQuestion = myListOfQuestion;
        this.c = c;
        this.pos=pos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_layout_for_question_display, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.questionNumber.setText(myListOfQuestion.get(position)+"");
            if(position==pos){
                holder.downArrow.setVisibility(View.VISIBLE);
            }else{
                holder.downArrow.setVisibility(View.INVISIBLE);
            }
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ob=(setValueOfPager)c;
                    ob.SetValue(position);
                }
            });
    }

    @Override
    public int getItemCount() {
        return myListOfQuestion.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView questionNumber;
        LinearLayout parent;
        ImageView downArrow;

        public MyViewHolder(View itemView) {
            super(itemView);
            questionNumber = (TextView) itemView.findViewById(R.id.questionNumber);
            parent = (LinearLayout) itemView.findViewById(R.id.parent);
            downArrow=(ImageView) itemView.findViewById(R.id.downArrow);
        }
    }
}

interface setValueOfPager{
    public void SetValue(int pos);
}
