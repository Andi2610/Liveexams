package in.truskills.liveexams.MainScreens;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
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
import in.truskills.liveexams.authentication.SplashScreen;

/**
 * Created by Shivansh Gupta on 13-05-2017.
 */

public class CoursesIncludedInKitListAdapter extends RecyclerView.Adapter<CoursesIncludedInKitListAdapter.MyViewHolder> {

    ArrayList<String> myList,ids;
    Context c;
    SharedPreferences prefs;
    Handler h;
    ProgressDialog dialog;
    String value,from;
    HashMap<String,ArrayList<String>> map;

    CoursesIncludedInKitListAdapter(ArrayList<String> myList,ArrayList<String> ids, Context c,String from) {
        this.myList = myList;
        this.c = c;
        this.ids=ids;
        this.from=from;
        setHasStableIds(true);
    }

    @Override
    public CoursesIncludedInKitListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_layout_my_streams, parent, false);

        return new CoursesIncludedInKitListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CoursesIncludedInKitListAdapter.MyViewHolder holder, int position) {
        value = myList.get(position);
        Typeface tff = Typeface.createFromAsset(c.getAssets(), "fonts/Comfortaa-Regular.ttf");
        holder.name.setText(value);
        holder.name.setTypeface(tff);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(from.equals("home")){

                    boolean isAppInstalled = appInstalledOrNot("in.truskills.truteach");

                    if(isAppInstalled) {
                        //This intent will help you to launch if the package is already installed
                        Intent LaunchIntent = c.getPackageManager()
                                .getLaunchIntentForPackage("in.truskills.truteach");
                        c.startActivity(LaunchIntent);

                        Toast.makeText(c, "app installed", Toast.LENGTH_SHORT).show();

                    } else {
                        // Do whatever we want to do if application not installed
                        // For example, Redirect to play store
                        final AlertDialog.Builder builder = new AlertDialog.Builder(c, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("TruTeach App Not Installed");  // GPS not found
                        builder.setMessage("You will be directed to play store now..\nPlease install the app"); // Want to enable?
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent ii=new Intent((Intent.ACTION_VIEW));
                                ii.setData(Uri.parse("market://details?id=in.truskills.truteach"));
                                c.startActivity(ii);
                            }
                        });
                        builder.setCancelable(true);
                        builder.create().show();

                    }

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

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = c.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

}

