package in.truskills.liveexams.ParticularExam;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.truskills.liveexams.R;

/**
 * Created by Shivansh Gupta on 02-03-2017.
 */

public class CustomSpinnerForDetailsAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> names;
    LayoutInflater inflter;

    public CustomSpinnerForDetailsAdapter(Context applicationContext, ArrayList<String> names) {
        this.context = applicationContext;
        this.names = names;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        return names.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public int getIndex(String value){
        return names.indexOf(value);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflter.inflate(R.layout.custom_spinner_layout_for_details, null);
        TextView namesTv = (TextView) view.findViewById(R.id.textForSpinnerForDetails);
        namesTv.setText(names.get(position));
        Typeface tff2 = Typeface.createFromAsset(context.getAssets(), "fonts/Comfortaa-Regular.ttf");
        namesTv.setTypeface(tff2);
        return view;
    }
}

