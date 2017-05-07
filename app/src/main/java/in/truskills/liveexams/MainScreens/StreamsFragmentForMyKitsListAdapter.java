package in.truskills.liveexams.MainScreens;

import android.app.Activity;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.R;

/**
 * Created by Shivansh Gupta on 06-04-2017.
 */

public class StreamsFragmentForMyKitsListAdapter extends RecyclerView.Adapter<StreamsFragmentForMyKitsListAdapter.MyViewHolder> {

    List<String> myList;
    Context c;
    SharedPreferences prefs;
    Handler h;
    ProgressDialog dialog;
    String value;
    StreamInterfaceForMyKits streamInterface;
    HashMap<String,ArrayList<String>> map;

    StreamsFragmentForMyKitsListAdapter(List<String> myList, Context c,StreamInterfaceForMyKits streamInterface) {
        this.myList = myList;
        this.c = c;
        this.streamInterface=streamInterface;
        setHasStableIds(true);
    }

    @Override
    public StreamsFragmentForMyKitsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
                    value = myList.get(holder.getAdapterPosition());

                    final RequestQueue requestQueue = Volley.newRequestQueue(c);
                    prefs = c.getSharedPreferences("prefs", Context.MODE_PRIVATE);

                    if(c!=null){
                        dialog = new ProgressDialog(c);
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setMessage("Loading. Please wait...");
                        dialog.setIndeterminate(true);
                        dialog.setCancelable(false);
                        dialog.show();
                    }

                    ConstantsDefined.updateAndroidSecurityProvider((Activity) c);
                    ConstantsDefined.beforeVolleyConnect();

                Log.d("kit", "onClick: "+value);

                value=value.replaceAll(" ","");

                    String url = ConstantsDefined.apiForKit + "searchProductKitsByStreamName/"+value;
                Log.d("kit", "onClick: "+url);
                    StringRequest stringRequest = new StringRequest(Request.Method.GET,
                            url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("kit", "onResponse: "+response);

                            if(dialog!=null)
                                dialog.dismiss();
                            try {

                                JSONObject jsonObject=new JSONObject(response);
                                String success=jsonObject.getString("success");
                                if(success.equals("true")){
                                    ArrayList<String> ans;
                                    ans=MiscellaneousParser.searchExamsByStreamNameParserForMyKits(jsonObject);

                                    if(ans.size()==0){
                                        if(c!=null)
                                            Toast.makeText(c, "No kits available for this stream at present", Toast.LENGTH_LONG).show();
                                    }else{

                                        Intent i =new Intent(c,AllAuthorsPerFieldForKitActivity.class);
                                        Bundle b=new Bundle();
                                        b.putStringArrayList("list",ans);
                                        b.putString("response",jsonObject.toString());
                                        i.putExtra("bundle",b);
                                        c.startActivity(i);
                                    }
                                }else{
                                    if(c!=null)
                                        Toast.makeText(c, "Something went wrong..\n" +
                                                "Please try again..", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.d("kit", "onErrorResponse: "+error);
                            if(dialog!=null)
                                dialog.dismiss();
                            if(ConstantsDefined.isOnline(c)){
                                //Do nothing..
                                if(c!=null)
                                    Toast.makeText(c, "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                            }else{
                                if(c!=null)
                                    Toast.makeText(c, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();

                            return params;
                        }
                    };
                    requestQueue.add(stringRequest);
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
