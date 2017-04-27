package in.truskills.liveexams.authentication;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import in.truskills.liveexams.R;

/**
 * This custom adapter is for the spinner of "Gender" and "Languages"
 * in Signup_Login.java
 */

public class CustomSpinnerAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> names;
    LayoutInflater inflater;

    public CustomSpinnerAdapter(Context applicationContext, ArrayList<String> names) {
        this.context = applicationContext;
        this.names = names;
        inflater = (LayoutInflater.from(applicationContext));
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

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.custom_spinner_layout, null);
        TextView namesTv = (TextView) view.findViewById(R.id.textForSpinner);
        namesTv.setText(names.get(position));
        Typeface tff2 = Typeface.createFromAsset(context.getAssets(), "fonts/Comfortaa-Regular.ttf");
        namesTv.setTypeface(tff2);
        return view;
    }
}
