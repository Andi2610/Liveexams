package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import in.truskills.liveexams.R;

/**
 * Created by Shivansh Gupta on 27-03-2017.
 */

public class FeedbackListAdapter extends RecyclerView.Adapter<FeedbackListAdapter.MyViewHolder> {

    ArrayList<String> questionTextList;
    ArrayList<String> questionIdList;
    ArrayList<String> questionTopicList;
    ArrayList<String> questionNumberList;
    static HashMap<Integer,String> feedback=new HashMap<>();
    Context c;

    FeedbackListAdapter(ArrayList<String> questionTextList,ArrayList<String> questionIdList,ArrayList<String> questionTopicList,ArrayList<String> questionNumberList,Context c) {
        this.questionTextList = questionTextList;
        this.questionIdList = questionIdList;
        this.questionTopicList = questionTopicList;
        this.questionNumberList=questionNumberList;
        this.c = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_layout_for_feedback, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
       holder.question.setText(questionTextList.get(holder.getAdapterPosition()));
        Typeface tff2 = Typeface.createFromAsset(c.getAssets(), "fonts/Comfortaa-Regular.ttf");

        holder.question.setTypeface(tff2);

        holder.layout1.setVisibility(View.INVISIBLE);
        holder.layout2.setVisibility(View.INVISIBLE);
        holder.layout3.setVisibility(View.INVISIBLE);
        holder.layout4.setVisibility(View.INVISIBLE);
        holder.layout5.setVisibility(View.INVISIBLE);

        final int c=holder.getAdapterPosition()+1;
        feedback.put(c,0+"");

        holder.l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               holder.layout1.setVisibility(View.VISIBLE);
               holder.layout2.setVisibility(View.INVISIBLE);
               holder.layout3.setVisibility(View.INVISIBLE);
               holder.layout4.setVisibility(View.INVISIBLE);
               holder.layout5.setVisibility(View.INVISIBLE);
                feedback.put(c,1+"");
            }
        });

        holder.l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.layout1.setVisibility(View.INVISIBLE);
                holder.layout2.setVisibility(View.VISIBLE);
                holder.layout3.setVisibility(View.INVISIBLE);
                holder.layout4.setVisibility(View.INVISIBLE);
                holder.layout5.setVisibility(View.INVISIBLE);
                feedback.put(c,2+"");

            }
        });

        holder.l3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.layout1.setVisibility(View.INVISIBLE);
                holder.layout2.setVisibility(View.INVISIBLE);
                holder.layout3.setVisibility(View.VISIBLE);
                holder.layout4.setVisibility(View.INVISIBLE);
                holder.layout5.setVisibility(View.INVISIBLE);
                feedback.put(c,3+"");

            }
        });

        holder.l4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.layout1.setVisibility(View.INVISIBLE);
                holder.layout2.setVisibility(View.INVISIBLE);
                holder.layout3.setVisibility(View.INVISIBLE);
                holder.layout4.setVisibility(View.VISIBLE);
                holder.layout5.setVisibility(View.INVISIBLE);
                feedback.put(c,4+"");

            }
        });

        holder.l5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.layout1.setVisibility(View.INVISIBLE);
                holder.layout2.setVisibility(View.INVISIBLE);
                holder.layout3.setVisibility(View.INVISIBLE);
                holder.layout4.setVisibility(View.INVISIBLE);
                holder.layout5.setVisibility(View.VISIBLE);
                feedback.put(c,5+"");
            }
        });
    }

    public static HashMap getFeedback(){
        return feedback;
    }

    @Override
    public int getItemCount() {
        return questionTextList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView question;
        Button l1,l2,l3,l4,l5;
        LinearLayout layout1,layout2,layout3,layout4,layout5;

        public MyViewHolder(View itemView) {
            super(itemView);
            question=(TextView)itemView.findViewById(R.id.question);
            l1=(Button) itemView.findViewById(R.id.l1);
            l2=(Button)itemView.findViewById(R.id.l2);
            l3=(Button)itemView.findViewById(R.id.l3);
            l4=(Button)itemView.findViewById(R.id.l4);
            l5=(Button)itemView.findViewById(R.id.l5);
            layout1=(LinearLayout)itemView.findViewById(R.id.layout1);
            layout2=(LinearLayout)itemView.findViewById(R.id.layout2);
            layout3=(LinearLayout)itemView.findViewById(R.id.layout3);
            layout4=(LinearLayout)itemView.findViewById(R.id.layout4);
            layout5=(LinearLayout)itemView.findViewById(R.id.layout5);
        }
    }
}

