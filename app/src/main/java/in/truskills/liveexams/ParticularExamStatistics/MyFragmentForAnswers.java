package in.truskills.liveexams.ParticularExamStatistics;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.util.ArrayList;

import in.truskills.liveexams.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragmentForAnswers extends Fragment {


    WebView webView;
    String myQuestion, myAnswer, correctAnswer;
    ArrayList<String> myOptions;

    public static final MyFragmentForAnswers newInstance(String question, ArrayList<String> options, String myAnswer, String correctAnswer) {
        MyFragmentForAnswers f = new MyFragmentForAnswers();
        Bundle bundle = new Bundle();
        bundle.putString("Question", question);
        bundle.putStringArrayList("Options", options);
        bundle.putString("myAnswer", myAnswer);
        bundle.putString("correctAnswer", correctAnswer);
        f.setArguments(bundle);
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_fragment_for_answers, container, false);

        myQuestion = getArguments().getString("Question");
        myOptions = getArguments().getStringArrayList("Options");
        myAnswer = getArguments().getString("myAnswer");
        correctAnswer = getArguments().getString("correctAnswer");

        //Initialise web view variable..
        webView = (WebView) v.findViewById(R.id.webViewForAnswers);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);

        WebViewContentForAnswers obj = new WebViewContentForAnswers();
        obj.contentGenerator(myQuestion, myOptions, webView, myAnswer, correctAnswer);

        return v;
    }


}
