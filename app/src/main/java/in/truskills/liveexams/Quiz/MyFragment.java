package in.truskills.liveexams.Quiz;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.truskills.liveexams.R;

//This fragment is used for quiz question and options display..

public class MyFragment extends Fragment {

    WebView webView;
    String stringVariable;
    String myQuestion, myExamId;
    ArrayList<String> myOptions;
    int mySi,myQi,myFragmentCount;
    MySqlDatabase ob;
    MyFragmentInterface o;


    public static final MyFragment newInstance(String question, ArrayList<String> options, String examId,int si,int qi,int fragmentCount) {
        MyFragment f = new MyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Question", question);
        bundle.putStringArrayList("Options", options);
        bundle.putString("ExamId", examId);
        bundle.putInt("SectionIndex",si);
        bundle.putInt("QuestionIndex",qi);
        bundle.putInt("FragmentCount",fragmentCount);
        f.setArguments(bundle);
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my, container, false);

        //Get the bundle arguments..
        myQuestion = getArguments().getString("Question");
        myOptions = getArguments().getStringArrayList("Options");
        myExamId = getArguments().getString("ExamId");
        mySi=getArguments().getInt("SectionIndex");
        myQi=getArguments().getInt("QuestionIndex");
        myFragmentCount=getArguments().getInt("FragmentCount");

        if(getArguments().getString("here")!=null){
            Log.d("here","again"+" "+getArguments().getString("here"));
        }else{
            Log.d("here","initially");
        }

        //Initialise web view variable..
        webView = (WebView) v.findViewById(R.id.webView);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        MyFragmentInterface obb=(MyFragmentInterface)getActivity();

        o=(MyFragmentInterface) getActivity();
        WebViewContent obj=new WebViewContent();
        obj.contentGenerator(myQuestion, myOptions, myExamId,webView,mySi,myQi,getActivity(),obb);

        return v;
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyFragment.this.refresh();
        }
    }

    MyReceiver r;
    public void refresh() {
        //your code in refresh.
        Log.d("Refresh", "YES");
        MyFragmentInterface obb=(MyFragmentInterface)getActivity();
        o=(MyFragmentInterface) getActivity();
        WebViewContent obj=new WebViewContent();
        obj.contentGenerator(myQuestion, myOptions, myExamId,webView,mySi,myQi,getActivity(),obb);
    }

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(r);
    }

    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(r,
                new IntentFilter("TAG_REFRESH"));
    }
}

interface MyFragmentInterface{
    public void enableButtons();
}

