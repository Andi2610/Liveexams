package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;

import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

public class MyQuestionPaperLoad extends AppCompatActivity {


    //Declare the variables..
    int languageArray[][], fragmentIndex[][], found = 0;
    HashMap<String, String> map1, map2, map3, map4, map5, map6, map7, map8, map9, map10, map11, map12;
    int noOfQuestions = 0, noOfExamName, noOfLanguage, noOfOption, noOfSections, fi = -1, hour, minute, myTime, curCount = 0, myCount = 0, questionArray[];
    RequestQueue requestQueue;
    String url, success, response, Paperset, Sections, Section, SectionQuestions, AttributesOfSection, Question, myAskedIn, myExamName, myYear, myLanguage;
    String myQuestionText, myOptions, myOption, nm, nmm, myOp, text, myAt, myAttri, section_id, section_max_marks, section_time, section_description, section_rules;
    String questionAttributes, opText, examDuration, examId, name, selectedLanguage, myExamDuration, paperName;
    ArrayList<Fragment> fList;
    TextView myWaitMessage;
    float per;
    QuizDatabase ob;
    ProgressBar progressBar;
    ArrayList<String> urls, groups;
    SharedPreferences prefs;
    Button retryButtonForDownload, exitButton;
    ThreadPoolExecutor executor;
    int NUMBER_OF_CORES;
    com.wang.avi.AVLoadingIndicatorView avi;
    String[] children;
    int len;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_question_paper_load);

        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.show();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        retryButtonForDownload = (Button) findViewById(R.id.retryButtonForDownload);
        exitButton = (Button) findViewById(R.id.exitButton);

        myWaitMessage = (TextView) findViewById(R.id.myWaitMessage);
        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        myWaitMessage.setTypeface(tff1);
        retryButtonForDownload.setTypeface(tff1);
        exitButton.setTypeface(tff1);

        retryButtonForDownload.setVisibility(View.INVISIBLE);
        exitButton.setVisibility(View.INVISIBLE);

        examId = getIntent().getStringExtra("examId");
        paperName = getIntent().getStringExtra("name");
        selectedLanguage = getIntent().getStringExtra("language");

        fList = new ArrayList<>();
        urls = new ArrayList<>();
        groups = new ArrayList<>();
        ob = new QuizDatabase(this);

        downloadQP();
    }

    public void downloadQP() {

    }
}
