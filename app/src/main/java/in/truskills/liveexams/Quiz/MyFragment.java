package in.truskills.liveexams.Quiz;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
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

        ob=new MySqlDatabase(getActivity());

        o=(MyFragmentInterface) getActivity();
        WebViewContent obj=new WebViewContent();
        obj.contentGenerator(myQuestion, myOptions, myExamId,webView,mySi,myQi,getActivity());

        return v;
    }
}

interface MyFragmentInterface{
    public void enableButtons();
}
