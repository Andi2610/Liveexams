package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Shivansh Gupta on 06-02-2017.
 */

public class GridViewAdapter extends BaseAdapter {

    int count,n=1;
    Context c;
    ArrayList<Integer> num=new ArrayList<>();
    ArrayList<Integer> myList=new ArrayList<>();

    GridViewAdapter(ArrayList<Integer> num,Context c){
        this.num=num;
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
        return tv;
    }
}
