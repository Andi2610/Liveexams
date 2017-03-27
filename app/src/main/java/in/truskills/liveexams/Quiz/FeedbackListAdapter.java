package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import in.truskills.liveexams.R;

/**
 * Created by Shivansh Gupta on 27-03-2017.
 */

public class FeedbackListAdapter extends RecyclerView.Adapter<FeedbackListAdapter.MyViewHolder> {

    ArrayList<String> questionList;
    Context c;

    FeedbackListAdapter(ArrayList<String> questionList,Context c) {
        this.questionList = questionList;
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
       holder.question.setText(questionList.get(holder.getAdapterPosition()));
        Typeface tff2 = Typeface.createFromAsset(c.getAssets(), "fonts/Comfortaa-Regular.ttf");

        holder.question.setTypeface(tff2);

        holder.l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.l1.setBackground(c.getResources().getDrawable(R.drawable.l1_clicked));
                holder.l2.setBackground(c.getResources().getDrawable(R.drawable.l2));
                holder.l3.setBackground(c.getResources().getDrawable(R.drawable.l3));
                holder.l4.setBackground(c.getResources().getDrawable(R.drawable.l4));
                holder.l5.setBackground(c.getResources().getDrawable(R.drawable.l5));
            }
        });

        holder.l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.l1.setBackground(c.getResources().getDrawable(R.drawable.l1));
                holder.l2.setBackground(c.getResources().getDrawable(R.drawable.l2_clicked));
                holder.l3.setBackground(c.getResources().getDrawable(R.drawable.l3));
                holder.l4.setBackground(c.getResources().getDrawable(R.drawable.l4));
                holder.l5.setBackground(c.getResources().getDrawable(R.drawable.l5));
            }
        });

        holder.l3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.l1.setBackground(c.getResources().getDrawable(R.drawable.l1));
                holder.l2.setBackground(c.getResources().getDrawable(R.drawable.l2));
                holder.l3.setBackground(c.getResources().getDrawable(R.drawable.l3_clicked));
                holder.l4.setBackground(c.getResources().getDrawable(R.drawable.l4));
                holder.l5.setBackground(c.getResources().getDrawable(R.drawable.l5));
            }
        });

        holder.l4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.l1.setBackground(c.getResources().getDrawable(R.drawable.l1));
                holder.l2.setBackground(c.getResources().getDrawable(R.drawable.l2));
                holder.l3.setBackground(c.getResources().getDrawable(R.drawable.l3));
                holder.l4.setBackground(c.getResources().getDrawable(R.drawable.l4_clicked));
                holder.l5.setBackground(c.getResources().getDrawable(R.drawable.l5));
            }
        });

        holder.l5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.l1.setBackground(c.getResources().getDrawable(R.drawable.l1));
                holder.l2.setBackground(c.getResources().getDrawable(R.drawable.l2));
                holder.l3.setBackground(c.getResources().getDrawable(R.drawable.l3));
                holder.l4.setBackground(c.getResources().getDrawable(R.drawable.l4));
                holder.l5.setBackground(c.getResources().getDrawable(R.drawable.l5_clicked));
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView question;
        Button l1,l2,l3,l4,l5;

        public MyViewHolder(View itemView) {
            super(itemView);
            question=(TextView)itemView.findViewById(R.id.question);
            l1=(Button)itemView.findViewById(R.id.l1);
            l2=(Button)itemView.findViewById(R.id.l2);
            l3=(Button)itemView.findViewById(R.id.l3);
            l4=(Button)itemView.findViewById(R.id.l4);
            l5=(Button)itemView.findViewById(R.id.l5);
        }
    }
}

