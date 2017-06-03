package in.truskills.liveexams.MainScreens;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import in.truskills.liveexams.R;

/**
 * Created by Shivansh Gupta on 08-04-2017.
 */

public class AllAuthorsPerFieldForKitActivityListAdapter extends RecyclerView.Adapter<AllAuthorsPerFieldForKitActivityListAdapter.MyViewHolder> {

    ArrayList<String> myList;
    Context c;
    SharedPreferences prefs;
    Handler h;
    ProgressDialog dialog;
    String value;
    HashMap<String,ArrayList<String>> map;
    String response;
    ArrayList<String> mykits;

    // Changed constructor for getting list of bought kits
    AllAuthorsPerFieldForKitActivityListAdapter(ArrayList<String> myList,ArrayList<String> mykits, Context c, String response) {
        this.myList = myList;
        this.mykits=mykits;
        this.c = c;
        setHasStableIds(true);
        this.response=response;
    }

    @Override
    public AllAuthorsPerFieldForKitActivityListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

                Bundle b=new Bundle();
                b.putString("author",value);
                b.putString("response",response);
                b.putStringArrayList("mykits",mykits); //passing the list of bought kits to AllkitsperAuthorActivity
                Intent i = new Intent(c,AllKitsPerAuthorActivity.class);
                i.putExtra("bundle",b);
                c.startActivity(i);
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
