package in.truskills.liveexams.MainScreens;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.R;

/**
 * Created by Shivansh Gupta on 08-04-2017.
 */

public class AuthorsListAdapterForMyKits extends RecyclerView.Adapter<AuthorsListAdapterForMyKits.MyViewHolder> {

    ArrayList<String> myList;
    Context c;
    SharedPreferences prefs;
    Handler h;
    ProgressDialog dialog;
    String value;
    AuthorInterfaceForMyKits authorInterface;
    HashMap<String,ArrayList<String>> map;
    String response;

    AuthorsListAdapterForMyKits(ArrayList<String> myList, Context c,AuthorInterfaceForMyKits authorInterface,String response) {
        this.myList = myList;
        this.c = c;
        this.authorInterface=authorInterface;
        setHasStableIds(true);
        this.response=response;
    }

    @Override
    public AuthorsListAdapterForMyKits.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

//                SharedPreferences allow=c.getSharedPreferences("allow",Context.MODE_PRIVATE);
//
//                Log.d("prefsAllow",allow.getInt("allow",1)+"");
//                if(allow.getInt("allow",1)==0){
//                    if(c!=null)
//                        Toast.makeText(c, "Your last paper submission is pending..\nPlease wait for few seconds before continuing..", Toast.LENGTH_SHORT).show();
//                }else{
//                    value = myList.get(holder.getAdapterPosition());
////                    ArrayList<String> names=map.get(value);
////
////                    for (int i=0;i<names.size();++i){
////                        Log.d("names", "onClick: "+names.get(i));
////                    }

                    KitsByAuthors f=new KitsByAuthors();
                    Bundle b=new Bundle();
                    b.putString("author",value);
                    b.putString("response",response);
                    f.setArguments(b);
                    String title="ADD NEW KITS";
                    authorInterface.changeFromAuthorForMyKits(f,title,response,value,myList);
//                }
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
