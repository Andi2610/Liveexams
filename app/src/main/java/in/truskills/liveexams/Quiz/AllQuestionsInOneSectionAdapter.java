package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.graphics.Typeface;
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
 * This is custom adapter for question number display on each page pf the quiz
 *  i.e AllQuestionsInOneSectionAdapter..
 *  Whenever a new holder position is clicked, the question corresponding to number is displayed..
 *  Also, the entire list is displayed with certain colors depending upon question status:
 *
 *  0 : submit
 *  1 : ticked and reviewed
 *  2 : un ticked and reviewed
 *  3. not answered
 *  4. not visited
 */

public class AllQuestionsInOneSectionAdapter extends RecyclerView.Adapter<AllQuestionsInOneSectionAdapter.MyViewHolder> {

    ArrayList<Integer> myListOfFragmentIndex, myType;
    Context c;
    int pos;
    setValueOfPager ob;

    AllQuestionsInOneSectionAdapter(ArrayList<Integer> myListOfFragmentIndex, Context c, int pos, ArrayList<Integer> myType) {
        this.myListOfFragmentIndex = myListOfFragmentIndex;
        this.myType = myType;
        this.c = c;
        this.pos = pos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_layout_for_question_display, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        int cn = holder.getAdapterPosition();
        cn++;
        holder.questionNumber.setText(cn + "");
        Typeface tff1 = Typeface.createFromAsset(c.getAssets(), "fonts/Comfortaa-Bold.ttf");
        holder.questionNumber.setTypeface(tff1);
        switch (myType.get(holder.getAdapterPosition())) {
            case 0:
                holder.questionNumber.setTextColor(c.getResources().getColor(R.color.green));
                break;
            case 1:
                holder.questionNumber.setTextColor(c.getResources().getColor(R.color.orange));
                break;
            case 2:
                holder.questionNumber.setTextColor(c.getResources().getColor(R.color.purple));
                break;
            case 3:
                holder.questionNumber.setTextColor(c.getResources().getColor(R.color.red));
                break;
            case 4:
                holder.questionNumber.setTextColor(c.getResources().getColor(R.color.black));
                break;
        }
        if (holder.getAdapterPosition() == pos) {
            holder.downArrow.setVisibility(View.VISIBLE);
        } else {
            holder.downArrow.setVisibility(View.INVISIBLE);
        }
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ob = (setValueOfPager) c;
                int jumpPosition = myListOfFragmentIndex.get(holder.getAdapterPosition());
                ob.SetValue(jumpPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myListOfFragmentIndex.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView questionNumber;
        LinearLayout parent;
        ImageView downArrow;

        public MyViewHolder(View itemView) {
            super(itemView);
            questionNumber = (TextView) itemView.findViewById(R.id.questionNumber);
            parent = (LinearLayout) itemView.findViewById(R.id.parent);
            downArrow = (ImageView) itemView.findViewById(R.id.downArrow);
        }
    }
}

interface setValueOfPager {
    public void SetValue(int pos);
}

