package in.truskills.liveexams.Quiz;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.util.ArrayList;

import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

//This fragment is used for quiz question and options display..

public class MyFragment extends Fragment implements Updateable {

    WebView webView;
    String stringVariable;
    String myQuestion, myExamId;
    ArrayList<String> myOptions;
    int mySi, myQi, myFragmentCount;
    MyFragmentInterface o;

    public static final MyFragment newInstance(String question, ArrayList<String> options, String examId, int si, int qi, int fragmentCount) {
        MyFragment f = new MyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Question", question);
        bundle.putStringArrayList("Options", options);
        bundle.putString("ExamId", examId);
        bundle.putInt("SectionIndex", si);
        bundle.putInt("QuestionIndex", qi);
        bundle.putInt("FragmentCount", fragmentCount);
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
        mySi = getArguments().getInt("SectionIndex");
        myQi = getArguments().getInt("QuestionIndex");
        myFragmentCount = getArguments().getInt("FragmentCount");

        if (getArguments().getString("here") != null) {
            Log.d("here", "again" + " " + getArguments().getString("here"));
        } else {
            Log.d("here", "initially");
        }

        //Initialise web view variable..
        webView = (WebView) v.findViewById(R.id.webView);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);

        MyFragmentInterface obb = (MyFragmentInterface) getActivity();

        o = (MyFragmentInterface) getActivity();
        WebViewContent obj = new WebViewContent();
        obj.contentGenerator(myQuestion, myOptions, webView, mySi, myQi, getActivity(), obb, myFragmentCount);

        return v;
    }

    @Override
    public void update() {
        MyFragmentInterface obb = (MyFragmentInterface) getActivity();
        o = (MyFragmentInterface) getActivity();
        WebViewContent obj = new WebViewContent();
        obj.contentGenerator(myQuestion, myOptions, webView, mySi, myQi, getActivity(), obb, myFragmentCount);
    }
}

interface MyFragmentInterface {
    public void enableButtons();

    public void putDetailsForNotAnswered(int si, int qi, int fi);
}

interface Updateable {
    public void update();
}

