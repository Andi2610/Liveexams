package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.truskills.liveexams.R;

/**
 * Created by Shivansh Gupta on 06-02-2017.
 */

public class GridViewAdapter extends BaseAdapter {

    int count,n=1;
    Context c;
    ArrayList<Integer> num=new ArrayList<>();
    ArrayList<Integer> type=new ArrayList<>();
    ArrayList<Integer> myList=new ArrayList<>();

    GridViewAdapter(ArrayList<Integer> num,ArrayList<Integer> type,Context c){
        this.num=num;
        this.type=type;
        this.c=c;
        int n=0;
        for(int i=0;i<num.size();++i){
            ++n;
            myList.add(n);
        }
    }
    @Override
    public int getCount() {
        return num.size();
    }

    @Override
    public Object getItem(int position) {
        return num.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv=new TextView(c);
        tv.setText(myList.get(position)+"");
        switch (type.get(position)){
            case 0:tv.setTextColor(c.getResources().getColor(R.color.black));
                break;
            case 1:tv.setTextColor(c.getResources().getColor(R.color.green));
                break;
            case 2:tv.setTextColor(c.getResources().getColor(R.color.orange));
                break;
            case 3:tv.setTextColor(c.getResources().getColor(R.color.red));
                break;
        }
        return tv;
    }
}
