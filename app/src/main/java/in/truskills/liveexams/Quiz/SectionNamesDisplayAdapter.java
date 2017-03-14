package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.truskills.liveexams.R;

/**
 * Created by Shivansh Gupta on 05-02-2017.
 */

public class SectionNamesDisplayAdapter extends RecyclerView.Adapter<SectionNamesDisplayAdapter.MyViewHolder> {


    ArrayList<String> myList;
    Context c;
    int pos;
    SharedPreferences quizPrefs;

    SectionNamesDisplayAdapter(ArrayList<String> myList, Context c, int pos) {
        this.myList = myList;
        this.c = c;
        this.pos = pos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_layout_for_section_names_display, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.textView.setText(myList.get(position));
        Typeface tff1 = Typeface.createFromAsset(c.getAssets(), "fonts/Comfortaa-Bold.ttf");
        holder.textView.setTypeface(tff1);
        if (holder.getAdapterPosition() == pos) {
            holder.textView.setTextColor(c.getResources().getColor(R.color.black));
        } else {
            holder.textView.setTextColor(c.getResources().getColor(R.color.very_light_black));
            holder.leftArrow.setVisibility(View.INVISIBLE);
            holder.rightArrow.setVisibility(View.INVISIBLE);
        }
        holder.containerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                quizPrefs=c.getSharedPreferences("quizPrefs",Context.MODE_PRIVATE);
                SharedPreferences.Editor e=quizPrefs.edit();
                e.putInt("exit",0);
                e.apply();


                Intent intentMessage = new Intent();
                // put the message in Intent
                //message=serial number of a section..
                intentMessage.putExtra("message", holder.getAdapterPosition());
                ((SectionNamesDisplay) c).setResult(1, intentMessage);
                ((SectionNamesDisplay) c).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        LinearLayout containerLayout;
        ImageView leftArrow, rightArrow;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            containerLayout = (LinearLayout) itemView.findViewById(R.id.containerLayout);
            rightArrow = (ImageView) itemView.findViewById(R.id.rightArrow);
            leftArrow = (ImageView) itemView.findViewById(R.id.leftArrow);
        }
    }
}
