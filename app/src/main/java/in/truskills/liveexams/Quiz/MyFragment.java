package in.truskills.liveexams.Quiz;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.truskills.liveexams.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment {

    WebView webView;
    String stringVariable;
    String myQuestion;
    ArrayList<String> myOptions;


    public static final MyFragment newInstance(String question, ArrayList<String> options)
    {
        MyFragment f = new MyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Question",question);
        bundle.putStringArrayList("Options",options);
        f.setArguments(bundle);
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_my, container, false);
       myQuestion = getArguments().getString("Question");
        myOptions=getArguments().getStringArrayList("Options");
        webView=(WebView) v.findViewById(R.id.webView);
        String string=WebViewContent.contentGenerator(myQuestion,myOptions);
        webView.loadDataWithBaseURL(null,string,"text/HTML","UTF-8",null);
        webView.addJavascriptInterface(new Object()
        {
            @JavascriptInterface           // For API 17+
            public void performClick(String strl)
            {
                stringVariable = strl;
                Toast.makeText (getActivity(), stringVariable, Toast.LENGTH_SHORT).show();
            }
        }, "ok");
        return v;
    }
}
