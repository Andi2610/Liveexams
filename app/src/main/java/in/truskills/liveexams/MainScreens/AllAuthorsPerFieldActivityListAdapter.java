package in.truskills.liveexams.MainScreens;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import in.truskills.liveexams.R;

/**
 * This is custom list adapter for list of authors per stream..
 * Click on a particular author, display the exams of that author..
 */

public class AllAuthorsPerFieldActivityListAdapter extends RecyclerView.Adapter<AllAuthorsPerFieldActivityListAdapter.MyViewHolder> {

    ArrayList<String> myList;
    Context c;
    SharedPreferences prefs;
    Handler h;
    ProgressDialog dialog;
    String value;
    HashMap<String,ArrayList<String>> map;
    String response;

    AllAuthorsPerFieldActivityListAdapter(ArrayList<String> myList, Context c, String response) {
        this.myList = myList;
        this.c = c;
        setHasStableIds(true);
        this.response=response;
    }

    @Override
    public AllAuthorsPerFieldActivityListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_layout_my_streams, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        value = myList.get(position);
        Typeface tff = Typeface.createFromAsset(c.getAssets(), "fonts/Comfortaa-Regular.ttf");
        holder.name.setText(value);
        holder.name.setTypeface(tff);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences allow=c.getSharedPreferences("allow",Context.MODE_PRIVATE);

                Log.d("prefsAllow",allow.getInt("allow",1)+"");
                if(allow.getInt("allow",1)==0){
                    if(c!=null)
                        Toast.makeText(c, "Your last paper submission is pending..\nPlease wait for few seconds before continuing..", Toast.LENGTH_SHORT).show();
                }else{
                    value = myList.get(holder.getAdapterPosition());
                    Intent i =new Intent(c,AllExamsPerAuthorActivity.class);
                    Bundle b=new Bundle();
                    b.putString("author",value);
                    b.putString("response",response);
                    i.putExtra("bundle",b);
                    c.startActivity(i);
                 }
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

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        LinearLayout container;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            container = (LinearLayout) itemView.findViewById(R.id.container);
        }
    }
}
